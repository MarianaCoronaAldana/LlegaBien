package com.example.llegabien.frontend.usuario.activity;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_ADMIN;
import static com.example.llegabien.backend.app.Preferences.PREFERENCE_EDITANDO_USUARIO_CON_ADMIN;
import static com.example.llegabien.backend.app.Preferences.PREFERENCE_ESTADO_BUTTON_SESION;
import static com.example.llegabien.backend.app.Preferences.PREFERENCE_GUIAMOSTRADA;
import static com.example.llegabien.backend.app.Preferences.PREFERENCE_MENSAJE_PORBATERIA_ENVIADO;
import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.llegabien.R;
import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.usuario.UsuarioDAO;
import com.example.llegabien.backend.usuario.usuario;
import com.example.llegabien.frontend.mapa.activity.ActivityMap;
import com.example.llegabien.frontend.usuario.fragmento.FragmentoIniciarSesion1;
import com.example.llegabien.frontend.usuario.fragmento.FragmentoRegistrarUsuario1;
import com.example.llegabien.frontend.usuario.fragmento.FragmentoUsuarioGuia1;

public class ActivityPaginaPrincipalUsuario extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagina_principal_usuario);

        if(!Preferences.getSavedBooleanFromPreference(this, PREFERENCE_GUIAMOSTRADA)){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            FragmentoUsuarioGuia1 fragmentoUsuarioGuia1 = new FragmentoUsuarioGuia1();
            fragmentTransaction.add(R.id.fragmentContainerView_guia_pagina_principal, fragmentoUsuarioGuia1).commit();
            Preferences.savePreferenceBoolean(this, true, PREFERENCE_GUIAMOSTRADA);
        }

        //para verificar si el boton de recordar sesion fue presionado y saber si ya se inicio sesion
        if(Preferences.getSavedBooleanFromPreference(this,PREFERENCE_ESTADO_BUTTON_SESION)
            && Preferences.getSavedObjectFromPreference(this, PREFERENCE_USUARIO, usuario.class) != null) {

            if(Preferences.getSavedBooleanFromPreference(this, PREFERENCE_EDITANDO_USUARIO_CON_ADMIN)) {
                UsuarioDAO usuarioDAO = new UsuarioDAO(this);
                usuario Usuario = usuarioDAO.readUsuarioPorCorreo(Preferences.getSavedObjectFromPreference(this, PREFERENCE_ADMIN, usuario.class).getCorreoElectronico());
                Preferences.savePreferenceObjectRealm(this, PREFERENCE_USUARIO, Usuario);
                Preferences.savePreferenceBoolean(this, false, PREFERENCE_EDITANDO_USUARIO_CON_ADMIN);
            }

            startActivity(new Intent(this, ActivityMap.class));
        }

        else
            Preferences.savePreferenceBoolean(this,false, PREFERENCE_MENSAJE_PORBATERIA_ENVIADO);

        //wiring up
        Button mBtnIniciarSesion = (Button) findViewById(R.id.button_inicia_sesion_pagina_principal);
        //Button
        Button mBtnCrearCuenta = (Button) findViewById(R.id.button_crea_cuenta_pagina_principal);
        //ImageView
        ImageView mImgViewGif = (ImageView) findViewById(R.id.imageView2_gif_pagina_principal);

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
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_up, R.anim.slide_down,R.anim.slide_up, R.anim.slide_down)
                .addToBackStack(null);

        if (view.getId() == R.id.button_inicia_sesion_pagina_principal){
            FragmentoIniciarSesion1 fragmentoIniciarSesion1 = new FragmentoIniciarSesion1();
            fragmentTransaction.add(R.id.fragment_pagina_principal, fragmentoIniciarSesion1).commit();
        }

        else if (view.getId() == R.id.button_crea_cuenta_pagina_principal){
            FragmentoRegistrarUsuario1 fragmentoRegistrarUsuario1 = new FragmentoRegistrarUsuario1();
            fragmentTransaction.add(R.id.fragment_pagina_principal, fragmentoRegistrarUsuario1).commit();
        }
    }

    public void hideKeyboard(View view){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
    }
}