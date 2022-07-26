package com.example.llegabien.backend.ruta;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.llegabien.backend.ruta.directions.Ruta;
import com.example.llegabien.backend.ruta.directions.RutaDirections;
import com.example.llegabien.backend.ruta.directions.UbicacionRuta;
import com.example.llegabien.backend.ruta.directions.UbicacionRutaDAO;
import com.example.llegabien.backend.ubicacion.UbicacionDAO;
import com.example.llegabien.backend.ubicacion.UbicacionGeocodificacion;
import com.example.llegabien.backend.ubicacion.ubicacion;
import com.example.llegabien.frontend.rutas.directionhelpers.TaskLoadedCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class EvaluacionRuta extends AsyncTask<String, Void, Ruta> {

    @SuppressLint("StaticFieldLeak")
    private final Context mContext;
    private UbicacionDAO ubicacionDAO;
    private List<Ruta> rutas;
    private List<String> tipoUbicacion;
    private boolean mIsCalleEnRutas = false;
    private RutaDirections directionsObtenidas;
    private final TaskLoadedCallback taskCallback;
    private final TaskLoadedCallback taskLoaderFragmento;
    private Ruta mRutaMasSegura;

    public EvaluacionRuta(Context mContext, TaskLoadedCallback TaskActivity, TaskLoadedCallback TaskFragmento) {
        this.mContext = mContext;
        this.taskCallback = TaskActivity;
        this.taskLoaderFragmento = TaskFragmento;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void inicializarAtributosMiembro(RutaDirections DirectionsObtenidas) {
        this.ubicacionDAO = new UbicacionDAO(mContext);
        this.tipoUbicacion = new ArrayList<>();
        this.rutas = new ArrayList<>();
        directionsObtenidas = DirectionsObtenidas;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected Ruta doInBackground(String... strings) {
        List<PolylineOptions> rutasObtenidas = directionsObtenidas.getRutasDirectionsPolylineOptions();
        if (rutasObtenidas != null) {
            obtenerPuntosRutas(rutasObtenidas);
            verificarExistenciaColonias();
            setNumeroDeRuta();
            if (!this.rutas.isEmpty()) {
                if (this.rutas.size() == 1)
                    this.mRutaMasSegura = this.rutas.get(0);
                else {
                    if (!this.mIsCalleEnRutas)
                        // Si solo se tienen valores de colonias en las rutas.
                        calcularMediaHistoricaSoloColonias(this.rutas.size(), false);
                    else {
                        calcularMediaHistorica(this.rutas.size(), this.rutas.get(0).getNumeroCalles(), false);
                        compararMediaHistorica();
                    }
                    comparacionFinalMediaHistorica();
                    calcularPorcentajes();
                    compararPocentajes();
                    comparacionFinalRutas();
                    setDelitosZonasInseguras();
                }
            } else
                Log.v("QUICKSTART", "¡No hay rutas disponibles porque no se encontró alguna colonia!");
        }else
            Log.v("QUICKSTART", "¡No hay rutas disponibles por Google Maps!");

        return this.mRutaMasSegura;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void obtenerPuntosRutas(List<PolylineOptions> rutasObtenidas) {
        // TODO: Poner lo que se va a hacer si Google solo nos da una ruta.
        UbicacionGeocodificacion ubicacionGeodicacion = new UbicacionGeocodificacion(mContext);
        UbicacionRutaDAO ubicacionRutaDAO = new UbicacionRutaDAO();

        List<PolylineOptions> rutaDirectionsPolyline = new ArrayList<>();
        List<List<PolylineOptions>> rutasDirectionsPolyline = new ArrayList<>();
        int longitudCalle;

        // Para obtener puntos medios y sus distancias.
        for (int i = 0; i < obtenerNumeroRutas(rutasObtenidas.size()); i++) {
            int distanciaTotalRuta = 0;
            List<LatLng> points = rutasObtenidas.get(i).getPoints();
            Ruta ruta = new Ruta();
            ruta.setCallesRuta(new ArrayList<>());

            // Recorre los puntos de una ruta.
            for (int o = 0; o < points.size(); o++) {
                PolylineOptions lineOptions = new PolylineOptions();
                lineOptions.add(points.get(o));
                if (o + 1 < points.size()) {
                    lineOptions.add(points.get(o + 1));
                    LatLng centro = LatLngBounds.builder().include(points.get(o)).include(points.get(o + 1)).build().getCenter();
                    longitudCalle = (int) SphericalUtil.computeDistanceBetween(points.get(o), points.get(o + 1));
                    distanciaTotalRuta += longitudCalle;

                    Address adressPuntoMedio = ubicacionGeodicacion.geocodificarUbiciacion(centro.latitude, centro.longitude);
                    String calle = (adressPuntoMedio.getThoroughfare()
                            + ", " + adressPuntoMedio.getSubLocality()
                            + ", " + adressPuntoMedio.getLocality()
                            + ", " + adressPuntoMedio.getAdminArea()
                            + ", " + adressPuntoMedio.getCountryName()).toUpperCase(Locale.ROOT);

                    // Para ver si la ubicacionRuta ya está en la lista "callesRuta" del objeto "Ruta".
                    UbicacionRuta ubicacionRuta = ubicacionRutaDAO.obtenerUbicacionRutaConAdress(calle, ruta.getCallesRuta());
                    if (ubicacionRuta == null) {
                        ubicacionRuta = new UbicacionRuta(longitudCalle, adressPuntoMedio, calle);
                        ubicacionRuta.setLatPuntoInicio((float) points.get(o).latitude);
                        ubicacionRuta.setLngPuntoInicio((float) points.get(o).longitude);
                        ruta.getCallesRuta().add(ubicacionRuta);
                    } else {
                        int index = ruta.getCallesRuta().indexOf(ubicacionRuta);
                        ubicacionRuta.setmDistancia(longitudCalle + ubicacionRuta.getmDistancia());
                        ruta.getCallesRuta().set(index, ubicacionRuta);
                    }
//                    mGoogleMap.addPolyline(lineOptions);
                    Log.v("QUICKSTART", "Calle: " + calle + " Distancia " + ubicacionRuta.getmDistancia());
                }
                rutaDirectionsPolyline = ruta.getPolyline();
                rutaDirectionsPolyline.add(lineOptions);
                ruta.setPolyline(rutaDirectionsPolyline);
                ruta.setNumeroCalles(ruta.getCallesRuta().size());
            }
            rutasDirectionsPolyline.add(rutaDirectionsPolyline);

            ruta.setDistanciaTotalDirections(directionsObtenidas.getDistancia().get(i));
            ruta.setTiempoTotal(directionsObtenidas.getDuracion().get(i));
            ruta.setDistanciaTotal(distanciaTotalRuta);
            this.rutas.add(ruta);

            Log.v("QUICKSTART", "DISTANCIA, TIEMPO: " + directionsObtenidas.getDistancia().get(i) + " , " + directionsObtenidas.getDuracion().get(i));
        }
        directionsObtenidas.setRutasEnPolylines(rutasDirectionsPolyline);

        this.rutas.sort(Comparator.comparing(Ruta::getNumeroCalles).reversed());

    }

    private int obtenerNumeroRutas(int size) {
        return Math.min(size, 3);
    }


    // Para verificar que existan las colonias de cada ruta.
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void verificarExistenciaColonias() {
        UbicacionDAO mUbicacionDAO = new UbicacionDAO(mContext);
        List<ubicacion> colonias = mUbicacionDAO.obtenerColonias();
        HashMap<String, ubicacion> coloniasEncontradas = new HashMap<>();
        ubicacion colonia, calle;
        String coloniaNombre;

        for (int y = 0; y < this.rutas.size(); y++) {
            Log.v("QUICKSTART", "Ruta número: " + y + ". Ruta tamaño: " + this.rutas.get(y).getNumeroCalles());
            for (int i = 0; i < this.rutas.get(y).getNumeroCalles(); i++) {
                coloniaNombre = this.rutas.get(y).getCallesRuta().get(i).getmNombreCalle().split(",", 2)[1].trim();
                if (!coloniasEncontradas.containsKey(coloniaNombre)) {
                    colonia = mUbicacionDAO.obtenerColonia(coloniaNombre, colonias, new LatLng(this.rutas.get(y).getCallesRuta().get(i).getmAddressPuntoMedio().getLatitude(), rutas.get(y).getCallesRuta().get(i).getmAddressPuntoMedio().getLongitude()));
                    if (colonia == null) {
                        Log.v("QUICKSTART", "No se encontró colonia: " + coloniaNombre);
                        rutas.remove(y);
                        y--;
                        break;
                    } else {
                        Log.v("QUICKSTART", "Se encontró colonia.");
                        // Se guarda la colonia para después poder obtener su MH.
                        this.rutas.get(y).getCallesRuta().get(i).setmUbicacionColonia(colonia);
                        coloniasEncontradas.put(coloniaNombre, colonia);

                        // Para obtener la calle de la BD, una vez que se "fomatea" el nombre de la calle a como se tiene en la BD.
                        calle = obtenerUbicacionCalle(UbicacionGeocodificacion.establecerNombreUbicacion(rutas.get(y).getCallesRuta().get(i).getmAddressPuntoMedio(), colonia));

                        // Se guarda la calle para después poder obtener su MH.
                        this.rutas.get(y).getCallesRuta().get(i).setmUbicacionCalle(calle);

                        // Para establecer si se tomara los datos de la colonia o calle.
                        establecerListaTipoUbicacion(calle, i);
                    }
                } else {
                    this.rutas.get(y).getCallesRuta().get(i).setmUbicacionColonia(coloniasEncontradas.get(coloniaNombre));
                    calle = obtenerUbicacionCalle(UbicacionGeocodificacion.establecerNombreUbicacion(rutas.get(y).getCallesRuta().get(i).getmAddressPuntoMedio(), coloniasEncontradas.get(coloniaNombre)));
                    this.rutas.get(y).getCallesRuta().get(i).setmUbicacionCalle(calle);
                    establecerListaTipoUbicacion(calle, i);
                }
            }
        }
    }

    private ubicacion obtenerUbicacionCalle(String calleNombre) {
        if (calleNombre != null) {
            return ubicacionDAO.obtenerUbicacionConNombre(calleNombre);
        }
        return null;
    }

    private void establecerListaTipoUbicacion(ubicacion ubicacionCalle, int i) {
        if (ubicacionCalle != null) {
            this.mIsCalleEnRutas = true;
            if (i >= this.tipoUbicacion.size())
                this.tipoUbicacion.add("calle");
            else {
                if (!this.tipoUbicacion.get(i).equals("colonia"))
                    this.tipoUbicacion.set(i, "calle");
            }

        } else {
            if (i >= this.tipoUbicacion.size())
                this.tipoUbicacion.add("colonia");
            else
                this.tipoUbicacion.set(i, "colonia");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setNumeroDeRuta() {
        this.rutas.sort(Comparator.comparing(Ruta::getNumeroCalles));
        for (int y = 0; y < this.rutas.size(); y++)
            this.rutas.get(y).setNumeroDeRuta(y + 1);
    }

    // Esta función se hizo para cuando se necesita calcular la MH hasta el numero de calles de la ruta más corta.
    // La MH se calcula con las valores de la calle o colonia, dependiendo del valor que se encuentre en la lista "TipoUbicacion".
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void calcularMediaHistorica(int numeroRutas, int numeroCalles, boolean continuarCalculoMH) {
        int inicioCiclo = 0;

        if (continuarCalculoMH)
            inicioCiclo = this.rutas.stream().filter(r -> r.getNumeroDeRuta() == 1)
                    .findAny().orElse(null).getNumeroCalles();

        for (int y = 0; y < numeroRutas; y++) {
            if (!continuarCalculoMH)
                this.rutas.get(y).setMediaHistorica(0);

            for (int i = inicioCiclo; i < numeroCalles; i++) {
                if (this.tipoUbicacion.get(i).equals("calle"))
                    this.rutas.get(y).setMediaHistorica(this.rutas.get(y).getMediaHistorica()
                            + this.rutas.get(y).getCallesRuta().get(i).getmUbicacionCalle().getMedia_historica_double());

                else if (this.tipoUbicacion.get(i).equals("colonia"))
                    this.rutas.get(y).setMediaHistorica(this.rutas.get(y).getMediaHistorica()
                            + this.rutas.get(y).getCallesRuta().get(i).getmUbicacionColonia().getMedia_historica_double());
            }
            Log.v("QUICKSTART", "Media historica: " + this.rutas.get(y).getMediaHistorica());
        }
        this.rutas.sort(Comparator.comparing(Ruta::getMediaHistorica));
    }

    // Esta funcion se hizo para calcular la MH con colonias.
    // No marcará la excepcion "IndexOutOfBounds" porque el valor de "i" se toma acorde al numero de calles de cada ruta.
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void calcularMediaHistoricaSoloColonias(int numeroRutas, boolean hasToSortByNumeroCalles) {
        Collections.replaceAll(this.tipoUbicacion, "calle", "colonia");

        if (hasToSortByNumeroCalles)
            // Se sortea por numero de calles (menor a mayor).
            this.rutas.sort(Comparator.comparing(Ruta::getNumeroCalles));

        for (int y = 0; y < numeroRutas; y++) {
            for (int i = 0; i < this.rutas.get(y).getNumeroCalles(); i++) {
                // Se toma el valor de MH de la colonia de cada calle de la ruta.
                this.rutas.get(y).setMediaHistorica(this.rutas.get(y).getMediaHistorica()
                        + this.rutas.get(y).getCallesRuta().get(i).getmUbicacionColonia().getMedia_historica_double());
            }
            Log.v("QUICKSTART", "Media historica: " + this.rutas.get(y).getMediaHistorica());
        }
        this.rutas.sort(Comparator.comparing(Ruta::getMediaHistorica));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void compararMediaHistorica() {
        // Para MH de Ruta 1 > MH de Ruta 2  && MH de Ruta 1 > MH de Ruta 3.
        // Para MH de Ruta 1 == MH de Ruta 2 == MH de Ruta 3.
        // Para MH de Ruta 1 == MH de Ruta 2.
        // Para MH de Ruta 1 == MH de Ruta 3.
        // Para verificar que no sea una lista de dos rutas.
        if (this.rutas.size() == 2) {
            if (this.rutas.get(this.rutas.size() - 1).getNumeroDeRuta() == 1
                    || this.rutas.stream().allMatch(r -> r.getMediaHistorica() == this.rutas.get(0).getMediaHistorica()))
                calcularMediaHistoricaSoloColonias(2, false);
        } else {
            if (this.rutas.get(this.rutas.size() - 1).getNumeroDeRuta() == 1
                    || this.rutas.stream().allMatch(r -> r.getMediaHistorica() == this.rutas.get(0).getMediaHistorica())
                    || this.rutas.stream().filter(r -> r.getNumeroDeRuta() == 1).findAny().orElse(null).getMediaHistorica()
                    == this.rutas.stream().filter(r -> r.getNumeroDeRuta() == 2).findAny().orElse(null).getMediaHistorica()
                    || this.rutas.stream().filter(r -> r.getNumeroDeRuta() == 1).findAny().orElse(null).getMediaHistorica()
                    == this.rutas.stream().filter(r -> r.getNumeroDeRuta() == 3).findAny().orElse(null).getMediaHistorica()) {

                // Se tendrá que calcular cuál ruta entre la 2 y la 3 tiene menor MH.
                // Se sigue sumando la MH de la Ruta 2 y Ruta 3 hasta el número de calles de la Ruta 2.
                calcularMediaHistorica(2, this.rutas.stream().filter(r -> r.getNumeroDeRuta() == 2)
                        .findAny().orElse(null).getNumeroCalles(), true);

                // Se excluye la Ruta 1 para poder hacer comparaciones y obtener la ruta con menor MH.
                List<Ruta> rutasSinRuta1 = rutas.stream()
                        .filter(r -> r.getNumeroDeRuta() > 1).sorted(Comparator.comparing(Ruta::getMediaHistorica)).collect(Collectors.toList());

                // Para MH de Ruta 2  > MH de Ruta 3.
                // Para MH de Ruta 2  == MH de Ruta 3.
                if (rutasSinRuta1.get(0).getNumeroDeRuta() == 3 || (rutasSinRuta1.get(0).getMediaHistorica() == rutasSinRuta1.get(1).getMediaHistorica()))
                    calcularMediaHistoricaSoloColonias(this.rutas.size(), false);

                    // Para MH de Ruta 2 < MH de Ruta 3.
                else
                    // En este caso, SÍ se necesita sortear por num. calles (menor a mayor) ->
                    // <- para que se comparen SOLO las colonias de la Ruta 1 y Ruta 2.
                    calcularMediaHistoricaSoloColonias(2, true);

            }

            // Para MH de Ruta 1 > MH de Ruta 2  && MH de Ruta 1 < MH de Ruta 3.
            // Para MH de Ruta 1 <  MH de Ruta 2 && MH de Ruta 1 > MH de Ruta 3.
            else if (this.rutas.get(1).getNumeroDeRuta() == 1) {
                calcularMediaHistoricaSoloColonias(2, false);
            }
        }

        // Para verificar si al final MH de Ruta 1 == MH de Ruta 2 == MH de Ruta 3.
        comparacionFinalMediaHistorica();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void comparacionFinalMediaHistorica() {
        // Para verificar si al final de todas las comparaciones la MH es igual en todas las rutas.
        if (this.rutas.stream().allMatch(r -> r.getMediaHistorica() == this.rutas.get(0).getMediaHistorica()))
            // Si es igual, en todas se establce que la condicion "HasMenorMediaHistorica" es false.
            // Se tendrá que evaluar las rutas con las siguientes condiciones más relevantes (Clase Ruta).
            this.rutas.forEach(ruta -> ruta.setHasMenorMediaHistorica(false));
        else
            // Si no es igual en todas las rutas, quiere decir que la ruta con menor MH cumple con la condicion.
            this.rutas.get(0).setHasMenorMediaHistorica(true);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void calcularPorcentajes() {
        for (Ruta ruta : this.rutas) {
            int numZonasSegurasRuta = 0, distanciaZonasSegurasRuta = 0;

            for (int i = 0; i < ruta.getNumeroCalles(); i++) {
                // Se verifica que la seguridad de la calle o colonia de la actual calle de la ruta sea ALTA.
                // Si es ALTA, el contador de numero de zonas seguras y la distancia de zonas seguras aumentará.
                if (this.tipoUbicacion.get(i).equals("calle")) {
                    if (ruta.getCallesRuta().get(i).getmUbicacionCalle().getSeguridad().equals("Seguridad alta")) {
                        numZonasSegurasRuta++;
                        distanciaZonasSegurasRuta += ruta.getCallesRuta().get(i).getmDistancia();
                    }
                    ruta.getCallesRuta().get(i).setmUbicacion(ruta.getCallesRuta().get(i).getmUbicacionCalle());
                    ruta.getCallesRuta().get(i).setmUbicacionColonia(null);
                    ruta.getCallesRuta().get(i).setmUbicacionCalle(null);
                } else if (this.tipoUbicacion.get(i).equals("colonia")) {
                    if (ruta.getCallesRuta().get(i).getmUbicacionColonia().getSeguridad().equals("Seguridad alta")) {
                        numZonasSegurasRuta++;
                        distanciaZonasSegurasRuta += ruta.getCallesRuta().get(i).getmDistancia();
                    }
                    ruta.getCallesRuta().get(i).setmUbicacion(ruta.getCallesRuta().get(i).getmUbicacionColonia());
                    ruta.getCallesRuta().get(i).setmUbicacionColonia(null);
                }
            }

            // Después de iterar por calles de la ruta se calcula el % de zonas seguras y distancia de zonas seguras con una regla de tres.
            ruta.setPorcentajeZonasSeguras((numZonasSegurasRuta * 100.0) / ruta.getNumeroCalles());
            ruta.setPorcentajeDistanciaSegura((distanciaZonasSegurasRuta * 100.0) / ruta.getDistanciaTotal());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void compararPocentajes() {
        // Se sortea la lista de rutas por el porcentaje de distancia segura (de mayor a menor).
        // La ruta que tenga mayor porcentaje cumple con la condición de "HasMayorPorcentajeDistanciaSegura".
        this.rutas.sort(Comparator.comparing(Ruta::getPorcentajeDistanciaSegura).reversed());
        this.rutas.get(0).setHasMayorPorcentajeDistanciaSegura(true);

        // Si todas las rutas tienen el mismo porcentaje, quiere decir que ninguna cumple con esta condición.
        // Las rutas se evaluan con la siguiente condición mas relevante.
        if (this.rutas.stream().allMatch(r -> r.getPorcentajeDistanciaSegura() == this.rutas.get(0).getPorcentajeDistanciaSegura()))
            this.rutas.forEach(ruta -> ruta.setHasMayorPorcentajeDistanciaSegura(false));

        // El mismo proceso aplica para el atributo "PorcentajeZonasSeguras".
        this.rutas.sort(Comparator.comparing(Ruta::getPorcentajeZonasSeguras).reversed());
        this.rutas.get(0).setHasMayorPorcentajeZonasSeguras(true);

        if (this.rutas.stream().allMatch(r -> r.getPorcentajeZonasSeguras() == this.rutas.get(0).getPorcentajeZonasSeguras()))
            this.rutas.forEach(ruta -> ruta.setHasMayorPorcentajeZonasSeguras(false));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void comparacionFinalRutas() {
        // CONDICIONES: ordenas por relevancia.
        // 1 -> Ruta que tenga la menor Media Histórica.
        // 2 -> Ruta que tenga el mayor porcentaje de Distancia Segura.
        // 3 -> Ruta que tenga el mayor porcentaje de Zonas Seguras.

        Log.v("QUICKSTART", "////////////////////////////////////////////////////////////////////////////////////////////////");
        for (Ruta ruta : this.rutas) {
            Log.v("QUICKSTART", "Ruta num.: " + ruta.getNumeroDeRuta());
            Log.v("QUICKSTART", "MH: " + ruta.getMediaHistorica());
            Log.v("QUICKSTART", "ZS: " + ruta.getPorcentajeZonasSeguras());
            Log.v("QUICKSTART", "DS: " + ruta.getPorcentajeDistanciaSegura());
            Log.v("QUICKSTART", "////////////////////////////////////////////////////////////////////////////////////////////////");
        }

        // Si las tres rutas tienen la misma MH.
        if (this.rutas.stream().noneMatch(Ruta::getHasMenorMediaHistorica)) {
            // Si las tres rutas tienen la misma distancia segura.
            if (this.rutas.stream().noneMatch(Ruta::getHasMayorPorcentajeDistanciaSegura)) {
                // Si las tres rutas tienen la misma distancia segura.
                if (this.rutas.stream().noneMatch(Ruta::getHasMayorPorcentajeZonasSeguras))
                    // Si se entra a este IF, quiere que las rutas tienen el mismo valor en las tres condiciones.
                    // Se escoge a la ruta más cortas (menor número de calles).
                    this.rutas.sort(Comparator.comparing(Ruta::getNumeroCalles));
                else
                    // Se sortea segun el % de Zonas Seguras (mayor a menor).
                    this.rutas.sort(Comparator.comparing(Ruta::getPorcentajeZonasSeguras).reversed());
            } else
                // Se sortea segun el % de Distancia Segura (mayor a menor).
                this.rutas.sort(Comparator.comparing(Ruta::getPorcentajeDistanciaSegura).reversed());
        } else
            // Se sortea segun la MH (menor a mayor).
            this.rutas.sort(Comparator.comparing(Ruta::getMediaHistorica));

        // La ruta más segura es la que queda en el primer lugar de la lista sorteada.
        this.mRutaMasSegura = this.rutas.get(0);
        Log.v("QUICKSTART", "Ruta más segura: " + this.mRutaMasSegura.getNumeroDeRuta());
    }

    private void setDelitosZonasInseguras() {
        for (UbicacionRuta ubicacionRuta : this.mRutaMasSegura.getCallesRuta()) {
            if (ubicacionRuta.getmUbicacion().getSeguridad().equals("Seguridad baja")) {
                String[] delitosFrecuentes = ubicacionRuta.getmUbicacion().getDelito_mas_frecuente().split(",");
                this.mRutaMasSegura.setDelitosZonasInseguras(new ArrayList<>());
                for (String delitoFrecuente : delitosFrecuentes) {
                    if (!this.mRutaMasSegura.getDelitosZonasInseguras().contains(delitoFrecuente))
                        this.mRutaMasSegura.getDelitosZonasInseguras().add(delitoFrecuente);
                }
            }
        }
    }

    // Se ejecuta una vez el doInBackground termina.
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onPostExecute(Ruta ruta) {
        taskCallback.onTaskDone(ruta, taskLoaderFragmento);
    }
}
