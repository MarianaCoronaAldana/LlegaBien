package com.example.llegabien.backend.ruta;

import android.content.Context;
import android.location.Address;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.llegabien.backend.mapa.ubicacion.UbicacionDAO;
import com.example.llegabien.backend.mapa.ubicacion.UbicacionGeocodificacion;
import com.example.llegabien.backend.mapa.ubicacion.ubicacion;
import com.example.llegabien.backend.ruta.directions.rutaDirections;
import com.example.llegabien.backend.ruta.directions.ubicacionRuta;
import com.example.llegabien.backend.ruta.directions.ubicacionRutaDAO;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class EvaluacionRuta {

    private final GoogleMap mGoogleMap;
    private final Context mContext;
    private UbicacionDAO ubicacionDAO;
    private List<List<ubicacionRuta>> rutasDistancias;
    private List<String> tipoUbicacion;

    public EvaluacionRuta(GoogleMap mGoogleMap, Context mContext) {
        this.mGoogleMap = mGoogleMap;
        this.mContext = mContext;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void obtenerRuta(rutaDirections directionsObtenidas) {
        this.rutasDistancias = new ArrayList<>();
        this.ubicacionDAO = new UbicacionDAO(mContext);
        this.tipoUbicacion = new ArrayList<>();

        obtenerPuntosRutas(directionsObtenidas);
        verificarExistenciaColonias();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void obtenerPuntosRutas(rutaDirections directionsObtenidas) {
        UbicacionGeocodificacion ubicacionGeodicacion = new UbicacionGeocodificacion(mContext);
        ubicacionRutaDAO ubicacionRutaDAO = new ubicacionRutaDAO();
        List<PolylineOptions> rutasObtenidas = directionsObtenidas.getRutasDirectionsPolylineOptions();
        List<PolylineOptions> ruta = new ArrayList<>();
        List<List<PolylineOptions>> rutas = new ArrayList<>();
        int distance;

        // PARA OBTENER PUNTOS MEDIO Y SUS DISTANCIAS
        // Recorre rutas
        for (int i = 0; i < rutasObtenidas.size(); i++) {
            List<LatLng> points = rutasObtenidas.get(i).getPoints();
            List<ubicacionRuta> rutaDistancias = new ArrayList<>();
            // Recorre los puntos de una ruta
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
                    ubicacionRuta ubicacionRuta = ubicacionRutaDAO.obtenerUbicacionRutaConAdress(calle, rutaDistancias);
                    if (ubicacionRuta == null) {
                        ubicacionRuta = new ubicacionRuta(distance, adress, calle);
                        rutaDistancias.add(ubicacionRuta);
                    } else {
                        int index = rutaDistancias.indexOf(ubicacionRuta);
                        ubicacionRuta.setmDistancia(distance + ubicacionRuta.getmDistancia());
                        rutaDistancias.set(index, ubicacionRuta);
                        Log.v("QUICKSTART", "");
                    }
                    Log.v("QUICKSTART", "Calle: " + calle + " Distancia " + ubicacionRuta.getmDistancia());
                }
                mGoogleMap.addPolyline(lineOptions);
                ruta.add(lineOptions);
            }
            rutas.add(ruta);
            this.rutasDistancias.add(rutaDistancias);
            Log.v("QUICKSTART", "DISTANCIA, TIEMPO: " + directionsObtenidas.getDistancia().get(i) + " , " + directionsObtenidas.getDuracion().get(i));
            // Log.v("QUICKSTART", "HASHMAP " + rutaDistancias);
        }
        directionsObtenidas.setRutasEnPolylines(rutas);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void verificarExistenciaColonias() {
        // PARA VERIFICAR QUE EXISTAN LAS COLONIAS DE CADA RUTA
        UbicacionDAO mUbicacionDAO = new UbicacionDAO(mContext);
        List<ubicacion> colonias = mUbicacionDAO.obtenerColonias();
        ubicacion colonia;
        HashMap<String, ubicacion> coloniasEncontradas = new HashMap<>();
        int rutaMasLarga = 0;
        int puntosPorRuta = 0;
        String coloniaNombre;
        ubicacion ubicacionCalle;

        for (int y = 0; y < this.rutasDistancias.size(); y++) {
            for (int i = 0; i < this.rutasDistancias.get(y).size(); i++) {
                coloniaNombre = rutasDistancias.get(y).get(i).getmDireccion().split(",", 2)[1].trim();
                if (!coloniasEncontradas.containsKey(coloniaNombre)) {
                    colonia = mUbicacionDAO.obtenerColonia(coloniaNombre, colonias, new LatLng(rutasDistancias.get(y).get(i).getmAddress().getLatitude(), rutasDistancias.get(y).get(i).getmAddress().getLongitude()));
                    if (colonia == null) {
                        Log.v("QUICKSTART", "Colonia no encontrada ");
                        rutasDistancias.remove(y);
                        y--;
                        break;
                    } else {
                        rutasDistancias.get(y).get(i).setmUbicacionColonia(colonia);
                        coloniasEncontradas.put(coloniaNombre, colonia);
                        Log.v("QUICKSTART", "SE ENCONTRO ");
                        ubicacionCalle= obtenerUbicacionCalle(UbicacionGeocodificacion.establecerNombreUbicacion(rutasDistancias.get(y).get(i).getmAddress(), colonia));
                        rutasDistancias.get(y).get(i).setmUbicacionCalle(ubicacionCalle);
                        establecerListatipoUbicacion(ubicacionCalle, i);
                    }
                } else {
                    ubicacionCalle= obtenerUbicacionCalle(UbicacionGeocodificacion.establecerNombreUbicacion(rutasDistancias.get(y).get(i).getmAddress(), coloniasEncontradas.get(coloniaNombre)));
                    rutasDistancias.get(y).get(i).setmUbicacionColonia(coloniasEncontradas.get(coloniaNombre));
                    rutasDistancias.get(y).get(i).setmUbicacionCalle(ubicacionCalle);
                    establecerListatipoUbicacion(ubicacionCalle, i);
                }
                Log.v("QUICKSTART", "ubicacion Tipo en ruta " + y + " en punto " + i + " " +this.tipoUbicacion.get(i));
            }
        }
        Log.v("QUICKSTART", "rutas size: " + rutasDistancias.size());

        if (rutasDistancias.isEmpty()) {
            Log.v("QUICKSTART", "No hay rutas disponibles!");
        }
        //inicializarObtenerUbicacionesRutas(rutasDistancias, coloniasEncontradas, rutaMasLarga);
    }


    private ubicacion obtenerUbicacionCalle(String calleNombre) {
        if (calleNombre != null) {
            return ubicacionDAO.obtenerUbicacionConNombre(calleNombre);
        }
        return null;
    }

    private void establecerListatipoUbicacion(ubicacion ubicacionCalle, int i) {
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

    private void inicializarObtenerUbicacionesRutas(List<List<ubicacionRuta>> rutasDistancias, HashMap<String, ubicacion> coloniasEncontradas, int rutaMasLarga) {
        List<String> tipoUbicacion = new ArrayList<>();
        ubicacion calle;

        // Para inicializar la forma en cómo se tomarán las rutas
        for (int i = 0; i < rutasDistancias.get(rutaMasLarga).size(); i++) {
            ubicacionRuta ubicacionRuta = rutasDistancias.get(rutaMasLarga).get(i);
            // AQUI VA LO DE INICIALIZAR CALLE
            calle = null;
            if (calle != null) {
                ubicacionRuta.setmUbicacionCalle(calle);
                this.tipoUbicacion.add("calle");
            } else {
                //ubicacionRuta.setmUbicacionColonia(coloniasEncontradas.get(ubicacionRuta.getmAddress().getSubLocality()));
                this.tipoUbicacion.add("colonia");
            }
            rutasDistancias.get(rutaMasLarga).set(i, ubicacionRuta);
        }

        //  obtenerUbicacionesRutas(rutasDistancias, coloniasEncontradas, this.tipoUbicacion, rutaMasLarga, true);
    }


}
