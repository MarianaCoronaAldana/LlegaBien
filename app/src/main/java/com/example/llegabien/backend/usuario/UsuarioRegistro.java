package com.example.llegabien.backend.usuario;

import android.app.Activity;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mongodb.lang.NonNull;

import java.util.concurrent.TimeUnit;

public class UsuarioRegistro {

    public void verificarNumTelefonico (String numTelefonico, Activity activity){

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+52" + numTelefonico,
                60,
                TimeUnit.SECONDS,
                activity,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential){

                    }

                    @Override
                    public void onVerificationFailed(@androidx.annotation.NonNull FirebaseException e) {
                        Toast.makeText(activity,e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent (@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken){
                        Toast.makeText(activity,"SI SE PUDO",Toast.LENGTH_SHORT).show();
                    }

                }
        );
    }
}
