package com.example.llegabien.frontend.usuario.fragmento;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.llegabien.R;

public class FragmentoConfiguracionUsuario extends Fragment {

    public FragmentoConfiguracionUsuario() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_configuracion_usuario, container, false);
        return root;
    }
}