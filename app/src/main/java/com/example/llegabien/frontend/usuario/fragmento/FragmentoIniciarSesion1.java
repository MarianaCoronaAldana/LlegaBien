package com.example.llegabien.frontend.usuario.fragmento;

import static com.example.llegabien.backend.permisos.Preferences.PREFERENCE_ESTADO_BUTTON_SESION;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;

import com.example.llegabien.frontend.FragmentoAuxiliar;
import com.example.llegabien.backend.permisos.Preferences;
import com.example.llegabien.R;
import com.example.llegabien.frontend.rutas.activity.MapsActivity;

public class FragmentoIniciarSesion1 extends Fragment implements View.OnClickListener{
    private RadioButton mBtnRecoradrSesion;
    private Button mBtnIniciarSesion, mBtnContraseñaOlvidada, mBtnCerrar, mBtnRegistrarse;
    private boolean isActivateRadioButton;

    public FragmentoIniciarSesion1() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_iniciar_sesion1, container, false);

        //para verificar si el boton de recordar contraseña fue presionado
        if(Preferences.obtenerPreference(this,PREFERENCE_ESTADO_BUTTON_SESION)){
            startActivity(new Intent(getActivity(), MapsActivity.class));
        }

        //wiring up
        mBtnRecoradrSesion = (RadioButton) root.findViewById(R.id.radioBtn_recordar_inicia_sesion_1);
        mBtnIniciarSesion = (Button) root.findViewById(R.id.button_inicia_inicia_sesion_1);
        mBtnContraseñaOlvidada = (Button) root.findViewById(R.id.button_contraseña_olvidada_inicia_sesion_1);
        mBtnCerrar = (Button) root.findViewById(R.id.button_cerrar_inicia_sesion_1);
        mBtnRegistrarse = (Button) root.findViewById(R.id.button_registrarse_inicia_sesion_1);

        //listeners
        mBtnRecoradrSesion.setOnClickListener(this);
        mBtnIniciarSesion.setOnClickListener(this);
        mBtnContraseñaOlvidada.setOnClickListener(this);
        mBtnCerrar.setOnClickListener(this);
        mBtnRegistrarse.setOnClickListener(this);

        isActivateRadioButton = mBtnRecoradrSesion.isChecked(); //DESACTIVADO

        return root;
    }

    //listener function
    @Override
    public void onClick(View view) {
        FragmentoAuxiliar fragmentoAuxiliar = new FragmentoAuxiliar();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        switch (view.getId()) {
            case R.id.radioBtn_recordar_inicia_sesion_1:
                //ACTIVADO
                if (isActivateRadioButton) {
                    mBtnRecoradrSesion.setChecked(false);
                }
                isActivateRadioButton = mBtnRecoradrSesion.isChecked();
                break;
            case R.id.button_inicia_inicia_sesion_1:
                Preferences.savePreferenceBoolean(FragmentoIniciarSesion1.this,mBtnRecoradrSesion.isChecked(), PREFERENCE_ESTADO_BUTTON_SESION);
                startActivity(new Intent(getActivity(), MapsActivity.class));
                break;
            case R.id.button_registrarse_inicia_sesion_1:
                FragmentoRegistrarUsuario1 fragmentoRegistrarUsuario1 = new FragmentoRegistrarUsuario1();
                fragmentTransaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);
                fragmentTransaction.replace(R.id.fragment_pantallaPrincipal,fragmentoRegistrarUsuario1).commit();
                break;
            case R.id.button_contraseña_olvidada_inicia_sesion_1:
                FragmentoIniciarSesion2 fragmentoIniciarSesion2 = new FragmentoIniciarSesion2();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_right);
                fragmentTransaction.replace(R.id.fragment_pantallaPrincipal,fragmentoIniciarSesion2).commit();
                fragmentTransaction.addToBackStack(null);
                break;
            case R.id.button_cerrar_inicia_sesion_1:
                fragmentTransaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);
                fragmentTransaction.replace(R.id.fragment_pantallaPrincipal,fragmentoAuxiliar).commit();
                fragmentTransaction.remove(fragmentoAuxiliar);
                break;

        }
    }
}