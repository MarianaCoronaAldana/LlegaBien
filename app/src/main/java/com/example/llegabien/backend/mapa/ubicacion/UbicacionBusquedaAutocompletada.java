package com.example.llegabien.backend.mapa.ubicacion;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_UBICACION;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.mapa.poligonos.Poligono;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.maps.android.PolyUtil;

import org.bson.types.ObjectId;

import java.util.Arrays;
import java.util.List;

import io.realm.RealmResults;

public class UbicacionBusquedaAutocompletada {

    private Intent mIntent;
    private RealmResults<ubicacion> mResultadosUbicaciones;
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
    
    public void verificarResultadoBusqueda (OnUbicacionBuscadaObtenida onUbicacionBuscadaObtenida, int resultCode, Intent data, Context c){
        if (resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + ", " + place.getAddress());
            String address = place.getAddress();

            UbicacionGeodicacion ubicacionGeodicacion = new UbicacionGeodicacion();
            Address ubicacionGeocodificada = ubicacionGeodicacion.geocodificarUbiciacion(c, address);
            LatLng ubicacionBuscada = new LatLng(ubicacionGeocodificada.getLatitude(), ubicacionGeocodificada.getLongitude());

            boolean isUbicacionBuscadaEnBD = getUbicacionBuscada(c,ubicacionBuscada.latitude,ubicacionBuscada.longitude);
            onUbicacionBuscadaObtenida.isUbicacionBuscadaObtenida(true, isUbicacionBuscadaEnBD,ubicacionBuscada, address);

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

    private boolean getUbicacionBuscada(Context c, double latitude, double longitude){
        Poligono poligono = new Poligono();
        ubicacion ubicacion;

        UbicacionBD_CRUD ubicacionBD_CRUD = new UbicacionBD_CRUD(c);

        mResultadosUbicaciones = ubicacionBD_CRUD.obetenerCalles();
        if(mResultadosUbicaciones != null){ }

        mResultadosUbicaciones = ubicacionBD_CRUD.obetenerColonias();
        if(mResultadosUbicaciones != null){
            ubicacion = poligono.isUbicacionEnPoligono(mResultadosUbicaciones,latitude, longitude);
            if (ubicacion != null) {
                Preferences.savePreferenceObjectRealm(c, PREFERENCE_UBICACION, ubicacion);
                return true;
            }
        }

        mResultadosUbicaciones = ubicacionBD_CRUD.obetenerMunicipios();
        if(mResultadosUbicaciones != null){
            ubicacion = poligono.isUbicacionEnPoligono(mResultadosUbicaciones,latitude, longitude);
            if (ubicacion != null) {
                Preferences.savePreferenceObjectRealm(c, PREFERENCE_UBICACION, ubicacion);
                return true;
            }
        }

        return false;
    }


}
