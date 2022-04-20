package com.example.llegabien.frontend.usuario.fragmento;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.llegabien.R;
import com.example.llegabien.backend.usuario.UsuarioFirebaseVerificaciones;
import com.example.llegabien.backend.usuario.UsuarioInputValidaciones;
import com.example.llegabien.backend.usuario.usuario;
import com.example.llegabien.backend.usuario.usuario_SharedViewModel;
import com.example.llegabien.mongoDB.usuario_validaciones;

import java.util.Locale;


public class FragmentoRegistrarUsuario2 extends Fragment implements View.OnClickListener {


    private Button mBtnSiguiente, mBtnRegresar;
    private EditText mEditTxtNumTelefonico, mEditTxtCorreo, mEditTxtContraseña, mEditTxtConfrimarContraseña;

    private usuario_SharedViewModel SharedViewModel;
    usuario Usuario = new usuario();
    usuario_validaciones validar = new usuario_validaciones();

    public FragmentoRegistrarUsuario2() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedViewModel = new ViewModelProvider(requireActivity()).get(usuario_SharedViewModel.class);

        final Observer<usuario> nameObserver = new Observer<usuario>() {
            @Override
            public void onChanged(@Nullable final usuario user) {
                Usuario = user;

                Log.v("QUICKSTART", "nombre: " + Usuario.getNombre()
                        + "apellido: " + Usuario.getApellidos() + "ESTOY DENTRO DE REGISTRO2");
            }
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
        mBtnSiguiente = (Button) root.findViewById(R.id.button_siguiente_registro_2);
        mBtnRegresar = (Button) root.findViewById(R.id.button_regresar_registro_2);
        mEditTxtCorreo = (EditText) root.findViewById(R.id.editText_correo_registro_2);
        mEditTxtNumTelefonico = (EditText) root.findViewById(R.id.editText_celular_registro_2);
        mEditTxtContraseña = (EditText) root.findViewById(R.id.editText_contraseña_registro_2);
        mEditTxtConfrimarContraseña = (EditText) root.findViewById(R.id.editText_confirmarContraseña_registro_2);

        //listeners
        mBtnSiguiente.setOnClickListener(this);
        mBtnRegresar.setOnClickListener(this);

        return root;
    }

    //listener function
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_siguiente_registro_2:
                if (validarAllInputs()){
                    if(!validarExistencia()) {

                        //para obtener los datos del fragmento y añadirlos a la clase usuario
                        usuarioConDatos();

                        //para mandar codigo a teléfono y email
        // REPONER:     enviarCodigos();

        // QUITAR CUANDO SE REPONGA:
        // DE AQUI ->
                        FragmentoRegistrarUsuario3 fragmentoRegistrarUsuario3 = FragmentoRegistrarUsuario3.newInstance("verificationId");
                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                        fragmentTransaction.replace(R.id.fragment_pantallaPrincipal, fragmentoRegistrarUsuario3).commit();
                        fragmentTransaction.addToBackStack(null);
        // A AQUI  ->
                    }
                    else
                            Toast.makeText(getActivity(),"El correo electronico o el numero telefonico ya está registrado",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.button_regresar_registro_2:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }
    }


    //otras funciones

    //Obtiene los datos introducidos por el usuario y los une al objeto usuario con el que se está trabajando
    private void usuarioConDatos() {

        Usuario.setCorreoElectronico(mEditTxtCorreo.getText().toString().toLowerCase(Locale.ROOT));
        Usuario.setTelCelular(mEditTxtNumTelefonico.getText().toString());
        Usuario.setContrasena(mEditTxtContraseña.getText().toString());

        SharedViewModel.setUsuario(Usuario);
    }

    private boolean validarAllInputs() {
        UsuarioInputValidaciones usuarioInputValidaciones = new UsuarioInputValidaciones();
        boolean esInputValido = true;
        if (!usuarioInputValidaciones.validarCorreoElectronico(getActivity(), mEditTxtCorreo))
            esInputValido = false;
        if (!usuarioInputValidaciones.validarNumTelefonico(getActivity(), mEditTxtNumTelefonico))
            esInputValido = false;
        if (!usuarioInputValidaciones.validarContraseña(getActivity(), mEditTxtContraseña))
            esInputValido = false;
        else if (!usuarioInputValidaciones.validarConfirmarContraseña(mEditTxtContraseña.getText().toString(), getActivity(), mEditTxtConfrimarContraseña))
            esInputValido = false;

        return esInputValido;
    }

    private void enviarCodigos (){
        UsuarioFirebaseVerificaciones usuarioFirebaseVerificaciones = new UsuarioFirebaseVerificaciones(this);
        usuarioFirebaseVerificaciones.enviarCodigoNumTelefonico(new UsuarioFirebaseVerificaciones.OnCodigoNumTelefonicoEnviado() {
            @Override
            public void isSMSEnviado(boolean isSMSEnviado, String verificationId) {
                if (isSMSEnviado){
                    usuarioFirebaseVerificaciones.enviarCorreoDeVerificacion(new UsuarioFirebaseVerificaciones.OnCodigoCorreoEnviado() {
                        @Override
                        public void isCorreoEnviado(boolean isCorreoEnviado) {
                            if(isCorreoEnviado){
                                FragmentoRegistrarUsuario3 fragmentoRegistrarUsuario3 = FragmentoRegistrarUsuario3.newInstance(verificationId);
                                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                                fragmentTransaction.replace(R.id.fragment_pantallaPrincipal, fragmentoRegistrarUsuario3).commit();
                                fragmentTransaction.addToBackStack(null);
                            }
                        }
                    }, mEditTxtCorreo.getText().toString(), mEditTxtContraseña.getText().toString());
                }
            }
        }, mEditTxtNumTelefonico.getText().toString());
    }

    private boolean validarExistencia() {
        return validar.validarExistenciaCorreoTelefono(mEditTxtCorreo.getText().toString(), mEditTxtNumTelefonico.getText().toString());
    }
}