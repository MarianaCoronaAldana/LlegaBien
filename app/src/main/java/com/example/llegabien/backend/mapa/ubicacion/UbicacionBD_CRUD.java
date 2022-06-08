package com.example.llegabien.backend.mapa.ubicacion;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_UBICACION;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.mongoDB.ConectarBD;

import io.realm.Realm;
import io.realm.RealmResults;

public class UbicacionBD_CRUD {
    Realm realm;
    ConectarBD conectarBD = new ConectarBD();
    private Context mContext;

    public UbicacionBD_CRUD(Context context){
        mContext = context;
    }

    public RealmResults<ubicacion> obetenerColonias() {
        realm = conectarBD.conseguirUsuarioMongoDB();

        if(realm!=null){
            RealmResults<ubicacion> realmResults = realm.where(ubicacion.class).equalTo("tipo","colonia").findAll();

            if (realmResults != null)
                return realmResults;
        }

        else
            errorConexion();

        return null;
    }

    public RealmResults<ubicacion> obetenerMunicipios() {
        realm = conectarBD.conseguirUsuarioMongoDB();

        if(realm!=null){
            RealmResults<ubicacion> realmResults = realm.where(ubicacion.class).equalTo("tipo","municipio").findAll();

            if (realmResults != null)
                return realmResults;

        }

        else
            errorConexion();

        return null;
    }

    public RealmResults<ubicacion> obetenerCalles() {
        realm = conectarBD.conseguirUsuarioMongoDB();

        if(realm!=null){
            RealmResults<ubicacion> realmResults = realm.where(ubicacion.class).equalTo("tipo","calle").findAll();

            if (realmResults != null)
                return realmResults;
        }

        else
            errorConexion();

        return null;
    }


    public void obetenerUbicacionConPoligono(String coordenadasPoligono, Context c) {
        realm = conectarBD.conseguirUsuarioMongoDB();

        if(realm!=null){

            ubicacion task = realm.where(ubicacion.class).equalTo("coordenadas_string", coordenadasPoligono).findFirst();

            if (task != null) {
                // Se guarda la ubicacion en Shared Preferences
                Preferences.savePreferenceObjectRealm(c, PREFERENCE_UBICACION, task);
            } 
        }

        else
            errorConexion();

    }



    private void errorConexion(){
        Toast.makeText(mContext, "Hubo un problema en conectarse, intenta mas tarde", Toast.LENGTH_SHORT).show();
    }
}

