package com.example.llegabien.frontend.usuario.fragmento;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;
import static io.realm.Realm.getApplicationContext;

import static android.app.PendingIntent.getActivity;

import android.content.Intent;
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

import com.example.llegabien.R;
import com.example.llegabien.backend.app.Encriptar;
import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.mongoDB.ConectarBD;
import com.example.llegabien.backend.usuario.UsuarioBD_Validaciones;
import com.example.llegabien.backend.usuario.UsuarioInputValidaciones;
import com.example.llegabien.backend.usuario.usuario;
import com.example.llegabien.backend.usuario.UsuarioBD_CRUD;
import com.example.llegabien.frontend.usuario.activity.ActivityPaginaPrincipalUsuario;

import java.util.Locale;

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
        mBtnAceptar = root.findViewById(R.id.button_aceptar_cambiarContra);
        mBtnRegresar = root.findViewById(R.id.button_regresar_cambiarContra);
        mEditTxtActualContraseña = root.findViewById(R.id.editText_actualContra_cambiarContra);
        mEditTxtNuevaContraseña = root.findViewById(R.id.editText_nuevaContra_cambiarContra);
        mEditTxtConfirmarContraseña = root.findViewById(R.id.editText_confirmarContra_cambiarContra);

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

                    startActivity(new Intent(getActivity(), ActivityPaginaPrincipalUsuario.class));

/*                    FragmentoEditarPerfilUsuario fragmentoEditarPerfilUsuario = new FragmentoEditarPerfilUsuario();
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                    fragmentTransaction.replace(R.id.fragment_configuracion, fragmentoEditarPerfilUsuario).commit();
                    fragmentTransaction.addToBackStack(null);*/
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
        UsuarioBD_Validaciones validar = new UsuarioBD_Validaciones(this.getActivity());

        boolean esInputValido = true;
        if (usuarioInputValidaciones.validarContraseña(getActivity(),mEditTxtNuevaContraseña)) {
            if (!usuarioInputValidaciones.validarConfirmarContraseña(mEditTxtNuevaContraseña.getText().toString(), getActivity(), mEditTxtConfirmarContraseña))
                esInputValido = false;
            if (mEditTxtActualContraseña.getText().toString().equals(mEditTxtNuevaContraseña.getText().toString())){
                esInputValido = false;
                Toast.makeText(getApplicationContext(), "La contraseña actual es igual a la nueva contraseña", Toast.LENGTH_LONG).show();
            }
            /*if (!Usuario.getContrasena().equals(encriptarContraseña(mEditTxtActualContraseña.getText().toString()))) {
                esInputValido = false;
                Toast.makeText(getApplicationContext(), "La contraseña actual escrita es incorrecta ", Toast.LENGTH_LONG).show();
                //Log.v("QUICKSTART", "La contraseña actual escrita es incorrecta, mongoDB: " + Usuario.getContrasena() + "P de Android: " + mEditTxtActualContraseña.getText().toString() +"P");
            }*/
            if(!validar.verificarCorreoContrasena(Usuario.getCorreoElectronico(), encriptarContraseña(mEditTxtActualContraseña.getText().toString()), "La contraseña 'actual' no existe")) {
                esInputValido = false;
                Log.v("QUICKSTART", "La contraseña actual escrita es incorrecta, mongoDB: " + Usuario.getContrasena() + "P de Android: "
                        + encriptarContraseña(mEditTxtActualContraseña.getText().toString()) +"P.    correo: " + Usuario.getCorreoElectronico());
            }
        }
        else
            esInputValido = false;

        return esInputValido;
    }

    // Actualizar contraseña del usuario en MongoDB
    private void actualizarUsuario() {
        Usuario.setContrasena(encriptarContraseña(mEditTxtNuevaContraseña.getText().toString()));

        UsuarioBD_CRUD usuarioBD_CRUD = new UsuarioBD_CRUD(this.getContext());
        usuarioBD_CRUD.updateUser(Usuario);
        Toast.makeText(getApplicationContext(), "Contraseña cambiada exitosamente", Toast.LENGTH_SHORT).show();
        mBtnAceptar.setEnabled(false);

        Usuario = usuarioBD_CRUD.readUsuarioPorCorreo(getActivity(), Usuario.getCorreoElectronico(), Usuario.getContrasena());
        Preferences.savePreferenceObjectRealm(getActivity(), PREFERENCE_USUARIO, Usuario);
    }

    // Recibe la contraseña en texto plano y la regresa encriptada
    private static String encriptarContraseña(String textoPlano) {
        return Encriptar.Encriptar(textoPlano);
    }

}