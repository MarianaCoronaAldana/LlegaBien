package com.example.llegabien.frontend.rutas.directionhelpers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchURL extends AsyncTask<String, Void, String> {
    TaskLoadedCallback taskLoader, taskLoaderFragmento;
    String directionMode = "driving";
    Context context;

    public FetchURL(TaskLoadedCallback Context) {
        this.taskLoader = Context;
    }

    public FetchURL(TaskLoadedCallback task, Context context) {
        this.taskLoader = task;
        this.context = context;
    }

    public FetchURL(TaskLoadedCallback taskActivity, TaskLoadedCallback taskFragmento, Context context) {
        this.taskLoader = taskActivity;
        this.taskLoaderFragmento = taskFragmento;
        this.context = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        // Aqui se guardará lo recibido de Directions
        String data = "";
        directionMode = strings[1];
        try {
            // Obteniendo info de Directions
            data = downloadUrl(strings[0]);
            Log.v("QUICKSTART", "Background: " + data.toString());
        } catch (Exception e) {
            Log.d("Background: ", e.toString());
        }
        return data;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        PointsParser parserTask = new PointsParser(taskLoader, taskLoaderFragmento, directionMode, context);
        // Para parsear la info de JSON
        parserTask.execute(s);
    }

    private String downloadUrl(String strUrl) throws IOException {
        String datos = "";
        InputStream iStream = null;
        HttpURLConnection urlConexion = null;
        try {
            URL url = new URL(strUrl);
            // Creando conexión http para conectarse con la url
            urlConexion = (HttpURLConnection) url.openConnection();
            // Conectando a url
            urlConexion.connect();
            // Obteniendo datos url
            iStream = urlConexion.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String linea = "";
            while ((linea = br.readLine()) != null) {
                sb.append(linea);
            }
            datos = sb.toString();
            Log.v("QUICKSTART", "URL descargada: " + datos.toString());
            br.close();
        } catch (Exception e) {
            Log.v("QUICKSTART", "Exception mientras se decaragaba URL: " + e.toString());
        } finally {
            iStream.close();
            urlConexion.disconnect();
        }
        return datos;
    }
}