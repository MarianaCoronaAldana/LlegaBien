package com.example.llegabien.frontend.usuario.fragmento;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.llegabien.R;

public class FragmentoConfigAvanAdmin extends Fragment implements View.OnClickListener{
    private Button mBtnConfigUsuario,mBtnRegresar;
    private ConstraintLayout mBtnSubirReporte, mBtnOtrosPerfiles;

    public FragmentoConfigAvanAdmin() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_config_avan_admin, container, false);

        //wiring up
        mBtnSubirReporte = (ConstraintLayout) root.findViewById(R.id.button_subirReporte_configuracionAdmin);
        mBtnConfigUsuario = (Button) root.findViewById(R.id.button_configUsuario_configuracionAdmin);
        mBtnRegresar = (Button) root.findViewById(R.id.button_regresar_configuracionAdmin);


        //listeners
        mBtnSubirReporte.setOnClickListener(this);
        mBtnConfigUsuario.setOnClickListener(this);
        mBtnRegresar.setOnClickListener(this);

        return root;
    }

    //FUNCIONES LISTENERS//
    @Override
    public void onClick(View view) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        switch (view.getId()) {
            case R.id.button_subirReporte_configuracionAdmin:
                FragmentoSubirReporteAdmin fragmentoSubirReporteAdmin = new FragmentoSubirReporteAdmin();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                fragmentTransaction.replace(R.id.fragment_configuracion, fragmentoSubirReporteAdmin).commit();
                fragmentTransaction.addToBackStack(null);
                break;
            case R.id.button_configUsuario_configuracionAdmin:
                FragmentoConfigUsuario fragmentoConfigUsuario = new FragmentoConfigUsuario();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                fragmentTransaction.replace(R.id.fragment_configuracion, fragmentoConfigUsuario).commit();
                fragmentTransaction.addToBackStack(null);
                break;
            case R.id.button_regresar_configuracionAdmin:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }
    }

}