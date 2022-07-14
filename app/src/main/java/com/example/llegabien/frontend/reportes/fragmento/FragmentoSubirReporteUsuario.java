package com.example.llegabien.frontend.reportes.fragmento;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.llegabien.R;
import com.example.llegabien.backend.app.Permisos;
import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.mapa.ubicacion.UbicacionBusquedaAutocompletada;
import com.example.llegabien.backend.mapa.ubicacion.UbicacionDispositivo;
import com.example.llegabien.backend.mapa.ubicacion.UbicacionGeodicacion;
import com.example.llegabien.backend.reporte.ReporteDAO;
import com.example.llegabien.backend.reporte.reporte;
import com.example.llegabien.backend.usuario.UsuarioInputValidaciones;
import com.example.llegabien.backend.usuario.usuario;
import com.example.llegabien.frontend.usuario.dialog.DialogDatePicker;
import com.example.llegabien.frontend.usuario.dialog.DialogTimePicker;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import io.realm.RealmResults;

public class FragmentoSubirReporteUsuario extends Fragment implements View.OnClickListener {

    private Spinner mSpinnerCualDelito;
    private Button mBtnUbicacion;
    private usuario Usuario;
    private EditText mEditTxtNombre, mEditTxtFechaDelito, mEditTxtHoraDelito, mEditTxtComentariosDelito;
    private reporte Reporte;
    private UbicacionBusquedaAutocompletada ubicacionBusquedaAutocompletada;
    private final ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult activityResult) {
                            int result = activityResult.getResultCode();
                            Intent data = activityResult.getData();
                            ubicacionBusquedaAutocompletada.verificarResultadoBusqueda((isUbicacionBuscadaObtenida, isUbicacionBuscadaenBD, ubicacionBuscada, ubicacionBuscadaString) -> {
                                if (isUbicacionBuscadaObtenida)
                                    mBtnUbicacion.setText(ubicacionBuscadaString);
                            }, result, data, requireActivity());
                        }
                    }
            );

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
        Button mBtnRegresar = (Button) root.findViewById(R.id.button_regresar_subirReporteUsuario);
        Button mBtnSubirReporte = (Button) root.findViewById(R.id.button_enviarReporte_subirReporteUsuario);
        mEditTxtNombre = (EditText) root.findViewById(R.id.editText_nombreUsuario_subirReporteUsuario);
        mBtnUbicacion = root.findViewById(R.id.button_ubicacionDelito_subirReporteUsuario);
        mEditTxtFechaDelito = (EditText) root.findViewById(R.id.editText_fechaDelito_subirReporteUsuario);
        mEditTxtHoraDelito = (EditText) root.findViewById(R.id.editText_horaDelito_subirReporteUsuario);
        mEditTxtComentariosDelito = (EditText) root.findViewById(R.id.editText_comentariosAdicionales_subirReporteUsuario);

        // Listeners
        mBtnRegresar.setOnClickListener(this);
        mBtnSubirReporte.setOnClickListener(this);
        mEditTxtFechaDelito.setOnClickListener(this);
        mEditTxtHoraDelito.setOnClickListener(this);
        mBtnUbicacion.setOnClickListener(this);

        // Para inicializar valores del reporte.
        inicializarDatos();

        // Para obtener ubicacion actual.
        obtenerUbicacionActual();

        // Para inicializar valores del spinner.
        setSpinner();

        return root;
    }

    //FUNCIONES LISTENER//
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onClick(View view) {
        if (view.getId() == R.id.button_enviarReporte_subirReporteUsuario) {
            if (validarAllInputs()) {
                Log.v("QUICKSTART", "Estoy en enviar reporte");
                ReporteDAO reporteDAO = new ReporteDAO(this.getContext());
                inicializarReporte();
                if (verificarHistorialReportes(reporteDAO)) {
                    reporteDAO.anadirReporte(Reporte);
                    Toast.makeText(this.getContext(), "Tu reporte será verificado el siguiente fin de semana", Toast.LENGTH_LONG).show();
                    requireActivity().finish();
                }
            }
        } else if (view.getId() == R.id.editText_fechaDelito_subirReporteUsuario){
            Log.v("QUICKSTART", "Estoy en poner fecha delito");
            DialogDatePicker dialogDatePicker = new DialogDatePicker();
            dialogDatePicker.mostrarDatePickerDialog(mEditTxtFechaDelito, this);
            mEditTxtFechaDelito.setError(null);
        }
        else if (view.getId() == R.id.editText_horaDelito_subirReporteUsuario){
            Log.v("QUICKSTART", "Estoy en poner HORA delito");
            DialogTimePicker dialogTimePicker = new DialogTimePicker();
            dialogTimePicker.mostrarTimePickerDialog(mEditTxtHoraDelito, this);
            mEditTxtHoraDelito.setError(null);
        }
        else if (view.getId() == R.id.button_ubicacionDelito_subirReporteUsuario){
            ubicacionBusquedaAutocompletada = new UbicacionBusquedaAutocompletada();
            ubicacionBusquedaAutocompletada.inicializarIntent(requireActivity());
            activityResultLauncher.launch(ubicacionBusquedaAutocompletada.getIntent());
        }
        else if (view.getId() == R.id.button_regresar_subirReporteUsuario)
            requireActivity().finish();
    }

    // OTRAS FUNCIONES//

    // Para poner de default el nombre del usuario en el formulario
    private void inicializarDatos() {
        Usuario = Preferences.getSavedObjectFromPreference(requireActivity(), PREFERENCE_USUARIO, usuario.class);
        if (Usuario != null) {
            String nombreUsuarioEditTxt = Usuario.getNombre() + " " + Usuario.getApellidos();
            mEditTxtNombre.setText(nombreUsuarioEditTxt);
        }
        mEditTxtNombre.setEnabled(false);
        mEditTxtNombre.setClickable(false);
    }

    private void obtenerUbicacionActual() {
        Permisos permisos = new Permisos();
        permisos.getPermisoUbicacion(requireActivity(), false);
        if (permisos.getLocationPermissionGranted()) {
            FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

            UbicacionDispositivo mUbicacionDispositivo = new UbicacionDispositivo();
            mUbicacionDispositivo.getUbicacionDelDispositivo((isUbicacionObtenida, ubicacionObtenida) -> {
                if (isUbicacionObtenida) {
                    UbicacionGeodicacion ubicacionGeodicacion = new UbicacionGeodicacion();
                    String Ubicacion = ubicacionGeodicacion.degeocodificarUbiciacion(requireActivity(),
                            ubicacionObtenida.getLatitude(), ubicacionObtenida.getLongitude());

                    mBtnUbicacion.setText(Ubicacion);
                }
            }, true, fusedLocationProviderClient, requireActivity());
        }
    }

    //Funcion para crear objeto reporte e inicializarlo con los datos obtenidos
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void inicializarReporte() {
        Reporte = new reporte();
        Reporte.setAutor(mEditTxtNombre.getText().toString());
        Reporte.setIdUsuario(Usuario.get_id());
        Reporte.setComentarios(mEditTxtComentariosDelito.getText().toString());
        Reporte.setUbicacion(mBtnUbicacion.getText().toString());
        Reporte.setTipoDelito(mSpinnerCualDelito.getSelectedItem().toString());
        Reporte.setFechaReporte(convertToDateViaInstant(LocalDateTime.now()));

        LocalDate localDate = LocalDate.parse(mEditTxtFechaDelito.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        LocalTime localTime = LocalTime.parse(mEditTxtHoraDelito.getText(), DateTimeFormatter.ofPattern("HH:mm"));
        Reporte.setFecha(convertToDateViaInstant(localDate.atTime(localTime)));


        Log.v("QUICKSTART", "Fecha y hora delito: " + Reporte.getFechaReporte());

    }

    // Se encarga de verificar si
    // 1. ya se subio un reporte hace menos de dos horas
    // 2. se subieron mas de 10 reportes en una semana
    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean verificarHistorialReportes(ReporteDAO reporte_DAO) {
        RealmResults<reporte> reportes = reporte_DAO.obtenerReportesPorUsuario(Reporte);
        int reportesAnteriores = 0;

        Duration diff;
        long diffmINUTTES, diffHoras, diffDias;

        for (int i = 0; i < reportes.size(); i++) {
            if (reportesAnteriores > 9) {
                Toast.makeText(this.getContext(), "Has subido demasiados reportes esta semana", Toast.LENGTH_LONG).show();
                return false;
            }
            diff = Duration.between(convertToLocalDateTimeViaInstant(reportes.get(i).getFechaReporte()), convertToLocalDateTimeViaInstant(Reporte.getFechaReporte()));
            diffHoras = diff.toHours();
            diffDias = diff.toDays();

            diffmINUTTES = diff.toMinutes();
            Log.v("QUICKSTART", "diferencia minutos: " + diffmINUTTES + ", REPORTESanteriores: " + reportesAnteriores);

            if (diffHoras < 2) {
                Toast.makeText(this.getContext(), "Has subido un reporte muy recientemente", Toast.LENGTH_LONG).show();
                return false;
            }
            if (diffDias < 7)
                reportesAnteriores++;
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

    private void setSpinner() {
        String[] delitos = {"Homicidio", "Secuestro",
                "Extorsión", "Acoso sexual",
                "Robo", "Lesiones",
                "Abuso sexual", "Abuso de autoridad",
                "Vandalismo", "Tiroteo"};

        // Create the instance of ArrayAdapter having the list of courses
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(requireActivity(), R.layout.spinner_item, delitos);

        // Set simple layout resource file for each item of spinner
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        // Set the ArrayAdapter (ad) data on the Spinner which binds data to spinner
        mSpinnerCualDelito.setAdapter(arrayAdapter);
    }

    // Verificar que algunos campos no estén vacios
    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean validarAllInputs() {
        UsuarioInputValidaciones usuarioInputValidaciones = new UsuarioInputValidaciones();
        boolean esInputValido = true;
        if (usuarioInputValidaciones.validarStringVacia(requireActivity(), mEditTxtFechaDelito))
            esInputValido = false;
        if (usuarioInputValidaciones.validarStringVacia(requireActivity(), mEditTxtHoraDelito))
            esInputValido = false;
        if (mBtnUbicacion.getText().toString().equals(requireActivity().getResources().getString(R.string.ubicacionDelito_subirReporteUsuario))) {
            esInputValido = false;
            mBtnUbicacion.setError("Ingresa una ubicación válida.");
        }
        return esInputValido;
    }
}