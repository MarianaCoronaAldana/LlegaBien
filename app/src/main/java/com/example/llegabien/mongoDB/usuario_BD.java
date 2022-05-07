package com.example.llegabien.mongoDB;

import static io.realm.Realm.getApplicationContext;

import android.util.Log;
import android.widget.Toast;

import com.example.llegabien.backend.usuario.usuario;

import org.bson.types.ObjectId;

import io.realm.ImportFlag;
import io.realm.Realm;

public class usuario_BD {

    public static Realm conseguirRealm() {
        Conectar conectar = new Conectar();
        Realm realm = conectar.ConectarAnonimoMongoDB();
        return realm;
    }

    public static Realm conseguirRealmCorreo(String email, String contrasena) {
        Conectar conectar = new Conectar();
        Realm realm = conectar.ConectarCorreoMongoDB(email, contrasena);
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
        Realm realm ;
        realm = conseguirRealmCorreo(Usuario.getCorreoElectronico(), Usuario.getContrasena());
        realm = conseguirRealm();
        Log.v("QUICKSTART", "pARTITION: " + Usuario.get_partition());
        Log.v("QUICKSTART", "ESTOY EN UPDATE ");

        if(!realm.isEmpty()){

            realm.executeTransactionAsync(transactionRealm -> {
                //transactionRealm.insertOrUpdate(Usuario);
                transactionRealm.copyToRealmOrUpdate(Usuario, ImportFlag.CHECK_SAME_VALUES_BEFORE_SET);
                Log.v("QUICKSTART", "SE HIZO UPDATE CON EXITOOOO ");
            });

            Toast.makeText(getApplicationContext(), "Datos actualizados con exito", Toast.LENGTH_SHORT).show();
            realm.close();
        }
        else
            Toast.makeText(getApplicationContext(), "Hubo un problema en conectarse, intenta mas tarde", Toast.LENGTH_SHORT).show();

    }



}
