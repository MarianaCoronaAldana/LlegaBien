package com.example.llegabien.backend.ruta.realm;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.llegabien.backend.mongoDB.ConectarBD;

import org.bson.types.ObjectId;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class rutaDAO {

    private final ConectarBD conectarBD = new ConectarBD();
    private Realm realm;
    private final Context mContext;

    public rutaDAO(Context mContext) {
        this.mContext = mContext;
    }

    public void anadirRuta(ruta Ruta) {
        Ruta.set_id(new ObjectId());
        Ruta.set_partition("LlegaBien");
        realm = conectarBD.conseguirUsuarioMongoDB();
        if(realm!=null){
            realm.executeTransactionAsync(transactionRealm -> transactionRealm.insert(Ruta));
            realm.close();
        }
        else
            errorConexion();
    }

    // Obtene todas las rutas relacionadas al id de un usuario
    public RealmResults<ruta> obtenerRutasPorUsuario(ObjectId usuario) {
        realm = conectarBD.conseguirUsuarioMongoDB();
        if(realm!=null){
            return realm.where(ruta.class).equalTo("idUsuario",usuario)
                    .sort("fUsoRuta", Sort.DESCENDING).findAll();
        }
        else
            errorConexion();
        return null;
    }

    // Devuelve un objeto RUTA basado en el id
    public ruta obtenerRutaPorId(ObjectId id) {
        realm = conectarBD.conseguirUsuarioMongoDB();
        if(realm!=null){
            ruta task = realm.where(ruta.class).equalTo("_id", id)
                    .findFirst();
            if (task != null) {
                Log.v("QUICKSTART", "estoy en conseguir ruta por id");
                return task;
            }
        }
        else
            errorConexion();
        return null;
    }

    private void errorConexion(){
        Toast.makeText(mContext, "Hubo un problema en conectarse, intenta mas tarde", Toast.LENGTH_SHORT).show();
    }
}
