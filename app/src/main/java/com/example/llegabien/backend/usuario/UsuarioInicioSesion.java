package com.example.llegabien.backend.usuario;

import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.llegabien.frontend.rutas.activity.MapsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class UsuarioInicioSesion {
    //boolean correoVerificado = false;

    public void validarCorreoVerificado(String correo, String contraseña, Fragment fragmento){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(correo, contraseña).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    if(firebaseAuth.getCurrentUser().isEmailVerified())
                        fragmento.startActivity(new Intent(fragmento.getActivity(), MapsActivity.class));
                    else
                        Toast.makeText(fragmento.getActivity(), "Por favor, verifica tu correo electrónico para completar tu registro", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(fragmento.getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
