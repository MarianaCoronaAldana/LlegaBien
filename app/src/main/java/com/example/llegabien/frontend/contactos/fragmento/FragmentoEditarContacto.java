package com.example.llegabien.frontend.contactos.fragmento;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;

import static io.realm.Realm.getApplicationContext;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.llegabien.R;
import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.contactos.usuario_contacto;
import com.example.llegabien.backend.usuario.UsuarioBD_CRUD;
import com.example.llegabien.backend.usuario.UsuarioInputValidaciones;
import com.example.llegabien.backend.usuario.usuario;

public class FragmentoEditarContacto extends Fragment implements View.OnClickListener {

    private int mIdContacto;
    private Button mBtnRegresar, mBtnEditarContacto;
    private EditText mEditTxtNombreContacto, mEditTxtCountryCode, mEditTxtTelefonoContacto;
    private String mStringIdContacto;
    private usuario mUsuario = new usuario();

    public FragmentoEditarContacto() {
    }

    public FragmentoEditarContacto(int idContacto, usuario Usuario) {
        Log.v("QUICKSTART", "Estoy en EDITAR contactos, id: " + mIdContacto);
        mStringIdContacto = String.valueOf(idContacto);
        mIdContacto = idContacto;
        mUsuario = Usuario;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_editar_contacto, container, false);

        //wiring up
        mBtnRegresar = root.findViewById(R.id.button_regresar_editarContacto);
        mBtnEditarContacto = root.findViewById(R.id.button_enviarReporte_subirReporteUsuario);
        mEditTxtNombreContacto = root.findViewById(R.id.editText1_nombre_editarContacto);
        mEditTxtCountryCode = root.findViewById(R.id.editText_celularCountryCode_editarContacto);
        mEditTxtTelefonoContacto = root.findViewById(R.id.editText_celular_editarContacto);

        //listeners
        mBtnRegresar.setOnClickListener(this);
        mBtnEditarContacto.setOnClickListener(this);

        Log.v("QUICKSTART", "Estoy en EDITAR contactos, id: " + mIdContacto);

        mostrarDatos();

        return root;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_regresar_editarContacto:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
            case R.id.button_enviarReporte_subirReporteUsuario:
                if(validarAllInputs()){
                    actualizarContacto();
                    getActivity().getSupportFragmentManager().popBackStack();
                }
                break;
        }
    }

    private void mostrarDatos() {
        mEditTxtNombreContacto.setText(mUsuario.getContacto().get(mIdContacto).getNombre());
        mEditTxtCountryCode.setText(mUsuario.getContacto().get(mIdContacto).getTelCelular().substring(0, 2));
        mEditTxtTelefonoContacto.setText(mUsuario.getContacto().get(mIdContacto).getTelCelular().substring(2));
    }

    private boolean validarAllInputs() {
        UsuarioInputValidaciones usuarioInputValidaciones = new UsuarioInputValidaciones();
        boolean esInputValido = true, esNumTelefonicoValido, esCountryCodeValido;
        esNumTelefonicoValido =  usuarioInputValidaciones.validarNumTelefonico(getActivity(),mEditTxtTelefonoContacto);
        esCountryCodeValido = usuarioInputValidaciones.validarNumTelefonico(getActivity(), mEditTxtCountryCode);
        if (!usuarioInputValidaciones.validarNombre(getActivity(),mEditTxtNombreContacto))
            esInputValido = false;
        if (esCountryCodeValido && esNumTelefonicoValido) {
            if(!usuarioInputValidaciones.validarNumTelefonico_libphonenumber(getActivity(),mEditTxtTelefonoContacto,mEditTxtCountryCode))
                esInputValido = false;
        }
        else
            esInputValido = false;
        return esInputValido;
    }

    private void actualizarContacto(){
        usuario_contacto contacto = new usuario_contacto();
        contacto.setNombre(mEditTxtNombreContacto.getText().toString());
        contacto.setTelCelular(mEditTxtCountryCode.getText().toString()+mEditTxtTelefonoContacto.getText().toString());

        mUsuario.getContacto().set(mIdContacto, contacto);

        UsuarioBD_CRUD usuarioBD_CRUD = new UsuarioBD_CRUD(this.getContext());
        if (usuarioBD_CRUD.updateUser(mUsuario)) {
            Toast.makeText(getApplicationContext(), "Datos actualizados con exito", Toast.LENGTH_SHORT).show();
            mUsuario = usuarioBD_CRUD.readUsuarioPorCorreo(mUsuario.getCorreoElectronico());
            Preferences.savePreferenceObjectRealm(getActivity(), PREFERENCE_USUARIO, mUsuario);
        }
        else
            Toast.makeText(getApplicationContext(), "Hubo un error, intente mas tarde", Toast.LENGTH_SHORT).show();
    }
}