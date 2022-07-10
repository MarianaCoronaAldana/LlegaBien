package com.example.llegabien.frontend.reportes.fragmento;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.llegabien.R;
import com.example.llegabien.backend.mapa.ubicacion.UbicacionDAO;
import com.example.llegabien.backend.reporte.ReporteDAO;
import com.example.llegabien.backend.usuario.UsuarioSubirReporteAdmin;

import java.io.IOException;

import jxl.read.biff.BiffException;

public class FragmentoSubirReporteAdmin extends Fragment implements View.OnClickListener{

    private UsuarioSubirReporteAdmin usuarioSubirReporteAdmin;
    private final ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onActivityResult(ActivityResult activityResult) {
                            int result = activityResult.getResultCode();
                            Intent data = activityResult.getData();
                            try {
                                usuarioSubirReporteAdmin.obtenerArchivoExcel(result,data,FragmentoSubirReporteAdmin.this.getContext());
                            } catch (BiffException | IOException e) {
                                e.printStackTrace();
                            }
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
            ReporteDAO reporteDAO = new ReporteDAO(requireContext());
            //reporteDAO.elimarReportes();
            reporteDAO.mostrarReportesLog();
            //UbicacionDAO ubicacionDAO = new UbicacionDAO(requireContext());
            //ubicacionDAO.sumaDelitosCeroUbicaciones();
            //usuarioSubirReporteAdmin = new UsuarioSubirReporteAdmin();
            //usuarioSubirReporteAdmin.inicializarIntent();
            //activityResultLauncher.launch(usuarioSubirReporteAdmin.getIntent());
        }
        else if(view.getId() == R.id.button_regresar_subirReporte)
            requireActivity().finish();
    }

}