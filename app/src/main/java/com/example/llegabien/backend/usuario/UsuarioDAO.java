package com.example.llegabien.backend.usuario;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;
import static io.realm.Realm.getApplicationContext;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.mongoDB.ConectarBD;
import com.example.llegabien.backend.reporte.reporte;

import org.bson.types.ObjectId;

import java.util.Objects;

import io.realm.ImportFlag;
import io.realm.Realm;
import io.realm.RealmResults;

public class UsuarioDAO {

    private final ConectarBD conectarBD = new ConectarBD();
    private Realm realm;
    private final Context mContext;

    public UsuarioDAO(Context context){
        mContext = context;
    }

    public void anadirUser(usuario Usuario) {
        Usuario.set_id(new ObjectId());
        Usuario.set_partition("LlegaBien");
        realm = conectarBD.conseguirUsuarioMongoDB();

        if(realm!=null){
            realm.executeTransactionAsync(transactionRealm -> transactionRealm.insert(Usuario));

            realm.close();
        }
        else
            errorConexion();

    }

    public void deleteUser(usuario Usuario) {
        //realm = conectarBD.ConectarCorreoMongoDB(Usuario.getCorreoElectronico(), Usuario.getContrasena());
        realm = conectarBD.conseguirUsuarioMongoDB();
        if(realm!=null){
            realm.executeTransactionAsync(transactionRealm -> {
                usuario task = transactionRealm.where(usuario.class).equalTo("_id",Usuario.get_id()).findFirst();
                Objects.requireNonNull(task).deleteFromRealm();
            });
            Toast.makeText(getApplicationContext(), "Cuenta eliminada con exito", Toast.LENGTH_SHORT).show();
            realm.close();
        }
        else
            errorConexion();
    }

    public boolean updateUsuario(usuario Usuario) {
        realm = conectarBD.conseguirUsuarioMongoDB();
        Log.v("QUICKSTART", "ESTOY EN UPDATE ");
        if(realm!=null){
            realm.executeTransactionAsync(transactionRealm -> {
                transactionRealm.copyToRealmOrUpdate(Usuario, ImportFlag.CHECK_SAME_VALUES_BEFORE_SET);
                Log.v("QUICKSTART", "SE HIZO UPDATE CON EXITOOOO ");
            });
            realm.close();
            return true;
        }
        else
            errorConexion();

        return false;
    }

    // Devuelve un objeto usuario basado en el correo electronico
    public usuario readUsuarioPorCorreo(String correo) {
        realm = conectarBD.conseguirUsuarioMongoDB();
        //realm = conectarBD.ConectarCorreoMongoDB(correo, contrasena);

        if(realm!=null){
            usuario task = realm.where(usuario.class).equalTo("correoElectronico", correo)
                    .findFirst();

            if (task != null) {
                Log.v("QUICKSTART", "estoy en conseguir usuario por coreoooooooooooooo");

                // Se guarda al usuario en una clase accesible para muchas clases
                Preferences.savePreferenceObjectRealm(mContext, PREFERENCE_USUARIO, task);

                // Se cierra la cuenta con la que se estan haciendo transacciones en MongoDB y se crea una con el correo y contraseña del usuario
                return task;
            }
        }

        else
            errorConexion();

        return null;
    }

    public void readUsuarioPorNumTelefonico(String numTelefonico) {
        realm = conectarBD.conseguirUsuarioMongoDB();
        if(realm!=null){
            usuario task = realm.where(usuario.class).equalTo("telCelular", numTelefonico)
                    .findFirst();

            if (task != null) {
                Log.v("QUICKSTART", "estoy en conseguir usuario por telCelular");

                // Se guarda al usuario en una clase accesible para muchas clases
                Preferences.savePreferenceObjectRealm(mContext, PREFERENCE_USUARIO, task);
            }
        }
        else
            errorConexion();
    }

    public RealmResults<usuario> obtenerTodosUsuarios() {
        realm = conectarBD.conseguirUsuarioMongoDB();
        if(realm!=null){
            return realm.where(usuario.class).isNull("status")
            .and()
            .isNotNull("contrasena").findAll();
        }
        else
            errorConexion();
        return null;
    }

    private void errorConexion(){
        Toast.makeText(mContext, "Hubo un problema en conectarse, intenta mas tarde", Toast.LENGTH_SHORT).show();
    }
}
