package com.example.llegabien.frontend.usuario.dialog;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
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

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT, listener, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());;

        return datePickerDialog;
    }

    public void mostrarDatePickerDialog(EditText editTextFechaNacimiento, Fragment fragmento) {
        DialogDatePicker dialogDatePicker = DialogDatePicker.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 porque Enero es 0
                final String fechaSeleccionada = day + "/" + (month+1) + "/" + year;
                editTextFechaNacimiento.setText(fechaSeleccionada);
            }
        });

        dialogDatePicker.show(fragmento.getActivity().getSupportFragmentManager(), "datePicker");
    }
}