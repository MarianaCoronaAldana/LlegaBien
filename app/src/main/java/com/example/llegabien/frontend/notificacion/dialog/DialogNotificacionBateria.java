package com.example.llegabien.frontend.notificacion.dialog;

import android.app.Activity;
import android.app.Dialog;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.llegabien.R;

import com.example.llegabien.backend.notificacion.Notificacion;


public class DialogNotificacionBateria extends Dialog implements View.OnClickListener{
    public Activity mActivity;
    public Button mBtnConfimarEmergencia;
    private float mBateria;

    public DialogNotificacionBateria(Activity activity, float nivelBateria) {
        super(activity);
        // TODO Auto-generated constructor stub}
        this.mActivity = activity;
        mBateria = nivelBateria;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_notificar_bateria);
        mBtnConfimarEmergencia = findViewById(R.id.button_confirmarnotificarBateria);
        mBtnConfimarEmergencia.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Notificacion notificar = new Notificacion(mBateria, this.getContext());
        dismiss();
    }


}
