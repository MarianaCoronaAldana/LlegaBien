package com.example.llegabien.frontend.usuario.fragmento;

import android.graphics.Color;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.example.llegabien.R;

public class FragmentoUsuarioGuia4 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_usuario_guia4, container, false);

        ConstraintLayout btnSiguiente = root.findViewById(R.id.button_siguiente_usuarioGuia4);
        btnSiguiente.setOnClickListener(view -> {
            Window window = requireActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setNavigationBarColor(getResources().getColor(R.color.morado_oscuro));
            window.setStatusBarColor(getResources().getColor(R.color.morado_oscuro));

            FragmentContainerView fragmentContainerView = requireActivity().findViewById(R.id.fragmentContainerView_guia_pagina_principal);
            fragmentContainerView.setBackgroundColor(Color.TRANSPARENT);

            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.remove(FragmentoUsuarioGuia4.this).commit();
        });

        return root;
    }
}