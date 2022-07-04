package com.example.llegabien.frontend.usuario.fragmento;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.llegabien.R;
import com.example.llegabien.backend.usuario.UsuarioDAO;
import com.example.llegabien.backend.usuario.UsuarioFirebaseVerificaciones;
import com.example.llegabien.backend.usuario.UsuarioInputValidaciones;

public class FragmentoRestablecerContrasena2 extends Fragment implements View.OnClickListener, TextWatcher {

    private EditText mEditTxtCodigo1, mEditTxtCodigo2, mEditTxtCodigo3, mEditTxtCodigo4, mEditTxtCodigo5, mEditTxtCodigo6;
    private String mNumTelefonico, mVerificacionIdFireBase;

    public FragmentoRestablecerContrasena2() {
        // Required empty public constructor
    }

    public FragmentoRestablecerContrasena2(String numTelefonico, String verificacionIdFireBase) {
        mNumTelefonico = numTelefonico;
        mVerificacionIdFireBase  = verificacionIdFireBase;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_restablecer_contrasena2, container, false);

        //wiring up
        Button mBtnVerificar = root.findViewById(R.id.button_verificar_restablecer_contraseña_2);
        Button mBtnRegresar = root.findViewById(R.id.button_regresar_restablecer_contraseña_2);
        mEditTxtCodigo1 = root.findViewById(R.id.editText1_codigo_restablecer_contraseña_2);
        mEditTxtCodigo2 = root.findViewById(R.id.editText2_codigo_restablecer_contraseña_2);
        mEditTxtCodigo3 = root.findViewById(R.id.editText3_codigo_restablecer_contraseña_2);
        mEditTxtCodigo4 = root.findViewById(R.id.editText4_codigo_restablecer_contraseña_2);
        mEditTxtCodigo5 = root.findViewById(R.id.editText5_codigo_restablecer_contraseña_2);
        mEditTxtCodigo6 = root.findViewById(R.id.editText6_codigo_restablecer_contraseña_2);

        //listeners
        mBtnVerificar.setOnClickListener(this);
        mBtnRegresar.setOnClickListener(this);
        mEditTxtCodigo1.addTextChangedListener(this);
        mEditTxtCodigo2.addTextChangedListener(this);
        mEditTxtCodigo3.addTextChangedListener(this);
        mEditTxtCodigo4.addTextChangedListener(this);
        mEditTxtCodigo5.addTextChangedListener(this);
        mEditTxtCodigo6.addTextChangedListener(this);

        return root;
    }

    //listener function
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_verificar_restablecer_contraseña_2){
            if (validarAllInputs()){
                verificarCodigoNumTelefonico();
            }
        }
        else if(view.getId() == R.id.button_regresar_restablecer_contraseña_2)
            requireActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
        EditText text = (EditText) requireActivity().getCurrentFocus();

        if (text != null && text.length() > 0)
        {
            View next = text.focusSearch(View.FOCUS_RIGHT); // or FOCUS_FORWARD
            if (next != null)
                next.requestFocus();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {}

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    private boolean validarAllInputs(){
        UsuarioInputValidaciones usuarioInputValidaciones = new UsuarioInputValidaciones();
        boolean esInputValido = true;
        if (usuarioInputValidaciones.validarStringVacia(this.requireActivity(), mEditTxtCodigo1))
            esInputValido = false;
        if (usuarioInputValidaciones.validarStringVacia(this.requireActivity(), mEditTxtCodigo2))
            esInputValido = false;
        if (usuarioInputValidaciones.validarStringVacia(this.requireActivity(), mEditTxtCodigo3))
            esInputValido = false;
        if (usuarioInputValidaciones.validarStringVacia(this.requireActivity(), mEditTxtCodigo4))
            esInputValido = false;
        if (usuarioInputValidaciones.validarStringVacia(this.requireActivity(), mEditTxtCodigo5))
            esInputValido = false;
        if (usuarioInputValidaciones.validarStringVacia(this.requireActivity(), mEditTxtCodigo6))
            esInputValido = false;

        return esInputValido;
    }

    private void verificarCodigoNumTelefonico(){
        String codigo =
                mEditTxtCodigo1.getText().toString() +
                        mEditTxtCodigo2.getText().toString() +
                        mEditTxtCodigo3.getText().toString() +
                        mEditTxtCodigo4.getText().toString() +
                        mEditTxtCodigo5.getText().toString() +
                        mEditTxtCodigo6.getText().toString();

        UsuarioFirebaseVerificaciones usuarioFirebaseVerificaciones = new UsuarioFirebaseVerificaciones(requireActivity());
        usuarioFirebaseVerificaciones.validarCodigoNumTelefonico(isNumTelefonicoVerificado -> {
            if (isNumTelefonicoVerificado){
                UsuarioDAO usuarioDAO = new UsuarioDAO(FragmentoRestablecerContrasena2.this.getContext());
                usuarioDAO.readUsuarioPorNumTelefonico(mNumTelefonico);

                FragmentoRestablecerContrasena3 fragmentoRestablecerContrasena3 = new FragmentoRestablecerContrasena3();
                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_right);
                fragmentTransaction.add(R.id.fragment_pagina_principal, fragmentoRestablecerContrasena3).commit();
                fragmentTransaction.addToBackStack(null);
            }
        }, mVerificacionIdFireBase, codigo);
    }
}