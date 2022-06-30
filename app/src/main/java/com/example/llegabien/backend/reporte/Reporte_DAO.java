package com.example.llegabien.backend.reporte;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;
import static io.realm.Realm.getApplicationContext;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.mapa.ubicacion.ubicacion;
import com.example.llegabien.backend.mongoDB.ConectarBD;
import com.example.llegabien.backend.usuario.usuario;

import org.bson.types.ObjectId;

import io.realm.ImportFlag;
import io.realm.Realm;
import io.realm.RealmResults;

public class Reporte_DAO {

    private ConectarBD conectarBD = new ConectarBD();
    private Realm realm;
    private Context mContext;

    public Reporte_DAO(Context context){
        mContext = context;
    }

    public void añadirReporte(reporte Reporte) {
        Reporte.set_id(new ObjectId());
        Reporte.set_partition("LlegaBien");
        realm = conectarBD.conseguirUsuarioMongoDB();

        if(realm!=null){
            realm.executeTransactionAsync(transactionRealm -> {
                transactionRealm.insert(Reporte);
            });

            realm.close();
        }
        else
            errorConexion();
    }

    public RealmResults<reporte> obtenerReportesPorUsuario(reporte Reporte) {
        realm = conectarBD.conseguirUsuarioMongoDB();

        if(realm!=null){
            RealmResults<reporte> realmResults = realm.where(reporte.class).equalTo("IdUsuario",Reporte.getIdUsuario()).findAll();

            if (realmResults != null)
                return realmResults;
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

            if (realmResults != null)
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
