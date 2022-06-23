package com.example.llegabien.backend.usuario;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mongodb.lang.NonNull;

import java.util.concurrent.TimeUnit;

public class UsuarioFirebaseVerificaciones {

    private Activity mActivity;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    public interface OnCodigoCorreoEnviado {
        void isCorreoEnviado(boolean isCorreoEnviado);
    }

    public interface OnCodigoNumTelefonicoEnviado {
        void isSMSEnviado(boolean isSMSEnviado, String verificationId);
    }

    public interface OnCodigoNumTelefonicoVerificado{
        void isNumTelefonicoVerificado(boolean isNumTelefonicoVerificado);
    }

    public interface OnCorreoVerificado{
        void isCorreoVerificado(boolean isCorreoVerificado);
    }

    public UsuarioFirebaseVerificaciones(Activity activity){ mActivity = activity; }

    public void enviarCodigoNumTelefonico(OnCodigoNumTelefonicoEnviado onCodigoNumTelefonicoEnviado, String numTelefonico){
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(mActivity, "SI SE PUDO ENVIAR SMS", Toast.LENGTH_SHORT).show();
                onCodigoNumTelefonicoEnviado.isSMSEnviado(true, null);
            }

            @Override
            public void onVerificationFailed(@androidx.annotation.NonNull FirebaseException e) {
                Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.v("QUICKSTART", "ERROR: " + e.getMessage());
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                Toast.makeText(mActivity, "SI SE PUDO", Toast.LENGTH_SHORT).show();
                onCodigoNumTelefonicoEnviado.isSMSEnviado(true, verificationId);
            }
        };

        PhoneAuthProvider.verifyPhoneNumber(
                PhoneAuthOptions
                        .newBuilder(FirebaseAuth.getInstance())
                        .setActivity(mActivity)
                        .setPhoneNumber(numTelefonico)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setCallbacks(mCallbacks)
                        .build());
    }


    public void enviarCorreoDeVerificacion(OnCodigoCorreoEnviado onCodigoCorreoEnviado, String correo, String contraseña){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(correo, contraseña)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override 
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        onCodigoCorreoEnviado.isCorreoEnviado(true);
                                        Toast.makeText(mActivity, "SI SE PUDO REGISTRAR Y SE ENVIO EL CODIGO AL CORREO", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        onCodigoCorreoEnviado.isCorreoEnviado(false);
                                        Toast.makeText(mActivity, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        Log.v("QUICKSTART", task.getException().getMessage());
                                    }
                                }
                            });
                        }
                        else
                            Toast.makeText(mActivity, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void validarCodigoNumTelefonico(OnCodigoNumTelefonicoVerificado onCodigoNumTelefonicoVerificado, String verificationId, String codigoNumTelefonico){
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId,codigoNumTelefonico);
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    onCodigoNumTelefonicoVerificado.isNumTelefonicoVerificado(true);
                }
                else {
                    onCodigoNumTelefonicoVerificado.isNumTelefonicoVerificado(false);
                    Toast.makeText(mActivity, "El código ingresaste es incorrecto.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void validarCorreoVerificado(OnCorreoVerificado onCorreoVerificado, String correo, String contraseña){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(correo, contraseña).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    if(firebaseAuth.getCurrentUser().isEmailVerified())
                        onCorreoVerificado.isCorreoVerificado(true);
                    else{
                        onCorreoVerificado.isCorreoVerificado(false);
                        Toast.makeText(mActivity, "Por favor, verifica tu correo electrónico para completar tu registro", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                    Toast.makeText(mActivity, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}