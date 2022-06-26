package com.example.llegabien.backend.mapa.ubicacion;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.Executor;

public class UbicacionDispositivo{

    private Location mUbicacion;

    public interface OnUbicacionObtenida {
        void isUbicacionObtenida(boolean isUbicacionObtenida, Location ubicacionObtenida);
    }

    public void getUbicacionDelDispositivo(OnUbicacionObtenida onUbicacionObtenida, boolean isLocationPermissionGranted, FusedLocationProviderClient fusedLocationProviderClient, Activity activity){
        try {
            if (isLocationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(activity, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mUbicacion = task.getResult();
                            onUbicacionObtenida.isUbicacionObtenida(true, mUbicacion);
                        } else {
                            Log.d("LOCATION NULL", "Current location is null. Using defaults.");
                            Log.e("TASK EXCEPTION", "Exception: %s", task.getException());
                            onUbicacionObtenida.isUbicacionObtenida(false, mUbicacion);
                        }
                    }
                });
            }
            else
                onUbicacionObtenida.isUbicacionObtenida(false, mUbicacion);

        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }
}
