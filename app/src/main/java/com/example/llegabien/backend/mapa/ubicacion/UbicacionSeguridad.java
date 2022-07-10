package com.example.llegabien.backend.mapa.ubicacion;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.llegabien.backend.reporte.ReporteDAO;
import com.example.llegabien.backend.reporte.reporte;

import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class UbicacionSeguridad {

    private int mSumaCantidadDelitosMunicipio = 0, mSumaCantidadDelitosSemanalesMunicipio = 0, mSumaCantidadDelitosColonia = 0;
    private final Context mContext;
    private UbicacionDAO ubicacionDAO;
    private String calleString, coloniaString;
    private List<ubicacion> mColonias;
    private final List<reporte> reportesParaAnadir = new ArrayList<>();


    public UbicacionSeguridad(Context context) {
        mContext = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<reporte> sumarDelitosColoniaIIEG(List<reporte> reportes) {
        coloniaString = reportes.get(0).getUbicacion();

        // Para buscar y obtener la colonia y municpio de la ubicacion en la BD.
        ubicacion colonia = ubicacionDAO.obetenerColoniaDeLista(coloniaString, this.mColonias);
        if (colonia != null) {
            // Para obtener todas las ubicaciones repetidas en la lista de reportes.
            List<reporte> reportesConUbicacionRepetida = reportes.stream()
                    .filter(reporte -> reporte.getUbicacion().equals(coloniaString))
                    .collect(Collectors.toList());

            // Para asignarles a todos los reportes con la misma ubicacion el id del objeto "ubicacion".
            reportesConUbicacionRepetida.forEach((reporte) -> reporte.setIdUbicacion(colonia.get_id()));
            this.reportesParaAnadir.addAll(reportesConUbicacionRepetida);

            // Para sumar la cantidad de delitos de cada objeto "reporte" de la lista "reportesConUbicacionRepetida" (lista filtarada).
            int sumaCantidadDelitos = reportesConUbicacionRepetida.stream().mapToInt(reporte::getCantidad).sum();
            this.mSumaCantidadDelitosMunicipio += sumaCantidadDelitos;

            Log.v("QUICKSTART", ("UBICACION BUSCADA: " + coloniaString));
            Log.v("QUICKSTART", "NOMBRE UBICACION: " + colonia.getNombre());
            Log.v("QUICKSTART", "SUMA DELITOS: " + sumaCantidadDelitos);

            // Para obtener el index de la colonia que se modificará en la lista de ubicaciones "colonias".
            int index = this.mColonias.indexOf(colonia);

            // Para sumar los delitos a la colonia y municipio de la ubicación.
            colonia.setSuma_delitos(colonia.getSuma_delitos() + sumaCantidadDelitos);

            //Para actulizar la colonia dentro de la misma lista.
            this.mColonias.set(index, colonia);
        }

        // Para remover todos los reportes que contengan la ubicacion que se acaba de buscar.
        reportes.removeIf(reporte -> reporte.getUbicacion().equals(coloniaString));
        Log.v("QUICKSTART", String.valueOf(reportes.size()));

        if (reportes.size() != 0)
            return sumarDelitosColoniaIIEG(reportes);
        else {
            // Para actualizar todas las ubicaciones una vez que ya no haya reportes.
            ubicacionDAO.updateUbicaciones(this.mColonias);
            return null;
        }
    }

    private void sumarDelitosMunicipio() {
        Log.v("QUICKSTART", String.valueOf(this.mSumaCantidadDelitosMunicipio));
        String municipioString = "GUADALAJARA, JALISCO, MEXICO";
        ubicacion municipio = ubicacionDAO.obtenerMunicipio(municipioString);
        municipio.setSuma_delitos(municipio.getSuma_delitos() + mSumaCantidadDelitosMunicipio);
        ubicacionDAO.updateUbicacion(municipio);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<reporte> sumarDelitosReportes(List<reporte> reportes) {
        ubicacionDAO = new UbicacionDAO(mContext);
        this.mColonias = ubicacionDAO.obetenerColonias();
        sumarDelitosColoniaIIEG(reportes);
        if (this.mSumaCantidadDelitosMunicipio != 0)
            sumarDelitosMunicipio();

        return this.reportesParaAnadir;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void actualizarSeguridad(){
        this.mColonias = ubicacionDAO.obetenerColonias();
        sumarDelitosSemales();
        for (ubicacion ubicacion : this.mColonias){
            calcularMediaHistorica(ubicacion);
            calcularMetaReduccion(ubicacion);
            calcularNivelSeguridad(ubicacion);
        }
        ubicacionDAO.updateUbicaciones(this.mColonias);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sumarDelitosSemales() {
        ReporteDAO reporteDAO = new ReporteDAO(mContext);
        Date fechaInicio = java.util.Date.from(LocalDate.parse("25/10/2021", DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                .atTime(LocalTime.parse("00:00", DateTimeFormatter.ofPattern("HH:mm")))
                .atZone(ZoneId.systemDefault()).toInstant());
        Date fechaTermino = java.util.Date.from(LocalDate.parse("31/10/2021", DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                .atTime(LocalTime.parse("23:59", DateTimeFormatter.ofPattern("HH:mm")))
                .atZone(ZoneId.systemDefault()).toInstant());

        actualizarDelitosSemanales(reporteDAO.obtenerReportesPorFecha(fechaInicio, fechaTermino));
        actualizarDelitosSemanalesMunicipio();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<reporte> actualizarDelitosSemanales(List<reporte> reportesSemanales) {
        ObjectId IdUbicacion = reportesSemanales.get(0).getIdUbicacion();

        List<reporte> reportesConMismoIdUbicadcion = reportesSemanales.stream()
                .filter(reporte -> reporte.getIdUbicacion().equals(IdUbicacion))
                .collect(Collectors.toList());

        int sumaCantidadDelitosSemanles = reportesConMismoIdUbicadcion.stream().mapToInt(reporte::getCantidad).sum();
        this.mSumaCantidadDelitosSemanalesMunicipio += sumaCantidadDelitosSemanles;

        ubicacion colonia = ubicacionDAO.obetenerUbicacionConId(IdUbicacion);

        // Para obtener el index de la colonia que se modificará en la lista de ubicaciones "colonias".
        int index = this.mColonias.indexOf(colonia);

        // Para modificar el numero de delitos semanales de la colonia.
        colonia.setDelitos_semana(sumaCantidadDelitosSemanles);

        // Para actulizar la colonia dentro de la misma lista.
        this.mColonias.set(index, colonia);

        // Para remover todos los reportes que contengan la ubicacion que se acaba de buscar.
        reportesSemanales.removeIf(reporte -> reporte.getUbicacion().equals(coloniaString));
        Log.v("QUICKSTART", String.valueOf(reportesSemanales.size()));

        if (reportesSemanales.size() != 0)
            return actualizarDelitosSemanales(reportesSemanales);
        else {
            return null;
        }
    }

    private void actualizarDelitosSemanalesMunicipio() {
        Log.v("QUICKSTART", String.valueOf(this.mSumaCantidadDelitosMunicipio));
        String municipioString = "ZAPOPAN, JALISCO, MEXICO";
        ubicacion municipio = ubicacionDAO.obtenerMunicipio(municipioString);
        municipio.setDelitos_semana(municipio.getSuma_delitos() + mSumaCantidadDelitosSemanalesMunicipio);
        this.mColonias.add(municipio);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private long calcularnUmeroSemanas() {
        LocalDateTime fechaInicio = LocalDate.parse("01/01/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay();
        LocalDateTime fechaTermino = LocalDate.parse("01/11/2021", DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay();

        return ChronoUnit.WEEKS.between(fechaInicio, fechaTermino);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void calcularMediaHistorica(ubicacion ubicacion) {
        ubicacion.setMedia_historica_double(ubicacion.getSuma_delitos().doubleValue() / calcularnUmeroSemanas());
    }

    private void calcularMetaReduccion(ubicacion ubicacion) {
        ubicacion.setMeta_reduccion_double(ubicacion.getMedia_historica_double() * 0.75);
    }

    private void calcularNivelSeguridad(ubicacion ubicacion){
        long mediaHistorica = Math.round(ubicacion.getMedia_historica_double());
        long metaReduccion = Math.round(ubicacion.getMeta_reduccion_double());
        int delitosSemanales = ubicacion.getDelitos_semana();

        if(delitosSemanales > mediaHistorica)
            ubicacion.setSeguridad("Seguridad baja");
        else if (delitosSemanales < metaReduccion)
            ubicacion.setSeguridad("Seguridad alta");
        else if (delitosSemanales > metaReduccion && delitosSemanales < mediaHistorica)
            ubicacion.setSeguridad("Seguridad media");
    }
}
