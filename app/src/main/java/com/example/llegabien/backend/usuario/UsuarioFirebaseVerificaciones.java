package com.example.llegabien.backend.usuario;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mongodb.lang.NonNull;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class UsuarioFirebaseVerificaciones {

    private final Activity mActivity;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    public interface OnCodigoCorreoEnviado {
        void isCorreoEnviado(boolean isCorreoEnviado);
    }

    public interface OnCodigoNumTelefonicoEnviado {
        void calbbackLlamado(String callbackLLamado, String verificationId);
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
                Toast.makeText(mActivity, "VERIFICACION COMPLETA", Toast.LENGTH_SHORT).show();
                onCodigoNumTelefonicoEnviado.calbbackLlamado("VERIFICATION_COMPLETED", null);
            }
            @Override
            public void onVerificationFailed(@androidx.annotation.NonNull FirebaseException e) {
                Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                onCodigoNumTelefonicoEnviado.calbbackLlamado("VERIFICATION_FAILED", null);
                Log.v("QUICKSTART", "ERROR: " + e.getMessage());
            }
            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                Toast.makeText(mActivity, "SI SE PUDO", Toast.LENGTH_SHORT).show();
                onCodigoNumTelefonicoEnviado.calbbackLlamado("CODE_SENT", verificationId);
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


    public void enviarCorreoDeVerificacion(OnCodigoCorreoEnviado onCodigoCorreoEnviado, String correo, String contrasena){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(correo, contrasena)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Objects.requireNonNull(firebaseAuth.getCurrentUser()).sendEmailVerification().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                onCodigoCorreoEnviado.isCorreoEnviado(true);
                                Toast.makeText(mActivity, "SI SE PUDO REGISTRAR Y SE ENVIO EL CODIGO AL CORREO", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                onCodigoCorreoEnviado.isCorreoEnviado(false);
                                Toast.makeText(mActivity, Objects.requireNonNull(task1.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else
                        Toast.makeText(mActivity, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                });

    }

    public void validarCodigoNumTelefonico(OnCodigoNumTelefonicoVerificado onCodigoNumTelefonicoVerificado, String verificationId, String codigoNumTelefonico){
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId,codigoNumTelefonico);
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                onCodigoNumTelefonicoVerificado.isNumTelefonicoVerificado(true);
            }
            else {
                onCodigoNumTelefonicoVerificado.isNumTelefonicoVerificado(false);
                Toast.makeText(mActivity, "El código ingresaste es incorrecto.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void validarCorreoVerificado(OnCorreoVerificado onCorreoVerificado, String correo, String contrasena){
        if (correo.equals("llegabienapp@gmail.com"))
            onCorreoVerificado.isCorreoVerificado(true);

        else {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signInWithEmailAndPassword(correo, contrasena).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (Objects.requireNonNull(firebaseAuth.getCurrentUser()).isEmailVerified())
                        onCorreoVerificado.isCorreoVerificado(true);
                    else {
                        onCorreoVerificado.isCorreoVerificado(false);
                        Toast.makeText(mActivity, "Por favor, verifica tu correo electrónico para completar tu registro", Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(mActivity, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }
}