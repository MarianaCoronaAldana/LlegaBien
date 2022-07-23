package com.example.llegabien.backend.ruta;

import static io.realm.Realm.getApplicationContext;

import android.content.Context;
import android.location.Address;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.llegabien.backend.ubicacion.UbicacionDAO;
import com.example.llegabien.backend.ubicacion.UbicacionGeocodificacion;
import com.example.llegabien.backend.ubicacion.ubicacion;
import com.example.llegabien.backend.ruta.directions.Ruta;
import com.example.llegabien.backend.ruta.directions.RutaDirections;
import com.example.llegabien.backend.ruta.directions.UbicacionRuta;
import com.example.llegabien.backend.ruta.directions.UbicacionRutaDAO;
import com.google.android.gms.maps.GoogleMap;
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

public class EvaluacionRuta {

    private final Context mContext;
    private UbicacionDAO ubicacionDAO;
    private final GoogleMap mGoogleMap;
    private List<Ruta> rutas;
    private List<String> tipoUbicacion;
    private boolean mIsCalleEnRutas = false;

    public EvaluacionRuta(GoogleMap mGoogleMap, Context mContext) {
        this.mGoogleMap = mGoogleMap;
        this.mContext = mContext;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void obtenerRuta(RutaDirections directionsObtenidas) {
        this.ubicacionDAO = new UbicacionDAO(mContext);
        this.tipoUbicacion = new ArrayList<>();
        this.rutas = new ArrayList<>();

        obtenerPuntosRutas(directionsObtenidas);
        verificarExistenciaColonias();
        if (!this.rutas.isEmpty()) {
            if (!this.mIsCalleEnRutas)
                calcularMediaHistoricaSoloColonias(this.rutas.size(), false);
            else {
                calcularMediaHistorica(this.rutas.size(), this.rutas.get(this.rutas.size() - 1).getmNumeroCalles(), false);
                compararMediaHistorica();
            }
            comparacionFinalMediaHistorica();
        } else {
            Toast.makeText(getApplicationContext(), "¡No hay rutas disponibles!", Toast.LENGTH_LONG).show();
            Log.v("QUICKSTART", "¡No hay rutas disponibles!");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void obtenerPuntosRutas(RutaDirections directionsObtenidas) {
        UbicacionGeocodificacion ubicacionGeodicacion = new UbicacionGeocodificacion(mContext);
        UbicacionRutaDAO ubicacionRutaDAO = new UbicacionRutaDAO();
        List<PolylineOptions> rutasObtenidas = directionsObtenidas.getRutasDirectionsPolylineOptions();

        List<PolylineOptions> rutaDirectionsPolyline = new ArrayList<>();
        List<List<PolylineOptions>> rutasDirectionsPolyline = new ArrayList<>();
        int distance;

        if (rutasObtenidas.isEmpty()) {
            Toast.makeText(getApplicationContext(), "¡No hay rutas disponibles!", Toast.LENGTH_LONG).show();
            return;
        }

        // Para obtener puntos medios y sus distancias.
        for (int i = 0; i < obtenerNumeroRutas(rutasObtenidas.size()); i++) {
            List<LatLng> points = rutasObtenidas.get(i).getPoints();
            Ruta ruta = new Ruta();
            ruta.setmCallesRuta(new ArrayList<>());
            // Recorre los puntos de una ruta.
            for (int o = 0; o < points.size(); o++) {
                PolylineOptions lineOptions = new PolylineOptions();
                lineOptions.add(points.get(o));
                if (o + 1 < points.size()) {
                    lineOptions.add(points.get(o + 1));
                    LatLng centro = LatLngBounds.builder().include(points.get(o)).include(points.get(o + 1)).build().getCenter();
                    Address adress = ubicacionGeodicacion.geocodificarUbiciacion(centro.latitude, centro.longitude);
                    distance = (int) SphericalUtil.computeDistanceBetween(points.get(o), points.get(o + 1));
                    String calle = (adress.getThoroughfare()
                            + ", " + adress.getSubLocality()
                            + ", " + adress.getLocality()
                            + ", " + adress.getAdminArea()
                            + ", " + adress.getCountryName()).toUpperCase(Locale.ROOT);
                    UbicacionRuta ubicacionRuta = ubicacionRutaDAO.obtenerUbicacionRutaConAdress(calle, ruta.getmCallesRuta());
                    if (ubicacionRuta == null) {
                        ubicacionRuta = new UbicacionRuta(distance, adress, calle);
                        ruta.getmCallesRuta().add(ubicacionRuta);
                    } else {
                        int index = ruta.getmCallesRuta().indexOf(ubicacionRuta);
                        ubicacionRuta.setmDistancia(distance + ubicacionRuta.getmDistancia());
                        ruta.getmCallesRuta().set(index, ubicacionRuta);
                        Log.v("QUICKSTART", "");
                    }
                    mGoogleMap.addPolyline(lineOptions);
                    Log.v("QUICKSTART", "Calle: " + calle + " Distancia " + ubicacionRuta.getmDistancia());
                }
                ruta.setmNumeroCalles(ruta.getmCallesRuta().size());
                rutaDirectionsPolyline = ruta.getmPolyline();
                rutaDirectionsPolyline.add(lineOptions);
                ruta.setmPolyline(rutaDirectionsPolyline);
            }
            rutasDirectionsPolyline.add(rutaDirectionsPolyline);

            ruta.setmDistanciaTotalDirections(directionsObtenidas.getDistancia().get(i));
            ruta.setmTiempoTotal(directionsObtenidas.getDuracion().get(i));
            this.rutas.add(ruta);

            Log.v("QUICKSTART", "DISTANCIA, TIEMPO: " + directionsObtenidas.getDistancia().get(i) + " , " + directionsObtenidas.getDuracion().get(i));
        }
        directionsObtenidas.setRutasEnPolylines(rutasDirectionsPolyline);

        this.rutas.sort(Comparator.comparing(Ruta::getmNumeroCalles).reversed());
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
            this.rutas.get(y).setNumeroDeRuta(this.rutas.size() - y);
            Log.v("QUICKSTART", "Ruta número: " + this.rutas.get(y).getNumeroDeRuta() + ". Ruta tamaño: " + this.rutas.get(y).getmNumeroCalles());
            for (int i = 0; i < this.rutas.get(y).getmNumeroCalles(); i++) {
                coloniaNombre = this.rutas.get(y).getmCallesRuta().get(i).getmDireccion().split(",", 2)[1].trim();
                if (!coloniasEncontradas.containsKey(coloniaNombre)) {
                    colonia = mUbicacionDAO.obtenerColonia(coloniaNombre, colonias, new LatLng(this.rutas.get(y).getmCallesRuta().get(i).getmAddress().getLatitude(), rutas.get(y).getmCallesRuta().get(i).getmAddress().getLongitude()));
                    if (colonia == null) {
                        Log.v("QUICKSTART", "No se encontró colonia.");
                        rutas.remove(y);
                        y--;
                        break;
                    } else {
                        Log.v("QUICKSTART", "Se encontró colonia.");
                        // Se guarda la colonia para después poder obtener su MH.
                        this.rutas.get(y).getmCallesRuta().get(i).setmUbicacionColonia(colonia);
                        coloniasEncontradas.put(coloniaNombre, colonia);

                        // Para obtener la calle de la BD, una vez que se "fomatea" el nombre de la calle a como se tiene en la BD.
                        calle = obtenerUbicacionCalle(UbicacionGeocodificacion.establecerNombreUbicacion(rutas.get(y).getmCallesRuta().get(i).getmAddress(), colonia));

                        // Se guarda la calle para después poder obtener su MH.
                        this.rutas.get(y).getmCallesRuta().get(i).setmUbicacionCalle(calle);

                        // Para establecer si se tomara los datos de la colonia o calle.
                        establecerListaTipoUbicacion(calle, i);
                    }
                } else {
                    this.rutas.get(y).getmCallesRuta().get(i).setmUbicacionColonia(coloniasEncontradas.get(coloniaNombre));
                    calle = obtenerUbicacionCalle(UbicacionGeocodificacion.establecerNombreUbicacion(rutas.get(y).getmCallesRuta().get(i).getmAddress(), coloniasEncontradas.get(coloniaNombre)));
                    this.rutas.get(y).getmCallesRuta().get(i).setmUbicacionCalle(calle);
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
    private void calcularMediaHistorica(int numeroRutas, int numeroCalles, boolean continuarCalculoMH) {
        int inicioCiclo = 0;

        if (continuarCalculoMH)
            inicioCiclo = this.rutas.stream().filter(r -> r.getNumeroDeRuta() == 1)
                    .findAny().orElse(null).getmNumeroCalles();

        for (int y = 0 ; y < numeroRutas; y++) {
            if (!continuarCalculoMH)
                this.rutas.get(y).setmMediaHistorica(0);

            for (int i = inicioCiclo; i < numeroCalles; i++) {
                if (this.tipoUbicacion.get(i).equals("calle"))
                    this.rutas.get(y).setmMediaHistorica(this.rutas.get(y).getmMediaHistorica()
                            + this.rutas.get(y).getmCallesRuta().get(i).getmUbicacionCalle().getMedia_historica_double());

                else if (this.tipoUbicacion.get(i).equals("colonia"))
                    this.rutas.get(y).setmMediaHistorica(this.rutas.get(y).getmMediaHistorica()
                            + this.rutas.get(y).getmCallesRuta().get(i).getmUbicacionColonia().getMedia_historica_double());
            }
            Log.v("QUICKSTART", "Media historica: " + this.rutas.get(y).getmMediaHistorica());
        }
        this.rutas.sort(Comparator.comparing(Ruta::getmMediaHistorica));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void calcularMediaHistoricaSoloColonias(int numeroRutas, boolean hasToSortByNumeroCalles) {
        Collections.replaceAll(this.tipoUbicacion, "calle", "colonia");

        if (hasToSortByNumeroCalles)
            this.rutas.sort(Comparator.comparing(Ruta::getmNumeroCalles));

        for (int y = 0; y < numeroRutas; y++) {
            for (int i = 0; i < this.rutas.get(y).getmNumeroCalles(); i++) {
                this.rutas.get(y).setmMediaHistorica(this.rutas.get(y).getmMediaHistorica()
                        + this.rutas.get(y).getmCallesRuta().get(i).getmUbicacionColonia().getMedia_historica_double());
            }
            Log.v("QUICKSTART", "Media historica: " + this.rutas.get(y).getmMediaHistorica());
        }
        this.rutas.sort(Comparator.comparing(Ruta::getmMediaHistorica));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void compararMediaHistorica() {
        // Para MH de Ruta 1 > MH de Ruta 2  && MH de Ruta 1 > MH de Ruta 3.
        if (this.rutas.get(this.rutas.size() - 1).getNumeroDeRuta() == 1
                || this.rutas.stream().allMatch(r -> r.getmMediaHistorica() == this.rutas.get(0).getmMediaHistorica())
                || this.rutas.stream().filter(r -> r.getNumeroDeRuta() == 1).findAny().orElse(null).getmMediaHistorica()
                == this.rutas.stream().filter(r -> r.getNumeroDeRuta() == 2).findAny().orElse(null).getmMediaHistorica()
                || this.rutas.stream().filter(r -> r.getNumeroDeRuta() == 1).findAny().orElse(null).getmMediaHistorica()
                == this.rutas.stream().filter(r -> r.getNumeroDeRuta() == 3).findAny().orElse(null).getmMediaHistorica()) {

            // Para verificar que no sea una lista de dos rutas.
            if (this.rutas.size() == 2) {
                calcularMediaHistoricaSoloColonias(2, false);
            } else {
                // Se tendrá que calcular cuál ruta entre la 2 y la 3 tiene menor MH.
                // Se sigue sumando la MH de la Ruta 2 y Ruta 3 hasta el número de calles de la Ruta 2.
                calcularMediaHistorica(2, this.rutas.stream().filter(r -> r.getNumeroDeRuta() == 2)
                        .findAny().orElse(null).getmNumeroCalles(), true);

                // Se excluye la Ruta 1 para poder hacer comparaciones y obtener la ruta con menor MH.
                List<Ruta> rutasSinRuta1 = rutas.stream()
                        .filter(r -> r.getNumeroDeRuta() > 1).sorted(Comparator.comparing(Ruta::getmMediaHistorica)).collect(Collectors.toList());

                // Para MH de Ruta 2  > MH de Ruta 3 o MH de Ruta 2  = MH de Ruta 3
                if (rutasSinRuta1.get(0).getNumeroDeRuta() == 3 || (rutasSinRuta1.get(0).getmMediaHistorica() == rutasSinRuta1.get(1).getmMediaHistorica()))
                    calcularMediaHistoricaSoloColonias(this.rutas.size(), false);

                // Para MH de Ruta 2 < MH de Ruta 3.
                else
                    calcularMediaHistoricaSoloColonias(2,true);
            }
        }

        // Para MH de Ruta 1 > MH de Ruta 2  && MH de Ruta 1 < MH de Ruta 3.
        // Para MH de Ruta 1 <  MH de Ruta 2 && MH de Ruta 1 > MH de Ruta 3.
        else if (this.rutas.get(1).getNumeroDeRuta() == 1) {
            calcularMediaHistoricaSoloColonias(2, false);
        }

        comparacionFinalMediaHistorica();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void comparacionFinalMediaHistorica() {
        // Para verificar si al final de todas las comparaciones la MH es igual en todas las rutas.
        if (this.rutas.stream().allMatch(r -> r.getmMediaHistorica() == this.rutas.get(0).getmMediaHistorica()))
            // Si es igual, en todas se establce que la condicion "HasMenorMediaHistorica" es false y se tendrá que evaluar las rutas con las otras condiciones (clase Ruta).
            this.rutas.forEach(ruta -> ruta.setHasMenorMediaHistorica(false));
        else
            this.rutas.get(0).setHasMenorMediaHistorica(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void calcularPorcentajeZonasSeguras() {
        for (int y = 0 ; y < this.rutas.size(); y++) {
            int numZonasSeguras;
            for (int i = 0; i < this.rutas.get(y).getmNumeroCalles(); i++) {
                // Se verifica que la seguridad de la calle o colonia de la actual calle de la ruta sea ALTA.
                // Si es ALTA, el contador de numero de zonas seguras aumentará.
                if (this.tipoUbicacion.get(i).equals("calle"))
                    this.rutas.get(y).setmMediaHistorica(this.rutas.get(y).getmMediaHistorica()
                            + this.rutas.get(y).getmCallesRuta().get(i).getmUbicacionCalle().getMedia_historica_double());

                else if (this.tipoUbicacion.get(i).equals("colonia"))
                    this.rutas.get(y).setmMediaHistorica(this.rutas.get(y).getmMediaHistorica()
                            + this.rutas.get(y).getmCallesRuta().get(i).getmUbicacionColonia().getMedia_historica_double());
            }
            Log.v("QUICKSTART", "Media historica: " + this.rutas.get(y).getmMediaHistorica());

            // Después de iterar por calles de la ruta se calcula el % de zonas seguras con una regla de tres.
        }
        this.rutas.sort(Comparator.comparing(Ruta::getmMediaHistorica));
    }


}
