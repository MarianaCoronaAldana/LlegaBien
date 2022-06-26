package com.example.llegabien.frontend.usuario.fragmento;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;
import static io.realm.Realm.getApplicationContext;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.llegabien.R;
import com.example.llegabien.backend.app.Encriptar;
import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.usuario.UsuarioBD_CRUD;
import com.example.llegabien.backend.usuario.UsuarioInputValidaciones;
import com.example.llegabien.backend.usuario.usuario;
import com.example.llegabien.frontend.app.Utilidades;

public class FragmentoRestablecerContrasena3 extends Fragment implements View.OnClickListener {
    private EditText mEditTxtContraseña, mEditTxtConfirmarContraseña;
    private Button mBtnAceptar, mBtnMostrarContra1, mBtnMostrarContra2, mBtnRegresar;

    public FragmentoRestablecerContrasena3() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_restablecer_contrasena3, container, false);

        //wiring up
        mEditTxtContraseña = root.findViewById(R.id.editText_contraseña_restablecer_contraseña_3);
        mEditTxtConfirmarContraseña = root.findViewById(R.id.editText_confirmarContraseña_restablecer_contraseña_3);
        mBtnAceptar= root.findViewById(R.id.button_aceptar_restablecer_contraseña_3);
        mBtnMostrarContra1= root.findViewById(R.id.button_mostrarContra_contraseña_restablecer_contraseña_3);
        mBtnMostrarContra2 = root.findViewById(R.id.button_mostrarContra_confirmarContra_restablecer_contraseña_3);
        mBtnRegresar  = root.findViewById(R.id.button_regresar_restablecer_contraseña_3);


        //listeners
        mBtnAceptar.setOnClickListener(this);
        mBtnRegresar.setOnClickListener(this);
        mBtnMostrarContra1.setOnClickListener(this);
        mBtnMostrarContra2.setOnClickListener(this);

        return root;
    }

    //FUNCIONES LISTENERS//

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_aceptar_restablecer_contraseña_3:
                if (validarAllInputs())
                    actualizarContraseñaUsuario();
                break;
            case R.id.button_mostrarContra_contraseña_restablecer_contraseña_3:
                Utilidades.mostrarContraseña(mEditTxtContraseña, mBtnMostrarContra1, this.getContext());
                break;
            case R.id.button_mostrarContra_confirmarContra_restablecer_contraseña_3:
                Utilidades.mostrarContraseña(mEditTxtConfirmarContraseña, mBtnMostrarContra2, this.getContext());
                break;
            case R.id.button_regresar_restablecer_contraseña_3:
                getActivity().getSupportFragmentManager().popBackStack();
                break;

        }
    }

    private void actualizarContraseñaUsuario() {
        usuario Usuario = Preferences.getSavedObjectFromPreference(getActivity(), PREFERENCE_USUARIO, usuario.class);
        Usuario.setContrasena(Encriptar.Encriptar(mEditTxtContraseña.getText().toString()));

        UsuarioBD_CRUD usuarioBD_CRUD = new UsuarioBD_CRUD(this.getContext());
        if (usuarioBD_CRUD.updateUser(Usuario)) {
            Toast.makeText(getApplicationContext(), "Contraseña cambiada exitosamente.", Toast.LENGTH_SHORT).show();

            mBtnAceptar.setEnabled(false);

            Usuario = usuarioBD_CRUD.readUsuarioPorCorreo(Usuario.getCorreoElectronico());
            Preferences.savePreferenceObjectRealm(getActivity(), PREFERENCE_USUARIO, Usuario);

            FragmentoIniciarSesion1 fragmentoIniciarSesion1 = new FragmentoIniciarSesion1();
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_up, R.anim.slide_down,R.anim.slide_up, R.anim.slide_down)
                    .addToBackStack(null);
            fragmentTransaction.add(R.id.fragment_pagina_principal, fragmentoIniciarSesion1).commit();
        }



    }

    private boolean validarAllInputs() {
        UsuarioInputValidaciones usuarioInputValidaciones = new UsuarioInputValidaciones();
        boolean esInputValido = true;

        if (usuarioInputValidaciones.validarContraseña(getActivity(), mEditTxtContraseña)){
            if (!usuarioInputValidaciones.validarConfirmarContraseña(mEditTxtContraseña.getText().toString(), getActivity(), mEditTxtConfirmarContraseña))
                esInputValido = false;
        }
        else
            esInputValido = false;

        return esInputValido;
    }


}