package com.example.llegabien.frontend.botonEmergencia.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.llegabien.R;
import com.example.llegabien.backend.app.Permisos;
import com.example.llegabien.backend.botonEmergencia.Emergencia;
import com.example.llegabien.frontend.mapa.activity.ActivityMap;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
//import com.example.llegabien.backend.botonEmergencia.Emergencia;

public class DialogConfirmarEmergencia extends Dialog implements View.OnClickListener{
    public Activity mActivity;
    public Button mBtnConfimarEmergencia;

    /**
     *
     * @param activity
     */
    public DialogConfirmarEmergencia (Activity activity) {
        super(activity);
        // TODO Auto-generated constructor stub}
        this.mActivity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_confirmar_emergencia);
        mBtnConfimarEmergencia = findViewById(R.id.button_confirmar_confirmarEmergencia);
        mBtnConfimarEmergencia.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        ActivityMap.clickOnEmergenciaDialog = true;
        Emergencia emergencia = new Emergencia(mActivity);
        emergencia.EmpezarProtocolo();
        dismiss();
    }


}