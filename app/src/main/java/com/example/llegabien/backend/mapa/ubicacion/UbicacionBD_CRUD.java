package com.example.llegabien.backend.mapa.ubicacion;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_UBICACION;
import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;

import static io.realm.Realm.getApplicationContext;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.favoritos.favorito;
import com.example.llegabien.backend.mongoDB.ConectarBD;
import com.example.llegabien.backend.usuario.usuario;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.mongodb.sync.SyncConfiguration;

public class UbicacionBD_CRUD {
    Realm realm;
    ConectarBD conectarBD = new ConectarBD();

    public RealmResults<ubicacion> obetenerUbicaciones() {
        realm = conectarBD.ConectarAnonimoMongoDB();

        if(!realm.isEmpty()) {

            RealmResults<ubicacion> realmResults = realm.where(ubicacion.class).findAll();

            if (realmResults != null) {
                Log.v("QUICKSTART", "OMG SI SE PUDO");
                // Se guarda al usuario en una clase accesible para muchas clases

                return realmResults;
            } else {
                Log.v("QUICKSTART", "NO SE PUDO");
            }
        }

        else
            errorConexion();

        return null;
    }

    public ubicacion obetenerUbicacionConPoligono(String coordenadasPoligono, Context c) {
        realm = conectarBD.ConectarAnonimoMongoDB();

        if(!realm.isEmpty()) {

            ubicacion task = realm.where(ubicacion.class).equalTo("poligono", coordenadasPoligono).findFirst();

            if (task != null) {
                Log.v("QUICKSTART", "OMG SI SE PUDO");
                // Se guarda al ubicacion en una clase accesible para muchas clases
                Preferences.savePreferenceObject(c, PREFERENCE_UBICACION, task);

                return task;
            } else {
                Log.v("QUICKSTART", "NO SE PUDO");
            }
        }

        else
            errorConexion();

        return null;
    }

    private void errorConexion(){
        Toast.makeText(getApplicationContext(), "Hubo un problema en conectarse, intenta mas tarde", Toast.LENGTH_SHORT).show();
    }
}

