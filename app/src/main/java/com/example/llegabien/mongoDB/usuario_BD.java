package com.example.llegabien.mongoDB;

import com.example.llegabien.backend.usuario.usuario;

import org.bson.types.ObjectId;

import io.realm.Realm;
import io.realm.mongodb.sync.SyncConfiguration;

public class usuario_BD {

    public static void AñadirUser(usuario Usuario) {
        Usuario.set_id(new ObjectId());

        Conectar conectar = new Conectar();
        SyncConfiguration config = conectar.ConectarAMongoDB();
        Realm realm = Realm.getInstance(config);

        realm.executeTransactionAsync(transactionRealm -> {
            transactionRealm.insert(Usuario);
        });

        realm.close();
    }

}
