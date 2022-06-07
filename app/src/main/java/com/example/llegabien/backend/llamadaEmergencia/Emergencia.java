package com.example.llegabien.backend.llamadaEmergencia;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.llegabien.R;
import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.usuario.usuario;

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

    public Emergencia(Context context){
        mContext = context;
    }

    public void EmpezarProtocolo(){
        InicializarDatos();

        try {
            //TODO Cambiar link de ngrok aqui
            post("https://d46b-2806-103e-29-a8e5-a4ec-7b6d-8cae-76c3.ngrok.io/emergencia", new  Callback(){
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
        for(int i = 0; i<Usuario.getContacto().size(); i++)
            mContactos.add("+52" + Usuario.getContacto().get(i).getTelCelular());
        for(int i = mContactos.size(); i<5; i++)
            mContactos.add("-1");
        mNombre = Usuario.getNombre() + " " + Usuario.getApellidos();
        mUbicacion = "Guadalajara";
    }

    private void HacerToast(String texto){
        int icon = R.drawable.bkgd_icon_emergencia;        // icon from resources
        CharSequence tickerText = "Hello";              // ticker-text
        long when = System.currentTimeMillis();         // notification time
        Context context = getApplicationContext();      // application Context
        CharSequence contentTitle = "My notification";  // expanded message title
        CharSequence contentText = "Hello World!";      // expanded message text

        Intent notificationIntent = new Intent(this, Emergencia.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        // the next two lines initialize the Notification, using the configurations above
        Notification notification = new Notification(icon, tickerText, when);
        //notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
    }
}