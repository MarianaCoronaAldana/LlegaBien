package com.example.llegabien.backend.reporte;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.llegabien.backend.mongoDB.ConectarBD;
import com.example.llegabien.backend.usuario.usuario;

import org.bson.types.ObjectId;

import io.realm.Realm;
import io.realm.RealmResults;

public class ReporteDAO {

    private final ConectarBD conectarBD = new ConectarBD();
    private Realm realm;
    private final Context mContext;

    public ReporteDAO(Context context){
        mContext = context;
    }

    public void anadirReporte(reporte Reporte) {
        Reporte.set_id(new ObjectId());
        Reporte.set_partition("LlegaBien");
        realm = conectarBD.conseguirUsuarioMongoDB();

        if(realm!=null){
            realm.executeTransactionAsync(transactionRealm -> transactionRealm.insert(Reporte));

            realm.close();
        }
        else
            errorConexion();
    }

    public RealmResults<reporte> obtenerReportesPorUsuario(reporte Reporte) {
        realm = conectarBD.conseguirUsuarioMongoDB();

        if(realm!=null){
            return realm.where(reporte.class).equalTo("IdUsuario",Reporte.getIdUsuario()).findAll();
        }

        else
            errorConexion();

        return null;
    }


    public RealmResults<reporte> obtenerReportesPorUsuario(usuario Usuario) {
        realm = conectarBD.conseguirUsuarioMongoDB();

        if(realm!=null){
            RealmResults<reporte> realmResults = realm.where(reporte.class).equalTo("IdUsuario",Usuario.get_id()).findAll();
            Log.v("QUICKSTART", "Estoy en REPORTE DAO, size: " + realmResults.size());
            return realmResults;
        }

        else
            errorConexion();

        return null;
    }

    private void errorConexion(){
        Toast.makeText(mContext, "Hubo un problema en conectarse, intenta mas tarde", Toast.LENGTH_SHORT).show();
    }
}
