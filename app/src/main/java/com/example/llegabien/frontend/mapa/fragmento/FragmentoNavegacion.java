package com.example.llegabien.frontend.mapa.fragmento;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.llegabien.R;
import com.example.llegabien.backend.app.Permisos;
import com.example.llegabien.frontend.app.Utilidades;
import com.example.llegabien.frontend.app.fragmento.FragmentoPermisos;
import com.example.llegabien.frontend.botonEmergencia.fragmento.FragmentoBotonEmergencia;
import com.example.llegabien.frontend.mapa.Mapa;
import com.example.llegabien.frontend.mapa.activity.ActivityMap;
import com.example.llegabien.frontend.reportes.activity.ActivityReportes;

public class FragmentoNavegacion extends Fragment implements View.OnClickListener, View.OnTouchListener {

    private ObjectAnimator mScaleDown = null;
    ConstraintLayout mBtnSubirReporte;
    ConstraintLayout mBtnAdvertencia;
    ConstraintLayout mBtnCentrarMapa;

    public FragmentoNavegacion() {
        // Required empty public constructor
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_navegacion, container, false);

        // Para cambiar el color de la status bar.
        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setNavigationBarColor(getResources().getColor(R.color.morado_claro));

        //wiring up
        Button btnEmergencia = root.findViewById(R.id.button_emergencia_navegacion);
        mBtnSubirReporte = root.findViewById(R.id.button_subirReporte_navegacion);
        mBtnAdvertencia = root.findViewById(R.id.button_advertencia_navegacion);
        mBtnCentrarMapa = root.findViewById(R.id.button_centrarMapa_navegacion);

        //listeners
        btnEmergencia.setOnTouchListener(this);
        mBtnSubirReporte.setOnClickListener(this);
        mBtnAdvertencia.setOnClickListener(this);
        mBtnCentrarMapa.setOnClickListener(this);


        this.mScaleDown = ObjectAnimator.ofPropertyValuesHolder(
                btnEmergencia,
                PropertyValuesHolder.ofFloat("scaleX", 0.6f),
                PropertyValuesHolder.ofFloat("scaleY", 0.6f)
        );
        Utilidades.startAnimacionBtnEmergencia(mScaleDown);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.mScaleDown.start();
    }

    //LISTENERS//

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();

        // Cuando el boton es presionado.
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Permisos mPermisos = new Permisos();
            mPermisos.getPermisoUbicacion(requireActivity(), false);
            if (mPermisos.getLocationPermissionGranted()) {
                this.mScaleDown.end();

                mBtnAdvertencia.setClickable(false);
                mBtnCentrarMapa.setClickable(false);
                mBtnSubirReporte.setClickable(false);

                // Para mostrar el ProgressCircle.
                FragmentoBotonEmergencia fragmentoBotonEmergencia = new FragmentoBotonEmergencia(mBtnSubirReporte,mBtnCentrarMapa, mBtnAdvertencia);
                fragmentTransaction.add(R.id.fragmentContainerView_botonEmergencia_navegacion, fragmentoBotonEmergencia, "FragmentoBotonEmergencia").commit();
            } else {
                FragmentoPermisos fragmentoPermisos = new FragmentoPermisos();
                fragmentTransaction.add(R.id.fragmentContainerView_reportes, fragmentoPermisos).commit();
            }
        }

        // Cuando el boton no está presionado.
        else if (event.getAction() == MotionEvent.ACTION_UP) {
            this.mScaleDown.start();
            // Para esconder el ProgressCircle.
            Fragment fragment = requireActivity().getSupportFragmentManager().findFragmentByTag("FragmentoBotonEmergencia");
            if (fragment != null)
                fragmentTransaction.remove(fragment).commit();
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_subirReporte_navegacion)
            startActivity(new Intent(requireActivity(), ActivityReportes.class));
        else if (view.getId() == R.id.button_centrarMapa_navegacion) {
            Mapa mapa = new Mapa((ActivityMap) requireActivity());
            mapa.centrarMapa();
        }

    }
}