package com.example.llegabien.backend.reporte;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.llegabien.backend.ubicacion.UbicacionSeguridad;
import com.example.llegabien.backend.usuario.UsuarioDAO;
import com.example.llegabien.backend.usuario.usuario;

public class VerificarReportesSemanales extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        UsuarioDAO u = new UsuarioDAO(context);
       /* usuario usuario = u.readUsuarioPorCorreo("llegabienapp@gmail.com");
        Toast.makeText(context, "HOLA CRAYOLA",Toast.LENGTH_LONG).show();*/
        Log.v("QUICKSTART", "WE ESTOY EN EL INTENT!");

        //Log.v("QUICKSTART", "Nombre de usuario conseguido: " + usuario.getNombre());
        UbicacionSeguridad ubicacionSeguridad = new UbicacionSeguridad(context);
        ubicacionSeguridad.actualizarUbicacionesCalculoSeguridadSemanal();
    }
}
