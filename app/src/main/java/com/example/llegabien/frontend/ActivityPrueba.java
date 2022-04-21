package com.example.llegabien.frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.llegabien.R;
import com.example.llegabien.frontend.usuario.dialog.DialogTipoConfiguracion;

public class ActivityPrueba extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba);
        DialogTipoConfiguracion dialogTipoConfiguracion = new DialogTipoConfiguracion(ActivityPrueba.this);
        dialogTipoConfiguracion.show();
    }
}