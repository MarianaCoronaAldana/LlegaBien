package com.example.llegabien.backend.app;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;

public class Preferences extends AppCompatActivity {

    private static android.content.SharedPreferences mSharedPreferences;
    public static final String STRING_PREFERENCES = "Preferences";
    public static final String PREFERENCE_ESTADO_BUTTON_SESION = "estado.button.sesion";
    public static final String PREFERENCE_MENSAJE_PORBATERIA_ENVIADO = "mensaje.por.bateria.enviado";
    public static final String PREFERENCE_ES_ADMIN = "usuario.admin";
    public static final String PREFERENCE_USUARIO = "usuario";
    public static final String PREFERENCE_UBICACION = "ubicacion";
    public static final String PREFERENCE_FAVORITO = "favorito";

    // Guardar preferencia sobre un tipo boolean
    public static void savePreferenceBoolean(Context c, boolean b, String key){
        mSharedPreferences = c.getSharedPreferences(STRING_PREFERENCES, MODE_PRIVATE);
        mSharedPreferences.edit().putBoolean(key,b).apply();
    }

    // Para guardar preferencias de un objeto realm
    public static void savePreferenceObjectRealm(Context c, String key, RealmObject object) {
        final Gson gson = new Gson();
        mSharedPreferences = c.getSharedPreferences(STRING_PREFERENCES, MODE_PRIVATE);
        String jsonObjectRealm = gson.toJson(Realm.getInstance(new RealmConfiguration.Builder()
                .schemaVersion(5)
                .deleteRealmIfMigrationNeeded()
                .build()).copyFromRealm(object));
        mSharedPreferences.edit().putString(key, jsonObjectRealm).apply();
    }


    // Obtener preferencia acerca de un tipo boolean
    public static boolean getSavedBooleanFromPreference(Context c,String key){
        mSharedPreferences = c.getSharedPreferences(STRING_PREFERENCES, MODE_PRIVATE);
        return mSharedPreferences.getBoolean(key,false);//Si es que nunca se ha guardado nada en esta key pues retornara false
    }

    // Para obtener preferencias de un objeto
    public static <GenericClass> GenericClass getSavedObjectFromPreference(Context c, String key, Class<GenericClass> classType) {
        mSharedPreferences = c.getSharedPreferences(STRING_PREFERENCES, MODE_PRIVATE);
        if (mSharedPreferences.contains(key)) {
            final Gson gson = new Gson();
            return gson.fromJson(mSharedPreferences.getString(key, null), classType);
        }
        return null;
    }
}
