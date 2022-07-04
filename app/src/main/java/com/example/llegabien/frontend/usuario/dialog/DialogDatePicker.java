package com.example.llegabien.frontend.usuario.dialog;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

public class DialogDatePicker extends DialogFragment {

    private DatePickerDialog.OnDateSetListener listener;

    public static DialogDatePicker newInstance(DatePickerDialog.OnDateSetListener listener) {
        DialogDatePicker fragment = new DialogDatePicker();
        fragment.setListener(listener);
        return fragment;
    }

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireActivity(), AlertDialog.THEME_HOLO_LIGHT, listener, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        return datePickerDialog;
    }

    public void mostrarDatePickerDialog(EditText editTextFechaNacimiento, Fragment fragmento) {
        DialogDatePicker dialogDatePicker = DialogDatePicker.newInstance((datePicker, year, month, day) -> {
            // +1 porque Enero es 0
            month++;

            String sMonth="", sDay="";
            if(month<10)
                sMonth+= "0";

            if(day<10)
                sDay+= "0";

            sMonth+=String.valueOf(month);
            sDay+=String.valueOf(day);

            final String fechaSeleccionada = sDay + "/" + sMonth + "/" + year;
            editTextFechaNacimiento.setText(fechaSeleccionada);
        });

        dialogDatePicker.show(fragmento.requireActivity().getSupportFragmentManager(), "datePicker");
    }
}