package com.example.llegabien.backend.mapa.poligonos;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_UBICACION;
import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.mapa.ubicacion.UbicacionBD_CRUD;
import com.example.llegabien.backend.mapa.ubicacion.ubicacion;
import com.example.llegabien.backend.usuario.usuario;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;

public class Poligono {

    private RealmResults<ubicacion> ubicacionResults;
    UbicacionBD_CRUD ubicacionBD_CRUD = new UbicacionBD_CRUD();

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getPoligonos(GoogleMap googleMap, Activity activity){
        String coordenadasPoligono = "", seguridad = "";

        ubicacionResults = ubicacionBD_CRUD.obetenerUbicaciones();

        for (int i = 0; i < ubicacionResults.size(); i++)
        {
            coordenadasPoligono = ubicacionResults.get(i).getPoligono();
            seguridad = ubicacionResults.get(i).getSeguridad();
            if (!(coordenadasPoligono == null) || !(seguridad == null)){
                mostrarPoligono(getCoordenadasFromString(coordenadasPoligono),googleMap,seguridad);
            }
        }

    }

    private void mostrarPoligono(List<LatLng> listLatLong, GoogleMap googleMap, String seguridad){
        //boolean isInside = PolyUtil.containsLocation(20.750693880142634, -103.38741952291363,listLatLong,true);
        LatLng[] points = listLatLong.toArray(new LatLng[listLatLong.size()]);

        switch (seguridad){
            case "Seguridad baja":
                crearPoligono(googleMap, points, 0x7FFF0000);
                break;
            case "Seguridad media":
                crearPoligono(googleMap, points, 0x7FF5EC0D);
                break;
            case "Seguridad alta":
                crearPoligono(googleMap, points, 0x9F85BB78);
                break;
            default:
                crearPoligono(googleMap, points, Color.BLUE);
                break;

        }
    }

    private void crearPoligono(GoogleMap googleMap, LatLng[] points,int fillColor){
        Polygon polygon = googleMap.addPolygon(
                new PolygonOptions()
                        .add(points)
                        .strokeWidth(7)
                        .strokeColor(Color.WHITE)
                        .fillColor(fillColor)
                        .clickable(true)
        );
    }

    public void getInfoPoligono(Polygon polygon, Context c){
        String coordenadasPoligono = getCoordenadasFromList(polygon.getPoints());
        ubicacionBD_CRUD.obetenerUbicacionConPoligono(coordenadasPoligono, c);
    }

    private List<LatLng> getCoordenadasFromString(String linea){
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
}
