package com.example.llegabien.frontend.usuario.fragmento;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_ESTADO_BUTTON_SESION;
import static com.example.llegabien.backend.app.Preferences.PREFERENCE_ES_ADMIN;
import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.llegabien.R;
import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.usuario.usuario;
import com.example.llegabien.frontend.contactos.activity.ActivityEditarLeerContactos;
import com.example.llegabien.frontend.contactos.fragmento.FragmentoLeerContactos;
import com.example.llegabien.frontend.reportes.activity.ActivityReportes;
import com.example.llegabien.frontend.reportes.fragmento.FragmentoListaReportes;
import com.example.llegabien.frontend.contactos.fragmento.FragmentoLeerContactos;
import com.example.llegabien.frontend.usuario.activity.ActivityPaginaPrincipalUsuario;

public class FragmentoConfigUsuario extends Fragment implements View.OnClickListener{

    private ConstraintLayout mBtnContactos, mBtnHistorialRutas, mBtnHistorialReportes, mBtnEditarPerfil;
    private Button mBtnCerrarSesion, mBtnRegresar;
    private TextView mTxtViewCorreo, mTxtViewNombre;

    public FragmentoConfigUsuario() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_configuracion_usuario, container, false);

        //wiring up
        mBtnEditarPerfil = root.findViewById(R.id.button_editarPerfil_configuracionUsuario);
        mBtnCerrarSesion = root.findViewById(R.id.button_cerrarSesion_configuracionUsuario);
        mBtnRegresar = root.findViewById(R.id.button_regresar_configuracionUsuario);
        mBtnHistorialReportes = root.findViewById(R.id.button_historialReportes_configuracionUsuario);
        mBtnContactos = root.findViewById(R.id.button_contactos_configuracionUsuario);
        mTxtViewCorreo = root.findViewById(R.id.textView_correoUsuario_configuracionUsuario);
        mTxtViewNombre = root.findViewById(R.id.textView_nombreUsuario_configuracionUsuario);

        //listeners
        mBtnEditarPerfil.setOnClickListener(this);
        mBtnCerrarSesion.setOnClickListener(this);
        mBtnRegresar.setOnClickListener(this);
        mBtnHistorialReportes.setOnClickListener(this);
        mBtnContactos.setOnClickListener(this);

        setDatosUsuario();

        return root;
    }

    //FUNCIONES LISTENER//
    @Override
    public void onClick(View view) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        switch (view.getId()) {
            case R.id.button_editarPerfil_configuracionUsuario:
                    FragmentoEditarPerfilUsuario fragmentoEditarPerfilUsuario = new FragmentoEditarPerfilUsuario();
                    fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                    fragmentTransaction.replace(R.id.fragment_configuracion, fragmentoEditarPerfilUsuario).commit();
                    fragmentTransaction.addToBackStack(null);
                break;
            case R.id.button_cerrarSesion_configuracionUsuario:
                // Se reinician los datos importantes guardados en Preferences
                Preferences.savePreferenceBoolean(this.getActivity(),false, PREFERENCE_ESTADO_BUTTON_SESION);
                Preferences.savePreferenceBoolean(this.getActivity(), false, PREFERENCE_ES_ADMIN);

                startActivity(new Intent(getActivity(), ActivityPaginaPrincipalUsuario.class));
                break;
            case R.id.button_historialReportes_configuracionUsuario:
                FragmentoListaReportes fragmentoListaReportes = new FragmentoListaReportes();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                fragmentTransaction.replace(R.id.fragment_configuracion, fragmentoListaReportes).commit();
                fragmentTransaction.addToBackStack(null);
                break;
            case R.id.button_regresar_configuracionUsuario:
                getActivity().finish();
                break;
            case R.id.button_contactos_configuracionUsuario:
                startActivity(new Intent(getActivity(), ActivityEditarLeerContactos.class));
                break;
        }

    }

    // Escribir dentro de las EditText los datos previos del usuario
    private void setDatosUsuario() {
        usuario Usuario = Preferences.getSavedObjectFromPreference(getActivity(), PREFERENCE_USUARIO, usuario.class);
        mTxtViewCorreo.setText(Usuario.getCorreoElectronico());
        mTxtViewNombre.setText(Usuario.getNombre());
    }

}