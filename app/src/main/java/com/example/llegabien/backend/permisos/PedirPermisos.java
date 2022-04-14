package com.example.llegabien.backend.permisos;

import static io.realm.Realm.getApplicationContext;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PedirPermisos {

    //Esta funcion por si sola se encarga de verificar si el dispositivo cuenta con unos permisos
    //en especifico, si no los tiene, los pide.
    public void PedirTodosPermisos(Context context){
        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS }, 0);
    }

    public void getLocalizacion(Context context) {

        int permiso = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);

        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS }, 0);

        if(permiso == PackageManager.PERMISSION_DENIED){
           if(ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.ACCESS_FINE_LOCATION)){
            }else{
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS }, 0);
            }
        }
    }

    public void HacerLlamada(Context context) {
        int permiso = ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);
        if(permiso == PackageManager.PERMISSION_DENIED){

                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, 1);
        }
    }

    public void EnviarMensaje(Context context) {
        int permiso = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS);
        if(permiso == PackageManager.PERMISSION_DENIED){

                ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.SEND_SMS}, 2);

        }
    }

}
