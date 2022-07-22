package com.example.llegabien.backend.ruta;

import android.content.Context;
import android.location.Address;
import android.os.Build;
import android.util.Log;

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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class EvaluacionRuta {

    private final GoogleMap mGoogleMap;
    private final Context mContext;
    private UbicacionDAO ubicacionDAO;
    //private List<List<UbicacionRuta>> rutasDistancias;
    private List<Ruta> rutas;
    private List<String> tipoUbicacion;

    public EvaluacionRuta(GoogleMap mGoogleMap, Context mContext) {
        this.mGoogleMap = mGoogleMap;
        this.mContext = mContext;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void obtenerRuta(RutaDirections directionsObtenidas) {
        //  this.rutasDistancias = new ArrayList<>();
        this.ubicacionDAO = new UbicacionDAO(mContext);
        this.tipoUbicacion = new ArrayList<>();
        this.rutas = new ArrayList<>();

        obtenerPuntosRutas(directionsObtenidas);
        verificarExistenciaColonias();
        Log.v("QUICKSTART", "tipo Ubicacion.size " + this.tipoUbicacion.size());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void obtenerPuntosRutas(RutaDirections directionsObtenidas) {
        UbicacionGeocodificacion ubicacionGeodicacion = new UbicacionGeocodificacion(mContext);
        UbicacionRutaDAO ubicacionRutaDAO = new UbicacionRutaDAO();
        List<PolylineOptions> rutasObtenidas = directionsObtenidas.getRutasDirectionsPolylineOptions();
        List<PolylineOptions> rutaDirectionsPolyline = new ArrayList<>();
        List<List<PolylineOptions>> rutasDirectionsPolyline = new ArrayList<>();
        int distance;

        // PARA OBTENER PUNTOS MEDIO Y SUS DISTANCIAS
        // Recorre rutas
        for (int i = 0; i < obtenerNumeroRutas(rutasObtenidas.size()); i++) {
            List<LatLng> points = rutasObtenidas.get(i).getPoints();
            Ruta ruta = new Ruta();
            ruta.setmCallesRuta(new ArrayList<>());
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
                    Log.v("QUICKSTART", "Calle: " + calle + " Distancia " + ubicacionRuta.getmDistancia());
                }
                ruta.setmPolyline(lineOptions);
                mGoogleMap.addPolyline(ruta.getmPolyline());
                rutaDirectionsPolyline.add(lineOptions);

                ruta.setmNumeroCalles(ruta.getmCallesRuta().size());
                ruta.setmDistanciaTotalDirections(directionsObtenidas.getDistancia().get(i));
                ruta.setmTiempoTotal(directionsObtenidas.getDuracion().get(i));
            }
            rutasDirectionsPolyline.add(rutaDirectionsPolyline);
            this.rutas.add(ruta);
            Log.v("QUICKSTART", "DISTANCIA, TIEMPO: " + directionsObtenidas.getDistancia().get(i) + " , " + directionsObtenidas.getDuracion().get(i));
        }
        directionsObtenidas.setRutasEnPolylines(rutasDirectionsPolyline);

        this.rutas.sort(Comparator.comparing(Ruta::getmNumeroCalles).reversed());
    }

    private int obtenerNumeroRutas(int size) {
        if (size > 3)
            return 3;
        else
            return size;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void verificarExistenciaColonias() {
        // PARA VERIFICAR QUE EXISTAN LAS COLONIAS DE CADA RUTA
        UbicacionDAO mUbicacionDAO = new UbicacionDAO(mContext);
        List<ubicacion> colonias = mUbicacionDAO.obtenerColonias();
        ubicacion colonia;
        HashMap<String, ubicacion> coloniasEncontradas = new HashMap<>();
        String coloniaNombre;
        ubicacion ubicacionCalle;

        for (int y = 0; y < this.rutas.size(); y++) {
            for (int i = 0; i < this.rutas.get(y).getmCallesRuta().size(); i++) {
                coloniaNombre = this.rutas.get(y).getmCallesRuta().get(i).getmDireccion().split(",", 2)[1].trim();
                if (!coloniasEncontradas.containsKey(coloniaNombre)) {
                    colonia = mUbicacionDAO.obtenerColonia(coloniaNombre, colonias, new LatLng(this.rutas.get(y).getmCallesRuta().get(i).getmAddress().getLatitude(), rutas.get(y).getmCallesRuta().get(i).getmAddress().getLongitude()));
                    if (colonia == null) {
                        Log.v("QUICKSTART", "Colonia no encontrada ");
                        rutas.remove(y);
                        y--;
                        break;
                    } else {
                        this.rutas.get(y).getmCallesRuta().get(i).setmUbicacionColonia(colonia);
                        coloniasEncontradas.put(coloniaNombre, colonia);
                        Log.v("QUICKSTART", "SE ENCONTRO ");
                        ubicacionCalle = obtenerUbicacionCalle(UbicacionGeocodificacion.establecerNombreUbicacion(rutas.get(y).getmCallesRuta().get(i).getmAddress(), colonia));
                        this.rutas.get(y).getmCallesRuta().get(i).setmUbicacionCalle(ubicacionCalle);
                        establecerListatipoUbicacion(ubicacionCalle, i);
                    }
                } else {
                    ubicacionCalle = obtenerUbicacionCalle(UbicacionGeocodificacion.establecerNombreUbicacion(rutas.get(y).getmCallesRuta().get(i).getmAddress(), coloniasEncontradas.get(coloniaNombre)));
                    this.rutas.get(y).getmCallesRuta().get(i).setmUbicacionColonia(coloniasEncontradas.get(coloniaNombre));
                    this.rutas.get(y).getmCallesRuta().get(i).setmUbicacionCalle(ubicacionCalle);
                    establecerListatipoUbicacion(ubicacionCalle, i);
                }
                Log.v("QUICKSTART", "ubicacion Tipo en ruta " + y + " en punto " + i + " " + this.tipoUbicacion.get(i));
            }
            Log.v("QUICKSTART", " " + rutas.get(y).getmCallesRuta().size());
            Log.v("QUICKSTART", "ruta " + y + " size: " + rutas.get(y).getmCallesRuta().size());
        }
        if (rutas.isEmpty()) {
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
/*
    private void calcularMediaHistorica() {
        int numCallesRutaUno=0, numCallesRutaDos = 0, numCallesRutaTres = 0 ;

        for (int y = this.rutas.size()-1; y >= 0; y--) {
            if(y==2)
                numCallesRutaUno = this.rutas.get(y).getmNumeroCalles();
            else if(y==1)
                numCallesRutaDos = this.rutas.get(y).getmNumeroCalles();
            else if(y==0)
                numCallesRutaTres = this.rutas.get(y).getmNumeroCalles();

            for (int i = 0; i < this.rutas.get(y).getmCallesRuta().size(); i++) {
                if(this.tipoUbicacion.get(i).equals("calle"))
                    this.rutas.get(y).setmMediaHistorica(this.rutas.get(y).getmMediaHistorica()
                            + this.rutas.get(y).getmCallesRuta().get(i).getmUbicacionCalle().getMedia_historica_double());

                else if(this.tipoUbicacion.get(i).equals("colonia"))
                    this.rutas.get(y).setmMediaHistorica(this.rutas.get(y).getmMediaHistorica()
                            + this.rutas.get(y).getmCallesRuta().get(i).getmUbicacionColonia().getMedia_historica_double());

                if(y==2 && i == numCallesRutaUno){
                    if(this.rutas.get(y-1).getmMediaHistorica() > this.rutas.get(y).getmMediaHistorica())

                }
            }

        }
    }*/

    private void inicializarObtenerUbicacionesRutas(List<List<UbicacionRuta>> rutasDistancias, HashMap<String, ubicacion> coloniasEncontradas, int rutaMasLarga) {
        List<String> tipoUbicacion = new ArrayList<>();
        ubicacion calle;

        // Para inicializar la forma en cómo se tomarán las rutas
        for (int i = 0; i < rutasDistancias.get(rutaMasLarga).size(); i++) {
            UbicacionRuta ubicacionRuta = rutasDistancias.get(rutaMasLarga).get(i);
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
