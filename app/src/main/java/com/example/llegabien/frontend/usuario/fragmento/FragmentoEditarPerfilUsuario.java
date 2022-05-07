package com.example.llegabien.frontend.usuario.fragmento;

import static com.example.llegabien.backend.permisos.Preferences.PREFERENCE_USUARIO;

import static io.realm.Realm.getApplicationContext;

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
import com.example.llegabien.backend.permisos.Preferences;
import com.example.llegabien.backend.usuario.UsuarioInputValidaciones;
import com.example.llegabien.backend.usuario.usuario;
import com.example.llegabien.frontend.usuario.activity.ActivityPaginaPrincipalUsuario;
import com.example.llegabien.frontend.usuario.dialog.DialogDatePicker;
import com.example.llegabien.mongoDB.usuario_BD;
import com.example.llegabien.mongoDB.usuario_validaciones;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class FragmentoEditarPerfilUsuario extends Fragment implements View.OnClickListener{
    private ConstraintLayout mBtnCambiarContra, mBtnAceptar;
    private Button mBtnEliminarCuenta, mBtnRegresar;
    private EditText mEditTxtNombres, mEditTxtApellidos, mEditTxtFechaNacimiento, mEditTxtNumTelefonico, mEditTxtCorreo;

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
        mBtnAceptar = (ConstraintLayout) root.findViewById(R.id.button2_aceptar_editarPerfil);
        mBtnCambiarContra  = (ConstraintLayout) root.findViewById(R.id.button1_cambiarContra_editarPerfil);
        mBtnEliminarCuenta  = (Button) root.findViewById(R.id.button_eliminarCuenta_editarPerfil);
        mBtnRegresar = (Button) root.findViewById(R.id.button_regresar_editarPerfil);
        mEditTxtNombres = (EditText) root.findViewById(R.id.editText_nombres_editarPerfil);
        mEditTxtApellidos = (EditText) root.findViewById(R.id.editText_apellidos_editarPerfil);
        mEditTxtFechaNacimiento = (EditText) root.findViewById(R.id.editText_fechaNacimiento_editarPerfil);
        mEditTxtCorreo = (EditText) root.findViewById(R.id.editText_correo_editarPerfil);
        mEditTxtNumTelefonico = (EditText) root.findViewById(R.id.editText_celular_editarPerfil);

        //listeners
        mBtnAceptar.setOnClickListener(this);
        mBtnCambiarContra.setOnClickListener(this);
        mBtnEliminarCuenta.setOnClickListener(this);
        mBtnRegresar.setOnClickListener(this);

        setDatosUsuario();
        return root;
    }

    //FUNCIONES LISTENERS//
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button1_cambiarContra_editarPerfil:
                FragmentoCambiarContraUsuario fragmentoCambiarContraUsuario = new FragmentoCambiarContraUsuario();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                fragmentTransaction.replace(R.id.fragment_configuracion, fragmentoCambiarContraUsuario).commit();
                fragmentTransaction.addToBackStack(null);
                break;
            case R.id.button2_aceptar_editarPerfil:
                if (validarAllInputs()){
                    updateUser();
                }
                break;
            case R.id.button_eliminarCuenta_editarPerfil:
                deleteUsuario();
                startActivity(new Intent(getActivity(), ActivityPaginaPrincipalUsuario.class));
                break;
            case R.id.editText_fechaNacimiento_editarPerfil:
                DialogDatePicker dialogDatePicker = new DialogDatePicker();
                dialogDatePicker.mostrarDatePickerDialog(mEditTxtFechaNacimiento, this);
                mEditTxtFechaNacimiento.setError(null);
                break;
            case R.id.button_regresar_editarPerfil:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }
    }

    //OTRAS FUNCIONES//
    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean validarAllInputs() {
        UsuarioInputValidaciones usuarioInputValidaciones = new UsuarioInputValidaciones();
        boolean esInputValido = true;
        if (!usuarioInputValidaciones.validarNombre(getActivity(),mEditTxtNombres))
            esInputValido = false;
        if ( !usuarioInputValidaciones.validarNombre(getActivity(),mEditTxtApellidos))
            esInputValido = false;
        if (!usuarioInputValidaciones.validarFechaNacimiento(getActivity(),mEditTxtFechaNacimiento))
            esInputValido = false;
        if (!usuarioInputValidaciones.validarCorreoElectronico(getActivity(), mEditTxtCorreo))
            esInputValido = false;
        if (!usuarioInputValidaciones.validarNumTelefonico(getActivity(), mEditTxtNumTelefonico))
            esInputValido = false;

        return esInputValido;
    }

    // Escribir dentro de las EditText los datos previos del usuario
    private void setDatosUsuario() {
        Usuario = Preferences.getSavedObjectFromPreference(getActivity(), PREFERENCE_USUARIO, usuario.class);
        mEditTxtNombres.setText(Usuario.getNombre());
        mEditTxtApellidos.setText(Usuario.getApellidos());
        mEditTxtCorreo.setText(Usuario.getCorreoElectronico());
        mEditTxtNumTelefonico.setText(Usuario.getTelCelular());

        DateFormat dateFormat = new SimpleDateFormat("dd / M / yyyy");
        mEditTxtFechaNacimiento.setText(dateFormat.format(Usuario.getFNacimiento()));
    }

    // Actualizar al objeto user dentro de Android Studio con lo nuevos datos
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateUser() {
        Usuario.setNombre(mEditTxtNombres.getText().toString());
        Usuario.setApellidos(mEditTxtApellidos.getText().toString());
        Usuario.setCorreoElectronico(mEditTxtCorreo.getText().toString());
        Usuario.setTelCelular(mEditTxtNumTelefonico.getText().toString());

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd / M / yyyy");
        LocalDate localDate =  LocalDate.parse(mEditTxtFechaNacimiento.getText().toString(), dateTimeFormatter);
        Usuario.setFNacimiento(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));

        updateUsuario();
    }

    // Actualizar al usuario en MongoDB
    public void updateUsuario(){
        usuario_validaciones validacion =  new usuario_validaciones();

        usuario_BD.UpdateUser(Usuario);
        mBtnAceptar.setEnabled(false);

        Usuario = validacion.conseguirUsuario_porCorreo(getActivity(), Usuario.getCorreoElectronico(), Usuario.getContrasena());
        //Preferences.savePreferenceRealmObject(getActivity(), PREFERENCE_USUARIO, Usuario);
    }

    // Borrar al usuario en MongoDB
    public void deleteUsuario(){
        usuario_BD.DeleteUser(Usuario);
        Toast.makeText(getApplicationContext(), "Cuenta eliminada con exito", Toast.LENGTH_SHORT).show();
        Usuario = null;
    }

}