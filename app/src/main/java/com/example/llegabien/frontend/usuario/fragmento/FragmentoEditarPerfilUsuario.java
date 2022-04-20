package com.example.llegabien.frontend.usuario.fragmento;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.llegabien.R;
public class FragmentoEditarPerfilUsuario extends Fragment {
    public FragmentoEditarPerfilUsuario() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragmento_editar_perfil_usuario, container, false);
    }
}