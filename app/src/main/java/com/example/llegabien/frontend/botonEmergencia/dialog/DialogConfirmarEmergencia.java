package com.example.llegabien.frontend.botonEmergencia.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.llegabien.R;
import com.example.llegabien.backend.botonEmergencia.Emergencia;
import com.example.llegabien.frontend.usuario.activity.ActivityConfiguracionAdmin;
import com.example.llegabien.frontend.usuario.activity.ActivityConfiguracionUsuario;

public class DialogConfirmarEmergencia extends Dialog implements View.OnClickListener{
    public Activity mActivity;
    public Button mBtnConfimarEmergencia;

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

        Emergencia e = new Emergencia(this.getContext());
        e.EmpezarProtocolo();
        dismiss();
    }
}
