package com.example.llegabien.backend.usuario;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;

import static io.realm.Realm.getApplicationContext;

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

public class UsuarioBD_Validaciones extends AppCompatActivity {
    Realm realm;
    ConectarBD conectarBD = new ConectarBD();

    // Se verifica que el correo y telefono del usuario no hayan sido registrados anteriormente
    public boolean validarExistenciaCorreoTelefono(String correo, String telefono) {
        realm = conectarBD.ConectarAnonimoMongoDB();
        if(!realm.isEmpty()) {
            usuario task = realm.where(usuario.class).equalTo("correoElectronico", correo)
                    .or()
                    .equalTo("telCelular", telefono)
                    .findFirst();

            if (task != null)
                return true;
        }

        else
            errorConexion();

        return false;
    }

    // Se verifica que los datos para iniciar sesion coincidan con un usuario ADMINISTRADOR
    public boolean validarAdmin(Context c, String correo, String contrasena){
        realm = conectarBD.ConectarAnonimoMongoDB();

        if(!realm.isEmpty()) {
            usuario task = realm.where(usuario.class).equalTo("correoElectronico", correo)
                    .and()
                    .equalTo("contrasena", contrasena)
                    .and()
                    .isNotNull("status")
                    .findFirst();

            if (task != null) {
                Log.v("QUICKSTART", "OMG SI SE PUDO ADMIN");
                // Se guarda al usuario en una clase accesible para muchas clases
                Preferences.savePreferenceObject(c, PREFERENCE_USUARIO, task);

                // Se abre una cuenta con el correo y contraseña del usuario
                conectarBD.ConectarCorreoMongoDB(correo, contrasena);
                return true;
            }
        }

        else
            errorConexion();

        return false;
    }


    // Se verifica que los datos para iniciar sesion coincidan con un usuario real
    public boolean verificarCorreoContrasena(Context c, String correo, String contrasena) {
        realm = conectarBD.ConectarAnonimoMongoDB();

        if(!realm.isEmpty()) {
            usuario task = realm.where(usuario.class).equalTo("correoElectronico", correo)
                    .and()
                    .equalTo("contrasena", contrasena)
                    .findFirst();

            if (task != null) {
                Log.v("AVERW", "NOMBRE D ETASK: " + task.getNombre());
                // Se guarda al usuario en una clase accesible para muchas clases
                Preferences.savePreferenceObject(c, PREFERENCE_USUARIO, task);
                // Se abre una cuenta con el correo y contraseña del usuario
                conectarBD.ConectarCorreoMongoDB(correo, contrasena);
                return true;
            }
            else
                Toast.makeText(c,"El correo electronico o el numero telefonico son incorrectos",Toast.LENGTH_LONG).show();
        }

        else
            errorConexion();

        return false;
    }

    private void errorConexion(){
        Toast.makeText(getApplicationContext(), "Hubo un problema en conectarse, intenta mas tarde", Toast.LENGTH_SHORT).show();
    }
}
