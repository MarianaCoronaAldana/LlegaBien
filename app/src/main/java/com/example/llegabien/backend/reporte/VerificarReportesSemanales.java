package com.example.llegabien.backend.reporte;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_ALARMANAGER_CONFIGURADO;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.ubicacion.UbicacionSeguridad;

public class VerificarReportesSemanales extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("QUICKSTART", "WE ESTOY EN EL INTENT!");
        UbicacionSeguridad ubicacionSeguridad = new UbicacionSeguridad(context);
        ubicacionSeguridad.actualizarUbicacionesCalculoSeguridadSemanal();
    }
}
