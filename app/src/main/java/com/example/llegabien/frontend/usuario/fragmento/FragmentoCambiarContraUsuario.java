package com.example.llegabien.frontend.usuario.fragmento;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;
import static io.realm.Realm.getApplicationContext;

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

import com.example.llegabien.R;
import com.example.llegabien.backend.app.Encriptar;
import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.usuario.UsuarioDAO;
import com.example.llegabien.backend.usuario.UsuarioBD_Validaciones;
import com.example.llegabien.backend.usuario.UsuarioInputValidaciones;
import com.example.llegabien.backend.usuario.usuario;
import com.example.llegabien.frontend.app.Utilidades;
import com.example.llegabien.frontend.usuario.activity.ActivityPaginaPrincipalUsuario;

public class FragmentoCambiarContraUsuario extends Fragment implements View.OnClickListener {

    private Button mBtnAceptar;
    private Button mBtnMostrarContra1;
    private Button mBtnMostrarContra2;
    private Button mBtnMostrarContra3;
    private EditText mEditTxtActualContrasena, mEditTxtNuevaContrasena, mEditTxtConfirmarContrasena;
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
        Button mBtnRegresar = root.findViewById(R.id.button_regresar_cambiarContra);
        mBtnMostrarContra1 = root.findViewById(R.id.button_mostrarContra_contraActual_cambiarContra);
        mBtnMostrarContra2 = root.findViewById(R.id.button_mostrarContra_contraNueva_cambiarContra);
        mBtnMostrarContra3 = root.findViewById(R.id.button_mostrarContra_confirmarContra_cambiarContra);
        mEditTxtActualContrasena = root.findViewById(R.id.editText_actualContra_cambiarContra);
        mEditTxtNuevaContrasena = root.findViewById(R.id.editText_nuevaContra_cambiarContra);
        mEditTxtConfirmarContrasena = root.findViewById(R.id.editText_confirmarContra_cambiarContra);

        //listeners
        mBtnAceptar.setOnClickListener(this);
        mBtnRegresar.setOnClickListener(this);
        mBtnMostrarContra1.setOnClickListener(this);
        mBtnMostrarContra2.setOnClickListener(this);
        mBtnMostrarContra3.setOnClickListener(this);

        return root;
    }

    //FUNCIONES LISTENERS//
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_aceptar_cambiarContra) {
            if (validarAllInputs()) {
                actualizarContrasenaUsuario();
                startActivity(new Intent(requireActivity(), ActivityPaginaPrincipalUsuario.class));
            }
        } else if (view.getId() == R.id.button_regresar_cambiarContra)
            requireActivity().getSupportFragmentManager().popBackStack();

        else if (view.getId() == R.id.button_mostrarContra_contraActual_cambiarContra)
            Utilidades.mostrarContraseña(mEditTxtActualContrasena, mBtnMostrarContra1, this.getContext());

        else if (view.getId() == R.id.button_mostrarContra_contraNueva_cambiarContra)
            Utilidades.mostrarContraseña(mEditTxtNuevaContrasena, mBtnMostrarContra2, this.getContext());

        else if (view.getId() == R.id.button_mostrarContra_confirmarContra_cambiarContra)
            Utilidades.mostrarContraseña(mEditTxtConfirmarContrasena, mBtnMostrarContra3, this.getContext());

    }

    //OTRAS FUNCIONES//
    private boolean validarAllInputs() {
        Usuario = Preferences.getSavedObjectFromPreference(requireActivity(), PREFERENCE_USUARIO, usuario.class);
        UsuarioInputValidaciones usuarioInputValidaciones = new UsuarioInputValidaciones();
        UsuarioBD_Validaciones validar = new UsuarioBD_Validaciones(this.requireActivity());

        boolean esInputValido = true;

        if (usuarioInputValidaciones.validarContrasena(requireActivity(), mEditTxtNuevaContrasena)) {
            if (usuarioInputValidaciones.validarConfirmarContrasena(mEditTxtNuevaContrasena.getText().toString(), requireActivity(), mEditTxtConfirmarContrasena))
                esInputValido = false;

            if (mEditTxtActualContrasena.getText().toString().equals(mEditTxtNuevaContrasena.getText().toString())) {
                esInputValido = false;
                Toast.makeText(this.getContext(), "La contraseña actual es igual a la nueva contraseña", Toast.LENGTH_LONG).show();
            }

            if (!validar.verificarCorreoContrasena(Usuario.getCorreoElectronico(), encriptarContrasena(mEditTxtActualContrasena.getText().toString()), "La contraseña 'actual' no existe")) {
                esInputValido = false;
                Log.v("QUICKSTART", "La contraseña actual escrita es incorrecta, mongoDB: " + Usuario.getContrasena() + "P de Android: "
                        + encriptarContrasena(mEditTxtActualContrasena.getText().toString()) + "P.    correo: " + Usuario.getCorreoElectronico());
            }
        } else
            esInputValido = false;

        return esInputValido;
    }

    // Actualizar contraseña del usuario en MongoDB
    private void actualizarContrasenaUsuario() {
        Usuario.setContrasena(encriptarContrasena(mEditTxtNuevaContrasena.getText().toString()));

        UsuarioDAO usuarioDAO = new UsuarioDAO(this.getContext());
        if (usuarioDAO.updateUser(Usuario)) {
            Toast.makeText(getApplicationContext(), "Contraseña cambiada exitosamente", Toast.LENGTH_SHORT).show();

            mBtnAceptar.setEnabled(false);

            Usuario = usuarioDAO.readUsuarioPorCorreo(Usuario.getCorreoElectronico());
            Preferences.savePreferenceObjectRealm(requireActivity(), PREFERENCE_USUARIO, Usuario);
        }
    }

    // Recibe la contraseña en texto plano y la regresa encriptada
    private static String encriptarContrasena(String textoPlano) {
        return Encriptar.EncriptarContrasena(textoPlano);
    }

}