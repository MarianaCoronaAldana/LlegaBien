package com.example.llegabien.frontend.usuario.dialog;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

public class DialogTimePicker extends DialogFragment {

    private TimePickerDialog.OnTimeSetListener listener;

    public static DialogTimePicker newInstance(TimePickerDialog.OnTimeSetListener listener) {
        DialogTimePicker fragment = new DialogTimePicker();
        fragment.setListener(listener);
        return fragment;
    }

    public void setListener(TimePickerDialog.OnTimeSetListener listener) {
        this.listener = listener;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(requireActivity(), listener, hour, minute,
                DateFormat.is24HourFormat(requireActivity()));
    }

    public void mostrarTimePickerDialog(EditText editTextHora, Fragment fragmento) {
        DialogTimePicker dialogTimePicker = DialogTimePicker.newInstance((datePicker, hourOfDay, minute) -> {
            String sMinute="", sHour="";
            if(minute<10)
                sMinute+= "0";

            if(hourOfDay<10)
                sHour+= "0";

            sMinute+=String.valueOf(minute);
            sHour+=String.valueOf(hourOfDay);

            final String fechaSeleccionada = sHour + ":" + sMinute;
            editTextHora.setText(fechaSeleccionada);
        });

        dialogTimePicker.show(fragmento.requireActivity().getSupportFragmentManager(), "datePicker");
    }
}