package com.example.llegabien.frontend.usuario.fragmento;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.llegabien.R;
import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.usuario.usuario;
import com.example.llegabien.frontend.reportes.fragmento.FragmentoListaReportes;
import com.example.llegabien.frontend.reportes.fragmento.FragmentoSubirReporteAdmin;

public class FragmentoConfigAvanAdmin extends Fragment implements View.OnClickListener {
    private ConstraintLayout mBtnOtrosPerfiles;
    private TextView mTxtViewCorreo, mTxtViewNombre;

    public FragmentoConfigAvanAdmin() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_config_avan_admin, container, false);

        //wiring up
        ConstraintLayout mBtnSubirReporte = (ConstraintLayout) root.findViewById(R.id.button_subirReporte_configuracionAdmin);
        Button mBtnConfigUsuario = (Button) root.findViewById(R.id.button_configUsuario_configuracionAdmin);
        Button mBtnRegresar = (Button) root.findViewById(R.id.button_regresar_configuracionAdmin);
        mBtnOtrosPerfiles = (ConstraintLayout) root.findViewById(R.id.button_otrosPerfiles_configuracionAdmin);
        mTxtViewCorreo = root.findViewById(R.id.textView_correoUsuario_configuracionAdmin);
        mTxtViewNombre = root.findViewById(R.id.textView_nombreUsuario_configuracionAdmin);

        //listeners
        mBtnSubirReporte.setOnClickListener(this);
        mBtnConfigUsuario.setOnClickListener(this);
        mBtnRegresar.setOnClickListener(this);
        mBtnOtrosPerfiles.setOnClickListener(this);

        setDatosUsuario();
        return root;
    }

    //FUNCIONES LISTENERS//
    @Override
    public void onClick(View view) {
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        if (view.getId() == R.id.button_subirReporte_configuracionAdmin) {
            FragmentoSubirReporteAdmin fragmentoSubirReporteAdmin = new FragmentoSubirReporteAdmin();
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
            fragmentTransaction.replace(R.id.fragment_configuracion, fragmentoSubirReporteAdmin).commit();
            fragmentTransaction.addToBackStack(null);
        }
        else if (view.getId() == R.id.button_configUsuario_configuracionAdmin) {
            FragmentoConfigUsuario fragmentoConfigUsuario = new FragmentoConfigUsuario();
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
            fragmentTransaction.replace(R.id.fragment_configuracion, fragmentoConfigUsuario).commit();
            fragmentTransaction.addToBackStack(null);
        }
        else if (view.getId() == R.id.button_otrosPerfiles_configuracionAdmin){
            FragmentoBuscarUsuariosAdmin fragmentoListaUsuariosAdmin = new FragmentoBuscarUsuariosAdmin();
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
            fragmentTransaction.replace(R.id.fragment_configuracion, fragmentoListaUsuariosAdmin).commit();
            fragmentTransaction.addToBackStack(null);
        }
        else if (view.getId() == R.id.button_regresar_configuracionAdmin)
            requireActivity().finish();

    }

    // Escribir dentro de las EditText los datos previos del usuario
    private void setDatosUsuario() {
        usuario Usuario = Preferences.getSavedObjectFromPreference(requireActivity(), PREFERENCE_USUARIO, usuario.class);
        if (Usuario != null) {
            mTxtViewCorreo.setText(Usuario.getCorreoElectronico());
            mTxtViewNombre.setText(Usuario.getNombre());
        }

    }

}