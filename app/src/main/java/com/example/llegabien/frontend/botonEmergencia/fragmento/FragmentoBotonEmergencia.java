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
    private ConstraintLayout mFondoBlancoBarraNavegacion;
    private int mColorActivityAnterior;

    public FragmentoBotonEmergencia() {
        // Required empty public constructor
    }

    public FragmentoBotonEmergencia(ConstraintLayout fondoBlancoBarraNavegacion) {
        mFondoBlancoBarraNavegacion = fondoBlancoBarraNavegacion;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_boton_emergencia, container, false);

        //wiring up
        mTxtSegundosFaltantes = root.findViewById(R.id.textView_segundosFaltantes_activityMaps);
        mProgressCircle= root.findViewById(R.id.progressCircle_btnEmergencia_activityMaps);

        //empezar contador
        startTimer();

        return root;
    }

    @Override
    public void onDestroy(){
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
                mFondoBlancoBarraNavegacion.setBackgroundColor(Color.TRANSPARENT);
                mProgressCircle.setProgress(0);

                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                Fragment fragment = requireActivity().getSupportFragmentManager().findFragmentByTag("FragmentoBotonEmergencia");
                if (fragment != null)
                    fragmentTransaction.remove(fragment).commit();

                Emergencia emergencia = new Emergencia(FragmentoBotonEmergencia.this.getActivity());
                emergencia.EmpezarProtocolo();
                /*
                DialogConfirmarEmergencia dialogConfirmarEmergencia = new DialogConfirmarEmergencia(requireActivity());
                dialogConfirmarEmergencia.show();*/
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