package com.example.llegabien.frontend.usuario.fragmento;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.llegabien.R;

public class FragmentoConfigAvanAdmin extends Fragment {

    public FragmentoConfigAvanAdmin() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragmento_config_avan_usuario, container, false);
    }
}