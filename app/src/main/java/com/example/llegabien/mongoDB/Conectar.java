package com.example.llegabien.mongoDB;

import android.util.Log;

import com.example.llegabien.aplicacionLlegaBien;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.sync.SyncConfiguration;


public class Conectar {

    private Realm realm=null;

    //PARA LOGING ANONIMO
    public SyncConfiguration ConectarAnonimoMongoDB(){

        App app = aplicacionLlegaBien.getApp();
        String partitionValue = "LlegaBien";
        SyncConfiguration config = null;
        Credentials credentials = Credentials.anonymous();
        Log.v("QUICKSTART", "hola");

        app.loginAsync(credentials, result -> {
            Log.v("QUICKSTART", "crayola");

            if (result.isSuccess()) {
                Log.v("QUICKSTART", "Successfully authenticated anonymously.");

            } else {
                Log.e("QUICKSTART", "Failed to log in. Error: " + result.getError());
            }
        });

        try{
        config = new SyncConfiguration.Builder(
                app.currentUser(),
                partitionValue)
                .build();

        return config;
        }
        catch(NumberFormatException e){
            return null;
        }
    }

    public SyncConfiguration ConectarCorreoMongoDB(String email, String password){
        App app = aplicacionLlegaBien.getApp();
        String partitionValue = "LlegaBien";
        SyncConfiguration config = null;
        Credentials emailPasswordCredentials = Credentials.emailPassword(email, password);

        app.loginAsync(emailPasswordCredentials, it -> {
            if (it.isSuccess()) {
                Log.v("QUICKSTART", "Successfully authenticated using an email and password.");
            } else {
                Log.e("QUICKSTART", it.getError().toString());
            }
        });

        try{
        config = new SyncConfiguration.Builder(
                app.currentUser(),
                partitionValue)
                .build();

        return config;
        }
        catch(NumberFormatException e){
            return null;
        }
    }

    public void registrarCuentaCorreo(String email, String password){
        App app = aplicacionLlegaBien.getApp();

        app.getEmailPassword().registerUserAsync(email, password, it -> {
            if (it.isSuccess()) {
                Log.i("EXAMPLE", "Successfully registered user.");
            } else {
                Log.e("EXAMPLE", "Failed to register user: " + it.getError().getErrorMessage());
            }
        });
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

