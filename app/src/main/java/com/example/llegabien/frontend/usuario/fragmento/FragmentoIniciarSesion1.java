package com.example.llegabien.frontend.usuario.fragmento;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_ESTADO_BUTTON_SESION;
import static com.example.llegabien.backend.app.Preferences.PREFERENCE_ES_ADMIN;
import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;
import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.llegabien.R;
import com.example.llegabien.backend.app.Encriptar;
import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.mongoDB.ConectarBD;
import com.example.llegabien.backend.usuario.UsuarioFirebaseVerificaciones;
import com.example.llegabien.backend.usuario.UsuarioInputValidaciones;
import com.example.llegabien.backend.usuario.usuario;
import com.example.llegabien.frontend.app.fragmento.FragmentoAuxiliar;
import com.example.llegabien.frontend.app.Utilidades;
import com.example.llegabien.frontend.mapa.activity.ActivityMap;
import com.example.llegabien.backend.usuario.UsuarioBD_Validaciones;

import java.util.Locale;

public class FragmentoIniciarSesion1 extends Fragment implements View.OnClickListener{
    private RadioButton mBtnRecordarSesion;
    private Button mBtnMostrarContra;
    private EditText mEditTxtCorreo, mEditTxtContrasena;
    private boolean isActivateRadioButton;
    private final ConectarBD mConectarBD = new ConectarBD();
    private UsuarioBD_Validaciones mValidar;

    public FragmentoIniciarSesion1() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_iniciar_sesion1, container, false);

        //wiring up
        mBtnRecordarSesion = root.findViewById(R.id.radioBtn_recordar_inicia_sesion_1);
        Button mBtnIniciarSesion = root.findViewById(R.id.button_inicia_inicia_sesion_1);
        Button mBtnContrasenaOlvidada = root.findViewById(R.id.button_contraseña_olvidada_inicia_sesion_1);
        mBtnMostrarContra = root.findViewById(R.id.button_mostrarContra_contraseña_inicia_sesion_1);
        Button mBtnCerrar = root.findViewById(R.id.button_cerrar_inicia_sesion_1);
        Button mBtnRegistrarse = root.findViewById(R.id.button_registrarse_inicia_sesion_1);
        mEditTxtCorreo = root.findViewById(R.id.editText_correo_inicia_sesion_1);
        mEditTxtContrasena = root.findViewById(R.id.editText_contraseña_inicia_sesion_1);

        //listeners
        mBtnRecordarSesion.setOnClickListener(this);
        mBtnIniciarSesion.setOnClickListener(this);
        mBtnContrasenaOlvidada.setOnClickListener(this);
        mBtnCerrar.setOnClickListener(this);
        mBtnRegistrarse.setOnClickListener(this);
        mBtnMostrarContra.setOnClickListener(this);

        //Para inicializar la clase UsuarioBD_Validaciones
        mValidar = new UsuarioBD_Validaciones(this.requireActivity());

        isActivateRadioButton = mBtnRecordarSesion.isChecked(); //DESACTIVADO

        mConectarBD.conectarAnonimoMongoDB();

        return root;
    }

    //listener function
    @Override
    public void onClick(View view) {
        FragmentoAuxiliar fragmentoAuxiliar = new FragmentoAuxiliar();
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        if (view.getId() == R.id.radioBtn_recordar_inicia_sesion_1){
            if (isActivateRadioButton)
                mBtnRecordarSesion.setChecked(false);

            isActivateRadioButton = mBtnRecordarSesion.isChecked();
        }
        else if (view.getId() == R.id.button_inicia_inicia_sesion_1){
            Preferences.savePreferenceBoolean(this.requireActivity(),mBtnRecordarSesion.isChecked(), PREFERENCE_ESTADO_BUTTON_SESION);
            if (validarAllInputs()) {
                verificarCorreoContrasena();
            }
        }
        else if (view.getId() == R.id.button_registrarse_inicia_sesion_1){
            FragmentoRegistrarUsuario1 fragmentoRegistrarUsuario1 = new FragmentoRegistrarUsuario1();
            fragmentTransaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);
            fragmentTransaction.replace(R.id.fragment_pagina_principal, fragmentoRegistrarUsuario1).commit();
        }
        else if (view.getId() == R.id.button_contraseña_olvidada_inicia_sesion_1){
            FragmentoRestablecerContrasena1 fragmentoRestablecerContrasena1 = new FragmentoRestablecerContrasena1();
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_right);
            fragmentTransaction.replace(R.id.fragment_pagina_principal,fragmentoRestablecerContrasena1).commit();
            fragmentTransaction.addToBackStack(null);
        }
        else if (view.getId() == R.id.button_cerrar_inicia_sesion_1){
            fragmentTransaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);
            fragmentTransaction.replace(R.id.fragment_pagina_principal,fragmentoAuxiliar).commit();
            fragmentTransaction.remove(fragmentoAuxiliar);
        }
        else if (view.getId() == R.id.button_mostrarContra_contraseña_inicia_sesion_1)
            Utilidades.mostrarContraseña(mEditTxtContrasena, mBtnMostrarContra, this.requireActivity());
    }


    //OTRAS FUNCIONES//

    private boolean validarAllInputs(){
        UsuarioInputValidaciones usuarioInputValidaciones = new UsuarioInputValidaciones();
        boolean esInputValido = true;
        if (usuarioInputValidaciones.validarStringVacia(this.requireActivity(), mEditTxtCorreo))
            esInputValido = false;
        if (usuarioInputValidaciones.validarStringVacia(this.requireActivity(), mEditTxtContrasena))
            esInputValido = false;

        return esInputValido;
    }

    private void verificarCorreoVerificado(){
        UsuarioFirebaseVerificaciones usuarioFirebaseVerificaciones = new UsuarioFirebaseVerificaciones(requireActivity());
        usuarioFirebaseVerificaciones.validarCorreoVerificado(isCorreoVerificado -> {
            if (isCorreoVerificado)
                startActivity(new Intent(requireActivity(), ActivityMap.class));
        }, mEditTxtCorreo.getText().toString(), mEditTxtContrasena.getText().toString());
    }


    private void verificarCorreoContrasena() {
        if(mValidar.verificarCorreoContrasena(mEditTxtCorreo.getText().toString().toLowerCase(Locale.ROOT), encriptarContrasena(mEditTxtContrasena.getText().toString()), "El correo electronico o la contraseña son incorrectos. ")){
            usuario usuario = Preferences.getSavedObjectFromPreference(this.requireContext(), PREFERENCE_USUARIO, com.example.llegabien.backend.usuario.usuario.class);
            if (usuario!= null) {
                mValidar.validarAdmin(usuario);
                verificarCorreoVerificado();
            }
        }
    }

    // Recibe la contraseña en texto plano y la regresa encriptada
    private static String encriptarContrasena(String textoPlano) {
        return Encriptar.EncriptarContrasena(textoPlano);
    }
}