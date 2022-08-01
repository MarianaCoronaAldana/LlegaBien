package com.example.llegabien.frontend.reportes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.llegabien.backend.usuario.UsuarioDAO;
import com.example.llegabien.backend.usuario.usuario;

public class VerificarReportesSemanales extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        UsuarioDAO u = new UsuarioDAO(context);
        usuario usuario = u.readUsuarioPorCorreo("llegabienapp@gmail.com");
        Toast.makeText(context, "HOLA CRAYOLA",Toast.LENGTH_LONG).show();
        Log.v("QUICKSTART", "WE ESTOY EN EL INTENT!");
        Log.v("QUICKSTART", "Nombre de usuario conseguido: " + usuario.getNombre());
    }
}
