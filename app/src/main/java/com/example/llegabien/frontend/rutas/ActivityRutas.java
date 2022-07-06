package com.example.llegabien.frontend.rutas;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.llegabien.frontend.rutas.directionhelpers.FetchURL;
import com.example.llegabien.frontend.rutas.directionhelpers.TaskLoadedCallback;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class ActivityRutas extends AppCompatActivity implements TaskLoadedCallback {

    private MarkerOptions place1, place2;
    private Context mContext;

    public ActivityRutas(Context context){
        mContext = context;
        PRUEBA();
    }

    private void PRUEBA(){
        //27.658143,85.3199503
        //20.6674235372583, -103.31179439549422

        //27.667491,85.3208583
        //20.67097726320246, -103.31441214692855
        place1 = new MarkerOptions().position(new LatLng(20.6674235372583, -103.31179439549422)).title("Location 1");
        place2 = new MarkerOptions().position(new LatLng(20.67097726320246, -103.31441214692855)).title("Location 2");

        new FetchURL(ActivityRutas.this).execute(getUrl(place1.getPosition(), place2.getPosition(), "walking"), "walking");
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters +"&alternatives=true" +"&key=" + "AIzaSyA_1vA9ikwZ0Ju7s5s2-C1QLQ51BQmwSlM";
        Log.v("QUICKSTART", "url: ");
        Log.v("QUICKSTART", url);
        //getString(R.string.api_key)
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        PolylineOptions a = (PolylineOptions) values[0];
        Log.v("QUICKSTART", "PRIMER PUNTO DEL POLILINE: "+ a.getPoints().get(0).toString());
    }
}