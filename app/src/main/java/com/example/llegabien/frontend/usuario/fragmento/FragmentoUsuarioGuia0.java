package com.example.llegabien.frontend.usuario.fragmento;

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

public class FragmentoUsuarioGuia0 extends Fragment {

    public FragmentoUsuarioGuia0() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_usuario_guia0, container, false);

        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setNavigationBarColor(getResources().getColor(R.color.blanco));
        window.setStatusBarColor(getResources().getColor(R.color.blanco));

        FragmentContainerView fragmentContainerView = requireActivity().findViewById(R.id.fragmentContainerView_guia_pagina_principal);
        fragmentContainerView.setBackgroundColor(requireActivity().getResources().getColor(R.color.blanco));

        ConstraintLayout btnSiguiente = root.findViewById(R.id.button_siguiente_usuarioGuia1);
        btnSiguiente.setOnClickListener(view -> {
            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            FragmentoUsuarioGuia1 fragmentoUsuarioGuia1 = new FragmentoUsuarioGuia1();
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            fragmentTransaction.replace(R.id.fragmentContainerView_guia_pagina_principal, fragmentoUsuarioGuia1).commit();
        });

        return root;
    }
}