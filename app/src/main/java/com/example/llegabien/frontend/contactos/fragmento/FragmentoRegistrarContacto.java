package com.example.llegabien.frontend.contactos.fragmento;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.llegabien.R;

public class FragmentoRegistrarContacto extends Fragment {

    private TextView mTxtTitulo;

    //PARAMETROS DE INICALIZACIÓN DEL FRAGMENTO
    private static final String mNumContacto_PARAM1 = "param1"; //etiqueta
    private int mNumContacto_param1 = 1; //tipo y valor

    public FragmentoRegistrarContacto() {
        // Required empty public constructor
    }

    //para inicalizar el fragmento con parametros y guardarlos en un bundle
    public static FragmentoRegistrarContacto newInstance(int param1) {
        FragmentoRegistrarContacto fragment = new FragmentoRegistrarContacto();
        Bundle args = new Bundle();
        args.putInt(mNumContacto_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    //para obtener los parametros que se guardan en el bundle
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNumContacto_param1 = getArguments().getInt(mNumContacto_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_registrar_contacto, container, false);

        //wiring up
        mTxtTitulo = (TextView) root.findViewById(R.id.textView_titulo_registroContactos);

        //para cambiar el titulo segun el numero de contacto
        String tituloRegistroContacto = getResources().getString(R.string.contactoEmergencia_registro5) + " " + String.valueOf(mNumContacto_param1);
        mTxtTitulo.setText(tituloRegistroContacto);

        return root;
    }
}