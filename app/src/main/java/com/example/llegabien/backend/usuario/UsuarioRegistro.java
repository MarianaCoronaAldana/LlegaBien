package com.example.llegabien.backend.usuario;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.llegabien.R;
import com.example.llegabien.frontend.usuario.fragmento.FragmentoRegistrarUsuario3;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mongodb.lang.NonNull;

import java.util.concurrent.TimeUnit;

public class UsuarioRegistro {

    public void verificarNumTelefonico (String numTelefonico, Fragment fragmento){
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
                        FragmentTransaction fragmentTransaction = fragmento.getActivity().getSupportFragmentManager().beginTransaction();
                        FragmentoRegistrarUsuario3 fragmentoRegistrarUsuario3 = FragmentoRegistrarUsuario3.newInstance(verificationId);
                        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                        fragmentTransaction.replace(R.id.fragment_pantallaPrincipal, fragmentoRegistrarUsuario3).commit();
                        fragmentTransaction.addToBackStack(null);
                    }

                }
        );
    }
}
