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
import com.example.llegabien.backend.ubicacion.UbicacionDAO;
import com.example.llegabien.backend.ubicacion.UbicacionGeocodificacion;
import com.example.llegabien.backend.ubicacion.ubicacion;
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
import java.util.stream.Collectors;

public class UsuarioSubirReporteAdmin {

    private Intent mIntent;
    private List<reporte> mReportesDeArchivo, mReportesParaAnadir;
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
            actualizarUbicacionesSumaDelitos(this.mReportesDeArchivo);

            ReporteDAO reporteDAO = new ReporteDAO(mContext);
            reporteDAO.anadirReportes(this.mReportesParaAnadir);

            // FUNCIÓN QUE SE UTILIZA PARA SUBIR REPORTES CUANDO LA APP SE DESINCRONIZA //
            //reporteDAO.anadirReportesIIEG(data);

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
                        // Para crear un objeto nuevo de reportes.
                        reporte reporte = new reporte();

                        // Para establecer la fecha en la que se sube el reporte.
                        reporte.setFechaReporte(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));

                        // Para establecer el autor del reporte.
                        reporte.setAutor(usuario.getNombre());

                        // Para establecer el Id del usuario que hizo el reportes (admin).
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

    // Primero se tienen que sumar todas los delitos a las ubicaciones.
    // En el cáclculo semanal se excluiran los reportes de admin a la hora de modificar la suma total de delitos, pues ya se hace aquí.
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void actualizarUbicacionesSumaDelitos(List<reporte> reportes) {
        this.mUbicacionDAO = new UbicacionDAO(mContext);
        this.mReportesParaAnadir = new ArrayList<>();
        this.mUbicaciones = new ArrayList<>();

        calcularSumaDelitosMunicipio(reportes);

        mUbicacionDAO.updateUbicaciones(this.mUbicaciones);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<reporte> calcularSumaDelitosMunicipio(List<reporte> reportes) {
        boolean isMunicipioEnLista = true;

        // Para obtener el nombre del municipio.
        String municipioNombre = reportes.get(0).getUbicacion().split(",", 3)[2].trim();

        // Para ver si el municipio ya esta en la lista de ubicaciones.
        ubicacion municipio = mUbicacionDAO.obtenerUbicacionConNombreDeLista(municipioNombre, this.mUbicaciones);
        if (municipio == null) {
            // Para ver si el municipio está en la BD.
            municipio = mUbicacionDAO.obtenerUbicacionConNombre(municipioNombre);
            isMunicipioEnLista = false;
        }

        if (municipio != null) {
            // Para obtener todos los reportes que tengan el mismo municipio.
            List<reporte> reportesConMunicipioRepetido = reportes.stream()
                    .filter(reporte -> reporte.getUbicacion().contains(municipioNombre))
                    .collect(Collectors.toList());

            // Variable que acumula la cantidad de delitos que pertenezcan al municipio.
            this.mSumaCantidadDelitosMunicipio = 0;

            // Para calcular la suma de delitos de las colonias que pertenezcan al municipio.
            calcularSumaDelitosColonia(reportesConMunicipioRepetido, municipio);

            // Se actualiza el municipio con el nuevo valor de "SumaCantidadDelitosMunicipio".
            actualizarSumaDelitos(municipio, this.mSumaCantidadDelitosMunicipio, isMunicipioEnLista);

        }

        // Para remover todos los reportes que contengan el municipio del reporte.
        reportes.removeIf(reporte -> reporte.getUbicacion().contains(municipioNombre));
        Log.v("QUICKSTART", String.valueOf(reportes.size()));

        if (reportes.size() != 0)
            // La función se repíte hasta que ya no haya municipios diferentes.
            return calcularSumaDelitosMunicipio(reportes);
        else {
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<reporte> calcularSumaDelitosColonia(List<reporte> reportes, ubicacion municipio) {
        boolean isColoniaEnLista = true;
        List<ubicacion> colonias = mUbicacionDAO.obtenerColoniasConIdMunicipio(municipio.get_id());

        // Para obtener el nombre de colonia.
        String coloniaNombre = reportes.get(0).getUbicacion().split(",", 2)[1].trim();


        ubicacion colonia = mUbicacionDAO.obtenerUbicacionConNombreDeLista(coloniaNombre, this.mUbicaciones);
        if (colonia == null) {
            // Para verificar si la colonia existe en la BD.
            colonia = mUbicacionDAO.obtenerColonia(coloniaNombre, colonias);
            isColoniaEnLista = false;
        }

        if (colonia != null) {
            // Para obtener todos los reportes que tengan la misma colonia.
            List<reporte> reportesConColoniaRepetida = reportes.stream()
                    .filter(reporte -> reporte.getUbicacion().contains(coloniaNombre))
                    .collect(Collectors.toList());

            // Variable que acumula la cantidad de delitos que pertenezcan a la colonia.
            this.mSumaCantidadDelitosColonia = 0;

            // Para calcular la suma de delitos de las calles que pertenezcan a la colonia.
            calcularSumaDelitosCalle(reportesConColoniaRepetida, colonia, municipio);
            Log.v("QUICKSTART", String.valueOf(this.mSumaCantidadDelitosColonia));

            // Se actualiza la colonia con el nuevo valor de "SumaCantidadDelitosColonia".
            actualizarSumaDelitos(colonia, this.mSumaCantidadDelitosColonia, isColoniaEnLista);

        }

        // Para remover todos los reportes que contengan la colonia del reporte.
        reportes.removeIf(reporte -> reporte.getUbicacion().contains(coloniaNombre));
        Log.v("QUICKSTART", String.valueOf(reportes.size()));

        if (reportes.size() != 0)
            // La funcion se repite hasta que ya no haya colonias que pertenezcan al municipio.
            return calcularSumaDelitosColonia(reportes, municipio);
        else {
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<reporte> calcularSumaDelitosCalle(List<reporte> reportes, ubicacion colonia, ubicacion municipio) {
        boolean isCalleEnLista = true;

        // Para obtener el nombre de la calle.
        String calleNombreReporte = reportes.get(0).getUbicacion();

        // -> ESTO SE HACE INDEPENDIENTEMENTE DE SI LA ADDRESS QUE NOS DA GOOGLE ES VALIDA O NO <- //

        // EXPLICACIÓN: si se ejecuta esta funcion quiere decir que la colonia sí se encontró,
        // entonces en caso de que la calle que proporciona Google no sea válida se le puede asignar el Id de su colonia a los reportes,
        // y así tomarlos en cuenta para la suma de delitos totales (de la colonia y muncipio correspondientes).

        // Para obtener todos los reportes que tengan la misma calle.
        List<reporte> reportesConCalleRepetida = reportes.stream()
                .filter(reporte -> reporte.getUbicacion().equals(calleNombreReporte))
                .collect(Collectors.toList());

        // Variable que acumula la cantidad de delitos que pertenezcan a la calle.
        int sumaCantidadDelitosCalle = reportesConCalleRepetida.stream().mapToInt(reporte::getCantidad).sum();
        Log.v("QUICKSTART", String.valueOf(sumaCantidadDelitosCalle));

        // Se suma la cantidad de delitos de la calle a su correspondiente colonia y municipio.
        this.mSumaCantidadDelitosColonia += sumaCantidadDelitosCalle;
        this.mSumaCantidadDelitosMunicipio += sumaCantidadDelitosCalle;

        // -> FIN <- //


        // Para geocodificar la calle del reporte y obtener la direccion como la tiene Google Maps.
        UbicacionGeocodificacion ubicacionGeodicacion = new UbicacionGeocodificacion(mContext);
        Address addressCalle = ubicacionGeodicacion.geocodificarUbiciacion(calleNombreReporte.split(",", 2)[0].trim()
                + ", " + colonia.getNombre());


        // Para establecer el nombre de la calle que se va a utilizar en el objeto ubicacion.
        String ubicacionNombreGoogle =  UbicacionGeocodificacion.establecerNombreUbicacion(addressCalle, colonia, municipio);


        // Si la dirección que entrega Google Maps es válida.
        if (ubicacionNombreGoogle != null) {
            // Para ver si la calle ya esta en la lista de ubicaciones.
            ubicacion calle = mUbicacionDAO.obtenerUbicacionConNombreDeLista(ubicacionNombreGoogle, this.mUbicaciones);
            if(calle == null){
                // Para ver si la calle está en la BD.
                calle = mUbicacionDAO.obtenerUbicacionConNombre(ubicacionNombreGoogle);
                isCalleEnLista = false;
            }
            if (calle == null) {
                // Si no existe la calle, se crea la ubicacion con los atributos necesarios.
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

            // Se actualiza la calle con el nuevo valor de "SumaCantidadDelitosCalle".
            actualizarSumaDelitos(calle, sumaCantidadDelitosCalle, isCalleEnLista);

            // Para asignarles a todos los reportes con la misma ubicacion el id de la calle.
            ubicacion calleAux = calle;
            reportesConCalleRepetida.forEach((reporte) -> reporte.setIdUbicacion(calleAux.get_id()));

        }
        // Si la calle que entrega Google Maps no es válida, se le asigna el Id de su colonia.
        else{
            ubicacion coloniaAux = colonia;
            reportesConCalleRepetida.forEach((reporte) -> reporte.setIdUbicacion(coloniaAux.get_id()));
        }

        // Para añadir los reportes a la lista de los que SÍ se van a añadir.
        this.mReportesParaAnadir.addAll(reportesConCalleRepetida);

        // Para remover todos los reportes que contengan la calle que se acaba de buscar.
        reportes.removeIf(reporte -> reporte.getUbicacion().equals(calleNombreReporte));

        if (reportes.size() != 0)
            // La funcion se repite hasta que ya no haya calles que pertenezcan a la colonia.
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

}