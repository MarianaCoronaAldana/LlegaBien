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
import android.widget.EditText;
import android.widget.RadioButton;

import com.example.llegabien.backend.usuario.UsuarioInputValidaciones;
import com.example.llegabien.backend.usuario.UsuarioFirebaseVerificaciones;
import com.example.llegabien.frontend.FragmentoAuxiliar;
import com.example.llegabien.backend.permisos.Preferences;
import com.example.llegabien.R;
import com.example.llegabien.frontend.rutas.activity.MapsActivity;

public class FragmentoIniciarSesion1 extends Fragment implements View.OnClickListener{
    private RadioButton mBtnRecoradrSesion;
    private Button mBtnIniciarSesion, mBtnContraseñaOlvidada, mBtnCerrar, mBtnRegistrarse;
    private EditText mEditTxtCorreo, mEditTxtContraseña;
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
        mEditTxtCorreo = (EditText) root.findViewById(R.id.editText_correo_inicia_sesion_1);
        mEditTxtContraseña = (EditText) root.findViewById(R.id.editText_contraseña_inicia_sesion_1);

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
                //para validar si los campos no están vacios
                if (validarAllInputs()){
                    //para validar si el correo está confirmado
                    enviarCodigos();
                }
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

    //otras funciones
    private boolean validarAllInputs(){
        UsuarioInputValidaciones usuarioInputValidaciones = new UsuarioInputValidaciones();
        boolean esInputValido = true;
        if (!usuarioInputValidaciones.validarStringVacia(this.getActivity(),mEditTxtCorreo))
            esInputValido = false;
        if ( !usuarioInputValidaciones.validarStringVacia(this.getActivity(),mEditTxtContraseña))
            esInputValido = false;

        return esInputValido;
    }

    private void enviarCodigos(){
        UsuarioFirebaseVerificaciones usuarioFirebaseVerificaciones = new UsuarioFirebaseVerificaciones(this);
        usuarioFirebaseVerificaciones.validarCorreoVerificado(new UsuarioFirebaseVerificaciones.OnCorreoVerificado() {
            @Override
            public void isCorreoVerificado(boolean isCorreoVerificado) {
                if (isCorreoVerificado)
                    startActivity(new Intent(getActivity(), MapsActivity.class));
            }
        }, mEditTxtCorreo.getText().toString(), mEditTxtContraseña.getText().toString());
    }
}