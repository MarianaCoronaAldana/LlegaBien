package com.example.llegabien.backend.botonEmergencia;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.mapa.ubicacion.UbicacionDispositivo;
import com.example.llegabien.backend.mapa.ubicacion.UbicacionGeodicacion;
import com.example.llegabien.backend.usuario.usuario;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Emergencia extends AppCompatActivity {

    private ArrayList<String> mContactos = new ArrayList<String>();
    private OkHttpClient mClient = new OkHttpClient();
    private String mNombre, mUbicacion;
    private Context mContext;
    private usuario Usuario;

    private Activity mActivity;
    private boolean mIsLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    public Emergencia(Context context){
        mContext = context;
    }

    public Emergencia(Context context, Activity mActivity, boolean mIsLocationPermissionGranted, FusedLocationProviderClient mFusedLocationProviderClient) {
        this.mActivity = mActivity;
        this.mIsLocationPermissionGranted = mIsLocationPermissionGranted;
        this.mFusedLocationProviderClient = mFusedLocationProviderClient;
        mContext = context;
    }

    public void EmpezarProtocolo(){
        InicializarDatos();

        try {
            //TODO Cambiar link de ngrok aqui
            post("https://128d-187-201-47-211.ngrok.io/emergencia", new  Callback(){
            //post("https://6d3a-2806-103e-29-a92b-c89d-16d6-3cf0-69a1.ngrok/", new  Callback(){
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext,response.message(),Toast.LENGTH_SHORT).show();
                            Log.v("QUICKSTART", response.message());

                            if(!response.isSuccessful())
                                Toast.makeText(mContext, "ERROR, CONTACTE A EMERGENCIAS DIRECTAMENTE!",Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Call post(String url, Callback callback) throws IOException {
        Log.v("QUICKSTART", "Contacto 1: " + Usuario.getContacto().first().getTelCelular());

        RequestBody formBody = new FormBody.Builder()
                .add("NombreUsuario", mNombre)
                .add("Ubicacion", mUbicacion)
                .add("NumeroALlamar", "+523321707532")
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
        /*for(int i = 0; i<Usuario.getContacto().size(); i++)
            mContactos.add("+52" + Usuario.getContacto().get(i).getTelCelular());*/
        for(int i = mContactos.size(); i<5; i++)
            mContactos.add("-1");
        mNombre = Usuario.getNombre() + " " + Usuario.getApellidos();
        //ubicacionDispositivoString();
        mUbicacion = "Guadalajara";
    }

    public void ubicacionDispositivoString() {
        UbicacionDispositivo mUbicacionDispositivo = new UbicacionDispositivo();
        mUbicacionDispositivo.getUbicacionDelDispositivo(new UbicacionDispositivo.OnUbicacionObtenida() {
            @Override
            public void isUbicacionObtenida(boolean isUbicacionObtenida, Location ubicacionObtenida) {
                if (isUbicacionObtenida) {
                    UbicacionGeodicacion ubicacionGeodicacion = new UbicacionGeodicacion();
                    mUbicacion = ubicacionGeodicacion.degeocodificarUbiciacion(mActivity, ubicacionObtenida.getLatitude(),ubicacionObtenida.getLongitude());
                }
                else {
                    Toast.makeText(mContext, "ERROR, CONTACTE A EMERGENCIAS DIRECTAMENTE!",Toast.LENGTH_LONG).show();
                }
            }
        }, mIsLocationPermissionGranted,mFusedLocationProviderClient, mActivity);
    }
}