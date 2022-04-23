package com.example.llegabien.mongoDB;

import android.util.Log;

import com.example.llegabien.backend.usuario.usuario;

import org.bson.types.ObjectId;

import io.realm.ImportFlag;
import io.realm.Realm;
import io.realm.mongodb.sync.SyncConfiguration;

public class usuario_BD {


    public static Realm conseguirRealm(){
        Conectar conectar = new Conectar();
        SyncConfiguration config = conectar.ConectarAnonimoMongoDB();
        Realm realm = Realm.getInstance(config);

        return realm;
    }

    public static void AñadirUser(usuario Usuario) {
        Usuario.set_id(new ObjectId());
        Usuario.set_partition("LlegaBien");
        Realm realm = conseguirRealm();

        realm.executeTransactionAsync(transactionRealm -> {
            transactionRealm.insert(Usuario);
        });

        realm.close();
    }

    public static void UpdateUser(usuario Usuario) {
        Realm realm = conseguirRealm();

        Log.v("QUICKSTART", "pARTITION: " + Usuario.get_partition());

        realm.executeTransactionAsync(transactionRealm -> {
            //transactionRealm.insertOrUpdate(Usuario);
            transactionRealm.copyToRealmOrUpdate(Usuario, ImportFlag.CHECK_SAME_VALUES_BEFORE_SET);
            Log.v("QUICKSTART", "SE HIZO UPDATE CON EXITOOOO ");
        });

        realm.close();
    }

}
