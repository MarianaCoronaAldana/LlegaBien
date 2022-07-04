package com.example.llegabien.frontend.usuario.fragmento;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.llegabien.R;
import com.example.llegabien.backend.usuario.UsuarioBD_Validaciones;
import com.example.llegabien.backend.usuario.UsuarioFirebaseVerificaciones;
import com.example.llegabien.backend.usuario.UsuarioInputValidaciones;

public class FragmentoRestablecerContrasena1 extends Fragment implements View.OnClickListener {

    private EditText mEditTxtCountryCode, mEditTxtNumTelefonico;

    public FragmentoRestablecerContrasena1() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_restablecer_contrasena1, container, false);

        //wiring up
        Button mBtnSiguiente = root.findViewById(R.id.button_siguiente_restablecer_contraseña_1);
        mEditTxtCountryCode = root.findViewById(R.id.editText_countryCode_restablecer_contraseña_1);
        mEditTxtNumTelefonico = root.findViewById(R.id.editText_numTelefonico_restablecer_contraseña_1);
        Button mBtnRegresar = root.findViewById(R.id.button_regresar_restablecer_contraseña_1);


        //listeners
        mBtnSiguiente.setOnClickListener(this);
        mBtnRegresar.setOnClickListener(this);

        return root;
    }

    //LISTENERS//

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.button_siguiente_restablecer_contraseña_1){
            if (validarAllInputs()) {
                UsuarioBD_Validaciones mValidar = new UsuarioBD_Validaciones(this.getContext());

                String numTelefonico = mEditTxtCountryCode.getText().toString() +
                        mEditTxtNumTelefonico.getText().toString();

                if (mValidar.validarExistenciaCorreoTelefono(null, numTelefonico))
                    enviarCodigoRestablecerContrasena(numTelefonico);
            }
        }
        else if(view.getId() == R.id.button_regresar_restablecer_contraseña_1)
            requireActivity().getSupportFragmentManager().popBackStack();
    }

    //OTRAS FUNCIONES//

    private boolean validarAllInputs() {
        UsuarioInputValidaciones usuarioInputValidaciones = new UsuarioInputValidaciones();
        boolean esInputValido = true, esNumTelefonicoValido, esCountryCodeValido;

        esNumTelefonicoValido = usuarioInputValidaciones.validarNumTelefonico(requireActivity(), mEditTxtNumTelefonico);
        esCountryCodeValido = usuarioInputValidaciones.validarNumTelefonico(requireActivity(), mEditTxtCountryCode);

        if (esCountryCodeValido && esNumTelefonicoValido) {
            if (usuarioInputValidaciones.validarNumTelefonico_libphonenumber(requireActivity(), mEditTxtNumTelefonico, mEditTxtCountryCode))
                esInputValido = false;
        } else
            esInputValido = false;

        return esInputValido;
    }

    private void enviarCodigoRestablecerContrasena(String numTelefonico) {
        UsuarioFirebaseVerificaciones usuarioFirebaseVerificaciones = new UsuarioFirebaseVerificaciones(requireActivity());
        usuarioFirebaseVerificaciones.enviarCodigoNumTelefonico((isSMSEnviado, verificationId) -> {
            if (isSMSEnviado) {
                FragmentoRestablecerContrasena2 fragmentoRestablecerContrasena2 = new FragmentoRestablecerContrasena2(numTelefonico, verificationId);
                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                fragmentTransaction.replace(R.id.fragment_pagina_principal, fragmentoRestablecerContrasena2).commit();
                fragmentTransaction.addToBackStack(null);
            }
        }, "+" + numTelefonico);
    }
}