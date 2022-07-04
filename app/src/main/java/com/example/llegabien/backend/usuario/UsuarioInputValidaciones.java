package com.example.llegabien.backend.usuario;

import android.content.Context;
import android.os.Build;
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
            return true;
        }
        return false;
    }

    public boolean validarNombre(Context context, EditText editText) {
        mStringPattern = "(^|\\s)([A-Za-zÑñÁáÉéÍíÓóÚú]+['\\-]?[A-Za-zÑñÁáÉéÍíÓóÚú]+)(\\s+([A-Za-zÑñÁáÉéÍíÓóÚú]+['\\-]?[A-Za-zÑñÁáÉéÍíÓóÚú]+))*($|\\s)";
        mStringParaValidar = editText.getText().toString().trim();

        if (validarStringVacia(context, editText))
            return true;
        else {
            mPattern = Pattern.compile(mStringPattern);
            mMatcher = mPattern.matcher(mStringParaValidar);
            if (!mMatcher.matches()) {
                editText.setError(context.getResources().getString(R.string.errorNombreApellidos));
                return true;
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean validarFechaNacimiento(Context context, EditText editText) {
        mStringParaValidar = editText.getText().toString().trim();
        if (validarStringVacia(context, editText))
            return true;
        else {
            LocalDate fechaHoy = LocalDate.now();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate fechaNacimiento = LocalDate.parse(mStringParaValidar, dateTimeFormatter);
            Period periodo = Period.between(fechaNacimiento, fechaHoy);
            if (periodo.getYears() < 16) {
                editText.setError(context.getResources().getString(R.string.errorFechaNacimiento));
                Toast.makeText(context, context.getResources().getString(R.string.errorFechaNacimiento), Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }

    public boolean validarCorreoElectronico(Context context, EditText editText) {
        mStringPattern = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        mStringParaValidar = editText.getText().toString().trim();

        if (validarStringVacia(context, editText))
            return true;
        else {
            mPattern = Pattern.compile(mStringPattern);
            mMatcher = mPattern.matcher(mStringParaValidar);
            if (!mMatcher.matches()) {
                editText.setError(context.getResources().getString(R.string.errorCorreoElectronico));
                return true;
            }
        }
        return false;
    }

    public boolean validarNumTelefonico(Context context, EditText editText) {
        mStringPattern = "\\d+";
        mStringParaValidar = editText.getText().toString().trim();

        mPattern = Pattern.compile(mStringPattern);
        mMatcher = mPattern.matcher(mStringParaValidar);

        if (validarStringVacia(context, editText))
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
        Phonenumber.PhoneNumber phoneNumber;

        try {
            phoneNumber = phoneNumberUtil.parse(numeroTelefonico, countryCode);
        } catch (NumberParseException e) {
            System.err.println(e);
            editTxtPhoneNumber.setError(context.getResources().getString(R.string.errorNumeroTelefonico));
            return true;
        }

        if (!phoneNumberUtil.isValidNumber(phoneNumber)){
            editTxtPhoneNumber.setError(context.getResources().getString(R.string.errorNumeroTelefonico));
            return true;
        }

        return false;
    }

    public boolean validarContrasena(Context context, EditText editText) {
        mStringPattern = "(^|\\s)(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&+-])[A-Za-z\\d@$!%*?&+-]{8,}($|\\s)";
        mStringParaValidar = editText.getText().toString().trim();

        if (validarStringVacia(context, editText))
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

    public boolean validarConfirmarContrasena(String contrasena, Context context, EditText editText) {
        if (validarStringVacia(context, editText))
            return true;

        else if (!contrasena.trim().equals(editText.getText().toString())) {
            editText.setError(context.getResources().getString(R.string.errorConfirmarContraseña));
            return true;
        }
        return false;
    }
}
