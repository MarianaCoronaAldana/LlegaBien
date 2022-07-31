package com.example.llegabien.backend.reporte;

import static android.app.Activity.RESULT_OK;
import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.usuario.usuario;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

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

public class UsuarioSubirReporteAdmin {

    private Intent mIntent;
    private List<reporte> mReportesDeArchivo;
    private final Context mContext;

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
            // Para obtener reportes del archivo .csv.
            obtenerReportesDeArchivo(data);

            // Para añadir los reportes del archivo a la BD.
            ReporteDAO reporteDAO = new ReporteDAO(mContext);
            reporteDAO.anadirReportes(this.mReportesDeArchivo);

            // CÓDIGO QUE SE UTILIZÓ PARA HACER EL CÁLCULO INICIAL DE SEGURIDAD //
            /*obtenerReportesDeArchivo(data);

            UbicacionSeguridad ubicacionSeguridad = new UbicacionSeguridad(mContext);
            ReporteDAO reporteDAO = new ReporteDAO(mContext);
            reporteDAO.anadirReportes(ubicacionSeguridad.actualizarUbicacionesSumaDelitos(this.mReportesDeArchivo));*/


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
}