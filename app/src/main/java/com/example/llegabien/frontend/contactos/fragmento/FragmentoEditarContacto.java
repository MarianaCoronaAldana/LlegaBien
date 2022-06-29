package com.example.llegabien.frontend.contactos.fragmento;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainer;
import androidx.fragment.app.FragmentContainerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.llegabien.R;

public class FragmentoEditarContacto extends Fragment implements View.OnClickListener {

    private int mIdContacto;
    private Button mBtnRegresar;

    public FragmentoEditarContacto(int idContacto) {
        mIdContacto = idContacto;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_editar_contacto, container, false);

        //wiring up
        mBtnRegresar = root.findViewById(R.id.button_regresar_editarContacto);

        //listeners
        mBtnRegresar.setOnClickListener(this);

        return root;


    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_regresar_editarContacto)
            getActivity().getSupportFragmentManager().popBackStack();
        else {

        }
    }
}