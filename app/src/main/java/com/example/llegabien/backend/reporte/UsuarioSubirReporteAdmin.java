package com.example.llegabien.backend.reporte;

import static android.app.Activity.RESULT_OK;
import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.mapa.ubicacion.UbicacionDAO;
import com.example.llegabien.backend.mapa.ubicacion.UbicacionGeodicacion;
import com.example.llegabien.backend.mapa.ubicacion.ubicacion;
import com.example.llegabien.backend.usuario.usuario;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import org.bson.types.ObjectId;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class UsuarioSubirReporteAdmin {

    private Intent mIntent;
    private List<reporte> mReportesParaAnadir, mReportesDeArchivo;
    private final Context mContext;
    private UbicacionDAO mUbicacionDAO;
    private List<ubicacion> mUbicaciones;
    private int mSumaCantidadDelitosColonia, mSumaCantidadDelitosMunicipio;

    public UsuarioSubirReporteAdmin(Context context) {
        this.mContext = context;
    }

    public Intent getIntent() {
        return mIntent;
    }

    public void inicializarIntent() {
        mIntent = new Intent(Intent.ACTION_GET_CONTENT);
        mIntent.addCategory(Intent.CATEGORY_OPENABLE);
        mIntent.setType("*/*");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void subirReportesAdmin(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            obtenerReportesDeArchivo(data);
            actualizarUbicaciones(this.mReportesDeArchivo);
            ReporteDAO reporteDAO = new ReporteDAO(mContext);
            reporteDAO.anadirReportes(this.mReportesParaAnadir);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void obtenerReportesDeArchivo(Intent data) {
        if (data != null) {
            usuario usuario = Preferences.getSavedObjectFromPreference(mContext, PREFERENCE_USUARIO, usuario.class);
            if (usuario != null) {
                try {
                    InputStreamReader inputStreamReader = new InputStreamReader(mContext.getContentResolver().openInputStream(data.getData()));
                    CSVReader csvReader = new CSVReader(inputStreamReader);
                    String[] nextLine = csvReader.readNext();

                    this.mReportesDeArchivo = new ArrayList<>();

                    while ((nextLine = csvReader.readNext()) != null) {
                        reporte reporte = new reporte();
                        reporte.setFechaReporte(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
                        reporte.setAutor(usuario.getNombre());
                        reporte.setIdUsuario(usuario.get_id());

                        // Para establecer las fechas.
                        LocalDate fechaDelito = LocalDate.parse(nextLine[0].trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        LocalTime horaDelito = LocalTime.parse("00:00", DateTimeFormatter.ofPattern("HH:mm"));
                        reporte.setFecha(Date.from(fechaDelito.atTime(horaDelito).atZone(ZoneId.systemDefault()).toInstant()));

                        // Para establecer la ubicacion.
                        reporte.setUbicacion(nextLine[1].trim());

                        // Para establecer el tipo de delito.
                        reporte.setTipoDelito(nextLine[2].trim());

                        // Para establecer la cantidad.
                        reporte.setCantidad(Integer.valueOf(nextLine[3]));

                        // Se añade el reporte a una lista de reportes.
                        this.mReportesDeArchivo.add(reporte);
                    }

                    Log.v("QUICKSTART", "TAMAÑO DE REPORTES: " + this.mReportesDeArchivo.size());

                } catch (IOException | CsvException e) {
                    Toast.makeText(mContext, "Error: " + e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void actualizarUbicaciones(List<reporte> reportes) {
        this.mReportesParaAnadir = new ArrayList<>();
        this.mUbicaciones = new ArrayList<>();
        this.mUbicacionDAO = new UbicacionDAO(mContext);

        calcularSumaDelitos(reportes);
        mUbicacionDAO.updateUbicaciones(this.mUbicaciones);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<reporte> calcularSumaDelitos(List<reporte> reportes) {
        boolean isMunicipioEnLista = true;

        // Para obtener el nombre de colonia para verficicar que esta exista en la BD.
        String municipioNombre = reportes.get(0).getUbicacion().split(",", 3)[2].trim();

        // Para ver si la colonia ya esta en la lista de ubicaciones.
        ubicacion municipio = mUbicacionDAO.obtenerUbicacionConNombreDeLista(municipioNombre, this.mUbicaciones);
        if (municipio == null) {
            municipio = mUbicacionDAO.obtenerUbicacionConNombre(municipioNombre);
            isMunicipioEnLista = false;
        }

        if (municipio != null) {
            // Para obtener todos los reportes que tengan el mismo municipio.
            List<reporte> reportesConMunicipioRepetido = reportes.stream()
                    .filter(reporte -> reporte.getUbicacion().contains(municipioNombre))
                    .collect(Collectors.toList());

            this.mSumaCantidadDelitosMunicipio = 0;
            calcularSumaDelitosColonia(reportesConMunicipioRepetido, municipio);
            actualizarSumaDelitos(municipio, this.mSumaCantidadDelitosMunicipio, isMunicipioEnLista);

        }

        // Para remover todos los reportes que contengan el municipio del reporte.
        reportes.removeIf(reporte -> reporte.getUbicacion().contains(municipioNombre));
        Log.v("QUICKSTART", String.valueOf(reportes.size()));

        if (reportes.size() != 0)
            return calcularSumaDelitos(reportes);
        else {
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<reporte> calcularSumaDelitosColonia(List<reporte> reportes, ubicacion municipio) {
        boolean isColoniaEnLista = true;
        List<ubicacion> colonias = mUbicacionDAO.obtenerColoniasConIdMunicipio(municipio.get_id());

        // Para obtener el nombre de colonia para verficicar que esta exista en la BD.
        String coloniaNombre = reportes.get(0).getUbicacion().split(",", 2)[1].trim();

        // Para ver si la colonia ya esta en la liata de ubicaciones.
        ubicacion colonia = mUbicacionDAO.obtenerUbicacionConNombreDeLista(coloniaNombre, this.mUbicaciones);
        if (colonia == null) {
            colonia = mUbicacionDAO.obtenerColonia(coloniaNombre, colonias);
            isColoniaEnLista = false;
        }

        if (colonia != null) {
            // Para obtener todos los reportes que tengan la misma colonia.
            List<reporte> reportesConColoniaRepetida = reportes.stream()
                    .filter(reporte -> reporte.getUbicacion().contains(coloniaNombre))
                    .collect(Collectors.toList());

            this.mSumaCantidadDelitosColonia = 0;
            calcularSumaDelitosCalle(reportesConColoniaRepetida, colonia, municipio);
            Log.v("QUICKSTART", String.valueOf(this.mSumaCantidadDelitosColonia));
            actualizarSumaDelitos(colonia, this.mSumaCantidadDelitosColonia, isColoniaEnLista);

        }

        // Para remover todos los reportes que contengan la colonia del reporte.
        reportes.removeIf(reporte -> reporte.getUbicacion().contains(coloniaNombre));
        Log.v("QUICKSTART", String.valueOf(reportes.size()));

        if (reportes.size() != 0)
            return calcularSumaDelitosColonia(reportes, municipio);
        else {
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<reporte> calcularSumaDelitosCalle(List<reporte> reportes, ubicacion colonia, ubicacion municipio) {
        boolean isCalleEnLista = true;
        String calleNombreReporte = reportes.get(0).getUbicacion();

        // Para geocodificar la calle del reporte y obtener la direccion como la tiene Google Maps.
        UbicacionGeodicacion ubicacionGeodicacion = new UbicacionGeodicacion(mContext);
        Address addressCalle = ubicacionGeodicacion.geocodificarUbiciacionPrueba(calleNombreReporte.split(",", 2)[0].trim()
                + ", " + colonia.getNombre());

        // Para establecer el nombre de la calle que se va a utilizar en el objeto ubicacion.
        String ubicacionNombreGoogle = establecerNombreUbicacion(addressCalle, colonia);

        if (ubicacionNombreGoogle != null) {
            ubicacion calle = mUbicacionDAO.obtenerUbicacionConNombreDeLista(ubicacionNombreGoogle, this.mUbicaciones);
            if(calle == null){
                calle = mUbicacionDAO.obtenerUbicacionConNombre(ubicacionNombreGoogle);
                isCalleEnLista = false;
            }
            if (calle == null) {
                // Si no existe la calle, se crea la ubicacion con los atributos neecesarios.
                calle = new ubicacion();
                calle.set_id(new ObjectId());
                calle.set_partition("LlegaBien");
                calle.setNombre(ubicacionNombreGoogle);
                calle.setIdColonia(colonia.get_id());
                calle.setIdMunicipio(municipio.get_id());
                calle.setSuma_delitos(0);
                calle.setDelitos_semana(0);
                calle.setMeta_reduccion_double(0.0);
                calle.setMedia_historica_double(0.0);
                calle.setTipo("calle");
                this.mUbicaciones.add(calle);
                isCalleEnLista = true;
            }

            // Para obtener todos los reportes que tengan la misma calle.
            List<reporte> reportesConCalleRepetida = reportes.stream()
                    .filter(reporte -> reporte.getUbicacion().equals(calleNombreReporte))
                    .collect(Collectors.toList());


            // Para sumar la cantidad de delitos de cada objeto "reporte" de la lista "reportesConCalleRepetida" (lista filtarada).
            int sumaCantidadDelitos = reportesConCalleRepetida.stream().mapToInt(reporte::getCantidad).sum();
            Log.v("QUICKSTART", String.valueOf(sumaCantidadDelitos));

            // Para reemplazar la calle, colonia y municipio en la lista de ubicaciones.
            actualizarSumaDelitos(calle, sumaCantidadDelitos, isCalleEnLista);
            this.mSumaCantidadDelitosColonia += sumaCantidadDelitos;
            this.mSumaCantidadDelitosMunicipio += sumaCantidadDelitos;

            // Para asignarles a todos los reportes con la misma ubicacion el id del objeto "ubicacion".
            ubicacion calleAux = calle;
            reportesConCalleRepetida.forEach((reporte) -> reporte.setIdUbicacion(calleAux.get_id()));
            this.mReportesParaAnadir.addAll(reportesConCalleRepetida);
        }

        // Para remover todos los reportes que contengan la calle que se acaba de buscar.
        reportes.removeIf(reporte -> reporte.getUbicacion().equals(calleNombreReporte));

        if (reportes.size() != 0)
            return calcularSumaDelitosCalle(reportes, colonia, municipio);
        else {
            return null;
        }
    }

    private void actualizarSumaDelitos(ubicacion ubicacion, int sumaCantidadDelitos, boolean isUbicacionEnLista) {
        int index = -1;

        if (isUbicacionEnLista) {
            // Para obtener el index de la ubicacion.
            for (int i = 0; i < this.mUbicaciones.size(); i++) {
                if (this.mUbicaciones.get(i).get_id().equals(ubicacion.get_id())) {
                    index = i;
                    break;
                }
            }

            // Para modificar el numero de delitos semanales de la ubicacion.
            ubicacion.setSuma_delitos(ubicacion.getSuma_delitos() + sumaCantidadDelitos);

            // Para actulizar la colonia dentro de la ubicacion.
            this.mUbicaciones.set(index, ubicacion);
        } else
            this.mUbicaciones.add(ubicacion);
    }

    private String establecerNombreUbicacion(Address addressGoogle, ubicacion colonia) {
        // Para establecer el nombre de la calle se establece con Thoroughfare o FeatureName.
        // En caso de que Thoroughfare o FeatureName sean nulos, quiere decir que la ubicacion no se encontró como calle o vía pública.
        String calleNombreGoogle = addressGoogle.getThoroughfare();
        if (calleNombreGoogle == null)
            calleNombreGoogle = addressGoogle.getFeatureName();

        if (calleNombreGoogle != null) {
            // Para establecer el nombre de la colonia se establece con SubLocality.
            // En caso de que SubLocality sea nulo, se tomara el nombre del objeto ubicacion que corresponde a la colonia de la calle.
            String coloniaNombreGoogle = addressGoogle.getSubLocality();
            if (coloniaNombreGoogle == null) {
                coloniaNombreGoogle = colonia.getNombre().split(",", 2)[0].trim();
            }

            // Se retorna el nombre completo de la calle.
            return (calleNombreGoogle + ", " + coloniaNombreGoogle + ", " + addressGoogle.getLocality() + ", " + addressGoogle.getAdminArea()
                    + ", " + addressGoogle.getCountryName()).toUpperCase(Locale.ROOT);
        }
        return null;
    }
}