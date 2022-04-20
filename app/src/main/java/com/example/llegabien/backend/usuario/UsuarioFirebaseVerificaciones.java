package com.example.llegabien.backend.usuario;

import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mongodb.lang.NonNull;

import java.util.concurrent.TimeUnit;

public class UsuarioFirebaseVerificaciones {

    private Fragment mFragmento;

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

    public UsuarioFirebaseVerificaciones(Fragment fragmento){
        mFragmento = fragmento;
    }

    public void enviarCodigoNumTelefonico(OnCodigoNumTelefonicoEnviado onCodigoNumTelefonicoEnviado, String numTelefonico){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+52" + numTelefonico,
                60,
                TimeUnit.SECONDS,
                mFragmento.getActivity(),
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential){
                        Toast.makeText(mFragmento.getActivity(),"SI SE PUDO ENVIAR SMS",Toast.LENGTH_SHORT).show();
                        onCodigoNumTelefonicoEnviado.isSMSEnviado(true, null);
                    }

                    @Override
                    public void onVerificationFailed(@androidx.annotation.NonNull FirebaseException e) {
                        Toast.makeText(mFragmento.getActivity(),e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.v("QUICKSTART", "ERROR: " + e.getMessage());
                    }

                    @Override
                    public void onCodeSent (@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken){
                        Toast.makeText(mFragmento.getActivity(),"SI SE PUDO",Toast.LENGTH_SHORT).show();
                        onCodigoNumTelefonicoEnviado.isSMSEnviado(true, verificationId);
                    }

                }
        );
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
                                        Toast.makeText(mFragmento.getActivity(), "SI SE PUDO REGISTRAR Y SE ENVIO EL CODIGO AL CORREO", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        onCodigoCorreoEnviado.isCorreoEnviado(false);
                                        Toast.makeText(mFragmento.getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        Log.v("QUICKSTART", task.getException().getMessage());
                                    }
                                }
                            });
                        }
                        else
                            Toast.makeText(mFragmento.getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(mFragmento.getActivity(), "El código ingresaste es incorrecto.", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(mFragmento.getActivity(), "Por favor, verifica tu correo electrónico para completar tu registro", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                    Toast.makeText(mFragmento.getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}