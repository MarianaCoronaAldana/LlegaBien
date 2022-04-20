package com.example.llegabien.frontend.usuario.fragmento;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.llegabien.R;
import com.example.llegabien.backend.usuario.usuario;

public class FragmentoRegistrarUsuario4 extends Fragment {

    //PARAMETROS DE INICALIZACIÓN DEL FRAGMENTO
    private static final String parametro_usuario = "usuario"; //etiqueta
    usuario Usuario;

    //para inicalizar el fragmento con parametros y guardarlos en un bundle
    public static FragmentoRegistrarUsuario4 newInstance(usuario Usuario) {
        FragmentoRegistrarUsuario4 fragment = new FragmentoRegistrarUsuario4();
        Bundle args = new Bundle();
        args.putSerializable(parametro_usuario, Usuario);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentoRegistrarUsuario4() {

    }

    //para obtener los parametros que se guardan en el bundle
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_registrar_usuario4, container, false);
        return root;
    }

}