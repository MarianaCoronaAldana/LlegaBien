package com.example.llegabien.frontend.reportes.fragmento;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.llegabien.R;

public class FragmentoSubirReporteUsuario extends Fragment implements AdapterView.OnItemSelectedListener{

    private Spinner mSpinnerCualDelito;

    public FragmentoSubirReporteUsuario() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_subir_reporte_usuario, container, false);

        // Wiring up
        mSpinnerCualDelito = root.findViewById(R.id.spinner_cualDelito_subirReporteUsuario);

        // Listeners
        mSpinnerCualDelito.setOnItemSelectedListener(this);

        // Para inicializar valores del spinner.
        setSpinner();



        return root;
    }

    //FUNCIONES LISTENER//

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void setSpinner(){
        String[] delitos = { "Homicidio", "Secuestro",
                "Extorsión", "Acoso sexual",
                "Robo", "Lesiones",
                "Abuso sexual", "Abuso de autoridad",
                "Vandalismo", "Tiroteo"};

        // Create the instance of ArrayAdapter having the list of courses
        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.spinner_item, delitos);

        // Set simple layout resource file for each item of spinner
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        // Set the ArrayAdapter (ad) data on the Spinner which binds data to spinner
        mSpinnerCualDelito.setAdapter(arrayAdapter);
    }
}