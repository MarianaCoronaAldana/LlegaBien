package com.example.llegabien.backend.ruta;

import static io.realm.Realm.getApplicationContext;

import android.content.Context;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.llegabien.backend.mapa.ubicacion.UbicacionDAO;
import com.example.llegabien.backend.mapa.ubicacion.UbicacionGeocodificacion;
import com.example.llegabien.backend.mapa.ubicacion.ubicacion;
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

public class EvaluacionRuta extends AsyncTask<String, Void, String> {

    private final Context mContext;
    private UbicacionDAO ubicacionDAO;
    private GoogleMap mGoogleMap;
    private List<Ruta> rutas;
    private List<String> tipoUbicacion;
    private RutaDirections directionsObtenidas;
    public EvaluacionRuta(GoogleMap mGoogleMap, Context mContext) {
        this.mGoogleMap = mGoogleMap;
        this.mContext = mContext;
    }

    public EvaluacionRuta(Context mContext) {
        this.mContext = mContext;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void obtenerRuta(RutaDirections DirectionsObtenidas) {
        this.ubicacionDAO = new UbicacionDAO(mContext);
        this.tipoUbicacion = new ArrayList<>();
        this.rutas = new ArrayList<>();
        directionsObtenidas = DirectionsObtenidas;

/*
        obtenerPuntosRutas(directionsObtenidas);
        verificarExistenciaColonias();
        if (!this.rutas.isEmpty()) {
            calcularMediaHistorica(this.rutas.size(), this.rutas.get(this.rutas.size() - 1).getmCallesRuta().size(), false);
            compararMediaHistorica();
        } else {
            Toast.makeText(getApplicationContext(), "No hay rutas disponibles!", Toast.LENGTH_LONG).show();
            Log.v("QUICKSTART", "No hay rutas disponibles!");
        }
  */  }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void obtenerPuntosRutas(RutaDirections directionsObtenidas) {
        UbicacionGeocodificacion ubicacionGeodicacion = new UbicacionGeocodificacion(mContext);
        UbicacionRutaDAO ubicacionRutaDAO = new UbicacionRutaDAO();
        List<PolylineOptions> rutasObtenidas = directionsObtenidas.getRutasDirectionsPolylineOptions();

        List<PolylineOptions> rutaDirectionsPolyline = new ArrayList<>();
        List<List<PolylineOptions>> rutasDirectionsPolyline = new ArrayList<>();
        int distance;

        if (rutasObtenidas.isEmpty()) {
            Toast.makeText(getApplicationContext(), "No hay rutas disponibles!", Toast.LENGTH_LONG).show();
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
//                    mGoogleMap.addPolyline(lineOptions);
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
            this.rutas.get(y).setNumeroDeRuta(y+1);
            Log.v("QUICKSTART", "Ruta número: " + this.rutas.get(y).getNumeroDeRuta() + ". Ruta tamaño: " + this.rutas.get(y).getmNumeroCalles());
            for (int i = 0; i < this.rutas.get(y).getmCallesRuta().size(); i++) {
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
                    calle = obtenerUbicacionCalle(UbicacionGeocodificacion.establecerNombreUbicacion(rutas.get(y).getmCallesRuta().get(i).getmAddress(), coloniasEncontradas.get(coloniaNombre)));
                    this.rutas.get(y).getmCallesRuta().get(i).setmUbicacionColonia(coloniasEncontradas.get(coloniaNombre));
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
    private void calcularMediaHistorica(int numeroRutas, int numeroCalles, boolean hasToSortByNumeroCalles) {
        if(hasToSortByNumeroCalles)
            this.rutas.sort(Comparator.comparing(Ruta::getmNumeroCalles).reversed());

        int inicioCiclo=0;
        boolean continuarCalculoMH=false;
        if(continuarCalculoMH)
            inicioCiclo = this.rutas.stream().filter(r -> r.getNumeroDeRuta() == 1)
                    .findAny().orElse(null).getmCallesRuta().size();

        for (int y = numeroRutas-1; y >= 0; y--) {
            this.rutas.get(y).setmMediaHistorica(0);
            for (int i = 0; i < numeroCalles; i++) {
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
    public void compararMediaHistorica() {

        if (this.rutas.get(this.rutas.size() - 1).getNumeroDeRuta() == 1) {
            calcularMediaHistorica(2,  this.rutas.stream().filter(r -> r.getNumeroDeRuta() == 2)
                    .findAny().orElse(null).getmCallesRuta().size(), false);

            Collections.replaceAll(this.tipoUbicacion, "calle", "colonia");

            if(this.rutas.get(0).getNumeroDeRuta() == 3)
                calcularMediaHistorica(this.rutas.size(), this.rutas.get(0).getmCallesRuta().size(), true);
            else
                calcularMediaHistorica(2, this.rutas.get(0).getmCallesRuta().size(), true);
        }

        else if (this.rutas.get(1).getNumeroDeRuta() == 1) {
            Collections.replaceAll(this.tipoUbicacion, "calle", "colonia");
            Log.v("QUICKSTART", " mira mira esta cambiadoo " + this.tipoUbicacion.get(0));
            //PONER UN SORT AL REVES?
            calcularMediaHistorica(2, this.rutas.get(0).getmCallesRuta().size(), false);
        }
        this.rutas.get(0).setHasMenorMediaHistorica(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected String doInBackground(String... strings) {
        obtenerPuntosRutas(directionsObtenidas);
        verificarExistenciaColonias();
        if (!this.rutas.isEmpty()) {
            calcularMediaHistorica(this.rutas.size(), this.rutas.get(this.rutas.size() - 1).getmCallesRuta().size(), false);
            compararMediaHistorica();
        } else {
            Toast.makeText(getApplicationContext(), "No hay rutas disponibles!", Toast.LENGTH_LONG).show();
            Log.v("QUICKSTART", "No hay rutas disponibles!");
        }
        return null;
    }
}