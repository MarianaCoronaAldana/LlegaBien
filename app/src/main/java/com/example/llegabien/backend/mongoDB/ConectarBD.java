package com.example.llegabien.backend.mongoDB;

import static io.realm.Realm.getApplicationContext;

import android.util.Log;
import android.widget.Toast;

import com.example.llegabien.backend.app.aplicacionLlegaBien;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.sync.SyncConfiguration;


public class ConectarBD {

    private Realm realm=null;
    User user;
    String partitionValue = "LlegaBien";
    SyncConfiguration config = null;

    //PARA LOGING ANONIMO
    public Realm ConectarAnonimoMongoDB(){
        App app = aplicacionLlegaBien.getApp();
        Credentials credentials = Credentials.anonymous();
        Log.v("QUICKSTART", "hola");

        app.loginAsync(credentials, result -> {
            Log.v("QUICKSTART", "crayola");
            if (result.isSuccess())
                Log.v("QUICKSTART", "Successfully authenticated anonymously.");

            else
                //Log.e("QUICKSTART", "Failed to log in. Error: " + result.getError());
                Toast.makeText(getApplicationContext(), "Error de conexion", Toast.LENGTH_LONG).show();
        });

        user = app.currentUser();
        if(user!=null) {
            config = new SyncConfiguration.Builder(
                    user,
                    partitionValue)
                    .build();
            realm = Realm.getInstance(config);
        }

        return realm;
    }

    public Realm ConectarCorreoMongoDB(String email, String password){
        App app = aplicacionLlegaBien.getApp();
        Credentials emailPasswordCredentials = Credentials.emailPassword(email, password);

        app.loginAsync(emailPasswordCredentials, it -> {
            if (it.isSuccess()) {
                Log.v("QUICKSTART", "Successfully authenticated using an email and password.");
            } else {
                ConectarAnonimoMongoDB();
            }
        });

        user = app.currentUser();
        if(user!=null) {
            config = new SyncConfiguration.Builder(
                    user,
                    partitionValue)
                    .build();
            realm = Realm.getInstance(config);
        }

        return realm;
    }


    // Para conseguir el usuario actual de la app
    public Realm conseguirUsuarioMongoDB(){
        App app = aplicacionLlegaBien.getApp();

        user = app.currentUser();
        if(user!=null) {
            config = new SyncConfiguration.Builder(
                    user,
                    partitionValue)
                    .build();
            realm = Realm.getInstance(config);
        }

        else {
            ConectarAnonimoMongoDB();
            conseguirUsuarioMongoDB();
        }
        return realm;
    }


    public static void registrarCuentaCorreo(String email, String password){
        App app = aplicacionLlegaBien.getApp();

        app.getEmailPassword().registerUserAsync(email, password, it -> {
            if (it.isSuccess()) {
                Log.i("EXAMPLE", "Successfully registered user.");
            } else {
                Log.e("EXAMPLE", "Failed to register user: " + it.getError().getErrorMessage());
            }
        });

        app.removeUser(app.currentUser());
    }

    public static void borrarCuentaCorreo(){
        App app = aplicacionLlegaBien.getApp();
        app.removeUser(app.currentUser());
    }


    public void cerrarMongoDB(){
        App app = aplicacionLlegaBien.getApp();
        app.currentUser().logOutAsync(result -> {
            if (result.isSuccess()) {
                Log.v("QUICKSTART", "Successfully logged out.");
            } else {
                Log.e("QUICKSTART", "Failed to log out, error: " + result.getError());
            }
        });
    }

}

