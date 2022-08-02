package com.example.llegabien.backend.ubicacion;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.llegabien.backend.reporte.ReporteDAO;
import com.example.llegabien.backend.reporte.reporte;

import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UbicacionSeguridad {

    private final Context mContext;
    private UbicacionDAO mUbicacionDAO;
    private List<ubicacion> mUbicaciones;
    private List<reporte> mReportesVerificados;

    public UbicacionSeguridad(Context context) {
        mContext = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void actualizarUbicacionesCalculoSeguridadSemanal() {
        mUbicacionDAO = new UbicacionDAO(mContext);
        this.mUbicaciones = mUbicacionDAO.obtenerUbicaciones();

        // Se inicializan estos valores para poder calcular la seguridad y el delito más frecuente de TODAS las ubicaciones.
        this.mUbicaciones.forEach(ubicacion -> ubicacion.setDelitos_semana(0));
        this.mUbicaciones.forEach(ubicacion -> ubicacion.setDelito_mas_frecuente("GENERAL"));

        // Para obtener fechas inicio y final de semana.
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_YEAR, -1);

        Date fechaInicio = calendar.getTime();
        Date fechaTermino = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());

        // Para obtenr reportes de la última semana.
        ReporteDAO reporteDAO = new ReporteDAO(mContext);
        List<reporte> reportes = reporteDAO.obtenerReportesPorFecha(fechaInicio, fechaTermino);
        reportes.removeIf(reporte -> reporte.getIdUbicacion() == null);

        // Para obtener los reportes de admin
        List<reporte> reportesAdmin
                = reportes.stream()
                .filter(reporte -> reporte.getAutor().equals("Administrador Admin"))
                .collect(Collectors.toList());

        // Para obtener solo los reportes válidos.
        this.mReportesVerificados = new ArrayList<>();
        reportes.removeAll(reportesAdmin);
        verificarReportes(reportes);

        if(reportesAdmin.size()>0) {
            // Se calculan los delitos semanales solo con los reportes de admin.
            calculoInicial(reportesAdmin, true);
        }

        if(this.mReportesVerificados.size()>0) {
            // Se calculan los delitos semanales solo con los reportes de usuario verificados.
            calculoInicial(this.mReportesVerificados, false);
        }

        // Se hace el calculo de seguridad y delito más frecuente INICIAL.
        for (ubicacion ubicacion : this.mUbicaciones) {
            calcularMediaHistorica(ubicacion);
            calcularMetaReduccion(ubicacion);
            calcularNivelSeguridad(ubicacion);
            calcularDelitoMasFrecuente(ubicacion);
        }

        // Se actualizan las ubicaciones.
        mUbicacionDAO.updateUbicaciones(this.mUbicaciones);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<reporte> verificarReportes(List<reporte> reportesUsuario) {
        // Se obtiene de reporte en 0.
        Date fechaReporte = reportesUsuario.get(0).getFecha();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fechaReporte);

        calendar.add(Calendar.HOUR, -1);
        Date inicioRangoFecha = calendar.getTime();

        calendar.add(Calendar.HOUR, +2);
        Date finalRangoFecha = calendar.getTime();

        // Se obtienen reportes que tengan el mismo IdUbicacion y que su fecha este dentro del rango (-1 hora y +1 hora).
        List<reporte> reportesDentroDeRango
                = reportesUsuario.stream()
                .filter(reporte -> reporte.getIdUbicacion().equals(reportesUsuario.get(0).getIdUbicacion()))
                .filter(reporte -> reporte.getFecha().after(inicioRangoFecha) && reporte.getFecha().before(finalRangoFecha))
                .collect(Collectors.toList());

        // Si existe más de un reporte que cumpla con las condicones, estos se guardan en la lista de reportes verificados.
        if (reportesDentroDeRango.size() > 1)
            this.mReportesVerificados.addAll(reportesDentroDeRango);


        // Para remover todos los reportes que esten dentro del rango establecido.
        reportesUsuario.removeAll(reportesDentroDeRango);

        if (reportesUsuario.size() != 0)
            // Se repite la función hasta que ya se hayan evaluado todos los reportes.
            return verificarReportes(reportesUsuario);
        else
            return null;
    }

    // Para sumar a suma delitos, delitos semanales y delitos frecuentes.
    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<reporte> calculoInicial(List<reporte> reportesSemanales, boolean isReportesAdmin) {
        ubicacion colonia, municipio;

        // Se obtiene el Id de la ubicacion.
        ObjectId IdUbicacion = reportesSemanales.get(0).getIdUbicacion();


        // Se obtiene ubicacion con el ID.
        ubicacion ubicacion = mUbicacionDAO.obtenerUbicacionConIdDeLista(IdUbicacion, this.mUbicaciones);

        if (ubicacion != null) {
            // Se recuperan los reportes con la misma ubicación.
            List<reporte> reportesConMismoIdUbicadcion = reportesSemanales.stream()
                    .filter(reporte -> reporte.getIdUbicacion().equals(IdUbicacion))
                    .collect(Collectors.toList());


            // Se suman la cantidad de  delitos que tengan la misma ubicación.
            int sumaCantidadDelitosSemanales = reportesConMismoIdUbicadcion.stream().mapToInt(reporte::getCantidad).sum();

            // Se obtienen los tipos de delitos de los reportes con la misma ubicacion.
            String delitos = reportesConMismoIdUbicadcion.stream()
                    .map(reporte -> reporte.getTipoDelito().trim())
                    .collect(Collectors.joining(","));


            // Si la ubicación es de tipo calle, tambien se actualizan su colonia y su muncipio.
            if (ubicacion.getTipo().equals("calle")) {
                colonia = mUbicacionDAO.obtenerUbicacionConIdDeLista(ubicacion.getIdColonia(), this.mUbicaciones);
                municipio = mUbicacionDAO.obtenerUbicacionConIdDeLista(ubicacion.getIdMunicipio(), this.mUbicaciones);

                if (!isReportesAdmin) {
                    actualizarSumaDelitos(ubicacion, sumaCantidadDelitosSemanales);
                    actualizarSumaDelitos(colonia, sumaCantidadDelitosSemanales);
                    actualizarSumaDelitos(municipio, sumaCantidadDelitosSemanales);
                }

                actualizarDelitosSemana(ubicacion, sumaCantidadDelitosSemanales);
                actualizarDelitosSemana(colonia, sumaCantidadDelitosSemanales);
                actualizarDelitosSemana(municipio, sumaCantidadDelitosSemanales);

                actualizarDelitoMasFrecuente(ubicacion, delitos);
                actualizarDelitoMasFrecuente(colonia, delitos);
                actualizarDelitoMasFrecuente(municipio, delitos);

            }
            // Si la ubicación es de tipo colonia, tambien se actualiza su muncipio.
            else if (ubicacion.getTipo().equals("colonia")) {
                municipio = mUbicacionDAO.obtenerUbicacionConIdDeLista(ubicacion.getIdMunicipio(), this.mUbicaciones);

                if (!isReportesAdmin) {
                    actualizarSumaDelitos(ubicacion, sumaCantidadDelitosSemanales);
                    actualizarSumaDelitos(municipio, sumaCantidadDelitosSemanales);
                }

                actualizarDelitosSemana(ubicacion, sumaCantidadDelitosSemanales);
                actualizarDelitosSemana(municipio, sumaCantidadDelitosSemanales);

                actualizarDelitoMasFrecuente(ubicacion, delitos);
                actualizarDelitoMasFrecuente(municipio, delitos);
            }

            // Si la ubicación es de tipo municipio, solo se esta se actualiza.
            else {
                if (!isReportesAdmin)
                    actualizarSumaDelitos(ubicacion, sumaCantidadDelitosSemanales);

                actualizarDelitosSemana(ubicacion, sumaCantidadDelitosSemanales);
                actualizarDelitoMasFrecuente(ubicacion, delitos);
            }

        }

        // Para remover todos los reportes que contengan la ubicacion que se acaba de buscar.
        reportesSemanales.removeIf(reporte -> reporte.getIdUbicacion().equals(IdUbicacion));
        Log.v("QUICKSTART", String.valueOf(reportesSemanales.size()));

        if (reportesSemanales.size() != 0)
            // Se repite la función hasta que ya no hay reportes con ubicaciones diferentes.
            return calculoInicial(reportesSemanales, isReportesAdmin);
        else
            return null;
    }

    private void calcularDelitoMasFrecuente(ubicacion ubicacion) {
        if (!ubicacion.getDelito_mas_frecuente().equals("GENERAL")) {
            String[] delitos = ubicacion.getDelito_mas_frecuente().split(",");
            ubicacion.setDelito_mas_frecuente("");

            // Se crea un HashMap para guardar el tipo de delito y su frecuencia.
            HashMap<String, Integer> delitosHashMap = new HashMap<>();

            // Se itera sobre el arreglo de delitos.
            for (String delito : delitos) {
                // Si el delito ya existe en el HahMap se incrementa su contador con 1.
                if (delitosHashMap.containsKey(delito)) {
                    delitosHashMap.put(delito, delitosHashMap.get(delito) + 1);
                }
                // Si no existe, se agrega al HashMap.
                else {
                    delitosHashMap.put(delito, 1);
                }
            }

            //Para obtener la fecuencia mayor.
            int valorMaximo = Collections.max(delitosHashMap.values());
            for (Map.Entry<String, Integer> entry : delitosHashMap.entrySet()) {
                if (entry.getValue() == valorMaximo) {
                    // Se añaden los delitos que tengan esa frecuencia.
                    if (ubicacion.getDelito_mas_frecuente().equals(""))
                        ubicacion.setDelito_mas_frecuente(entry.getKey());
                    else
                        ubicacion.setDelito_mas_frecuente(ubicacion.getDelito_mas_frecuente() + "," + entry.getKey());
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private long calcularNumeroSemanas() {
        LocalDateTime fechaInicio = LocalDate.parse("01/01/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay();
        LocalDateTime fechaTermino = LocalDateTime.now();

        long semanas = ChronoUnit.WEEKS.between(fechaInicio, fechaTermino) - 39;
       // Log.v("QUICKSTART", "NUMERO DE SEMANAS TRANSCURRIDAS: " + semanas);

        return semanas;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void calcularMediaHistorica(ubicacion ubicacion) {
        ubicacion.setMedia_historica_double(ubicacion.getSuma_delitos().doubleValue() / calcularNumeroSemanas());
    }

    private void calcularMetaReduccion(ubicacion ubicacion) {
        ubicacion.setMeta_reduccion_double(ubicacion.getMedia_historica_double() * 0.75);
    }

    private void calcularNivelSeguridad(ubicacion ubicacion) {
        long mediaHistorica = Math.round(ubicacion.getMedia_historica_double());
        long metaReduccion = Math.round(ubicacion.getMeta_reduccion_double());
        int delitosSemanales = ubicacion.getDelitos_semana();

        if (delitosSemanales > mediaHistorica)
            ubicacion.setSeguridad("Seguridad baja");
        else if (delitosSemanales < metaReduccion)
            ubicacion.setSeguridad("Seguridad alta");
        else if (delitosSemanales >= metaReduccion && delitosSemanales <= mediaHistorica)
            ubicacion.setSeguridad("Seguridad media");
    }

    private void actualizarDelitosSemana(ubicacion ubicacion, int sumaCantidadDelitosSemanales) {
        // Para obtener el index de la ubicacion.
        int index = this.mUbicaciones.indexOf(ubicacion);

        // Para modificar el numero de delitos semanales de la ubicacion.
        ubicacion.setDelitos_semana(ubicacion.getDelitos_semana() + sumaCantidadDelitosSemanales);

        // Para actulizar la colonia dentro de la ubicacion.
        this.mUbicaciones.set(index, ubicacion);
    }


    private void actualizarSumaDelitos(ubicacion ubicacion, int sumaCantidadDelitos) {
        int index = this.mUbicaciones.indexOf(ubicacion);

        // Para modificar la suma de delitos de la ubicacion.
        ubicacion.setSuma_delitos(ubicacion.getSuma_delitos() + sumaCantidadDelitos);

        // Para actulizar la colonia dentro de la ubicacion.
        this.mUbicaciones.set(index, ubicacion);
    }

    private void actualizarDelitoMasFrecuente(ubicacion ubicacion, String delitos) {
        if (ubicacion.getDelito_mas_frecuente().equals("GENERAL"))
            ubicacion.setDelito_mas_frecuente("");

        // Para obtener el index de la ubicacion.
        int index = this.mUbicaciones.indexOf(ubicacion);

        // Para modificar el numero de delitos semanales de la ubicacion.
        if (ubicacion.getDelito_mas_frecuente().equals(""))
            ubicacion.setDelito_mas_frecuente(delitos);
        else
            ubicacion.setDelito_mas_frecuente(ubicacion.getDelito_mas_frecuente() + "," + delitos);

        // Para actulizar la colonia dentro de la ubicacion.
        this.mUbicaciones.set(index, ubicacion);
    }

    /*// FUNCIONES QUE FUERON UTLIZADAS PARA HACER EL CÁLCULO INICIAL //

    // Después de calcular la suma de delitos total, se obtienen los delitos de la ultima semana de la que se tienen registros.
    // Después se hace el cálculo de seguridad inicial.
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void actualizarUbicacionesCalculoSeguridad() {
        mUbicacionDAO = new UbicacionDAO(mContext);
        this.mUbicaciones = mUbicacionDAO.obtenerUbicaciones();

        // Se inicializan estos valores para poder calcular la seguridad y el delito más frecuente de TODAS las ubicaciones.
        this.mUbicaciones.forEach(ubicacion -> ubicacion.setDelitos_semana(0));
        this.mUbicaciones.forEach(ubicacion -> ubicacion.setDelito_mas_frecuente("GENERAL"));

        // La última semana de la que tenimos reportes del IIEG y Ayuntamientos.
        Date fechaInicio = java.util.Date.from(LocalDate.parse("25/10/2021", DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                .atTime(LocalTime.parse("00:00", DateTimeFormatter.ofPattern("HH:mm")))
                .atZone(ZoneId.systemDefault()).toInstant());
        Date fechaTermino = java.util.Date.from(LocalDate.parse("31/10/2021", DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                .atTime(LocalTime.parse("23:59", DateTimeFormatter.ofPattern("HH:mm")))
                .atZone(ZoneId.systemDefault()).toInstant());

        // Se calculan los delitos semanales.
        ReporteDAO reporteDAO = new ReporteDAO(mContext);
        calcularDelitosSemanales(reporteDAO.obtenerReportesPorFecha(fechaInicio, fechaTermino));

        // Se hace el calculo de seguridad y delito más frecuente INICIAL.
        for (ubicacion ubicacion : this.mUbicaciones) {
            calcularMediaHistorica(ubicacion);
            calcularMetaReduccion(ubicacion);
            calcularNivelSeguridad(ubicacion);
            calcularDelitoMasFrecuente(ubicacion);
        }

        // Se actualizan las ubicaciones.
        mUbicacionDAO.updateUbicaciones(this.mUbicaciones);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<reporte> calcularDelitosSemanales(List<reporte> reportesSemanales) {
        ubicacion colonia, municipio;

        // Se obtiene el Id de la ubicacion.
        ObjectId IdUbicacion = reportesSemanales.get(0).getIdUbicacion();


        // Se obtiene ubicacion con el ID.
        ubicacion ubicacion = mUbicacionDAO.obtenerUbicacionConIdDeLista(IdUbicacion, this.mUbicaciones);

        if (ubicacion != null) {
            // Se recuperan los reportes con la misma ubicación.
            List<reporte> reportesConMismoIdUbicadcion = reportesSemanales.stream()
                    .filter(reporte -> reporte.getIdUbicacion().equals(IdUbicacion))
                    .collect(Collectors.toList());


            // Se suman la cantidad de  delitos que tengan la misma ubicación.
            int sumaCantidadDelitosSemanales = reportesConMismoIdUbicadcion.stream().mapToInt(reporte::getCantidad).sum();

            // Se obtienen los tipos de delitos de los reportes con la misma ubicacion.
            String delitos = reportesConMismoIdUbicadcion.stream()
                    .map(reporte -> reporte.getTipoDelito().trim())
                    .collect(Collectors.joining(","));


            // Si la ubicación es de tipo calle, tambien se actualizan su colonia y su muncipio.
            if (ubicacion.getTipo().equals("calle")) {
                colonia = mUbicacionDAO.obtenerUbicacionConIdDeLista(ubicacion.getIdColonia(), this.mUbicaciones);
                municipio = mUbicacionDAO.obtenerUbicacionConIdDeLista(ubicacion.getIdMunicipio(), this.mUbicaciones);

                actualizarDelitosSemana(ubicacion, sumaCantidadDelitosSemanales);
                actualizarDelitosSemana(colonia, sumaCantidadDelitosSemanales);
                actualizarDelitosSemana(municipio, sumaCantidadDelitosSemanales);

                actualizarDelitoMasFrecuente(ubicacion, delitos);
                actualizarDelitoMasFrecuente(colonia, delitos);
                actualizarDelitoMasFrecuente(municipio, delitos);

            }
            // Si la ubicación es de tipo colonia, tambien se actualiza su muncipio.
            else if (ubicacion.getTipo().equals("colonia")) {
                municipio = mUbicacionDAO.obtenerUbicacionConIdDeLista(ubicacion.getIdMunicipio(), this.mUbicaciones);
                actualizarDelitosSemana(ubicacion, sumaCantidadDelitosSemanales);
                actualizarDelitosSemana(municipio, sumaCantidadDelitosSemanales);

                actualizarDelitoMasFrecuente(ubicacion, delitos);
                actualizarDelitoMasFrecuente(municipio, delitos);
            }

            // Si la ubicación es de tipo municipio, solo se esta se actualiza.
            else {
                actualizarDelitosSemana(ubicacion, sumaCantidadDelitosSemanales);
                actualizarDelitoMasFrecuente(ubicacion, delitos);
            }

        }

        // Para remover todos los reportes que contengan la ubicacion que se acaba de buscar.
        reportesSemanales.removeIf(reporte -> reporte.getIdUbicacion().equals(IdUbicacion));
        Log.v("QUICKSTART", String.valueOf(reportesSemanales.size()));

        if (reportesSemanales.size() != 0)
            // Se repite la función hasta que ya no hay ubicaciones diferentes.
            return calcularDelitosSemanales(reportesSemanales);
        else {
            return null;
        }
    }

    private void calcularDelitoMasFrecuente(ubicacion ubicacion) {
        if (!ubicacion.getDelito_mas_frecuente().equals("GENERAL")) {
            String[] delitos = ubicacion.getDelito_mas_frecuente().split(",");
            ubicacion.setDelito_mas_frecuente("");

            // Se crea un HashMap para guardar el tipo de delito y su frecuencia.
            HashMap<String, Integer> delitosHashMap = new HashMap<>();

            // Se itera sobre el arreglo de delitos.
            for (String delito : delitos) {
                // Si el delito ya existe en el HahMap se incrementa su contador con 1.
                if (delitosHashMap.containsKey(delito)) {
                    delitosHashMap.put(delito, delitosHashMap.get(delito) + 1);
                }
                // Si no existe, se agrega al HashMap.
                else {
                    delitosHashMap.put(delito, 1);
                }
            }

            //Para obtener la fecuencia mayor.
            int valorMaximo = Collections.max(delitosHashMap.values());
            for (Map.Entry<String, Integer> entry : delitosHashMap.entrySet()) {
                if (entry.getValue() == valorMaximo) {
                    // Se añaden los delitos que tengan esa frecuencia.
                    if (ubicacion.getDelito_mas_frecuente().equals(""))
                        ubicacion.setDelito_mas_frecuente(entry.getKey());
                    else
                        ubicacion.setDelito_mas_frecuente(ubicacion.getDelito_mas_frecuente() + "," + entry.getKey());
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private long calcularNumeroSemanas(ubicacion ubicacion) {
        LocalDateTime fechaInicio = null, fechaTermino = null;
        if (ubicacion.getTipo().equals("calle")) {
            ubicacion municipio = mUbicacionDAO.obtenerUbicacionConIdDeLista(ubicacion.getIdMunicipio(), this.mUbicaciones);
            if (municipio != null) {
                if (municipio.getNombre().equals("TLAJOMULCO DE ZUÑIGA, JALISCO, MEXICO")) {
                    fechaInicio = LocalDate.parse("01/01/2021", DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay();
                } else {
                    fechaInicio = LocalDate.parse("01/03/2021", DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay();
                }
                fechaTermino = LocalDate.parse("01/11/2021", DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay();
            }
        } else {
            fechaInicio = LocalDate.parse("01/01/2016", DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay();
            fechaTermino = LocalDate.parse("01/11/2021", DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay();
        }

        long semanas = ChronoUnit.WEEKS.between(fechaInicio, fechaTermino);
        Log.v("QUICKSTART", "NUMERO DE SEMANAS TRANSCURRIDAS: " + semanas);

        return semanas;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void calcularMediaHistorica(ubicacion ubicacion) {
        ubicacion.setMedia_historica_double(ubicacion.getSuma_delitos().doubleValue() / calcularNumeroSemanas(ubicacion));
    }

    private void calcularMetaReduccion(ubicacion ubicacion) {
        ubicacion.setMeta_reduccion_double(ubicacion.getMedia_historica_double() * 0.75);
    }

    private void calcularNivelSeguridad(ubicacion ubicacion) {
        long mediaHistorica = Math.round(ubicacion.getMedia_historica_double());
        long metaReduccion = Math.round(ubicacion.getMeta_reduccion_double());
        int delitosSemanales = ubicacion.getDelitos_semana();

        if (delitosSemanales > mediaHistorica)
            ubicacion.setSeguridad("Seguridad baja");
        else if (delitosSemanales < metaReduccion)
            ubicacion.setSeguridad("Seguridad alta");
        else if (delitosSemanales >= metaReduccion && delitosSemanales <= mediaHistorica)
            ubicacion.setSeguridad("Seguridad media");
    }

    private void actualizarDelitosSemana(ubicacion ubicacion, int sumaCantidadDelitosSemanales) {
        // Para obtener el index de la ubicacion.
        int index = this.mUbicaciones.indexOf(ubicacion);

        // Para modificar el numero de delitos semanales de la ubicacion.
        ubicacion.setDelitos_semana(ubicacion.getDelitos_semana() + sumaCantidadDelitosSemanales);

        // Para actulizar la colonia dentro de la ubicacion.
        this.mUbicaciones.set(index, ubicacion);
    }

    private void actualizarDelitoMasFrecuente(ubicacion ubicacion, String delitos) {
        if (ubicacion.getDelito_mas_frecuente().equals("GENERAL"))
            ubicacion.setDelito_mas_frecuente("");

        // Para obtener el index de la ubicacion.
        int index = this.mUbicaciones.indexOf(ubicacion);

        // Para modificar el numero de delitos semanales de la ubicacion.
        if (ubicacion.getDelito_mas_frecuente().equals(""))
            ubicacion.setDelito_mas_frecuente(delitos);
        else
            ubicacion.setDelito_mas_frecuente(ubicacion.getDelito_mas_frecuente() + "," + delitos);

        // Para actulizar la colonia dentro de la ubicacion.
        this.mUbicaciones.set(index, ubicacion);
    }*/


}
