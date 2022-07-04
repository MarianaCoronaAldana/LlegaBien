package com.example.llegabien.backend.app;

import android.app.Application;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;

public class aplicacionLlegaBien extends Application {
    static App app_LlegaBien;

    @Override public void onCreate() {
        super.onCreate();

        String appID = "llegabien-fcvux"; // el id del realm de LlegaBien
        Realm.init(this); // context, usually an Activity or Application

        app_LlegaBien = new App(new AppConfiguration.Builder(appID)
                .build());
    }

    public static App getApp(){
        return app_LlegaBien;
    }

}
