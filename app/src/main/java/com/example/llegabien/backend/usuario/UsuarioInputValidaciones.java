package com.example.llegabien.backend.usuario;

import android.content.Context;
import android.os.Build;
import android.widget.EditText;

import androidx.annotation.RequiresApi;

import com.example.llegabien.R;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UsuarioInputValidaciones {

    private String mStringParaValidar;
    private String mStringPattern;
    private Pattern mPattern;
    private Matcher mMatcher;

    public boolean validarStringVacia(Context context, EditText editText){
        mStringParaValidar = editText.getText().toString();

        if (mStringParaValidar.isEmpty()) {
            editText.setError(context.getResources().getString(R.string.errorStringVacia));
            return false;
        }
        return true;
    }

    /*public boolean validarInput (Context context, EditText editText, char tipoValidacion){
        String mStringPattern = "";
        String stringAValidar = editText.getText().toString();

        if (!validarStringVacia(context,editText))
            return false;

        else {
            switch (tipoValidacion) {
                case 'e':
                    mStringPattern = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
                    break;
                case 'c':
                    mStringPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&+-])[A-Za-z\\d@$!%*?&+-]{8,}$";
                    break;
                case 't':
                    mStringPattern = "\\d{10}";
                    break;
                case 'n':
                    mStringPattern = "^([A-Za-zÑñÁáÉéÍíÓóÚú]+['\\-]?[A-Za-zÑñÁáÉéÍíÓóÚú]+)(\\s+([A-Za-zÑñÁáÉéÍíÓóÚú]+['\\-]?[A-Za-zÑñÁáÉéÍíÓóÚú]+))*$";
                    break;
                case 'f':
                    return true;
            }

            Pattern mPattern = Pattern.compile(mStringPattern);
            Matcher mMatcher = mPattern.mMatcher(stringAValidar);
            if (!mMatcher.matches()) {
                switch (tipoValidacion) {
                    case 'e':
                        editText.setError(context.getResources().getString(R.string.errorEmail));
                        break;
                    case 'c':
                        editText.setError(context.getResources().getString(R.string.errorContraseña));
                        break;
                    case 't':
                        editText.setError(context.getResources().getString(R.string.errorNumeroTelefonico));
                        break;
                    case 'n':
                        editText.setError(context.getResources().getString(R.string.errorNombreApellidos));
                        break;
                }
                return false;
            }
        }
        return true;
    }*/

    public boolean validarNombre(Context context, EditText editText){
        mStringPattern = "^([A-Za-zÑñÁáÉéÍíÓóÚú]+['\\-]?[A-Za-zÑñÁáÉéÍíÓóÚú]+)(\\s+([A-Za-zÑñÁáÉéÍíÓóÚú]+['\\-]?[A-Za-zÑñÁáÉéÍíÓóÚú]+))*$";
        mStringParaValidar = editText.getText().toString();

        if (!validarStringVacia(context,editText))
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
    public boolean validarFechaNacimiento(Context context, EditText editText){
        mStringParaValidar = editText.getText().toString();
        if (!validarStringVacia(context,editText))
            return false;
        else {
            LocalDate fechaHoy = LocalDate.now();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd / M / yyyy");
            LocalDate fechaNacimiento = LocalDate.parse(mStringParaValidar, dateTimeFormatter);
            Period periodo = Period.between(fechaNacimiento, fechaHoy);
            if (periodo.getYears() < 16) {
                editText.setError(context.getResources().getString(R.string.errorFechaNacimiento));
                return false;
            }
        }
        return true;
    }

    public boolean validarCorreoElectronico(Context context, EditText editText){
        mStringPattern = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        mStringParaValidar = editText.getText().toString();

        if (!validarStringVacia(context,editText))
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

    public boolean validarNumTelefonico(Context context, EditText editText){
        mStringPattern = "\\d{10}";
        mStringParaValidar = editText.getText().toString();

        if (!validarStringVacia(context,editText))
            return false;
        else {
            mPattern = Pattern.compile(mStringPattern);
            mMatcher = mPattern.matcher(mStringParaValidar);
            if (!mMatcher.matches()) {
                editText.setError(context.getResources().getString(R.string.errorNumeroTelefonico));
                return false;
            }
        }
        return true;
    }

    public boolean validarContraseña(Context context, EditText editText){
        mStringPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&+-])[A-Za-z\\d@$!%*?&+-]{8,}$";
        mStringParaValidar = editText.getText().toString();

        if (!validarStringVacia(context,editText))
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

    public boolean validarConfirmarContraseña(String contraseña, Context context, EditText editText){
        if (!validarStringVacia(context,editText))
            return false;

        else if (!contraseña.equals(editText.getText().toString())) {
            editText.setError(context.getResources().getString(R.string.errorConfirmarContraseña));
            return false;
        }
        return true;
    }
}
