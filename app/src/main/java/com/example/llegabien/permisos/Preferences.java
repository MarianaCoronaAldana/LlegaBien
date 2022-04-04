package com.example.llegabien.permisos;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.example.llegabien.usuario.fragmento.FragmentoIniciarSesion1;

public class Preferences extends AppCompatActivity {

    public static final String STRING_PREFERENCES = "michattimereal.Mensajes.Mensajeria";
    public static final String PREFERENCE_ESTADO_BUTTON_SESION = "estado.button.sesion";
    public static final String PREFERENCE_USUARIO_LOGIN = "usuario.login";

    public static void savePreferenceBoolean(FragmentoIniciarSesion1 c, boolean b, String key){
        SharedPreferences preferences = c.getActivity().getSharedPreferences(STRING_PREFERENCES,c.getActivity().MODE_PRIVATE);
        preferences.edit().putBoolean(key,b).apply();
    }

    public static void savePreferenceString(Context c, String b, String key){
        SharedPreferences preferences = c.getSharedPreferences(STRING_PREFERENCES,c.MODE_PRIVATE);
        preferences.edit().putString(key,b).apply();
    }

    public static boolean obtenerPreferenceBool(Context c,String key){
        SharedPreferences preferences = c.getSharedPreferences(STRING_PREFERENCES,c.MODE_PRIVATE);
        return preferences.getBoolean(key,false);//Si es que nunca se ha guardado nada en esta key pues retornara false
    }

    public static boolean obtenerPreference(FragmentoIniciarSesion1 c, String key){
        SharedPreferences preferences = c.getActivity().getSharedPreferences(STRING_PREFERENCES,c.getActivity().MODE_PRIVATE);
        return preferences.getBoolean(key,false);//Si es que nunca se ha guardado nada en esta key pues retornara false
    }
}
