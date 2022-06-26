package com.example.llegabien.frontend.app.fragmento;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.llegabien.R;
import com.example.llegabien.backend.app.Permisos;
import com.example.llegabien.frontend.botonEmergencia.fragmento.FragmentoBotonEmergencia;
import com.example.llegabien.frontend.mapa.activity.ActivityMap;
import com.example.llegabien.frontend.reportes.activity.ActivityReportes;
import com.example.llegabien.frontend.usuario.fragmento.FragmentoIniciarSesion1;
import com.example.llegabien.frontend.usuario.fragmento.FragmentoRegistrarUsuario1;

public class FragmentoBarraNavegacion extends Fragment implements View.OnTouchListener, View.OnClickListener {

    private Button mBtnEmergencia;
    private ConstraintLayout mBtnFavoritos, mBtnSubirReporte, mBtnHistorialRutas,mBtnContactos, mFondoBlanco;;
    private ObjectAnimator mScaleDown;
    private Permisos mPermisos;

    public FragmentoBarraNavegacion() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_barra_navegacion, container, false);
        mBtnEmergencia = root.findViewById(R.id.button_emergencia_barraNavegacion);
        mBtnFavoritos = root.findViewById(R.id.button_favoritos_barraNavegacion);
        mBtnSubirReporte = root.findViewById(R.id.button_subirReporte_barraNavegacion);
        mBtnHistorialRutas = root.findViewById(R.id.button_historialRutas_barraNavegacion);
        mBtnContactos = root.findViewById(R.id.button_contactos_barraNavegacion);
        mFondoBlanco = root.findViewById(R.id.consLyt_parentPrincipal_barraNavegacion);

        startAnimacionBtnEmergencia();

        //listeners
        mBtnEmergencia.setOnTouchListener(this);
        mBtnSubirReporte.setOnClickListener(this);

        return root;
    }

    //FUNCIONES LISTENER//
    @Override
    public void onClick(View view)
    {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_up, R.anim.slide_down,R.anim.slide_up, R.anim.slide_down)
                .addToBackStack(null);

        switch (view.getId()) {
            case R.id.button_subirReporte_barraNavegacion:
                startActivity(new Intent(getActivity(), ActivityReportes.class));
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        FragmentoBotonEmergencia fragmentoBotonEmergencia = new FragmentoBotonEmergencia(mFondoBlanco);
        FragmentoPermisos fragmentoPermisos = new FragmentoPermisos();
        //when button is pressed
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mPermisos = new Permisos();
            mPermisos.getPermisoUbicacion(getActivity(), false);
            if(mPermisos.getLocationPermissionGranted()){
                mBtnFavoritos.setVisibility(View.INVISIBLE);
                mBtnSubirReporte.setVisibility(View.INVISIBLE);
                mBtnHistorialRutas.setVisibility(View.INVISIBLE);
                mBtnContactos.setVisibility(View.INVISIBLE);
                mScaleDown.end();
                mFondoBlanco.setBackgroundColor(getActivity().getResources().getColor(R.color.blanco));

                if(this.getActivity() instanceof ActivityMap){
                    //show the progressCircle
                    fragmentTransaction.add(R.id.fragmentContainerView_botonEmergencia, fragmentoBotonEmergencia, "FragmentoBotonEmergencia").commit();
                }
            }
            else
                fragmentTransaction.add(R.id.fragmentContainerView1_fragemntoBuscarLugar_activityMaps, fragmentoPermisos).commit();

        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            mFondoBlanco.setBackgroundColor(Color.TRANSPARENT);
            mBtnFavoritos.setVisibility(View.VISIBLE);
            mBtnSubirReporte.setVisibility(View.VISIBLE);
            mBtnHistorialRutas.setVisibility(View.VISIBLE);
            mBtnContactos.setVisibility(View.VISIBLE);
            mScaleDown.start();

            //hide the progressCircle
            Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag("FragmentoBotonEmergencia");
            if (fragment != null)
                fragmentTransaction.remove(fragment).commit();
        }
        return true;
    }


    // OTRAS FUNCIONES//

    private void startAnimacionBtnEmergencia() {
        mScaleDown = ObjectAnimator.ofPropertyValuesHolder(
                mBtnEmergencia,
                PropertyValuesHolder.ofFloat("scaleX", 0.6f),
                PropertyValuesHolder.ofFloat("scaleY", 0.6f)
        );
        mScaleDown.setDuration(2000);
        mScaleDown.setRepeatMode(ValueAnimator.REVERSE);
        mScaleDown.setRepeatCount(ValueAnimator.INFINITE);
        mScaleDown.start();
    }


}