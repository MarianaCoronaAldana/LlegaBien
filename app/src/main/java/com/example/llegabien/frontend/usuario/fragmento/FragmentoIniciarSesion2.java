package com.example.llegabien.frontend.usuario.fragmento;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.llegabien.R;

public class FragmentoIniciarSesion2 extends Fragment implements View.OnClickListener{

    private Button mBtnRegresar;

    public FragmentoIniciarSesion2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_iniciar_sesion2, container, false);

        //wiring up
        mBtnRegresar = (Button) root.findViewById(R.id.button_regresar_inicia_sesion_2);

        //listeners
        mBtnRegresar.setOnClickListener(this);

        return root;
    }

    //listener function
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_regresar_inicia_sesion_2:
                getActivity().getSupportFragmentManager().popBackStack();
                break;

        }
    }
}