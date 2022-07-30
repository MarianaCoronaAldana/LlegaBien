package com.example.llegabien.backend.favoritos;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_FAVORITO;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.mongoDB.ConectarBD;
import com.example.llegabien.backend.usuario.usuario;

import org.bson.types.ObjectId;

import io.realm.Realm;
import io.realm.RealmResults;

public class Favorito_DAO {

    private final ConectarBD conectarBD = new ConectarBD();
    private Realm realm;
    private final Context mContext;

    public Favorito_DAO(Context context) {
        mContext = context;
    }

    public void anadirFavorito(favorito Favorito) {
        Favorito.set_id(new ObjectId());
        Favorito.set_partition("LlegaBien");
        realm = conectarBD.conseguirUsuarioMongoDB();

        if (realm != null) {
            realm.executeTransactionAsync(transactionRealm -> transactionRealm.insert(Favorito));
            realm.close();
        } else
            errorConexion();
    }

    // Obtiene todos los objetos favorito asociados al id del usuario
    public RealmResults<favorito> obtenerFavoritosDeUsuario(usuario Usuario) {
        realm = conectarBD.conseguirUsuarioMongoDB();

        if (realm != null) {
            return realm.where(favorito.class).equalTo("IdUsuario", Usuario.get_id()).findAll();
        } else
            errorConexion();

        return null;
    }

    // Devuelve un objeto favorito basado en el id
    public favorito obtenerFavoritoPorId(ObjectId id) {
        if (realm == null)
            realm = conectarBD.conseguirUsuarioMongoDB();

        if (realm != null) {
            favorito task = realm.where(favorito.class).equalTo("_id", id)
                    .findFirst();

            if (task != null) {
                Log.v("QUICKSTART", "estoy en conseguir favorito por id");
                // Se guarda al usuario en una clase accesible para muchas clases
                Preferences.savePreferenceObjectRealm(mContext, PREFERENCE_FAVORITO, task);
                return task;
            }
        } else
            errorConexion();

        return null;
    }

    // Devuelve un booleano, depensiendo de si el objeto favorito ya existe
    public boolean verificarExistenciaFavorito(ObjectId id, String nombre) {
        realm = conectarBD.conseguirUsuarioMongoDB();

        if (realm != null) {
            favorito task = realm.where(favorito.class).equalTo("IdUsuario", id)
                    .and()
                    .equalTo("nombre", nombre)
                    .findFirst();

            return task != null;
        } else
            errorConexion();

        return false;
    }


    private void errorConexion() {
        Toast.makeText(mContext, "Hubo un problema en conectarse, intenta mas tarde", Toast.LENGTH_SHORT).show();
    }
}
