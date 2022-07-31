package com.example.llegabien.frontend.botonEmergencia.fragmento;

import android.graphics.Color;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.llegabien.R;
import com.example.llegabien.backend.botonEmergencia.Emergencia;

import java.util.concurrent.TimeUnit;

public class FragmentoBotonEmergencia extends Fragment {
    private CountDownTimer mCountDownTimer = null;

    private ProgressBar mProgressCircle;
    private TextView mTxtSegundosFaltantes;
    private Window mWindow;
    private ConstraintLayout mFondoBlancoBarraNavegacion, mBtnFavoritosBarraNavegacion,
            mBtnSubirReporte, mBtnHistorialRutasBarraNavegacion, mBtnContactosBarraNavegacion,
            mBtnCentrarMapaNavegacion, mBtnAdvertenciaNavegacion;
    private int mColorActivityAnterior;

    public FragmentoBotonEmergencia() {
        // Required empty public constructor
    }

    public FragmentoBotonEmergencia(ConstraintLayout btnSubirReporte, ConstraintLayout btnCentrarNavegacion, ConstraintLayout btnAdvertenciaNavegacion) {
        mBtnSubirReporte = btnSubirReporte;
        mBtnAdvertenciaNavegacion = btnAdvertenciaNavegacion;
        mBtnCentrarMapaNavegacion = btnCentrarNavegacion;
    }

    public FragmentoBotonEmergencia(ConstraintLayout fondoBlancoBarraNavegacion, ConstraintLayout btnFavoritosBarraNavegacion,
                                    ConstraintLayout btnSubirReporteBarraNavegacion, ConstraintLayout btnHistorialRutasBarraNavegacion,
                                    ConstraintLayout btnContactosBarraNavegacion) {
        mFondoBlancoBarraNavegacion = fondoBlancoBarraNavegacion;
        mBtnFavoritosBarraNavegacion = btnFavoritosBarraNavegacion;
        mBtnSubirReporte = btnSubirReporteBarraNavegacion;
        mBtnHistorialRutasBarraNavegacion = btnHistorialRutasBarraNavegacion;
        mBtnContactosBarraNavegacion = btnContactosBarraNavegacion;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_boton_emergencia, container, false);

        //wiring up
        mTxtSegundosFaltantes = root.findViewById(R.id.textView_segundosFaltantes_activityMaps);
        mProgressCircle = root.findViewById(R.id.progressCircle_btnEmergencia_activityMaps);

        //empezar contador
        startTimer();

        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelTimer();
    }

    //start timer
    public void startTimer() {
        mWindow = requireActivity().getWindow();
        mColorActivityAnterior = mWindow.getStatusBarColor();
        mWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        mWindow.setStatusBarColor(getResources().getColor(R.color.blanco));
        mWindow.setNavigationBarColor(getResources().getColor(R.color.blanco));

        mCountDownTimer = new CountDownTimer(3000, 300) {
            public void onTick(long millisUntilFinished) {
                String segundosFaltantes = "0" + TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished + 1000);

                mTxtSegundosFaltantes.setText(segundosFaltantes);
                mProgressCircle.setProgress(mProgressCircle.getProgress() + 10);
            }

            public void onFinish() {
                mWindow.setStatusBarColor(mColorActivityAnterior);
                mWindow.setNavigationBarColor(mColorActivityAnterior);
                if (mFondoBlancoBarraNavegacion != null) {
                    mFondoBlancoBarraNavegacion.setBackgroundColor(Color.TRANSPARENT);
                    mBtnContactosBarraNavegacion.setVisibility(View.VISIBLE);
                    mBtnHistorialRutasBarraNavegacion.setVisibility(View.VISIBLE);
                    mBtnFavoritosBarraNavegacion.setVisibility(View.VISIBLE);
                    mBtnSubirReporte.setVisibility(View.VISIBLE);
                } else {
                    mBtnCentrarMapaNavegacion.setClickable(true);
                    mBtnAdvertenciaNavegacion.setClickable(true);
                    mBtnSubirReporte.setClickable(true);
                }

                mProgressCircle.setProgress(0);

                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.remove(FragmentoBotonEmergencia.this).commit();

                Emergencia emergencia = new Emergencia(FragmentoBotonEmergencia.this.getActivity());
                emergencia.EmpezarProtocolo();
            }
        };
        mCountDownTimer.start();
    }

    //cancel timer
    public void cancelTimer() {
        mWindow.setStatusBarColor(mColorActivityAnterior);
        mWindow.setNavigationBarColor(mColorActivityAnterior);

        if (mCountDownTimer != null)
            mCountDownTimer.cancel();
    }

}