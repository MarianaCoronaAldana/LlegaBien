package com.example.llegabien.mongoDB;

import static com.example.llegabien.backend.permisos.Preferences.PREFERENCE_USUARIO;

import android.content.Context;
import android.util.Log;

import com.example.llegabien.backend.permisos.Preferences;
import com.example.llegabien.backend.usuario.usuario;

import io.realm.Realm;
import io.realm.mongodb.sync.SyncConfiguration;

public class usuario_validaciones {

    Conectar conectar = new Conectar();
    SyncConfiguration config = conectar.ConectarAMongoDB();
    Realm realm = Realm.getInstance(config);

    public boolean validarExistenciaCorreoTelefono(String correo, String telefono) {
        usuario task = realm.where(usuario.class).equalTo("correoElectronico",  correo)
                .or()
                .equalTo("telCelular",telefono)
                .findFirst();

        if (task != null)
            return true;

        return false;
    }

    public boolean validarAdmin(Context c, String correo, String contrasena){
        usuario task = realm.where(usuario.class).equalTo("correoElectronico", correo)
                .and()
                .equalTo("contrasena",  contrasena)
                .and()
                .isNotNull("status")
                .findFirst();

        if (task != null) {
            Log.v("QUICKSTART", "OMG SI SE PUDO ADMIN");
            Preferences.savePreferenceObject(c, PREFERENCE_USUARIO, task);
            return true;
        }

        return false;
    }

    public boolean verificarCorreoContrasena(Context c, String correo, String contrasena) {
        usuario task = realm.where(usuario.class).equalTo("correoElectronico", correo)
                .and()
                .equalTo("contrasena",  contrasena)
                .findFirst();

        if (task != null) {
            Log.v("QUICKSTART", "OMG SI SE PUDO");
            Preferences.savePreferenceObject(c, PREFERENCE_USUARIO, task);
            return true;
        }

        return false;
    }

}
