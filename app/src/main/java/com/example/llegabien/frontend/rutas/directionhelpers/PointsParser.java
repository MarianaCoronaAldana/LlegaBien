package com.example.llegabien.frontend.rutas.directionhelpers;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.example.llegabien.backend.ruta.directions.rutaDirections;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PointsParser extends AsyncTask<String, Integer, rutaDirections> {
    TaskLoadedCallback taskCallback;
    String directionMode = "driving";
    rutaDirections rutasDirections = new rutaDirections();

    public PointsParser(TaskLoadedCallback mContext, String directionMode) {
        this.taskCallback =  mContext;
        this.directionMode = directionMode;
    }

    // Se encarga de parsear la info
    @Override
    protected rutaDirections doInBackground(String... jsonData) {
        JSONObject jObject;
        List<List<HashMap<String, String>>> rutas = null;
        try {
            jObject = new JSONObject(jsonData[0]);
            Log.v("QUICKSTART", jsonData[0].toString());
            DataParser parser = new DataParser();
            Log.v("QUICKSTART", parser.toString());
            // Empieza a parsear
            rutasDirections = parser.parser(jObject);
            rutas = rutasDirections.getRutasDirectionsJSON();
            Log.v("QUICKSTART", "Ejecutando rutas");
            Log.v("QUICKSTART", rutas.toString());
        } catch (Exception e) {
            Log.v("QUICKSTART", e.toString());
            e.printStackTrace();
        }
        rutasDirections.setRutasDirectionsJSON(rutas);
        return rutasDirections;
    }

    // Se ejecuta una vez el doInBackground termina, 
    @Override
    protected void onPostExecute(rutaDirections directionsResult) {
        ArrayList<LatLng> puntos;
        List<PolylineOptions> rutas = new ArrayList<PolylineOptions>();
        PolylineOptions ruta = null;
        List<List<HashMap<String, String>>> directionsJSON = directionsResult.getRutasDirectionsJSON();
        
        // Rutas
        for (int i = 0; i < directionsJSON.size(); i++) {
            puntos = new ArrayList<>();
            ruta = new PolylineOptions();
            List<HashMap<String, String>> path = directionsJSON.get(i);
            // Puntos
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);
                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);
                puntos.add(position);
            }
            // los puntos se agregan a la ruta
            ruta.addAll(puntos);
            if (directionMode.equalsIgnoreCase("walking")) {
                ruta.width(10);
                ruta.color(Color.MAGENTA);
            } else {
                ruta.width(20);
                ruta.color(Color.BLUE);
            }
            Log.v("QUICKSTART", "onPostExecute ruta decodificada");
            rutas.add(ruta);
        }

        // Devolviendo las rutas encontradas
        if (rutas != null) {
            rutasDirections.setRutasDirectionsPolylineOptions(rutas);
            taskCallback.onTaskDone(rutasDirections);
        } else {
            Log.v("QUICKSTART", "no hay rutas");
        }
    }
}
