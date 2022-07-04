package com.example.llegabien.frontend.usuario.fragmento;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_ADMIN;
import static com.example.llegabien.backend.app.Preferences.PREFERENCE_EDITANDO_USUARIO_CON_ADMIN;
import static com.example.llegabien.backend.app.Preferences.PREFERENCE_ESTADO_BUTTON_SESION;
import static com.example.llegabien.backend.app.Preferences.PREFERENCE_ES_ADMIN;
import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;

import static io.realm.Realm.getApplicationContext;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.llegabien.R;
import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.usuario.UsuarioInputValidaciones;
import com.example.llegabien.backend.usuario.usuario;
import com.example.llegabien.frontend.app.Utilidades;
import com.example.llegabien.frontend.usuario.activity.ActivityPaginaPrincipalUsuario;
import com.example.llegabien.frontend.usuario.dialog.DialogDatePicker;
import com.example.llegabien.backend.usuario.UsuarioDAO;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class FragmentoEditarPerfilUsuario extends Fragment implements View.OnClickListener {
    private ConstraintLayout mBtnAceptar,  mBtnCambiarContra;
    private EditText mEditTxtNombres, mEditTxtApellidos, mEditTxtFechaNacimiento, mEditTxtNumTelefonico, mEditTxtCorreo, mEditTxtCountryCode;

    usuario Usuario;

    public FragmentoEditarPerfilUsuario() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_editar_perfil_usuario, container, false);

        //wiring up
        mBtnAceptar = root.findViewById(R.id.button2_aceptar_editarPerfil);
        mBtnCambiarContra = root.findViewById(R.id.button1_cambiarContra_editarPerfil);
        Button mBtnEliminarCuenta = root.findViewById(R.id.button_eliminarCuenta_editarPerfil);
        Button mBtnRegresar = root.findViewById(R.id.button_regresar_editarPerfil);
        mEditTxtNombres = root.findViewById(R.id.editText_nombres_editarPerfil);
        mEditTxtApellidos = root.findViewById(R.id.editText_apellidos_editarPerfil);
        mEditTxtFechaNacimiento = root.findViewById(R.id.editText_fechaNacimiento_editarPerfil);
        mEditTxtCorreo = root.findViewById(R.id.editText_correo_editarPerfil);
        mEditTxtNumTelefonico = root.findViewById(R.id.editText_celular_editarPerfil);
        mEditTxtCountryCode = root.findViewById(R.id.editText_celularCountryCode_editarPerfil);

        //listeners
        mBtnAceptar.setOnClickListener(this);
        mBtnCambiarContra.setOnClickListener(this);
        mBtnEliminarCuenta.setOnClickListener(this);
        mBtnRegresar.setOnClickListener(this);

        ajustesAdministrador();
        setDatosUsuario();
        return root;
    }

    private void ajustesAdministrador() {
        if(Preferences.getSavedBooleanFromPreference(this.requireActivity(), PREFERENCE_EDITANDO_USUARIO_CON_ADMIN)) {
            mBtnCambiarContra.setEnabled(false);
            mBtnCambiarContra.setVisibility(View.INVISIBLE);
        }
    }

    //FUNCIONES LISTENERS//

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button1_cambiarContra_editarPerfil) {
            FragmentoCambiarContraUsuario fragmentoCambiarContraUsuario = new FragmentoCambiarContraUsuario();
            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
            fragmentTransaction.replace(R.id.fragment_configuracion, fragmentoCambiarContraUsuario).commit();
            fragmentTransaction.addToBackStack(null);
        }
        else if (view.getId() == R.id.button2_aceptar_editarPerfil) {
            if (validarAllInputs())
                updateUser();
        }
        else if (view.getId() == R.id.button_eliminarCuenta_editarPerfil) {
            deleteUsuario();
            if(!Preferences.getSavedBooleanFromPreference(this.requireActivity(), PREFERENCE_EDITANDO_USUARIO_CON_ADMIN)){
                Preferences.savePreferenceBoolean(requireActivity(), false, PREFERENCE_ESTADO_BUTTON_SESION);
                Preferences.savePreferenceBoolean(requireActivity(), false, PREFERENCE_ES_ADMIN);
                startActivity(new Intent(requireActivity(), ActivityPaginaPrincipalUsuario.class));
            }
            else {
                configuracionesFinalesPreferences();
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        }
        else if (view.getId() == R.id.editText_fechaNacimiento_editarPerfil) {
            DialogDatePicker dialogDatePicker = new DialogDatePicker();
            dialogDatePicker.mostrarDatePickerDialog(mEditTxtFechaNacimiento, this);
            mEditTxtFechaNacimiento.setError(null);
        }
        else if (view.getId() == R.id.button_regresar_editarPerfil) {
            if(Preferences.getSavedBooleanFromPreference(this.requireActivity(), PREFERENCE_EDITANDO_USUARIO_CON_ADMIN))
                configuracionesFinalesPreferences();
            requireActivity().getSupportFragmentManager().popBackStack();
        }
    }

    //OTRAS FUNCIONES//

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean validarAllInputs() {
        UsuarioInputValidaciones usuarioInputValidaciones = new UsuarioInputValidaciones();
        boolean esInputValido = true, esNumTelefonicoValido, esCountryCodeValido;

        esNumTelefonicoValido = usuarioInputValidaciones.validarNumTelefonico(requireActivity(), mEditTxtNumTelefonico);
        esCountryCodeValido = usuarioInputValidaciones.validarNumTelefonico(requireActivity(), mEditTxtCountryCode);

        if (usuarioInputValidaciones.validarNombre(requireActivity(), mEditTxtNombres))
            esInputValido = false;
        if (usuarioInputValidaciones.validarNombre(requireActivity(), mEditTxtApellidos))
            esInputValido = false;
        if (usuarioInputValidaciones.validarFechaNacimiento(requireActivity(), mEditTxtFechaNacimiento))
            esInputValido = false;
        if (usuarioInputValidaciones.validarCorreoElectronico(requireActivity(), mEditTxtCorreo))
            esInputValido = false;
        if (esCountryCodeValido && esNumTelefonicoValido) {
            if (usuarioInputValidaciones.validarNumTelefonico_libphonenumber(requireActivity(), mEditTxtNumTelefonico, mEditTxtCountryCode))
                esInputValido = false;
        } else
            esInputValido = false;

        return esInputValido;
    }

    // Escribir dentro de las EditText los datos previos del usuario
    private void setDatosUsuario() {
        String countryCode, numTel = null;
        Usuario = Preferences.getSavedObjectFromPreference(requireActivity(), PREFERENCE_USUARIO, usuario.class);
        if (Usuario != null) {
            countryCode = Utilidades.obtenerCountryCode(Usuario.getTelCelular());

            if (countryCode != null) {
                numTel = Usuario.getTelCelular().replace(countryCode, "");
            }

            mEditTxtNombres.setText(Usuario.getNombre());
            mEditTxtApellidos.setText(Usuario.getApellidos());
            mEditTxtCorreo.setText(Usuario.getCorreoElectronico());
            mEditTxtNumTelefonico.setText(numTel);
            mEditTxtCountryCode.setText(countryCode);
        }

        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        mEditTxtFechaNacimiento.setText(dateFormat.format(Usuario.getFNacimiento()));
    }

    // Actualizar al objeto user dentro de Android Studio con lo nuevos datos
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateUser() {
        Usuario.setNombre(mEditTxtNombres.getText().toString());
        Usuario.setApellidos(mEditTxtApellidos.getText().toString());
        Usuario.setCorreoElectronico(mEditTxtCorreo.getText().toString());
        Usuario.setTelCelular(mEditTxtCountryCode.getText().toString() + mEditTxtNumTelefonico.getText().toString());

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate localDate = LocalDate.parse(mEditTxtFechaNacimiento.getText().toString(), dateTimeFormatter);
        Usuario.setFNacimiento(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));

        updateUsuario();
    }

    // Actualizar al usuario en MongoDB
    public void updateUsuario() {
        UsuarioDAO usuarioDAO = new UsuarioDAO(this.getContext());
        if (usuarioDAO.updateUser(Usuario)) {
            Toast.makeText(getApplicationContext(), "Datos actualizados con exito", Toast.LENGTH_SHORT).show();

            mBtnAceptar.setEnabled(false);

            configuracionesFinalesPreferences();
        }
    }

    private void configuracionesFinalesPreferences() {
        UsuarioDAO usuarioDAO = new UsuarioDAO(this.getContext());

        // En caso de haber modificado como admin, entonces se reestablece en preferences su usuario
        if(Preferences.getSavedBooleanFromPreference(this.requireActivity(), PREFERENCE_EDITANDO_USUARIO_CON_ADMIN))
            Usuario = Preferences.getSavedObjectFromPreference(requireActivity(), PREFERENCE_ADMIN, usuario.class);

        Usuario = usuarioDAO.readUsuarioPorCorreo(Usuario.getCorreoElectronico());
        Preferences.savePreferenceObjectRealm(requireActivity(), PREFERENCE_USUARIO, Usuario);
        Preferences.savePreferenceBoolean(this.requireActivity(), false, PREFERENCE_EDITANDO_USUARIO_CON_ADMIN);
    }

    // Borrar al usuario en MongoDB
    public void deleteUsuario() {
        UsuarioDAO usuarioDAO = new UsuarioDAO(this.getContext());
        usuarioDAO.deleteUser(Usuario);
        Usuario = null;
    }

}