package com.example.llegabien.backend.mapa.ubicacion;

import android.app.Activity;
import android.location.Location;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.llegabien.R;
import com.example.llegabien.backend.app.Permisos;
import com.example.llegabien.frontend.app.fragmento.FragmentoPermisos;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

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

    public void mostrarStringUbicacionActual(Activity activity, Button button, Fragment fragment) {
        Permisos permisos = new Permisos();
        permisos.getPermisoUbicacion(activity, false);
        if (permisos.getLocationPermissionGranted()) {
            FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);

            UbicacionDispositivo mUbicacionDispositivo = new UbicacionDispositivo();
            mUbicacionDispositivo.getUbicacionDelDispositivo((isUbicacionObtenida, ubicacionObtenida) -> {
                if (isUbicacionObtenida) {
                    UbicacionGeocodificacion ubicacionGeocodificacion = new UbicacionGeocodificacion(activity);
                    String Ubicacion = ubicacionGeocodificacion.degeocodificarUbiciacion(ubicacionObtenida.getLatitude(),
                            ubicacionObtenida.getLongitude());
                    button.setText(Ubicacion);
                }
            }, true, fusedLocationProviderClient, activity);
        }else {
            FragmentTransaction fragmentTransaction = fragment.requireActivity().getSupportFragmentManager().beginTransaction();
            FragmentoPermisos fragmentoPermisos = new FragmentoPermisos();
            fragmentTransaction.add(R.id.fragmentContainerView_reportes, fragmentoPermisos).commit();
        }
    }
}
