package com.example.llegabien.backend.app;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;

public class Preferences extends AppCompatActivity {

    private static android.content.SharedPreferences mSharedPreferences;
    public static final String STRING_PREFERENCES = "Preferences";
    public static final String PREFERENCE_ESTADO_BUTTON_SESION = "estado.button.sesion";
    public static final String PREFERENCE_ES_ADMIN = "usuario.admin";
    public static final String PREFERENCE_USUARIO = "usuario";
    public static final String PREFERENCE_UBICACION_BUSQUEDA_AUTOCOMPLETADA = "ubicacionBusquedaAutocompletada";
    public static final String PREFERENCE_UBICACION = "Ubicacion";

    // Guaradar preferencia sobre un tipo boolean
    public static void savePreferenceBoolean(Context c, boolean b, String key){
        mSharedPreferences = c.getSharedPreferences(STRING_PREFERENCES,c.MODE_PRIVATE);
        mSharedPreferences.edit().putBoolean(key,b).apply();
    }

    public static void savePreferenceString(Context c, String b, String key){
        android.content.SharedPreferences preferences = c.getSharedPreferences(STRING_PREFERENCES,c.MODE_PRIVATE);
        preferences.edit().putString(key,b).apply();
    }

    // Para guardar preferencias de un objeto realm
    public static void savePreferenceObject(Context c, String key, RealmObject object) {
        final Gson gson = new Gson();
        mSharedPreferences = c.getSharedPreferences(STRING_PREFERENCES, c.MODE_PRIVATE);
        String a = gson.toJson(Realm.getInstance(new RealmConfiguration.Builder()
                .schemaVersion(2)
                .deleteRealmIfMigrationNeeded()
                .build()).copyFromRealm(object));
        mSharedPreferences.edit().putString(key, a).apply();

    }

    public static String getSavedStringFromPreference(Context c,String key){
        mSharedPreferences = c.getSharedPreferences(STRING_PREFERENCES,c.MODE_PRIVATE);
        return mSharedPreferences.getString(key,"no hay string");
    }

    // Obtener preferencia acerca de un tipo boolean
    public static boolean getSavedBooleanFromPreference(Context c,String key){
        mSharedPreferences = c.getSharedPreferences(STRING_PREFERENCES,c.MODE_PRIVATE);
        return mSharedPreferences.getBoolean(key,false);//Si es que nunca se ha guardado nada en esta key pues retornara false
    }

    // Para obtener preferencias de un objeto
    public static <GenericClass> GenericClass getSavedObjectFromPreference(Context c, String preferenceKey, Class<GenericClass> classType) {
        mSharedPreferences = c.getSharedPreferences(STRING_PREFERENCES,c.MODE_PRIVATE);
        if (mSharedPreferences.contains(preferenceKey)) {
            final Gson gson = new Gson();
            return gson.fromJson(mSharedPreferences.getString(preferenceKey, null), classType);
        }
        return null;
    }
}
