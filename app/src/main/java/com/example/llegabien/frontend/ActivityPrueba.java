package com.example.llegabien.frontend;

import static com.example.llegabien.backend.permisos.Preferences.PREFERENCE_ES_ADMIN;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.llegabien.R;
import com.example.llegabien.backend.permisos.Preferences;
import com.example.llegabien.frontend.usuario.activity.ActivityConfiguracionUsuario;
import com.example.llegabien.frontend.usuario.dialog.DialogTipoConfiguracion;

public class ActivityPrueba extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba);

        // Si el usuario es del tipo Administrador, se manda a un lugar distinto que a un usuario normal
        if(Preferences.obtenerPreferenceBool(this, PREFERENCE_ES_ADMIN)){
            DialogTipoConfiguracion dialogTipoConfiguracion = new DialogTipoConfiguracion(ActivityPrueba.this);
            dialogTipoConfiguracion.show();
        }
        else {
            startActivity(new Intent(this, ActivityConfiguracionUsuario.class));
        }

    }
}