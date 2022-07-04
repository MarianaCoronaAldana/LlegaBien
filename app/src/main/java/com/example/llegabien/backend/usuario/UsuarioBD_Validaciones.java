package com.example.llegabien.backend.usuario;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.mongoDB.ConectarBD;

import io.realm.Realm;

public class UsuarioBD_Validaciones extends AppCompatActivity {
    Context mContext;
    Realm realm;
    ConectarBD conectarBD = new ConectarBD();

    public UsuarioBD_Validaciones(Context c){
        mContext = c;
    }

    // Se verifica que el correo y telefono del usuario no hayan sido registrados anteriormente
    public boolean validarExistenciaCorreoTelefono(String correo, String telefono) {
        realm = conectarBD.conseguirUsuarioMongoDB();
        if(realm!=null){
            usuario task = realm.where(usuario.class).equalTo("correoElectronico", correo)
                    .or()
                    .equalTo("telCelular", telefono)
                    .findFirst();

            return task != null;
        }

        else
            errorConexion();

        return false;
    }

    // Se verifica que los datos para iniciar sesion coincidan con un usuario ADMINISTRADOR
    public boolean validarAdmin( String correo, String contrasena){
       // realm = conectarBD.ConectarAnonimoMongoDB();
        realm = conectarBD.conseguirUsuarioMongoDB();

        if(realm!=null){
            usuario task = realm.where(usuario.class).equalTo("correoElectronico", correo)
                    .and()
                    .equalTo("contrasena", contrasena)
                    .and()
                    .isNotNull("status")
                    .findFirst();

            if (task != null) {
                Log.v("QUICKSTART", "OMG SI SE PUDO ADMIN");
                // Se guarda al usuario en una clase accesible para muchas clases
                Preferences.savePreferenceObjectRealm(mContext, PREFERENCE_USUARIO, task);

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
    public boolean verificarCorreoContrasena(String correo, String contrasena, String error) {
        realm = conectarBD.conseguirUsuarioMongoDB();

        if(realm!=null){
            usuario task = realm.where(usuario.class).equalTo("correoElectronico", correo)
                    .and()
                    .equalTo("contrasena", contrasena)
                    .findFirst();

            if (task != null) {
                Log.v("AVERW", "NOMBRE DE TASK: " + task.getNombre());
                // Se guarda al usuario en una clase accesible para muchas clases
                Preferences.savePreferenceObjectRealm(mContext, PREFERENCE_USUARIO, task);
                // Se abre una cuenta con el correo y contraseña del usuario
                //conectarBD.ConectarCorreoMongoDB(correo, contrasena);
                conectarBD.conseguirUsuarioMongoDB();
                return true;
            }
            else
                Toast.makeText(mContext,error,Toast.LENGTH_LONG).show();
        }

        else
            errorConexion();

        return false;
    }

    private void errorConexion(){
        Toast.makeText(mContext, "Hubo un problema en conectarse, intenta mas tarde", Toast.LENGTH_LONG).show();
    }
}
