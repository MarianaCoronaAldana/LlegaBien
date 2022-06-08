package com.example.llegabien.frontend;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.llegabien.R;
import com.example.llegabien.frontend.mapa.activity.ActivityMap;

public class FragmentoBarraNavegacion extends Fragment implements View.OnTouchListener {

    Button mBtnEmergencia;
    ConstraintLayout mBtnFavoritos, mBtnSubirReporte, mBtnHistorialRutas,mBtnContactos, mFondoBlanco;;
    ObjectAnimator mScaleDown;

    public FragmentoBarraNavegacion() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_barra_navegacion, container, false);
        mBtnEmergencia = (Button) root.findViewById(R.id.button_emergencia_barraNavegacion);
        mBtnFavoritos = root.findViewById(R.id.button_favoritos_barraNavegacion);
        mBtnSubirReporte = root.findViewById(R.id.button_subirReporte_barraNavegacion);
        mBtnHistorialRutas = root.findViewById(R.id.button_historialRutas_barraNavegacion);
        mBtnContactos = root.findViewById(R.id.button_contactos_barraNavegacion);
        mFondoBlanco = root.findViewById(R.id.consLyt_parentPrincipal_barraNavegacion);

        startAnimacionBtnEmergencia();

        mBtnEmergencia.setOnTouchListener(this);

        return root;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //when button is pressed
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mFondoBlanco.setBackgroundColor(getActivity().getResources().getColor(R.color.blanco));
            mBtnFavoritos.setVisibility(View.INVISIBLE);
            mBtnSubirReporte.setVisibility(View.INVISIBLE);
            mBtnHistorialRutas.setVisibility(View.INVISIBLE);
            mBtnContactos.setVisibility(View.INVISIBLE);
            mScaleDown.end();
            //show the progressBar
            ((ActivityMap)getActivity()).startTimer(mFondoBlanco);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            mFondoBlanco.setBackgroundColor(Color.TRANSPARENT);
            mBtnFavoritos.setVisibility(View.VISIBLE);
            mBtnSubirReporte.setVisibility(View.VISIBLE);
            mBtnHistorialRutas.setVisibility(View.VISIBLE);
            mBtnContactos.setVisibility(View.VISIBLE);
            mScaleDown.start();
            //hide the progressBar
            ((ActivityMap)getActivity()).cancelTimer();
        }
        return true;
    }


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