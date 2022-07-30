package com.example.llegabien.backend.ubicacion;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.util.Log;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class UbicacionBusquedaAutocompletada {

    private Intent mIntent;

    public interface OnUbicacionBuscadaObtenida {
        void isUbicacionBuscadaObtenida(boolean isUbicacionBuscadaObtenida, boolean isUbicacionBuscadaEnBD, LatLng ubicacionBuscada, String ubicacionBuscadaString);
    }


    public UbicacionBusquedaAutocompletada(){
    }

    public Intent getIntent(){ return mIntent; }

    public void inicializarIntent(Activity activity) {
        // Se configura los campos para especificar qué tipos de datos de lugar devolver.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

        // Se inicializa el intent y se abre la actividad de autocomplete
        mIntent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields).setCountry("MX")
                .build(activity);
    }
    
    public void verificarResultadoBusqueda (OnUbicacionBuscadaObtenida onUbicacionBuscadaObtenida, int resultCode, Intent data, Context context){
        if (resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);

            UbicacionDAO ubicacionDAO = new UbicacionDAO(context);
            boolean isUbicacionBuscadaEnBD = ubicacionDAO.obtenerUbicacionBuscada(Objects.requireNonNull(place.getLatLng()).latitude,place.getLatLng().longitude);
            onUbicacionBuscadaObtenida.isUbicacionBuscadaObtenida(true, isUbicacionBuscadaEnBD,place.getLatLng(), place.getAddress());
        }

        else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            // TODO: Handle the error.
            Status status = Autocomplete.getStatusFromIntent(data);
            onUbicacionBuscadaObtenida.isUbicacionBuscadaObtenida(false, false,null, null);
            Log.i(TAG, status.getStatusMessage());
        }

        else if (resultCode == RESULT_CANCELED) {
            onUbicacionBuscadaObtenida.isUbicacionBuscadaObtenida(false, false,null, null);
        }
    }

    public void verificarResultadoBusqueda (OnUbicacionBuscadaObtenida onUbicacionBuscadaObtenida, int resultCode, Intent data){
        if (resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            onUbicacionBuscadaObtenida.isUbicacionBuscadaObtenida(true, false,place.getLatLng(), place.getAddress());
        }

        else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
            onUbicacionBuscadaObtenida.isUbicacionBuscadaObtenida(false, false,null, null);
            Log.i(TAG, status.getStatusMessage());
        }

        else if (resultCode == RESULT_CANCELED) {
            onUbicacionBuscadaObtenida.isUbicacionBuscadaObtenida(false, false,null, null);
        }
    }
}
