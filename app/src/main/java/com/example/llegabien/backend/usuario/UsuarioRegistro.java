package com.example.llegabien.backend.usuario;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.llegabien.R;
import com.example.llegabien.frontend.usuario.fragmento.FragmentoRegistrarUsuario3;
import com.example.llegabien.frontend.usuario.fragmento.FragmentoRegistrarUsuario4;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mongodb.lang.NonNull;

import java.util.concurrent.TimeUnit;

public class UsuarioRegistro {

    public void enviarCodigoNumTelefonico(String numTelefonico, String correo, String contraseña, Fragment fragmento){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+52" + numTelefonico,
                60,
                TimeUnit.SECONDS,
                fragmento.getActivity(),
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential){}

                    @Override
                    public void onVerificationFailed(@androidx.annotation.NonNull FirebaseException e) {
                        Toast.makeText(fragmento.getActivity(),e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent (@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken){
                        Toast.makeText(fragmento.getActivity(),"SI SE PUDO",Toast.LENGTH_SHORT).show();
                        enviarCorreoDeVerificacion(correo, contraseña, fragmento, verificationId);

                    }

                }
        );
    }

    public void enviarCorreoDeVerificacion(String correo, String contraseña, Fragment fragmento, String verificationId){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(correo, contraseña)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(fragmento.getActivity(), "SI SE PUDO REGISTRAR Y SE ENVIO EL CODIGO", Toast.LENGTH_SHORT).show();
                                        FragmentTransaction fragmentTransaction = fragmento.getActivity().getSupportFragmentManager().beginTransaction();
                                        FragmentoRegistrarUsuario3 fragmentoRegistrarUsuario3 = FragmentoRegistrarUsuario3.newInstance(verificationId);
                                        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                                        fragmentTransaction.replace(R.id.fragment_pantallaPrincipal, fragmentoRegistrarUsuario3).commit();
                                        fragmentTransaction.addToBackStack(null);
                                    }
                                    else
                                        Toast.makeText(fragmento.getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(fragmento.getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public void verificarCodigoNumTelefonico(String verificationId, String codigoNumTelefonico, Fragment fragmento){
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId,codigoNumTelefonico);
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FragmentTransaction fragmentTransaction = fragmento.getActivity().getSupportFragmentManager().beginTransaction();
                    FragmentoRegistrarUsuario4 fragmentoRegistrarUsuario4 = new FragmentoRegistrarUsuario4();
                    fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_right);
                    fragmentTransaction.replace(R.id.fragment_pantallaPrincipal, fragmentoRegistrarUsuario4).commit();
                    fragmentTransaction.addToBackStack(null);
                }
                else
                    Toast.makeText(fragmento.getActivity(), "El código ingresaste es incorrecto.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
