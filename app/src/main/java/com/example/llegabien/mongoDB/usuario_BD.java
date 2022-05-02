package com.example.llegabien.mongoDB;

import static io.realm.Realm.getApplicationContext;

import android.util.Log;
import android.widget.Toast;

import com.example.llegabien.backend.usuario.usuario;

import org.bson.types.ObjectId;

import io.realm.ImportFlag;
import io.realm.Realm;
import io.realm.mongodb.sync.SyncConfiguration;

public class usuario_BD {


    public static Realm conseguirRealm() {
        Realm realm = null;
        Conectar conectar = new Conectar();
        SyncConfiguration config = conectar.ConectarAnonimoMongoDB();
        if (config != null)
            realm = Realm.getInstance(config);

        return realm;
    }

    public static void AñadirUser(usuario Usuario) {
        Usuario.set_id(new ObjectId());
        Usuario.set_partition("LlegaBien");
        Realm realm = conseguirRealm();

        if(!realm.isEmpty()){
            realm.executeTransactionAsync(transactionRealm -> {
                transactionRealm.insert(Usuario);
            });

            realm.close();
        }

        else
            Toast.makeText(getApplicationContext(), "Hubo un problema en conectarse, intenta mas tarde", Toast.LENGTH_SHORT).show();

    }

    public static void DeleteUser(usuario Usuario) {
        Realm realm = conseguirRealm();

        Log.v("QUICKSTART", "pARTITION: " + Usuario.get_partition());

        if(!realm.isEmpty()){
            realm.executeTransactionAsync(transactionRealm -> {

                usuario_validaciones v = new usuario_validaciones();
                usuario a = v.conseguirUsuario_porCorreo(getApplicationContext(), Usuario.getCorreoElectronico(), Usuario.getContrasena());
                a.deleteFromRealm();
                //transactionRealm.insertOrUpdate(Usuario);
                //transactionRealm.copyToRealmOrUpdate(Usuario, ImportFlag.CHECK_SAME_VALUES_BEFORE_SET);
                //usuario u = realm.where(usuario.class).equalTo("_id", Usuario.get_id()).findFirst();
               // Usuario.deleteFromRealm();
                Log.v("QUICKSTART", "SE HIZO DELETE CON EXITOOOO ");
            });

            realm.close();
        }

        else
            Toast.makeText(getApplicationContext(), "Hubo un problema en conectarse, intenta mas tarde", Toast.LENGTH_SHORT).show();
    }


    public static void UpdateUser(usuario Usuario) {
        Realm realm = conseguirRealm();

        Log.v("QUICKSTART", "pARTITION: " + Usuario.get_partition());

        if(!realm.isEmpty()){

            realm.executeTransactionAsync(transactionRealm -> {
                //transactionRealm.insertOrUpdate(Usuario);
                transactionRealm.copyToRealmOrUpdate(Usuario, ImportFlag.CHECK_SAME_VALUES_BEFORE_SET);
                Log.v("QUICKSTART", "SE HIZO UPDATE CON EXITOOOO ");
            });

            realm.close();
        }

        else
            Toast.makeText(getApplicationContext(), "Hubo un problema en conectarse, intenta mas tarde", Toast.LENGTH_SHORT).show();

    }

}
