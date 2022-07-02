package com.example.llegabien.backend.mapa.poligonos;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_UBICACION;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.llegabien.R;
import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.mapa.ubicacion.UbicacionBD_CRUD;
import com.example.llegabien.backend.mapa.ubicacion.ubicacion;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.PolyUtil;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;

public class Poligono {

    private RealmResults<ubicacion> mResultadosColonias;
    private Context mContext;
    private UbicacionBD_CRUD mUbicacionBD_CRUD = new UbicacionBD_CRUD(mContext);

    public Poligono(Context context){
        mContext = context;
    }

    public Poligono() {}

    public void getColonias(GoogleMap googleMap){
        String coordenadasPoligono = "", seguridad = "";
        mResultadosColonias = mUbicacionBD_CRUD.obetenerColonias();
        if (mResultadosColonias != null) {
            for (int i = 0; i < mResultadosColonias.size(); i++) {
                coordenadasPoligono = mResultadosColonias.get(i).getCoordenadas_string();
                seguridad = mResultadosColonias.get(i).getSeguridad();
                if (coordenadasPoligono != null) {
                    mostrarPoligono(getCoordenadasFromString(coordenadasPoligono), googleMap, seguridad);
                }
            }
        }
    }

    private void mostrarPoligono(List<LatLng> listLatLong, GoogleMap googleMap, String seguridad){
        //boolean isInside = PolyUtil.containsLocation(20.750693880142634, -103.38741952291363,listLatLong,true);
        LatLng[] points = listLatLong.toArray(new LatLng[listLatLong.size()]);

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
        mUbicacionBD_CRUD.obetenerUbicacionConPoligono(coordenadasPoligono, mContext);
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
        String coordenadasPoligono = "";

        for(int i = 0; i < points.size(); i++){
            Double latitude = points.get(i).latitude;
            Double longitude = points.get(i).longitude;
            if (i == 0)
                coordenadasPoligono = coordenadasPoligono + String.valueOf(longitude) + "," + String.valueOf(latitude);
            else
                coordenadasPoligono = coordenadasPoligono + "," + String.valueOf(longitude) + "," + String.valueOf(latitude);
        }

        return coordenadasPoligono;
    }

    public ubicacion isUbicacionEnPoligono (RealmResults<ubicacion> resultadosUbicaciones, double latitude, double longitude){
        String coordenadas = "";

        for (int i = 0; i < resultadosUbicaciones.size(); i++) {
            coordenadas = resultadosUbicaciones.get(i).getCoordenadas_string();
            if(PolyUtil.containsLocation(new LatLng(latitude, longitude), getCoordenadasFromString(coordenadas), true))
                return resultadosUbicaciones.get(i);
        }

        return null;
    }

}
