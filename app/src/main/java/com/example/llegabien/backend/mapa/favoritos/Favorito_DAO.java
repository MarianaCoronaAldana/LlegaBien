package com.example.llegabien.backend.mapa.favoritos;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_FAVORITO;
import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.mongoDB.ConectarBD;
import com.example.llegabien.backend.reporte.reporte;
import com.example.llegabien.backend.usuario.usuario;

import org.bson.types.ObjectId;

import io.realm.Realm;
import io.realm.RealmResults;

public class Favorito_DAO {

    private ConectarBD conectarBD = new ConectarBD();
    private Realm realm;
    private Context mContext;

    public Favorito_DAO(Context context){
        mContext = context;
    }

    public void añadirFavorito(favorito Favorito) {
        Favorito.set_id(new ObjectId());
        Favorito.set_partition("LlegaBien");
        realm = conectarBD.conseguirUsuarioMongoDB();

        if(realm!=null){
            realm.executeTransactionAsync(transactionRealm -> {
                transactionRealm.insert(Favorito);
            });

            realm.close();
        }
        else
            errorConexion();
    }

    // Obtiene todos los objetos favorito asociados al id del usuario
    public RealmResults<favorito> obtenerFavoritosDeUsuario(favorito Favorito) {
        realm = conectarBD.conseguirUsuarioMongoDB();

        if(realm!=null){
            RealmResults<favorito> realmResults = realm.where(favorito.class).equalTo("IdUsuario",Favorito.getIdUsuario()).findAll();

            if (realmResults != null)
                return realmResults;
        }

        else
            errorConexion();

        return null;
    }

    // Obtiene todos los objetos favorito asociados al id del usuario
    public RealmResults<favorito> obtenerFavoritosDeUsuario(usuario Usuario) {
        realm = conectarBD.conseguirUsuarioMongoDB();

        if(realm!=null){
            RealmResults<favorito> realmResults = realm.where(favorito.class).equalTo("IdUsuario",Usuario.get_id()).findAll();

            if (realmResults != null)
                return realmResults;
        }

        else
            errorConexion();

        return null;
    }

    // Devuelve un objeto favorito basado en el id
    public favorito obtenerFavoritoPorId(ObjectId id) {
        realm = conectarBD.conseguirUsuarioMongoDB();
        //realm = conectarBD.ConectarCorreoMongoDB(correo, contrasena);

        if(realm!=null){
            favorito task = realm.where(favorito.class).equalTo("_id", id)
                    .findFirst();

            if (task != null) {
                Log.v("QUICKSTART", "estoy en conseguir favorito por id");
                // Se guarda al usuario en una clase accesible para muchas clases
                Preferences.savePreferenceObjectRealm(mContext, PREFERENCE_FAVORITO, task);
                return task;
            }
        }

        else
            errorConexion();

        return null;
    }

    // Devuelve un objeto favorito basado en el id
    public boolean obtenerFavoritoPorNombre_Id(ObjectId id, String nombre) {
        realm = conectarBD.conseguirUsuarioMongoDB();

        if(realm!=null){
            favorito task = realm.where(favorito.class).equalTo("IdUsuario", id)
                    .and()
                    .equalTo("nombre", nombre)
                    .findFirst();

            if (task != null) {
                return true;
            }
            return false;
        }

        else
            errorConexion();

        return false;
    }


    private void errorConexion(){
        Toast.makeText(mContext, "Hubo un problema en conectarse, intenta mas tarde", Toast.LENGTH_SHORT).show();
    }
}
