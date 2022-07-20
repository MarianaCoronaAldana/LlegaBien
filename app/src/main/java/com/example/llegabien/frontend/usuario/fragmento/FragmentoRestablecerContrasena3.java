package com.example.llegabien.frontend.usuario.fragmento;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;

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
import com.example.llegabien.backend.usuario.UsuarioDAO;
import com.example.llegabien.backend.usuario.UsuarioInputValidaciones;
import com.example.llegabien.backend.usuario.usuario;
import com.example.llegabien.frontend.app.Utilidades;

public class FragmentoRestablecerContrasena3 extends Fragment implements View.OnClickListener {
    private EditText mEditTxtContrasena, mEditTxtConfirmarContrasena;
    private Button mBtnAceptar;
    private Button mBtnMostrarContra1;
    private Button mBtnMostrarContra2;

    public FragmentoRestablecerContrasena3() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_restablecer_contrasena3, container, false);

        //wiring up
        mEditTxtContrasena = root.findViewById(R.id.editText_contraseña_restablecer_contraseña_3);
        mEditTxtConfirmarContrasena = root.findViewById(R.id.editText_confirmarContraseña_restablecer_contraseña_3);
        mBtnAceptar= root.findViewById(R.id.button_aceptar_restablecer_contraseña_3);
        mBtnMostrarContra1= root.findViewById(R.id.button_mostrarContra_contraseña_restablecer_contraseña_3);
        mBtnMostrarContra2 = root.findViewById(R.id.button_mostrarContra_confirmarContra_restablecer_contraseña_3);
        Button mBtnRegresar = root.findViewById(R.id.button_regresar_restablecer_contraseña_3);


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
        if (view.getId() == R.id.button_aceptar_restablecer_contraseña_3){
            if (validarAllInputs())
                actualizarContrasenaUsuario();
        }
       else if (view.getId() == R.id.button_mostrarContra_contraseña_restablecer_contraseña_3)
            Utilidades.mostrarContraseña(mEditTxtContrasena, mBtnMostrarContra1, this.getContext());
        else if (view.getId() == R.id.button_mostrarContra_confirmarContra_restablecer_contraseña_3)
            Utilidades.mostrarContraseña(mEditTxtConfirmarContrasena, mBtnMostrarContra2, this.getContext());
        else if (view.getId() == R.id.button_regresar_restablecer_contraseña_3)
            requireActivity().getSupportFragmentManager().popBackStack();
    }

    private void actualizarContrasenaUsuario() {
        String correo = null;
        usuario Usuario = Preferences.getSavedObjectFromPreference(requireActivity(), PREFERENCE_USUARIO, usuario.class);

        if (Usuario != null) {
            Usuario.setContrasena(Encriptar.EncriptarContrasena(mEditTxtContrasena.getText().toString()));
            correo = Usuario.getCorreoElectronico();
        }

        UsuarioDAO usuarioDAO = new UsuarioDAO(this.getContext());
        if (usuarioDAO.updateUser(Usuario)) {
            Toast.makeText(requireContext(), "Contraseña cambiada exitosamente.", Toast.LENGTH_SHORT).show();

            mBtnAceptar.setEnabled(false);

            Usuario = usuarioDAO.readUsuarioPorCorreo(correo);
            Preferences.savePreferenceObjectRealm(requireActivity(), PREFERENCE_USUARIO, Usuario);

            FragmentoIniciarSesion1 fragmentoIniciarSesion1 = new FragmentoIniciarSesion1();
            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_up, R.anim.slide_down,R.anim.slide_up, R.anim.slide_down)
                    .addToBackStack(null);
            fragmentTransaction.add(R.id.fragment_pagina_principal, fragmentoIniciarSesion1).commit();
        }
    }

    private boolean validarAllInputs() {
        UsuarioInputValidaciones usuarioInputValidaciones = new UsuarioInputValidaciones();
        boolean esInputValido = true;

        if (usuarioInputValidaciones.validarContrasena(requireActivity(), mEditTxtContrasena)){
            if (usuarioInputValidaciones.validarConfirmarContrasena(mEditTxtContrasena.getText().toString(), requireActivity(), mEditTxtConfirmarContrasena))
                esInputValido = false;
        }
        else
            esInputValido = false;

        return esInputValido;
    }


}