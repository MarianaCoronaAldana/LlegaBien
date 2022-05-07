package com.example.llegabien.mongoDB;

import static com.example.llegabien.backend.permisos.Preferences.PREFERENCE_USUARIO;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.llegabien.backend.permisos.Preferences;
import com.example.llegabien.backend.usuario.usuario;

import io.realm.Realm;

public class usuario_validaciones extends AppCompatActivity {
    Realm realm;
    Conectar conectar = new Conectar();

    // Se verifica que el correo y telefono del usuario no hayan sido registrados anteriormente
    public boolean validarExistenciaCorreoTelefono(String correo, String telefono) {
        realm = conectar.ConectarAnonimoMongoDB();
        usuario task = realm.where(usuario.class).equalTo("correoElectronico",  correo)
                .or()
                .equalTo("telCelular",telefono)
                .findFirst();

        if (task != null)
            return true;

        return false;
    }

    // Se verifica que los datos para iniciar sesion coincidan con un usuario ADMINISTRADOR
    public boolean validarAdmin(Context c, String correo, String contrasena){
        realm = conectar.ConectarAnonimoMongoDB();

        if(realm!=null) {
            usuario task = realm.where(usuario.class).equalTo("correoElectronico", correo)
                    .and()
                    .equalTo("contrasena", contrasena)
                    .and()
                    .isNotNull("status")
                    .findFirst();

            if (task != null) {
                Log.v("QUICKSTART", "OMG SI SE PUDO ADMIN");
                // Se guarda al usuario en una clase accesible para muchas clases
                Preferences.savePreferenceRealmObject(c, PREFERENCE_USUARIO, task);

                // Se abre una cuenta con el correo y contraseña del usuario
                conectar.ConectarCorreoMongoDB(correo, contrasena);
                return true;
            }
        }

        return false;
    }


    // Se verifica que los datos para iniciar sesion coincidan con un usuario real
    public boolean verificarCorreoContrasena(Context c, String correo, String contrasena) {
        realm = conectar.ConectarAnonimoMongoDB();

        if(realm!=null) {
            usuario task = realm.where(usuario.class).equalTo("correoElectronico", correo)
                    .and()
                    .equalTo("contrasena", contrasena)
                    .findFirst();

            if (task != null) {
                Log.v("AVERW", "NOMBRE D ETASK: " + task.getNombre());
                // Se guarda al usuario en una clase accesible para muchas clases
                Preferences.savePreferenceRealmObject(c, PREFERENCE_USUARIO, task);
                // Se abre una cuenta con el correo y contraseña del usuario
                conectar.ConectarCorreoMongoDB(correo, contrasena);
                return true;
            }
            else
                Toast.makeText(c,"El correo electronico o el numero telefonico son incorrectos",Toast.LENGTH_LONG).show();
        }

        return false;
    }


    // Devuelve un objeto usuario basado en el correo electronico
    public usuario conseguirUsuario_porCorreo(Context c, String correo, String contrasena) {
        realm = conectar.ConectarCorreoMongoDB(correo, contrasena);

        if(realm!=null) {
            usuario task = realm.where(usuario.class).equalTo("correoElectronico", correo)
                    .findFirst();

            if (task != null) {
                Log.v("QUICKSTART", "estoy en conseguir usuario por coreoooooooooooooo");
                // Se guarda al usuario en una clase accesible para muchas clases
                Preferences.savePreferenceRealmObject(c, PREFERENCE_USUARIO, task);
                // Se cierra la cuenta con la que se estan haciendo transacciones en MongoDB y se crea una con el correo y contraseña del usuario
                return task;
            }
        }
        return null;
    }

    private void ErrorConexion(Context c){
        Toast.makeText(getApplicationContext(), "Hubo un problema en conectarse, intenta mas tarde", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, c.getClass()));
    }
}
