package com.example.llegabien.frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.llegabien.frontend.usuario.activity.ActivityPaginaPrincipalUsuario;

public class ActivitySplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(ActivitySplashScreen.this, ActivityPaginaPrincipalUsuario.class));
        finish();
    }
}