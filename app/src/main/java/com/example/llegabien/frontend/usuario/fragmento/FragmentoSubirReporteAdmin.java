package com.example.llegabien.frontend.usuario.fragmento;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.llegabien.R;

public class FragmentoSubirReporteAdmin extends Fragment implements View.OnClickListener{

    private Button mBtnRegresar;

    public FragmentoSubirReporteAdmin() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_subir_reporte_admin, container, false);

        //wiring up
        mBtnRegresar = (Button) root.findViewById(R.id.button_regresar_subirReporte);

        //listeners
        mBtnRegresar.setOnClickListener(this);

        return root;
    }

    //FUNCIONES LISTENERS//
    @Override
    public void onClick(View view) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        switch (view.getId()) {
            case R.id.button_regresar_subirReporte:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }
    }

}