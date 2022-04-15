package com.example.llegabien.backend.usuario;

import com.example.llegabien.backend.contactos.usuario_contacto;
import com.example.llegabien.mongoDB.Conectar;

import org.bson.types.ObjectId;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.mongodb.sync.SyncConfiguration;

public class CRUD_usuario {

    public static void AñadirUser(usuario Usuario) {

        RealmList<usuario_contacto> Contacto =  new  RealmList <usuario_contacto>();
        Contacto.add(new usuario_contacto("Aurora", "432432432"));
        Contacto.add(new usuario_contacto("Bella", "432432432"));
        Contacto.add(new usuario_contacto("Jasmine", "432432432"));
        Contacto.add(new usuario_contacto("Cenicienta", "432432432"));

        usuario UsuariO = new usuario(new ObjectId(), "Princesa", Contacto,"abcd", "a@gmail.com", new Date(),"Ariel", "3321707532");

        Usuario.set_id(new ObjectId());
        Usuario.setContacto(Contacto);

        Conectar conectar = new Conectar();
        SyncConfiguration config = conectar.ConectarAMongoDB();
        Realm realm = Realm.getInstance(config);

        realm.executeTransactionAsync(transactionRealm -> {
            transactionRealm.insert(Usuario);
        });




        realm.close();
    }


}
