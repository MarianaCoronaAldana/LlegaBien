package com.example.llegabien.backend.usuario;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.llegabien.R;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UsuarioInputValidaciones {

    private String mStringParaValidar, mStringPattern;
    private Pattern mPattern;
    private Matcher mMatcher;

    public boolean validarStringVacia(Context context, EditText editText) {
        mStringParaValidar = editText.getText().toString().trim();

        if (mStringParaValidar.isEmpty()) {
            editText.setError(context.getResources().getString(R.string.errorStringVacia));
            return false;
        }
        return true;
    }

    public boolean validarNombre(Context context, EditText editText) {
        mStringPattern = "(^|\\s)([A-Za-zÑñÁáÉéÍíÓóÚú]+['\\-]?[A-Za-zÑñÁáÉéÍíÓóÚú]+)(\\s+([A-Za-zÑñÁáÉéÍíÓóÚú]+['\\-]?[A-Za-zÑñÁáÉéÍíÓóÚú]+))*($|\\s)";
        mStringParaValidar = editText.getText().toString().trim();

        if (!validarStringVacia(context, editText))
            return false;
        else {
            mPattern = Pattern.compile(mStringPattern);
            mMatcher = mPattern.matcher(mStringParaValidar);
            if (!mMatcher.matches()) {
                editText.setError(context.getResources().getString(R.string.errorNombreApellidos));
                return false;
            }
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean validarFechaNacimiento(Context context, EditText editText) {
        mStringParaValidar = editText.getText().toString().trim();
        if (!validarStringVacia(context, editText))
            return false;
        else {
            LocalDate fechaHoy = LocalDate.now();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate fechaNacimiento = LocalDate.parse(mStringParaValidar, dateTimeFormatter);
            Period periodo = Period.between(fechaNacimiento, fechaHoy);
            if (periodo.getYears() < 16) {
                editText.setError(context.getResources().getString(R.string.errorFechaNacimiento));
                Toast.makeText(context, context.getResources().getString(R.string.errorFechaNacimiento), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    public boolean validarCorreoElectronico(Context context, EditText editText) {
        mStringPattern = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        mStringParaValidar = editText.getText().toString().trim();

        if (!validarStringVacia(context, editText))
            return false;
        else {
            mPattern = Pattern.compile(mStringPattern);
            mMatcher = mPattern.matcher(mStringParaValidar);
            if (!mMatcher.matches()) {
                editText.setError(context.getResources().getString(R.string.errorCorreoElectronico));
                return false;
            }
        }
        return true;
    }

    public boolean validarNumTelefonico(Context context, EditText editText) {
        mStringPattern = "\\d+";
        mStringParaValidar = editText.getText().toString().trim();

        mPattern = Pattern.compile(mStringPattern);
        mMatcher = mPattern.matcher(mStringParaValidar);

        if (!validarStringVacia(context, editText))
            return false;
        else {

            if (!mMatcher.matches()) {
                editText.setError(context.getResources().getString(R.string.errorNumeroTelefonico));
                return false;
            }
        }
        return true;
    }

    public boolean validarNumTelefonico_libphonenumber(Context context, EditText editTxtPhoneNumber, EditText editTxtCountryCode) {
        String numeroTelefonico = editTxtPhoneNumber.getText().toString().trim();
        String countryCode = editTxtCountryCode.getText().toString().trim();

        //TODO:evaluar por separado el countrycode y el numero como tal, despues utilizar la esta funcion

        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        countryCode = phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(countryCode));
        Phonenumber.PhoneNumber phoneNumber = null;

        try {
            phoneNumber = phoneNumberUtil.parse(numeroTelefonico, countryCode);
        } catch (NumberParseException e) {
            System.err.println(e);
            editTxtPhoneNumber.setError(context.getResources().getString(R.string.errorNumeroTelefonico));
            return false;
        }

        if (!phoneNumberUtil.isValidNumber(phoneNumber)){
            editTxtPhoneNumber.setError(context.getResources().getString(R.string.errorNumeroTelefonico));
            return false;
        }

        return true;
    }

    public boolean validarContraseña(Context context, EditText editText) {
        mStringPattern = "(^|\\s)(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&+-])[A-Za-z\\d@$!%*?&+-]{8,}($|\\s)";
        mStringParaValidar = editText.getText().toString().trim();

        if (!validarStringVacia(context, editText))
            return false;
        else {
            mPattern = Pattern.compile(mStringPattern);
            mMatcher = mPattern.matcher(mStringParaValidar);
            if (!mMatcher.matches()) {
                editText.setError(context.getResources().getString(R.string.errorContraseña));
                return false;
            }
        }
        return true;
    }

    public boolean validarConfirmarContraseña(String contraseña, Context context, EditText
            editText) {
        if (!validarStringVacia(context, editText))
            return false;

        else if (!contraseña.equals(editText.getText().toString())) {
            editText.setError(context.getResources().getString(R.string.errorConfirmarContraseña));
            return false;
        }
        return true;
    }
}
