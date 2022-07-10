package com.example.llegabien.backend.usuario;

import static android.app.Activity.RESULT_OK;
import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.mapa.ubicacion.UbicacionSeguridad;
import com.example.llegabien.backend.reporte.ReporteDAO;
import com.example.llegabien.backend.reporte.reporte;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class UsuarioSubirReporteAdmin {

    private Intent mIntent;


    public Intent getIntent() {
        return mIntent;
    }

    public void inicializarIntent() {
        mIntent = new Intent();
        // Para que se admita cualquier tipo de documento.
        mIntent.setType("*/*");
        // Permite seleccionar archivos.
        mIntent.setAction(Intent.ACTION_GET_CONTENT);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void obtenerArchivoExcel(int resultCode, Intent data, Context context) throws IOException, BiffException {
        if (resultCode == RESULT_OK) {
            if (data != null) {
                subirReportes(data.getData(), context);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void subirReportes(Uri uri, Context context) throws IOException, BiffException {
        Sheet sheet = null;
        int rows = 0, columns = 0;

        List<reporte> reportes = new ArrayList<>();
        usuario usuario = Preferences.getSavedObjectFromPreference(context, PREFERENCE_USUARIO, usuario.class);
        if (usuario != null) {

            // Para obtener el archivo .xls del dispositivo y abrirlo.
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            Workbook workbook = Workbook.getWorkbook(inputStream);

            sheet = workbook.getSheet(0);
            rows = sheet.getRows();
            columns = sheet.getColumns();

            // Se itera en cada fila del archivo y se se inicializa un objeto "reporte".
            for (int i = 1; i < rows; i++) {

                reporte reporte = new reporte();
                reporte.setFechaReporte(java.util.Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
                reporte.setAutor(usuario.getNombre());
                reporte.setIdUsuario(usuario.get_id());

                for (int c = 0; c < columns; c++) {
                    Cell cell = sheet.getCell(c, i);

                    // Se establecen los valores del objeto segun sea la celda.
                    switch (c) {
                        case 0:
                            LocalDate fechaDelito = LocalDate.parse(cell.getContents(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                            LocalTime horaDelito = LocalTime.parse("00:00", DateTimeFormatter.ofPattern("HH:mm"));
                            reporte.setFecha(java.util.Date.from(fechaDelito.atTime(horaDelito).atZone(ZoneId.systemDefault()).toInstant()));
                            break;
                        case 1:
                            reporte.setUbicacion(cell.getContents());
                            break;
                        case 2:
                            reporte.setTipoDelito(cell.getContents());
                            break;
                        case 3:
                            reporte.setCantidad(Integer.valueOf(cell.getContents()));
                            break;
                    }
                }

                // Se añade el reporte a una lista de reportes.
                reportes.add(reporte);
            }

            // Para actualizar el nivel de seguridad de las ubicaciones encontradas y filtrar la lista de reportes.
            UbicacionSeguridad ubicacionSeguridad = new UbicacionSeguridad(context);
            reportes = ubicacionSeguridad.sumarDelitosReportes(reportes);

            // Se añaden todos los reportes que estan en la lista a la BD.
            ReporteDAO reporteDAO = new ReporteDAO(context);
            reporteDAO.anadirReportes(reportes);

            Toast.makeText(context, "SI SE PUDO", Toast.LENGTH_SHORT).show();

        }
    }
}