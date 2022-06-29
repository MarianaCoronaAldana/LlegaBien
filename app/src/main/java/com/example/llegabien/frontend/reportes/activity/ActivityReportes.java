package com.example.llegabien.frontend.reportes.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.os.Bundle;

import com.example.llegabien.R;
import com.example.llegabien.frontend.contactos.fragmento.FragmentoEditarContacto;
import com.example.llegabien.frontend.mapa.activity.ActivityMap;
import com.example.llegabien.frontend.reportes.fragmento.FragmentoListaReportes;
import com.example.llegabien.frontend.usuario.activity.ActivityConfiguracionUsuario;

public class ActivityReportes extends AppCompatActivity {

    private Activity mActivityAnterior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportes);
    }
}