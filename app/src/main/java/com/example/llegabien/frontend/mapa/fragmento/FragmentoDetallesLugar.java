package com.example.llegabien.frontend.mapa.fragmento;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_UBICACION;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.llegabien.R;
import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.mapa.ubicacion.ubicacion;

public class FragmentoDetallesLugar extends Fragment {

    private TextView mTxtNombre1Lugar, mTxtNombre2Lugar, mTxtDelitosSemana, mTxtMediaHistorica, mTxtSeguridad;
    private ubicacion Ubicacion;

    public FragmentoDetallesLugar() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //para obtener ubicacon de Preferences
        Ubicacion = Preferences.getSavedObjectFromPreference(getActivity(), PREFERENCE_UBICACION, ubicacion.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_detalles_lugar, container, false);

        //wiring up
        mTxtNombre1Lugar = (TextView) root.findViewById(R.id.textView1_nombreLugar_detallesLugar);
        mTxtNombre2Lugar = (TextView) root.findViewById(R.id.textView2_nombreLugar_detallesLugar);

        //para establecer valores dependiendo ubicacion
        if (Ubicacion != null) {
            String[] nombreLugar = Ubicacion.getNombre().split(",", 2);
            mTxtNombre1Lugar.setText(nombreLugar[0]);
            mTxtNombre1Lugar.setText(nombreLugar[1]);
        }


        return root;
    }
}