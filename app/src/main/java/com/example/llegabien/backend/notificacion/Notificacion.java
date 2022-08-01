package com.example.llegabien.backend.notificacion;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_MENSAJE_BATERIA_ENVIADO;
import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;
import android.widget.Toast;
import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.usuario.usuario;
import com.example.llegabien.frontend.notificacion.dialog.DialogNotificacionBateria;

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

    private final ArrayList<String> mContactos = new ArrayList<>();
    private final OkHttpClient mClient = new OkHttpClient();
    private String mNombre;
    private final Context mContext;
    private usuario Usuario;
    private float mBateria;
    private Activity mActivity;

    public Notificacion(float Bateria, Context c) {
        mBateria = Bateria;
        mContext = c;
    }

    public Notificacion(Context context, Activity activity) {
        mContext = context;
        mActivity = activity;
    }

    public void monitorearBateria() {
        // Se verifica el nivel de bateria del celular, si es menor a 21%, se hace un protocolo
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = mContext.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        mBateria = level * 100 / (float) scale;
        Log.v("QUICKSTART", "Nivel bateria: " + mBateria);

        if (mBateria < 21) {
            if (!Preferences.getSavedBooleanFromPreference(mContext, PREFERENCE_MENSAJE_BATERIA_ENVIADO)) {
                DialogNotificacionBateria dialogNotificacionBateria = new DialogNotificacionBateria(mActivity, mBateria);
                dialogNotificacionBateria.show();
            }
        } else
            Preferences.savePreferenceBoolean(mContext, false, PREFERENCE_MENSAJE_BATERIA_ENVIADO);
    }


    public void empezarProtocolo() {
        inicializarDatos();
        if (Usuario.getContacto().isEmpty())
            Toast.makeText(mContext, "No tienes contactos de emergencia.", Toast.LENGTH_SHORT).show();
        else {
            try {
                //TODO Cambiar link de ngrok aqui
                post(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.v("QUICKSTART", "NO SE PUDO CONTACTAR PARA NOTIFICAR WE, ESTOY EN ON FAILURE.");
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) {
                        runOnUiThread(() -> {
                            Log.v("QUICKSTART", response.message());
                            Preferences.savePreferenceBoolean(mContext, true, PREFERENCE_MENSAJE_BATERIA_ENVIADO);

                            if (!response.isSuccessful()) {
                                Toast.makeText(mContext, "NO SE PUDO NOTIFICAR A CONTACTOS POR BATERIA.", Toast.LENGTH_LONG).show();
                                Log.v("QUICKSTART", "NO SE PUDO NOTIFICAR A CONTACTOS POR BATERIA.");
                            }
                        });
                    }
                });
            } catch (IOException e) {
                Log.v("QUICKSTART", "NO SE PUDO CONTACTAR PARA NOTIFICAR WE, ESTOY EN CATCH");
                e.printStackTrace();
            }
        }
    }

    private void post(Callback callback) throws IOException {
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
                .url("https://8451-2806-103e-29-8948-30d2-28d-935a-664a.ngrok.io/bateria")
                .post(formBody)
                .build();
        Call response = mClient.newCall(request);
        response.enqueue(callback);

    }

    private void inicializarDatos() {
        Usuario = Preferences.getSavedObjectFromPreference(mContext, PREFERENCE_USUARIO, usuario.class);
        if (Usuario != null) {
            for (int i = 0; i < Usuario.getContacto().size(); i++)
                mContactos.add("+" + Usuario.getContacto().get(i).getTelCelular());
            for (int i = mContactos.size(); i < 5; i++)
                mContactos.add("-1");
            mNombre = Usuario.getNombre() + " " + Usuario.getApellidos();
        }
    }
}