package com.example.llegabien.frontend.mapa.rutas.directionhelpers;

import com.example.llegabien.backend.ruta.directions.RutaDirections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {
    
    public RutaDirections parser(JSONObject jObject) {
        List<List<HashMap<String, String>>> routes = new ArrayList<>();
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;
        RutaDirections directions = new RutaDirections();
        List <String> distancias = new ArrayList(), duraciones = new ArrayList();
        try {
            jRoutes = jObject.getJSONArray("routes");
            // Recorriendo todas las rutas
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<>();
                // Recorriendo todas las legs
                for (int j = 0; j < jLegs.length(); j++) {
                    distancias.add ((String) ((JSONObject) ((JSONObject) jLegs.get(j)).get("distance")).get("text"));
                    duraciones.add ((String) ((JSONObject) ((JSONObject) jLegs.get(j)).get("duration")).get("text"));

                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                    // Recorriendo todos los steps
                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyline = "";
                        HashMap<String, String> hm = new HashMap<>();
                        String lat = Double.toString((Double) ((JSONObject) ((JSONObject) jSteps.get(k)).get("start_location")).get("lat"));
                        String lng = Double.toString((Double) ((JSONObject) ((JSONObject) jSteps.get(k)).get("start_location")).get("lng"));
                        hm.put("lat", lat);
                        hm.put("lng", lng);
                        path.add(hm);
                        //Log.v("QUICKSTART", "HAsh punto inicial " + hm);
                        //Log.v("QUICKSTART", "Paso "+ k + " de ruta " + i);
                    }
                    routes.add(path);
                    //Log.v("QUICKSTART", "path size: " + path.size());
                    //Log.v("QUICKSTART", "ruta size: " + routes.size());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }
        directions.setDistancia(distancias);
        directions.setDuracion(duraciones);
        directions.setRutasDirectionsJSON(routes);
        return directions;
    }
}