package com.example.llegabien.frontend.usuario.fragmento;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.llegabien.R;
import com.example.llegabien.backend.app.Encriptar;
import com.example.llegabien.backend.usuario.UsuarioBD_Validaciones;
import com.example.llegabien.backend.usuario.UsuarioFirebaseVerificaciones;
import com.example.llegabien.backend.usuario.UsuarioInputValidaciones;
import com.example.llegabien.backend.usuario.UsuarioSharedViewModel;
import com.example.llegabien.backend.usuario.usuario;
import com.example.llegabien.frontend.app.Utilidades;

import java.util.Locale;


public class FragmentoRegistrarUsuario2 extends Fragment implements View.OnClickListener {

    private Button mBtnMostrarContra1, mBtnMostrarContra2;
    private EditText mEditTxtNumTelefonico, mEditTxtCorreo, mEditTxtContrasena, mEditTxtConfirmarContrasena,mEditTxtCountryCode;

    private UsuarioSharedViewModel SharedViewModel;
    usuario Usuario = new usuario();
    UsuarioBD_Validaciones validar = new UsuarioBD_Validaciones(requireActivity());

    public FragmentoRegistrarUsuario2() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedViewModel = new ViewModelProvider(requireActivity()).get(UsuarioSharedViewModel.class);

        final Observer<usuario> nameObserver = user -> {
            Usuario = user;

            Log.v("QUICKSTART", "nombre: " + Usuario.getNombre()
                    + "apellido: " + Usuario.getApellidos() + "ESTOY DENTRO DE REGISTRO2");
        };

        //para usar el mismo ViewModel que los otros fragmentos y compartir informacion
        SharedViewModel.getUsuario().observe(this, nameObserver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_registrar_usuario2, container, false);

        //wiring up
        Button mBtnSiguiente = root.findViewById(R.id.button_siguiente_registro_2);
        Button mBtnRegresar = root.findViewById(R.id.button_regresar_registro_2);
        mBtnMostrarContra1 = root.findViewById(R.id.button_mostrarContra_contraseña_registro_2);
        mBtnMostrarContra2 = root.findViewById(R.id.button_mostrarContra_confirmarContra_registro_2);
        mEditTxtCorreo = root.findViewById(R.id.editText_correo_registro_2);
        mEditTxtNumTelefonico = root.findViewById(R.id.editText_celular_registro_2);
        mEditTxtContrasena = root.findViewById(R.id.editText_contraseña_registro_2);
        mEditTxtConfirmarContrasena = root.findViewById(R.id.editText_confirmarContraseña_registro_2);
        mEditTxtCountryCode = root.findViewById(R.id.editText_celularCountryCode_registro_2);

        //listeners
        mBtnSiguiente.setOnClickListener(this);
        mBtnRegresar.setOnClickListener(this);
        mBtnMostrarContra1.setOnClickListener(this);
        mBtnMostrarContra2.setOnClickListener(this);

        return root;
    }

    // FUNCIONES LISTENER //
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_siguiente_registro_2){
            if (validarAllInputs()){
                if(!validarExistencia()) {

                    //para obtener los datos del fragmento y añadirlos a la clase usuario
                    usuarioConDatos();

                    //REPONER//
                    //para mandar codigo a teléfono y email
                    //enviarCodigos();

                    FragmentoRegistrarUsuario3 fragmentoRegistrarUsuario3 = new FragmentoRegistrarUsuario3();
                    FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                    fragmentTransaction.replace(R.id.fragment_pagina_principal, fragmentoRegistrarUsuario3).commit();
                    fragmentTransaction.addToBackStack(null);
                }
                else
                    Toast.makeText(requireActivity(),"El correo electronico o el numero telefonico ya está registrado",Toast.LENGTH_LONG).show();
            }
        }
        else if (view.getId() == R.id.button_regresar_registro_2)
            requireActivity().getSupportFragmentManager().popBackStack();
        else if (view.getId() == R.id.button_mostrarContra_contraseña_registro_2)
            Utilidades.mostrarContraseña(mEditTxtContrasena,mBtnMostrarContra1,this.requireActivity());
        else if (view.getId() == R.id.button_mostrarContra_confirmarContra_registro_2)
            Utilidades.mostrarContraseña(mEditTxtConfirmarContrasena,mBtnMostrarContra2,this.requireActivity());
    }


    //OTRAS FUNCIONES//

    //Obtiene los datos introducidos por el usuario y los une al objeto usuario con el que se está trabajando
    private void usuarioConDatos() {
        Usuario.setCorreoElectronico(mEditTxtCorreo.getText().toString().toLowerCase(Locale.ROOT).trim());
        Usuario.setTelCelular(mEditTxtCountryCode.getText().toString().trim() + mEditTxtNumTelefonico.getText().toString().trim());
        Usuario.setContrasena(encriptarContrasena(mEditTxtContrasena.getText().toString()));

        SharedViewModel.setUsuario(Usuario);
    }

    private boolean validarAllInputs() {
        UsuarioInputValidaciones usuarioInputValidaciones = new UsuarioInputValidaciones();
        boolean esInputValido = true, esNumTelefonicoValido, esCountryCodeValido;

        esNumTelefonicoValido =  usuarioInputValidaciones.validarNumTelefonico(requireActivity(),mEditTxtNumTelefonico);
        esCountryCodeValido = usuarioInputValidaciones.validarNumTelefonico(requireActivity(), mEditTxtCountryCode);

        if (usuarioInputValidaciones.validarCorreoElectronico(requireActivity(), mEditTxtCorreo))
            esInputValido = false;

        if (esCountryCodeValido && esNumTelefonicoValido) {
            if(usuarioInputValidaciones.validarNumTelefonico_libphonenumber(requireActivity(), mEditTxtNumTelefonico, mEditTxtCountryCode))
                esInputValido = false;
        }
        else
            esInputValido = false;

        if (usuarioInputValidaciones.validarContrasena(requireActivity(), mEditTxtContrasena)){
            if (usuarioInputValidaciones.validarConfirmarContrasena(mEditTxtContrasena.getText().toString(), requireActivity(), mEditTxtConfirmarContrasena))
                esInputValido = false;
        }
        else
            esInputValido = false;

        return esInputValido;
    }

    private void enviarCodigos(){
        String numTelefonico = mEditTxtCountryCode.getText().toString() +
                                            mEditTxtNumTelefonico.getText().toString();

        UsuarioFirebaseVerificaciones usuarioFirebaseVerificaciones = new UsuarioFirebaseVerificaciones(requireActivity());
        usuarioFirebaseVerificaciones.enviarCodigoNumTelefonico((isSMSEnviado, verificationId) -> {
            if (isSMSEnviado){
                usuarioFirebaseVerificaciones.enviarCorreoDeVerificacion(isCorreoEnviado -> {
                    if(isCorreoEnviado){
                        FragmentoRegistrarUsuario3 fragmentoRegistrarUsuario3 = new FragmentoRegistrarUsuario3(verificationId);
                        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                        fragmentTransaction.replace(R.id.fragment_pagina_principal, fragmentoRegistrarUsuario3).commit();
                        fragmentTransaction.addToBackStack(null);
                    }
                }, mEditTxtCorreo.getText().toString(), mEditTxtContrasena.getText().toString());
            }
        }, "+" + numTelefonico);
    }

    private boolean validarExistencia() {
        return validar.validarExistenciaCorreoTelefono(mEditTxtCorreo.getText().toString(), mEditTxtNumTelefonico.getText().toString());
    }

    // Recibe la contraseña en texto plano y la regresa encriptada
    private static String encriptarContrasena(String textoPlano) {
        return Encriptar.EncriptarContrasena(textoPlano);
    }
}