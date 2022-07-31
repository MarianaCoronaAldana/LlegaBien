package com.example.llegabien.frontend.usuario.fragmento;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.llegabien.R;

public class FragmentoUsuarioGuia3 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_usuario_guia3, container, false);

        ConstraintLayout btnSiguiente = root.findViewById(R.id.button_siguiente_usuarioGuia3);
        btnSiguiente.setOnClickListener(view -> {
            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            FragmentoUsuarioGuia4 fragmentoUsuarioGuia4 = new FragmentoUsuarioGuia4();
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            fragmentTransaction.replace(R.id.fragmentContainerView_guia_pagina_principal, fragmentoUsuarioGuia4).commit();
        });

        return root;
    }
}