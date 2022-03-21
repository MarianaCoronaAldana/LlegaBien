package com.example.llegabien;
import com.example.llegabien.Preferences;

import static com.example.llegabien.Preferences.PREFERENCE_ESTADO_BUTTON_SESION;
import static com.example.llegabien.Preferences.STRING_PREFERENCES;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;


public class InicioSesion0 extends AppCompatActivity{

    private RadioButton RBsesion;
    private Button Btn_IniciarSesion;
    private boolean isActivateRadioButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion_0);


        if(Preferences.obtenerPreference(this,PREFERENCE_ESTADO_BUTTON_SESION)){
            iniciarActividadSiguiente();
        }

        RBsesion = (RadioButton) findViewById(R.id.RB_MantenerSesion);
        Btn_IniciarSesion = (Button) findViewById(R.id.btn_InicarSesion);

        isActivateRadioButton = RBsesion.isChecked(); //DESACTIVADO

        RBsesion.setOnClickListener(new View.OnClickListener() {
            //ACTIVADO
            @Override
            public void onClick(View v) {
                if(isActivateRadioButton){
                    RBsesion.setChecked(false);
                }
                isActivateRadioButton = RBsesion.isChecked();
            }
        });

        Btn_IniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.savePreferenceBoolean(InicioSesion0.this,RBsesion.isChecked(), PREFERENCE_ESTADO_BUTTON_SESION);
                iniciarActividadSiguiente();
            }
        });
    }
    
    public void iniciarActividadSiguiente(){
        Intent i = new Intent(this,MapsActivity.class);
        startActivity(i);
        finish();
    }

}
