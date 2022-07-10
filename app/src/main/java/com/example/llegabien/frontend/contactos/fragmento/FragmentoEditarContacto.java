package com.example.llegabien.frontend.contactos.fragmento;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;

import static io.realm.Realm.getApplicationContext;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.llegabien.R;
import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.contactos.usuario_contacto;
import com.example.llegabien.backend.usuario.UsuarioDAO;
import com.example.llegabien.backend.usuario.UsuarioInputValidaciones;
import com.example.llegabien.backend.usuario.usuario;
import com.example.llegabien.frontend.app.Utilidades;

public class FragmentoEditarContacto extends Fragment implements View.OnClickListener {

    private int mIdContacto;
    private EditText mEditTxtNombreContacto, mEditTxtCountryCode, mEditTxtTelefonoContacto;
    private usuario mUsuario = new usuario();

    public FragmentoEditarContacto() {
    }

    public FragmentoEditarContacto(int idContacto, usuario Usuario) {
        Log.v("QUICKSTART", "Estoy en EDITAR contactos, id: " + mIdContacto);
        mIdContacto = idContacto;
        mUsuario = Usuario;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_editar_contacto, container, false);

        //wiring up
        Button mBtnRegresar = root.findViewById(R.id.button_regresar_editarContacto);
        Button mBtnEditarContacto = root.findViewById(R.id.button_enviarReporte_subirReporteUsuario);
        mEditTxtNombreContacto = root.findViewById(R.id.editText1_nombre_editarContacto);
        mEditTxtCountryCode = root.findViewById(R.id.editText_celularCountryCode_editarContacto);
        mEditTxtTelefonoContacto = root.findViewById(R.id.editText_celular_editarContacto);
        TextView txtViewNumContacto = root.findViewById(R.id.textView_numContacto_editarContacto);

        //listeners
        mBtnRegresar.setOnClickListener(this);
        mBtnEditarContacto.setOnClickListener(this);

        Log.v("QUICKSTART", "Estoy en EDITAR contactos, id: " + mIdContacto);

        // Para imprimir el número de contacto.
        txtViewNumContacto.setText(txtViewNumContacto.getText().toString() + " " + (mIdContacto + 1));

        // Para mostrar los datos del correspondiente contacto.
        mostrarDatos();

        return root;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_regresar_editarContacto)
            requireActivity().getSupportFragmentManager().popBackStack();
        else if (view.getId() == R.id.button_enviarReporte_subirReporteUsuario){
            if(validarAllInputs()){
                actualizarContacto();
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        }
    }

    private void mostrarDatos() {
            String countryCode = Utilidades.obtenerCountryCode(mUsuario.getContacto().get(mIdContacto).getTelCelular());
            String numTel = mUsuario.getContacto().get(mIdContacto).getTelCelular().replace(countryCode, "");

            mEditTxtNombreContacto.setText(mUsuario.getContacto().get(mIdContacto).getNombre());
            mEditTxtCountryCode.setText(countryCode);
            mEditTxtTelefonoContacto.setText(numTel);

    }

    private boolean validarAllInputs() {
        UsuarioInputValidaciones usuarioInputValidaciones = new UsuarioInputValidaciones();
        boolean esInputValido = true, esNumTelefonicoValido, esCountryCodeValido;
        esNumTelefonicoValido =  usuarioInputValidaciones.validarNumTelefonico(requireActivity(),mEditTxtTelefonoContacto);
        esCountryCodeValido = usuarioInputValidaciones.validarNumTelefonico(requireActivity(), mEditTxtCountryCode);
        if (usuarioInputValidaciones.validarNombre(requireActivity(), mEditTxtNombreContacto))
            esInputValido = false;
        if (esCountryCodeValido && esNumTelefonicoValido) {
            if(usuarioInputValidaciones.validarNumTelefonico_libphonenumber(requireActivity(), mEditTxtTelefonoContacto, mEditTxtCountryCode))
                esInputValido = false;
        }
        else
            esInputValido = false;
        return esInputValido;
    }

    private void actualizarContacto(){
        usuario_contacto contacto = new usuario_contacto();
        contacto.setNombre(mEditTxtNombreContacto.getText().toString().trim());
        contacto.setTelCelular(mEditTxtCountryCode.getText().toString()+mEditTxtTelefonoContacto.getText().toString().trim());

        mUsuario.getContacto().set(mIdContacto, contacto);

        UsuarioDAO usuarioDAO = new UsuarioDAO(this.getContext());
        if (usuarioDAO.updateUsuario(mUsuario)) {
            Toast.makeText(getApplicationContext(), "Datos actualizados con exito", Toast.LENGTH_SHORT).show();
            mUsuario = usuarioDAO.readUsuarioPorCorreo(mUsuario.getCorreoElectronico());
            Preferences.savePreferenceObjectRealm(requireActivity(), PREFERENCE_USUARIO, mUsuario);
        }
        else
            Toast.makeText(getApplicationContext(), "Hubo un error, intente mas tarde", Toast.LENGTH_SHORT).show();
    }
}