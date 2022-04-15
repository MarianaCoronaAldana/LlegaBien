package com.example.llegabien.frontend.usuario.fragmento;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.llegabien.R;
import com.example.llegabien.backend.usuario.CRUD_usuario;
import com.example.llegabien.backend.usuario.UsuarioInputValidaciones;
import com.example.llegabien.backend.usuario.UsuarioRegistro;
import com.example.llegabien.backend.usuario.usuario;

public class FragmentoRegistrarUsuario2 extends Fragment implements View.OnClickListener{

    private Button mBtnSiguiente, mBtnRegresar;
    private EditText mEditTxtNumTelefonico, mEditTxtCorreo, mEditTxtContraseña, mEditTxtConfrimarContraseña;

    private static final String parametro_usuario = "usuario"; //etiqueta
    usuario Usuario;

    public FragmentoRegistrarUsuario2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_registrar_usuario2, container, false);

        //Recibir datos del otro Fragmento
        if (getArguments() != null) {
            Usuario = (usuario) getArguments().getSerializable(parametro_usuario);
            Log.v("QUICKSTART", "nombre" + Usuario.getNombre());
        }
        else
            Log.v("QUICKSTART", "no hay no existe");

        //wiring up
        mBtnSiguiente= (Button) root.findViewById(R.id.button_siguiente_registro_2);
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
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        switch (view.getId()) {
            case R.id.button_siguiente_registro_2:
                if(validarAllInputs()) {

                    //para mandar codigo a teléfono y email
                    UsuarioRegistro usuarioRegistro = new UsuarioRegistro();
                    usuarioRegistro.enviarCodigoNumTelefonico(mEditTxtNumTelefonico.getText().toString(), mEditTxtCorreo.getText().toString(), mEditTxtContraseña.getText().toString(),this);

                    //para cambiar de fragmento
                    FragmentoRegistrarUsuario3 fragmentoRegistrarUsuario3 = new FragmentoRegistrarUsuario3();

                    //obtener datos
                    usuarioConDatos();
                    Bundle args = new Bundle();
                    args.putSerializable(parametro_usuario, Usuario);
                    fragmentoRegistrarUsuario3.setArguments(args);

//TEMPORAL porque faltan los contactos

                    CRUD_usuario.AñadirUser(Usuario);

//TEMPORAL porque faltan los contactos

                    fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                    fragmentTransaction.replace(R.id.fragment_pantallaPrincipal, fragmentoRegistrarUsuario3).commit();
                    fragmentTransaction.addToBackStack(null);

                }
                break;
            case R.id.button_regresar_registro_2:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }
    }

    //otras funciones

    private boolean validarAllInputs(){
        UsuarioInputValidaciones usuarioInputValidaciones = new UsuarioInputValidaciones();
        boolean esInputValido = true;
        if (!usuarioInputValidaciones.validarCorreoElectronico(getActivity(),mEditTxtCorreo))
            esInputValido = false;
        if ( !usuarioInputValidaciones.validarNumTelefonico(getActivity(),mEditTxtNumTelefonico))
            esInputValido = false;
        if (!usuarioInputValidaciones.validarContraseña(getActivity(),mEditTxtContraseña))
            esInputValido = false;
        else if (!usuarioInputValidaciones.validarConfirmarContraseña(mEditTxtContraseña.getText().toString(),getActivity(),mEditTxtConfrimarContraseña))
            esInputValido = false;

        return esInputValido;
    }

    private void usuarioConDatos() {
        Usuario.setCorreoElectronico(mEditTxtCorreo.getText().toString());
        Usuario.setTelCelular(mEditTxtNumTelefonico.getText().toString());
        Usuario.setContrasena(mEditTxtContraseña.getText().toString());
    }

}