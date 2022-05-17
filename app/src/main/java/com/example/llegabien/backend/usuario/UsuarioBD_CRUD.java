package com.example.llegabien.backend.usuario;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;
import static io.realm.Realm.getApplicationContext;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.mongoDB.ConectarBD;

import org.bson.types.ObjectId;

import io.realm.ImportFlag;
import io.realm.Realm;

public class UsuarioBD_CRUD {

    private ConectarBD conectarBD = new ConectarBD();
    private Realm realm;


    public static Realm conseguirRealm() {
        ConectarBD conectarBD = new ConectarBD();
        Realm realm = conectarBD.ConectarAnonimoMongoDB();
        return realm;
    }

    public static Realm conseguirRealmCorreo(String email, String contrasena) {
        ConectarBD conectarBD = new ConectarBD();
        Realm realm = conectarBD.ConectarCorreoMongoDB(email, contrasena);
        return realm;
    }

    public void añadirUser(usuario Usuario) {
        Usuario.set_id(new ObjectId());
        Usuario.set_partition("LlegaBien");
        //Realm realm = conseguirRealm();
        realm = conectarBD.ConectarAnonimoMongoDB();

        if(!realm.isEmpty()){
            realm.executeTransactionAsync(transactionRealm -> {
                transactionRealm.insert(Usuario);
            });

            realm.close();
        }

        else
            errorConexion();

    }

    public void deleteUser(usuario Usuario) {
        //Realm realm = conseguirRealm();
        realm = conectarBD.ConectarAnonimoMongoDB();

        Log.v("QUICKSTART", "pARTITION: " + Usuario.get_partition());

        if(!realm.isEmpty()){
            realm.executeTransactionAsync(transactionRealm -> {

                usuario a = readUsuarioPorCorreo(getApplicationContext(), Usuario.getCorreoElectronico(), Usuario.getContrasena());
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
            errorConexion();
    }


    public void updateUser(usuario Usuario) {
        //Realm realm ;
        //realm = conseguirRealmCorreo(Usuario.getCorreoElectronico(), Usuario.getContrasena());
        realm = conectarBD.ConectarCorreoMongoDB(Usuario.getCorreoElectronico(), Usuario.getContrasena());
        //realm = conseguirRealm();
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
            errorConexion();
    }

    // Devuelve un objeto usuario basado en el correo electronico
    public usuario readUsuarioPorCorreo(Context c, String correo, String contrasena) {
        realm = conectarBD.ConectarCorreoMongoDB(correo, contrasena);

        if(!realm.isEmpty()) {
            usuario task = realm.where(usuario.class).equalTo("correoElectronico", correo)
                    .findFirst();

            if (task != null) {
                Log.v("QUICKSTART", "estoy en conseguir usuario por coreoooooooooooooo");
                // Se guarda al usuario en una clase accesible para muchas clases
                Preferences.savePreferenceObject(c, PREFERENCE_USUARIO, task);
                // Se cierra la cuenta con la que se estan haciendo transacciones en MongoDB y se crea una con el correo y contraseña del usuario
                return task;
            }
        }

        else
            errorConexion();

        return null;
    }

    private void errorConexion(){
        Toast.makeText(getApplicationContext(), "Hubo un problema en conectarse, intenta mas tarde", Toast.LENGTH_SHORT).show();
    }
}
