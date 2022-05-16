package com.example.llegabien.backend.usuario;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.contactos.usuario_contacto;
import com.example.llegabien.backend.favoritos.favorito;
import com.example.llegabien.backend.mongoDB.ConectarBD;

import io.realm.Realm;
import io.realm.mongodb.sync.SyncConfiguration;

public class UsuarioBDValidaciones extends AppCompatActivity {
    Realm realm;
    ConectarBD conectarBD = new ConectarBD();

    // Se verifica que el correo y telefono del usuario no hayan sido registrados anteriormente
    public boolean validarExistenciaCorreoTelefono(String correo, String telefono) {
        SyncConfiguration config = conectarBD.ConectarAnonimoMongoDB();

        if (config !=null)
            realm = Realm.getInstance(config);

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
        SyncConfiguration config = conectarBD.ConectarAnonimoMongoDB();

        if (config == null) {
            ErrorConexion(c);
        }

        realm = Realm.getInstance(config);
        usuario task = realm.where(usuario.class).equalTo("correoElectronico", correo)
                .and()
                .equalTo("contrasena",  contrasena)
                .and()
                .isNotNull("status")
                .findFirst();

        if (task != null) {
            Log.v("QUICKSTART", "OMG SI SE PUDO ADMIN");
            // Se guarda al usuario en una clase accesible para muchas clases
            Preferences.savePreferenceObject(c, PREFERENCE_USUARIO, task);
            // Se cierra la cuenta con la que se estan haciendo transacciones en MongoDB y se crea una con el correo y contraseña del usuario
            conectarBD.cerrarMongoDB();
            conectarBD.ConectarCorreoMongoDB(correo, contrasena);
            return true;
        }

        return false;
    }


    // Se verifica que los datos para iniciar sesion coincidan con un usuario real
    public boolean verificarCorreoContrasena(Context c, String correo, String contrasena) {
        SyncConfiguration config = conectarBD.ConectarAnonimoMongoDB();

        if (config == null) {
            ErrorConexion(c);
        }

        realm = Realm.getInstance(config);
        usuario task = realm.where(usuario.class).equalTo("correoElectronico", correo)
                .and()
                .equalTo("contrasena",  contrasena)
                .findFirst();

        if (task != null) {
            Log.v("QUICKSTART", "OMG SI SE PUDO");
            // Se guarda al usuario en una clase accesible para muchas clases
            Preferences.savePreferenceObject(c, PREFERENCE_USUARIO, task);
            // Se cierra la cuenta con la que se estan haciendo transacciones en MongoDB y se crea una con el correo y contraseña del usuario
            //conectar.cerrarMongoDB();
            conectarBD.ConectarCorreoMongoDB(correo, contrasena);
            return true;
        }

        return false;
    }


    // Devuelve un objeto usuario basado en el correo electronico
    public usuario conseguirUsuarioPorCorreo(Context c, String correo, String contrasena) {
        SyncConfiguration config = conectarBD.ConectarCorreoMongoDB(correo, contrasena);

        if (config == null) {
            ErrorConexion(c);
        }

        realm = Realm.getInstance(config);
        usuario task = realm.where(usuario.class).equalTo("correoElectronico", correo)
                .findFirst();

        if (task != null) {
            Log.v("QUICKSTART", "OMG SI SE PUDO");
            // Se guarda al usuario en una clase accesible para muchas clases
            Preferences.savePreferenceObject(c, PREFERENCE_USUARIO, task);
            // Se cierra la cuenta con la que se estan haciendo transacciones en MongoDB y se crea una con el correo y contraseña del usuario
            return task;
        }

        return null;
    }

    private void ErrorConexion(Context c){
        Toast.makeText(getApplicationContext(), "Hubo un problema en conectarse, intenta mas tarde", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, c.getClass()));
    }
}
