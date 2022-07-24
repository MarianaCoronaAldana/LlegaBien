package com.example.llegabien.frontend.app;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;

import androidx.core.content.ContextCompat;

import com.example.llegabien.R;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class Utilidades {

    public static void mostrarContraseña(EditText editText, Button button, Context context){
        if (button.getText().toString().equals("hide")) {
            button.setBackground(ContextCompat.getDrawable(context, R.drawable.bkgd_icon_ojo_abierto));
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            button.setText("show");
        }

        else if (button.getText().toString().equals("show")){
            button.setBackground(ContextCompat.getDrawable(context, R.drawable.bkgd_icon_ojo_cerrado));
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            button.setText("hide");
        }
    }

    public static String obtenerCountryCode(String numTelefonico){
        numTelefonico = "+" + numTelefonico;
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            // phone must begin with '+'
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(numTelefonico, "");
            return String.valueOf(numberProto.getCountryCode());
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
        }
        return null;
    }

    @SuppressLint("ObjectAnimatorBinding")
    public static void startAnimacionBtnEmergencia(ObjectAnimator scaleDown) {
        scaleDown.setDuration(2000);
        scaleDown.setRepeatMode(ValueAnimator.REVERSE);
        scaleDown.setRepeatCount(ValueAnimator.INFINITE);
        scaleDown.start();
    }
}
