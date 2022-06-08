package com.example.llegabien.frontend.usuario.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.llegabien.R;
import com.example.llegabien.frontend.usuario.activity.ActivityConfiguracionAdmin;
import com.example.llegabien.frontend.usuario.activity.ActivityConfiguracionUsuario;

public class DialogTipoConfiguracion extends Dialog implements View.OnClickListener {

    public Activity mActivity;
    public Button mBtnConfigUsuario, mBtnConfigAdmin;

  public DialogTipoConfiguracion(Activity activity) {
        super(activity);
        // TODO Auto-generated constructor stub}
        this.mActivity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_tipo_configuracion);
        mBtnConfigUsuario = findViewById(R.id.button_configUsuario_tipoConfig);
        mBtnConfigAdmin = findViewById(R.id.button_configAdmin_tipoConfig);
        mBtnConfigUsuario.setOnClickListener(this);
        mBtnConfigAdmin.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_configUsuario_tipoConfig:
                mActivity.startActivity(new Intent(mActivity, ActivityConfiguracionUsuario.class));
                break;
            case R.id.button_configAdmin_tipoConfig:
                mActivity.startActivity(new Intent(mActivity, ActivityConfiguracionAdmin.class));
                break;
        }
        dismiss();
    }
}

