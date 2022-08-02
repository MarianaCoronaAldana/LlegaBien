package com.example.llegabien.backend.usuario;

import static android.app.PendingIntent.FLAG_IMMUTABLE;
import static com.example.llegabien.backend.app.Preferences.PREFERENCE_ALARMANAGER_CONFIGURADO;
import static com.example.llegabien.backend.app.Preferences.PREFERENCE_ES_ADMIN;
import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.mongoDB.ConectarBD;
import com.example.llegabien.backend.reporte.VerificarReportesSemanales;

import java.util.Calendar;

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
    public void validarAdmin(usuario usuario){
        if (usuario.getStatus() != null) {
            Preferences.savePreferenceBoolean(mContext, true, PREFERENCE_ES_ADMIN);
            establecerIntentVerificarReportesSemanales();
        }
            else
            Preferences.savePreferenceBoolean(mContext, false, PREFERENCE_ES_ADMIN);
    }

    private void establecerIntentVerificarReportesSemanales() {
        if(!Preferences.getSavedBooleanFromPreference(mContext, PREFERENCE_ALARMANAGER_CONFIGURADO)){
            AlarmManager alarmMgr = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(mContext, VerificarReportesSemanales.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(mContext, 0, intent, FLAG_IMMUTABLE);

            // Set the alarm to start
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 11);
            calendar.set(Calendar.MINUTE, 15);

            // setRepeating() lets you specify a precise custom interval
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    1000 * 60 * 5, alarmIntent);

            Preferences.savePreferenceBoolean(mContext, true, PREFERENCE_ALARMANAGER_CONFIGURADO);
        }
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
                Log.v("AVER", "NOMBRE DE TASK: " + task.getNombre());
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
