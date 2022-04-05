package com.example.llegabien;

import static com.example.llegabien.permisos.Preferences.PREFERENCE_ESTADO_BUTTON_SESION;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;


import com.bumptech.glide.Glide;
import com.example.llegabien.permisos.Preferences;
import com.example.llegabien.rutas.MapsActivity;
import com.example.llegabien.usuario.fragmento.FragmentoIniciarSesion1;
import com.example.llegabien.usuario.fragmento.FragmentoRegistrarUsuario1;

public class pantallaPrincipalActivity extends AppCompatActivity implements View.OnClickListener {

    //Button
    private Button mBtnCrearCuenta, mBtnIniciarSesion;

    //ImageView
    private ImageView mImgViewGif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal);

        //wiring up
        mBtnIniciarSesion = (Button) findViewById(R.id.button_inicia_sesion_pagina_principal);
        mBtnCrearCuenta = (Button) findViewById(R.id.button_crea_cuenta_pagina_principal);
        mImgViewGif = (ImageView) findViewById(R.id.imageView2_gif_pagina_principal);

        //listeners
        mBtnCrearCuenta.setOnClickListener(this);
        mBtnIniciarSesion.setOnClickListener(this);

        //para añadir el gif utilizanfo la libreria glide
         Glide.with(this).load(R.drawable.gif_pantalla_principal).into(mImgViewGif);
        
    }

    //funcion listener
    @Override
    public void onClick(View view)
    {

        FragmentoIniciarSesion1 fragmentoIniciarSesion1 = new FragmentoIniciarSesion1();
        FragmentoRegistrarUsuario1 fragmentoRegistrarUsuario1 = new FragmentoRegistrarUsuario1();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down,R.anim.slide_up, R.anim.slide_down);
        fragmentTransaction.addToBackStack("text");



        switch (view.getId()) {
            case R.id.button_inicia_sesion_pagina_principal:
                fragmentTransaction.add(R.id.fragment_pantallaPrincipal, fragmentoIniciarSesion1).commit();
                break;
            case R.id.button_crea_cuenta_pagina_principal:
                fragmentTransaction.add(R.id.fragment_pantallaPrincipal, fragmentoRegistrarUsuario1).commit();
                break;
        }
    }

}