package com.example.llegabien.backend.app;

import static io.realm.Realm.getApplicationContext;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Permisos {

    private boolean mLocationPermissionGranted = false;

    public void setLocationPermissionGranted(boolean locationPermissionGranted) {
        mLocationPermissionGranted = locationPermissionGranted;
    }

    public boolean getLocationPermissionGranted() {
        return mLocationPermissionGranted;
    }


    public void getPermisoUbicacion(Activity activity, boolean isInActivity) {
        if (ContextCompat.checkSelfPermission(activity.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            if (isInActivity)
                ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }

    public void HacerLlamada(Context context) {
        int permiso = ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);
        if (permiso == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, 1);
        }
    }

    public void EnviarMensaje(Context context) {
        int permiso = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS);
        if (permiso == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.SEND_SMS}, 2);

        }
    }

}