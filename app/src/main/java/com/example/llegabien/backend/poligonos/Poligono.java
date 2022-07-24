package com.example.llegabien.backend.poligonos;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_UBICACION;

import android.content.Context;
import android.graphics.Color;

import com.example.llegabien.R;
import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.ubicacion.UbicacionDAO;
import com.example.llegabien.backend.ubicacion.ubicacion;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;

public class Poligono {

    private Context mContext;
    private final UbicacionDAO mUbicacionDAO = new UbicacionDAO(mContext);

    public Poligono(Context context){
        mContext = context;
    }

    public Poligono() {}

    public void getColonias(GoogleMap googleMap){
        String coordenadasPoligono, seguridad;
        List<ubicacion> listaColonias = mUbicacionDAO.obtenerColonias();
        if (listaColonias != null) {
            for (int i = 0; i < listaColonias.size(); i++) {
                coordenadasPoligono = listaColonias.get(i).getCoordenadas_string();
                seguridad = listaColonias.get(i).getSeguridad();
                if (coordenadasPoligono != null) {
                    mostrarPoligono(getCoordenadasFromString(coordenadasPoligono), googleMap, seguridad);
                }
            }
        }
    }

    private void mostrarPoligono(List<LatLng> listLatLong, GoogleMap googleMap, String seguridad){
        //boolean isInside = PolyUtil.containsLocation(20.750693880142634, -103.38741952291363,listLatLong,true);
        LatLng[] points = listLatLong.toArray(new LatLng[0]);
        switch (seguridad){
            case "Seguridad baja":
                crearPoligono(googleMap, points, mContext.getResources().getColor(R.color.rojo_poligono));
                break;
            case "Seguridad media":
                crearPoligono(googleMap, points, mContext.getResources().getColor(R.color.amarillo_poligono));
                break;
            case "Seguridad alta":
                crearPoligono(googleMap, points, mContext.getResources().getColor(R.color.verde_poligono));
                break;
            default:
                crearPoligono(googleMap, points, Color.BLUE);
                break;
        }
    }

    private void crearPoligono(GoogleMap googleMap, LatLng[] points,int fillColor){
        googleMap.addPolygon(
                new PolygonOptions()
                        .add(points)
                        .strokeWidth(7)
                        .strokeColor(Color.WHITE)
                        .fillColor(fillColor)
                        .clickable(true)
        );
    }

    public void getInfoPoligono(Polygon polygon){
        String coordenadasPoligono = getCoordenadasFromList(polygon.getPoints());
        mUbicacionDAO.obetenerUbicacionConPoligono(coordenadasPoligono, mContext);
        ubicacion ubicacion = Preferences.getSavedObjectFromPreference(mContext, PREFERENCE_UBICACION, ubicacion.class);
        if (ubicacion != null) {
            String seguridad = ubicacion.getSeguridad();
            if (seguridad.equals("Seguridad baja"))
                polygon.setFillColor(mContext.getResources().getColor(R.color.rojo_oscuro_poligono));
            if (seguridad.equals("Seguridad media"))
                polygon.setFillColor(mContext.getResources().getColor(R.color.amarillo_oscuro_poligono));
            if (seguridad.equals("Seguridad alta"))
                polygon.setFillColor(mContext.getResources().getColor(R.color.verde_oscuro_poligono));
        }
    }

    public LatLng getCentroPoligono(Polygon polygon){
        LatLngBounds.Builder latLngBounds = LatLngBounds.builder();
        for (LatLng latLng : polygon.getPoints()) {
            latLngBounds.include(latLng);
        }
        return latLngBounds.build().getCenter();
    }

    public List<LatLng> getCoordenadasFromString(String linea){
        int cuenta = 0;
        StringBuilder latLongString = new StringBuilder();
        String [] latLongArray;
        List<LatLng> points = new ArrayList<>();
        for(int i = 0; i < linea.length(); i++){
            char charAux = linea.charAt(i);
            if(charAux == ',') {
                cuenta++;
                if (cuenta == 2) {
                    latLongArray = latLongString.toString().split(",");
                    double longitude = Double.parseDouble(latLongArray[0]);
                    double latitude = Double.parseDouble(latLongArray[1]);
                    LatLng location = new LatLng(latitude, longitude);
                    points.add(location);
                    latLongString = new StringBuilder();
                    cuenta = 0;
                }
                else
                    latLongString.append(charAux);
            }
            else
                latLongString.append(charAux);
        }
        return points;
    }

    public String getCoordenadasFromList(List<LatLng> points){
        StringBuilder coordenadasPoligono = new StringBuilder();
        for(int i = 0; i < points.size(); i++){
            double latitude = points.get(i).latitude;
            double longitude = points.get(i).longitude;
            if (i == 0)
                coordenadasPoligono.append(longitude).append(",").append(latitude);
            else
                coordenadasPoligono.append(",").append(longitude).append(",").append(latitude);
        }
        return coordenadasPoligono.toString();
    }

    public ubicacion isUbicacionEnPoligono (RealmResults<ubicacion> resultadosUbicaciones, double latitude, double longitude){
        String coordenadas;

        for (int i = 0; i < resultadosUbicaciones.size(); i++) {
            coordenadas = resultadosUbicaciones.get(i).getCoordenadas_string();
            if(PolyUtil.containsLocation(new LatLng(latitude, longitude), getCoordenadasFromString(coordenadas), true))
                return resultadosUbicaciones.get(i);
        }

        return null;
    }

    public ubicacion isUbicacionEnPoligono(List<ubicacion> resultadosUbicaciones, double latitude, double longitude){
        String coordenadas;

        for (int i = 0; i < resultadosUbicaciones.size(); i++) {
            coordenadas = resultadosUbicaciones.get(i).getCoordenadas_string();
            if(PolyUtil.containsLocation(new LatLng(latitude, longitude), getCoordenadasFromString(coordenadas), true))
                return resultadosUbicaciones.get(i);
        }
        return null;
    }
}
