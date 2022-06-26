package com.example.llegabien.frontend.app.fragmento;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.llegabien.R;
import com.example.llegabien.frontend.mapa.activity.ActivityMap;

public class FragmentoPermisos extends Fragment implements View.OnClickListener {
    private Button mBtnAceptarPermisos;

    public FragmentoPermisos() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_permisos, container, false);

        //wiring up
        mBtnAceptarPermisos = root.findViewById(R.id.button_aceptarPermiso_permisos);

        //listeners
        mBtnAceptarPermisos.setOnClickListener(this);

        return root;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        getActivity().startActivity(intent);
    }
}