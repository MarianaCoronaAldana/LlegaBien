package com.example.llegabien.backend.reporte;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;
import static io.realm.Realm.getApplicationContext;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.mongoDB.ConectarBD;
import com.example.llegabien.backend.usuario.usuario;

import org.bson.types.ObjectId;

import io.realm.ImportFlag;
import io.realm.Realm;

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

    private void errorConexion(){
        Toast.makeText(mContext, "Hubo un problema en conectarse, intenta mas tarde", Toast.LENGTH_SHORT).show();
    }
}
