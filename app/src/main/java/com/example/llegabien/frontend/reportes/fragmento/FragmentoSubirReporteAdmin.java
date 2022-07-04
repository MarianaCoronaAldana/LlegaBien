package com.example.llegabien.frontend.reportes.fragmento;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.llegabien.R;
import com.example.llegabien.backend.usuario.UsuarioSubirReporte;

public class FragmentoSubirReporteAdmin extends Fragment implements View.OnClickListener{

    private UsuarioSubirReporte usuarioSubirReporte;
    private final ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult activityResult) {
                            int result = activityResult.getResultCode();
                            Intent data = activityResult.getData();
                            usuarioSubirReporte.obtenerArchivo(result,data,FragmentoSubirReporteAdmin.this);
                        }
                    }
            );

    public FragmentoSubirReporteAdmin() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_subir_reporte_admin, container, false);

        //wiring up
        Button mBtnRegresar = (Button) root.findViewById(R.id.button_regresar_subirReporte);
        Button mBtnSubirReporte = (Button) root.findViewById(R.id.button_seleccionarArchivo_subirReporte);

        //listeners
        mBtnRegresar.setOnClickListener(this);
        mBtnSubirReporte.setOnClickListener(this);

        return root;
    }

    //FUNCIONES LISTENERS//
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_seleccionarArchivo_subirReporte){
            usuarioSubirReporte = new UsuarioSubirReporte();
            usuarioSubirReporte.inicializarIntent();
            activityResultLauncher.launch(usuarioSubirReporte.getmIntent());
        }
        else if(view.getId() == R.id.button_regresar_subirReporte)
            requireActivity().finish();
    }

}