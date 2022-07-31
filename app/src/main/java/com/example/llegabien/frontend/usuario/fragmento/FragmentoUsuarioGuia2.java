package com.example.llegabien.frontend.usuario.fragmento;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.llegabien.R;

public class FragmentoUsuarioGuia2 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_usuario_guia2, container, false);

        ConstraintLayout btnSiguiente = root.findViewById(R.id.button_siguiente_usuarioGuia2);
        btnSiguiente.setOnClickListener(view -> {
            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            FragmentoUsuarioGuia3 fragmentoUsuarioGuia3 = new FragmentoUsuarioGuia3();
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            fragmentTransaction.replace(R.id.fragmentContainerView_guia_pagina_principal, fragmentoUsuarioGuia3).commit();
        });

        return root;
    }
}