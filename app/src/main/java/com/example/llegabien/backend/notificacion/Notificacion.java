package com.example.llegabien.backend.notificacion;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.BatteryManager;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.mapa.ubicacion.UbicacionDispositivo;
import com.example.llegabien.backend.mapa.ubicacion.UbicacionGeodicacion;
import com.example.llegabien.backend.usuario.usuario;
import com.google.android.gms.location.FusedLocationProviderClient;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Notificacion extends AppCompatActivity {

    private ArrayList<String> mContactos = new ArrayList<String>();
    private OkHttpClient mClient = new OkHttpClient();
    private String mNombre;
    private Context mContext;
    private usuario Usuario;
    private float mBateria;


    private Activity mActivity;
    private boolean mIsLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    public Notificacion(Context context){
        mContext = context;

        // Se verifica el nivel de bateria del celular, si es menor a 21%, se hace un protocolo
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = mContext.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        mBateria = level * 100 / (float)scale;
        Log.v("QUICKSTART", "Nivel bateria: " + mBateria);

        if(mBateria<21){
            EmpezarProtocolo();
        }
    }

    public void EmpezarProtocolo(){
        InicializarDatos();

        try {
            //TODO Cambiar link de ngrok aqui
            post("https://c0af-200-68-166-53.ngrok.io/bateriaS", new  Callback(){
//            post("https://c0af-200-68-166-53.ngrok.io/bateria", new  Callback(){
                @Override
                public void onFailure(Call call, IOException e) {
                   // Toast.makeText(mContext, "ERROR, NO SE PUDO CONTACTAR A CONTACTOS", Toast.LENGTH_LONG).show();
                    Log.v("QUICKSTART", "NO SE PUDO CONTACTAR PARA NOTIFICAR WE, ESTOY EN on failure");
                    e.printStackTrace();
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext,"MENOS DEL 20% DE BATERIA, CUIDADO",Toast.LENGTH_LONG).show();
                            Log.v("QUICKSTART", response.message());

                            if(!response.isSuccessful()) {
                                Toast.makeText(mContext, "ERROR, NO SE PUDO CONTACTAR A CONTACTOS", Toast.LENGTH_LONG).show();
                                Log.v("QUICKSTART", "NO SE PUDO CONTACTAR PARA NOTIFICAR WE");
                            }
                        }
                    });
                }
            });
        } catch (IOException e) {
            Log.v("QUICKSTART", "NO SE PUDO CONTACTAR PARA NOTIFICAR WE, ESTOY EN CATCH");
            e.printStackTrace();
        }
    }

    Call post(String url, Callback callback) throws IOException {
        Log.v("QUICKSTART", "Contacto 1: " + Usuario.getContacto().first().getTelCelular());

        RequestBody formBody = new FormBody.Builder()
                .add("NombreUsuario", mNombre)
                .add("Bateria", String.valueOf(mBateria))
                .add("CantidadContactos", String.valueOf(mContactos.size()))
                .add("Contacto1", mContactos.get(0))
                .add("Contacto2", mContactos.get(1))
                .add("Contacto3", mContactos.get(2))
                .add("Contacto4", mContactos.get(3))
                .add("Contacto5", mContactos.get(4))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        Call response = mClient.newCall(request);
        response.enqueue(callback);

        return response;
    }

    private void InicializarDatos(){
        Usuario = Preferences.getSavedObjectFromPreference(mContext, PREFERENCE_USUARIO, usuario.class);
        for(int i = 0; i<Usuario.getContacto().size(); i++)
            mContactos.add("+" + Usuario.getContacto().get(i).getTelCelular());
        for(int i = mContactos.size(); i<5; i++)
            mContactos.add("-1");
        mNombre = Usuario.getNombre() + " " + Usuario.getApellidos();
    }
}