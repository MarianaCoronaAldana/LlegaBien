package com.example.llegabien.mongoDB;

import android.app.Activity;
import android.util.Log;

import com.example.llegabien.aplicacionLlegaBien;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.sync.SyncConfiguration;


public class Conectar {

    private Realm realm=null;

    public SyncConfiguration ConectarAMongoDB(Activity Context){

        SyncConfiguration config;

        //PARA LOGING ANONIMO
        Credentials credentials = Credentials.anonymous();

        App app = aplicacionLlegaBien.getApp();
        String partitionValue = "LlegaBien";
        Log.v("QUICKSTART", "Aver" + app.currentUser());

        app.loginAsync(credentials, result -> {
            if (result.isSuccess()) {
                Log.v("QUICKSTART", "Successfully authenticated anonymously." + app.currentUser());
                User user = app.currentUser();

            } else {
                Log.e("QUICKSTART", "Failed to log in. Error: " + result.getError());
            }
        });

        config = new SyncConfiguration.Builder(
                app.currentUser(),
                partitionValue)
                .build();

/*
        app.currentUser().logOutAsync(result -> {
            if (result.isSuccess()) {
                Log.v("QUICKSTART", "Successfully logge
                d out.");
            } else {
                Log.e("QUICKSTART", "Failed to log out, error: " + result.getError());
            }
        });
*/
        return config;
    }
}

