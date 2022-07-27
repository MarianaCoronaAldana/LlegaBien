package com.example.llegabien.frontend.rutas.directionhelpers;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.llegabien.backend.ruta.EvaluacionRuta;
import com.example.llegabien.backend.ruta.directions.RutaDirections;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PointsParser extends AsyncTask<String, Integer, RutaDirections> {
    TaskLoadedCallback taskCallback, taskLoaderFragmento;
    String directionMode;
    RutaDirections rutasDirections = new RutaDirections();
    Context mContext;

    public PointsParser(TaskLoadedCallback taskCallbackActivity, TaskLoadedCallback taskCallbackFragmento, String directionMode, Context c) {
        this.taskCallback =  taskCallbackActivity;
        this.taskLoaderFragmento = taskCallbackFragmento;
        this.directionMode = directionMode;
        this.mContext = c;
    }


    // Se encarga de parsear la info
    @Override
    protected RutaDirections doInBackground(String... jsonData) {
        JSONObject jObject;
        List<List<HashMap<String, String>>> rutas = null;
        try {
            jObject = new JSONObject(jsonData[0]);
            Log.v("QUICKSTART", jsonData[0]);
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
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onPostExecute(RutaDirections directionsResult) {
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
                Log.v("QUICKSTART", "Punto "+ j + " de ruta " + i + " valor: " + position);
            }
            // los puntos se agregan a la ruta
            ruta.addAll(puntos);
            Log.v("QUICKSTART", "onPostExecute ruta decodificada");
            rutas.add(ruta);
        }

        // Devolviendo las rutas encontradas
        if (rutas != null) {
            rutasDirections.setRutasDirectionsPolylineOptions(rutas);
            EvaluacionRuta evaluacionRuta = new EvaluacionRuta(mContext, taskCallback, taskLoaderFragmento);
            evaluacionRuta.inicializarAtributosMiembro(rutasDirections);
            evaluacionRuta.execute();
            //taskCallback.onTaskDone(rutasDirections);
        } else {
            Log.v("QUICKSTART", "no hay rutas");
        }
    }
}
