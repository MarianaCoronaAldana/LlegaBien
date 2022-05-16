package com.example.llegabien.backend.mapa.ubicacion;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static com.example.llegabien.backend.app.Preferences.PREFERENCE_UBICACION_BUSQUEDA_AUTOCOMPLETADA;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.llegabien.backend.app.Preferences;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

public class UbicacionBusquedaAutocompletada {

    private Fragment mFragmento;
    private Intent mIntent;
    
    public UbicacionBusquedaAutocompletada(Fragment fragmento){
        mFragmento = fragmento;
    }
    public Intent getmIntent(){ return mIntent; }
    
    public void inicializarIntent() {
        // Se configura los campos para especificar qué tipos de datos de lugar devolver.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

        // Se inicializa el intent y se abre la actividad de autocomplete
        mIntent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields).setCountry("MX")
                .build(mFragmento.getActivity());

    }
    
    public void verificarResultadoBusqueda (int resultCode, Intent data){
        if (resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + ", " + place.getAddress());
            //Toast.makeText(mFragmento.getActivity(), "ID: " + place.getId() + "address:" + place.getAddress() + "Name:" + place.getName() + " latlong: " + place.getLatLng(), Toast.LENGTH_LONG).show();
            String address = place.getAddress();
            Preferences.savePreferenceString(mFragmento.getActivity(),address,PREFERENCE_UBICACION_BUSQUEDA_AUTOCOMPLETADA);
        }

        else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            // TODO: Handle the error.
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(mFragmento.getActivity(), "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
            Log.i(TAG, status.getStatusMessage());
        }

        else if (resultCode == RESULT_CANCELED) {
            // The user canceled the operation.
        }
    }


}
