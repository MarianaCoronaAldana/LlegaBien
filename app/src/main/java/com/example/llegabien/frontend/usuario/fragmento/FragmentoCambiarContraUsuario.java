package com.example.llegabien.frontend.usuario.fragmento;

import static com.example.llegabien.backend.permisos.Preferences.PREFERENCE_USUARIO;
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
import com.example.llegabien.backend.permisos.Preferences;
import com.example.llegabien.backend.usuario.UsuarioInputValidaciones;
import com.example.llegabien.backend.usuario.usuario;
import com.example.llegabien.mongoDB.usuario_BD;
import com.example.llegabien.mongoDB.usuario_validaciones;

public class FragmentoCambiarContraUsuario extends Fragment implements View.OnClickListener{

    private Button mBtnAceptar, mBtnRegresar;
    private EditText mEditTxtActualContraseña, mEditTxtNuevaContraseña, mEditTxtConfirmarContraseña;
    usuario Usuario;

    public FragmentoCambiarContraUsuario() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_cambiar_contra_usuario, container, false);

        //wiring up
        mBtnAceptar = (Button) root.findViewById(R.id.button_aceptar_cambiarContra);
        mBtnRegresar = (Button) root.findViewById(R.id.button_regresar_cambiarContra);
        mEditTxtActualContraseña = (EditText) root.findViewById(R.id.editText_actualContra_cambiarContra);
        mEditTxtNuevaContraseña = (EditText) root.findViewById(R.id.editText_nuevaContra_cambiarContra);
        mEditTxtConfirmarContraseña = (EditText) root.findViewById(R.id.editText_confirmarContra_cambiarContra);

        //listeners
        mBtnAceptar.setOnClickListener(this);
        mBtnRegresar.setOnClickListener(this);

        return root;
    }

    //FUNCIONES LISTENERS//
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_aceptar_cambiarContra:
                if(validarAllInputs()) {
                    actualizarUsuario();

                    FragmentoEditarPerfilUsuario fragmentoEditarPerfilUsuario = new FragmentoEditarPerfilUsuario();
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                    fragmentTransaction.replace(R.id.fragment_configuracion, fragmentoEditarPerfilUsuario).commit();
                    fragmentTransaction.addToBackStack(null);
                }
                break;
            case R.id.button_regresar_cambiarContra:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }
    }

    //OTRAS FUNCIONES//
    private boolean validarAllInputs() {
        Usuario = Preferences.getSavedObjectFromPreference(getActivity(), PREFERENCE_USUARIO, usuario.class);
        UsuarioInputValidaciones usuarioInputValidaciones = new UsuarioInputValidaciones();
        boolean esInputValido = true;
        if (usuarioInputValidaciones.validarContraseña(getActivity(),mEditTxtNuevaContraseña)) {
            if (!usuarioInputValidaciones.validarConfirmarContraseña(mEditTxtNuevaContraseña.getText().toString(), getActivity(), mEditTxtConfirmarContraseña))
                esInputValido = false;
            if (mEditTxtActualContraseña.getText().toString() == mEditTxtNuevaContraseña.getText().toString()){
                esInputValido = false;
                Toast.makeText(getApplicationContext(), "La contraseña actual es igual a la nueva contraseña", Toast.LENGTH_LONG).show();
            }
            /*if (Usuario.getContrasena() != mEditTxtActualContraseña.getText().toString()) {
                esInputValido = false;
                //Toast.makeText(getApplicationContext(), "La contraseña actual escrita es incorrecta ", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), Usuario.getContrasena() + " " +  mEditTxtActualContraseña.getText().toString(), Toast.LENGTH_LONG).show();
                Log.v("QUICKSTART", "La contraseña actual escrita es incorrecta, mongoDB: " + Usuario.getContrasena() + "P de Android: " + mEditTxtActualContraseña.getText().toString() +"P");
            }*/
        }
        else
            esInputValido = false;

        return esInputValido;
    }

    // Actualizar contraseña del usuario en MongoDB
    private void actualizarUsuario() {
        usuario_validaciones validacion =  new usuario_validaciones();
        usuario_BD.UpdateUser(Usuario);
        Toast.makeText(getApplicationContext(), "Contraseña cambiada con éxito", Toast.LENGTH_SHORT).show();
        mBtnAceptar.setEnabled(false);

        Usuario = validacion.conseguirUsuario_porCorreo(getActivity(), Usuario.getCorreoElectronico(), Usuario.getContrasena());
        //Preferences.savePreferenceRealmObject(getActivity(), PREFERENCE_USUARIO, Usuario);
    }

}