package com.example.llegabien.backend.permisos;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.example.llegabien.frontend.usuario.fragmento.FragmentoIniciarSesion1;
import com.google.gson.Gson;

import io.realm.Realm;
import io.realm.RealmObject;

public class Preferences extends AppCompatActivity {

    public static final String STRING_PREFERENCES = "Preferences";
    public static final String PREFERENCE_ESTADO_BUTTON_SESION = "estado.button.sesion";
    public static final String PREFERENCE_ES_ADMIN = "usuario.admin";
    public static final String PREFERENCE_USUARIO = "usuario";

    // Guaradar preferencia sobre un tipo boolean
    public static void savePreferenceBoolean(FragmentoIniciarSesion1 c, boolean b, String key){
        SharedPreferences preferences = c.getActivity().getSharedPreferences(STRING_PREFERENCES,c.getActivity().MODE_PRIVATE);
        preferences.edit().putBoolean(key,b).apply();
    }

    public static void savePreferenceString(Context c, String b, String key){
        SharedPreferences preferences = c.getSharedPreferences(STRING_PREFERENCES,c.MODE_PRIVATE);
        preferences.edit().putString(key,b).apply();
    }

    // Obtener preferencia acerca de un tipo boolean
    public static boolean obtenerPreferenceBool(Context c,String key){
        SharedPreferences preferences = c.getSharedPreferences(STRING_PREFERENCES,c.MODE_PRIVATE);
        return preferences.getBoolean(key,false);//Si es que nunca se ha guardado nada en esta key pues retornara false
    }

    // Para guardar preferencias de un objeto realm
    public static void savePreferenceObject(Context c, String key, RealmObject object) {
        final Gson gson = new Gson();

        SharedPreferences preferences = c.getSharedPreferences(STRING_PREFERENCES, c.MODE_PRIVATE);
        String a = gson.toJson(Realm.getDefaultInstance().copyFromRealm(object));
        preferences.edit().putString(key, a).apply();

    }

    // Para obtener preferencias de un objeto
    public static <GenericClass> GenericClass getSavedObjectFromPreference(Context c, String preferenceKey, Class<GenericClass> classType) {
        SharedPreferences sharedPreferences = c.getSharedPreferences(STRING_PREFERENCES,c.MODE_PRIVATE);
        if (sharedPreferences.contains(preferenceKey)) {
            final Gson gson = new Gson();
            return gson.fromJson(sharedPreferences.getString(preferenceKey, null), classType);
        }
        return null;
    }
}
