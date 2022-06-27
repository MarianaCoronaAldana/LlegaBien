package com.example.llegabien.frontend.reportes.fragmento;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.llegabien.R;
import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.mapa.ubicacion.ubicacion;
import com.example.llegabien.backend.mongoDB.ConectarBD;
import com.example.llegabien.backend.reporte.Reporte_DAO;
import com.example.llegabien.backend.reporte.reporte;
import com.example.llegabien.backend.usuario.UsuarioBD_CRUD;
import com.example.llegabien.backend.usuario.UsuarioInputValidaciones;
import com.example.llegabien.backend.usuario.usuario;
import com.example.llegabien.frontend.app.fragmento.FragmentoAuxiliar;
import com.example.llegabien.frontend.mapa.activity.ActivityMap;
import com.example.llegabien.frontend.usuario.dialog.DialogDatePicker;
import com.example.llegabien.frontend.usuario.dialog.DialogTimePicker;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import io.realm.RealmResults;

public class FragmentoSubirReporteUsuario extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private Spinner mSpinnerCualDelito;
    private Button mBtnRegresar, mBtnSubirReporte;
    private usuario Usuario;
    private EditText mEditTxtNombre, mEditTxtUbicacion, mEditTxtFechaDelito, mEditTxtHoraDelito, mEditTxtComentariosDelito ;
    private reporte Reporte;

    public FragmentoSubirReporteUsuario() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_subir_reporte_usuario, container, false);

        // Wiring up
        mSpinnerCualDelito = root.findViewById(R.id.spinner_cualDelito_subirReporteUsuario);
        mBtnRegresar = (Button) root.findViewById(R.id.button_regresar_subirReporteUsuario);
        mBtnSubirReporte = (Button) root.findViewById(R.id.button_enviarReporte_subirReporteUsuario);
        mEditTxtNombre = (EditText) root.findViewById(R.id.editText_nombreUsuario_subirReporteUsuario);
        mEditTxtUbicacion = (EditText) root.findViewById(R.id.editText_ubicacionDelito_subirReporteUsuario);
        mEditTxtFechaDelito = (EditText) root.findViewById(R.id.editText_fechaDelito_subirReporteUsuario);
        mEditTxtHoraDelito = (EditText) root.findViewById(R.id.editText_horaDelito_subirReporteUsuario);
        mEditTxtComentariosDelito = (EditText) root.findViewById(R.id.editText_comentariosAdicionales_subirReporteUsuario);

        // Listeners
        mSpinnerCualDelito.setOnItemSelectedListener(this);
        mBtnRegresar.setOnClickListener(this);
        mBtnSubirReporte.setOnClickListener(this);
        mEditTxtFechaDelito.setOnClickListener(this);
        mEditTxtHoraDelito.setOnClickListener(this);

        // Para inicializar valores del del reporte.
        inicializarDatos();

        return root;
    }

    //FUNCIONES LISTENER//
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_enviarReporte_subirReporteUsuario:
                if (validarAllInputs()) {
                    Log.v("QUICKSTART", "Estoy en enviar reporte");
                    Reporte_DAO usuarioBD_CRUD = new Reporte_DAO(this.getContext());
                    inicializarReporte();
                    if(verificarHistorialReportes(usuarioBD_CRUD)) {
                        usuarioBD_CRUD.añadirReporte(Reporte);
                        Toast.makeText(this.getContext(), "Tu reporte será verificado el siguiente fin de semana", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case R.id.button_regresar_subirReporteUsuario:
                    startActivity(new Intent(getActivity(), ActivityMap.class));
            break;
            case R.id.editText_fechaDelito_subirReporteUsuario:
                Log.v("QUICKSTART", "Estoy en poner fecha delito");
                DialogDatePicker dialogDatePicker = new DialogDatePicker();
                dialogDatePicker.mostrarDatePickerDialog(mEditTxtFechaDelito, this);
                mEditTxtFechaDelito.setError(null);
                break;
            case R.id.editText_horaDelito_subirReporteUsuario:
                Log.v("QUICKSTART", "Estoy en poner HORA delito");
                DialogTimePicker dialogTimePicker = new DialogTimePicker();
                dialogTimePicker.mostrarTimePickerDialog(mEditTxtHoraDelito, this);
                mEditTxtHoraDelito.setError(null);
                break;
        }
    }

    // Para poner de default el nombre del usuario en el formulario
    private void inicializarDatos() {
        Usuario = Preferences.getSavedObjectFromPreference(getActivity(), PREFERENCE_USUARIO, usuario.class);
        mEditTxtNombre.setText(Usuario.getNombre() + " " + Usuario.getApellidos());
        mEditTxtNombre.setEnabled(false);
        mEditTxtNombre.setClickable(false);

        // Para inicializar valores del spinner.
        setSpinner();
    }

    private void setSpinner(){
        String[] delitos = { "Homicidio", "Secuestro",
                "Extorsión", "Acoso sexual",
                "Robo", "Lesiones",
                "Abuso sexual", "Abuso de autoridad",
                "Vandalismo", "Tiroteo"};

        // Create the instance of ArrayAdapter having the list of courses
        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.spinner_item, delitos);

        // Set simple layout resource file for each item of spinner
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        // Set the ArrayAdapter (ad) data on the Spinner which binds data to spinner
        mSpinnerCualDelito.setAdapter(arrayAdapter);
    }

    // Verificar que algunos campos no estén vacios
    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean validarAllInputs(){
        UsuarioInputValidaciones usuarioInputValidaciones = new UsuarioInputValidaciones();
        boolean esInputValido = true;
      if (!usuarioInputValidaciones.validarStringVacia(getActivity(),mEditTxtFechaDelito))
            esInputValido = false;
      if (!usuarioInputValidaciones.validarStringVacia(getActivity(),mEditTxtHoraDelito))
            esInputValido = false;
      if (!usuarioInputValidaciones.validarStringVacia(getActivity(),mEditTxtUbicacion))
            esInputValido = false;
        return esInputValido;
    }

    //Funcion para crear objeto reporte e inicializarlo con los datos obtenidos
    @RequiresApi(api = Build.VERSION_CODES.O)
    private reporte inicializarReporte(){
        Reporte = new reporte();
        Reporte.setAutor(mEditTxtNombre.getText().toString());
        Reporte.setIdUsuario(Usuario.get_id());
        Reporte.setComentarios(mEditTxtComentariosDelito.getText().toString());
        Reporte.setUbicacion(mEditTxtUbicacion.getText().toString());
        Reporte.setTipoDelito(mSpinnerCualDelito.getSelectedItem().toString());

        LocalDate localDate =  LocalDate.parse(mEditTxtFechaDelito.getText(), DateTimeFormatter.ofPattern("d/M/yyyy"));
        LocalTime localTime = LocalTime.parse(mEditTxtHoraDelito.getText(), DateTimeFormatter.ofPattern("HH:mm"));
        Reporte.setFecha(convertToDateViaInstant(localDate.atTime(localTime)));

        Log.v("QUICKSTART", "Fecha y hora delito: "+Reporte.getFecha());

        return Reporte;
    }

    // Se encarga de verificar si
    // 1. ya se subio un reporte hace menos de dos horas
    // 2. se subieron mas de 10 reportes en una semana
    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean verificarHistorialReportes(Reporte_DAO reporte_DAO){
        RealmResults<reporte> reportes = reporte_DAO.obtenerReportesPorUsuario(Reporte);
        int reportesAnteriores =0;

        convertToLocalDateTimeViaInstant(Reporte.getFecha());
        Duration diff;
        diff = Duration.between(convertToLocalDateTimeViaInstant(Reporte.getFecha()), convertToLocalDateTimeViaInstant(Reporte.getFecha()));
        long diffmINUTTES, diffHoras, diffDias;
        diffmINUTTES = diff.toMinutes();

        for(int i = 0; i< reportes.size(); i++) {
            if (reportesAnteriores > 10) {
                Toast.makeText(this.getContext(), "Has subido demasiados reportes esta semana", Toast.LENGTH_LONG).show();
                return false;
            }
            diff = Duration.between(convertToLocalDateTimeViaInstant(reportes.get(i).getFecha()), convertToLocalDateTimeViaInstant(Reporte.getFecha()));
            diffHoras = diff.toHours();
            diffDias = diff.toDays();

            diffmINUTTES = diff.toMinutes();
            Log.v("QUICKSTART", "diferencia minutos: " + diffmINUTTES);

            if (diffHoras < 2){
                Toast.makeText(this.getContext(), "Has subido un reporte muy recientemente", Toast.LENGTH_LONG).show();
                return false;
            }
            if(diffDias<7)
                reportesAnteriores ++;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    Date convertToDateViaInstant(LocalDateTime dateToConvert) {
        return java.util.Date
                .from(dateToConvert.atZone(ZoneId.systemDefault())
                        .toInstant());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}