package com.example.llegabien.frontend.usuario.fragmento;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.llegabien.R;
import com.example.llegabien.frontend.FragmentoAuxiliar;
import com.example.llegabien.frontend.rutas.activity.MapsActivity;
import com.example.llegabien.frontend.usuario.activity.ActivityPaginaPrincipalUsuario;

public class FragmentoConfigUsuario extends Fragment implements View.OnClickListener{

    private ConstraintLayout mBtnContactos, mBtnHistorialRutas, mBtnHistorialReportes, mBtnEditarPerfil;
    private Button mBtnCerrarSesion, mBtnRegresar;

    public FragmentoConfigUsuario() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_configuracion_usuario, container, false);

        //wiring up
        mBtnEditarPerfil = (ConstraintLayout) root.findViewById(R.id.button_editarPerfil_configuracionUsuario);
        mBtnCerrarSesion = (Button) root.findViewById(R.id.button_cerrarSesion_configuracionUsuario);
        mBtnRegresar = (Button) root.findViewById(R.id.button_regresar_configuracionUsuario);


        //listeners
        mBtnEditarPerfil.setOnClickListener(this);
        mBtnCerrarSesion.setOnClickListener(this);
        mBtnRegresar.setOnClickListener(this);


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
                startActivity(new Intent(getActivity(), ActivityPaginaPrincipalUsuario.class));
                break;
            case R.id.button_regresar_configuracionUsuario:
                getActivity().getSupportFragmentManager().popBackStack();
                break;

        }

    }
}