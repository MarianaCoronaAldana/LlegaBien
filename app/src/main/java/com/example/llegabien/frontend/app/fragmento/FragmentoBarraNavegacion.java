package com.example.llegabien.frontend.app.fragmento;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.llegabien.R;
import com.example.llegabien.backend.app.Permisos;
import com.example.llegabien.frontend.app.Utilidades;
import com.example.llegabien.frontend.botonEmergencia.fragmento.FragmentoBotonEmergencia;
import com.example.llegabien.frontend.contactos.activity.ActivityEditarLeerContactos;
import com.example.llegabien.frontend.favoritos.activity.ActivityFavoritos;
import com.example.llegabien.frontend.reportes.activity.ActivityReportes;
import com.example.llegabien.frontend.rutas.ActivityMostrarRutas;

public class FragmentoBarraNavegacion extends Fragment implements View.OnTouchListener, View.OnClickListener {

    private ConstraintLayout mBtnFavoritos, mBtnSubirReporte, mBtnHistorialRutas, mBtnContactos, mFondoBlanco;
    private ObjectAnimator mScaleDown = null;

    public FragmentoBarraNavegacion() {
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_barra_navegacion, container, false);
        Button btnEmergencia = root.findViewById(R.id.button_emergencia_barraNavegacion);
        mBtnFavoritos = root.findViewById(R.id.button_favoritos_barraNavegacion);
        mBtnSubirReporte = root.findViewById(R.id.button_subirReporte_barraNavegacion);
        mBtnHistorialRutas = root.findViewById(R.id.button_historialRutas_barraNavegacion);
        mBtnContactos = root.findViewById(R.id.button_contactos_barraNavegacion);
        mFondoBlanco = root.findViewById(R.id.consLyt_parentPrincipal_barraNavegacion);

        //listeners
        btnEmergencia.setOnTouchListener(this);
        mBtnSubirReporte.setOnClickListener(this);
        mBtnContactos.setOnClickListener(this);
        mBtnFavoritos.setOnClickListener(this);
        mBtnHistorialRutas.setOnClickListener(this);

        this.mScaleDown = ObjectAnimator.ofPropertyValuesHolder(
                btnEmergencia,
                PropertyValuesHolder.ofFloat("scaleX", 0.6f),
                PropertyValuesHolder.ofFloat("scaleY", 0.6f)
        );

        Utilidades.startAnimacionBtnEmergencia(this.mScaleDown);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.mScaleDown.start();
    }

    //FUNCIONES LISTENER//
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_subirReporte_barraNavegacion)
            startActivity(new Intent(requireActivity(), ActivityReportes.class));
        else if (view.getId() == R.id.button_contactos_barraNavegacion)
            startActivity(new Intent(requireActivity(), ActivityEditarLeerContactos.class));
        else if (view.getId() == R.id.button_favoritos_barraNavegacion)
            startActivity(new Intent(requireActivity(), ActivityFavoritos.class));
        else if (view.getId() == R.id.button_historialRutas_barraNavegacion)
            startActivity(new Intent(requireActivity(), ActivityMostrarRutas.class));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();

        // Cuando el boton es presionado.
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Permisos mPermisos = new Permisos();
            mPermisos.getPermisoUbicacion(requireActivity(), false);
            if (mPermisos.getLocationPermissionGranted()) {
                // Para hacer invisible los otros botones del fragmento.
                mBtnFavoritos.setVisibility(View.INVISIBLE);
                mBtnSubirReporte.setVisibility(View.INVISIBLE);
                mBtnHistorialRutas.setVisibility(View.INVISIBLE);
                mBtnContactos.setVisibility(View.INVISIBLE);

                // Para cambiar el color del fragmento.
                mFondoBlanco.setBackgroundColor(requireActivity().getResources().getColor(R.color.blanco));

                this.mScaleDown.end();

                // Para mostrar el ProgressCircle.
                FragmentoBotonEmergencia fragmentoBotonEmergencia = new FragmentoBotonEmergencia(mFondoBlanco, mBtnFavoritos, mBtnSubirReporte,
                        mBtnHistorialRutas, mBtnContactos);
                fragmentTransaction.add(R.id.fragmentContainerView_botonEmergencia, fragmentoBotonEmergencia, "FragmentoBotonEmergencia").commit();

            } else {
                FragmentoPermisos fragmentoPermisos = new FragmentoPermisos();
                fragmentTransaction.add(R.id.fragmentContainerView_reportes, fragmentoPermisos).commit();
            }
        }

        // Cuando el boton no está presionado.
        else if (event.getAction() == MotionEvent.ACTION_UP) {
            mFondoBlanco.setBackgroundColor(Color.TRANSPARENT);
            mBtnFavoritos.setVisibility(View.VISIBLE);
            mBtnSubirReporte.setVisibility(View.VISIBLE);
            mBtnHistorialRutas.setVisibility(View.VISIBLE);
            mBtnContactos.setVisibility(View.VISIBLE);
            this.mScaleDown.start();

            // Para esconder el ProgressCircle.
            Fragment fragment = requireActivity().getSupportFragmentManager().findFragmentByTag("FragmentoBotonEmergencia");
            if (fragment != null)
                fragmentTransaction.remove(fragment).commit();
        }
        return true;
    }

    // OTRAS FUNCIONES //

}