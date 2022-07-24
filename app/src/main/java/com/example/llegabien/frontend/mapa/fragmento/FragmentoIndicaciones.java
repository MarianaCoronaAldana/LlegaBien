package com.example.llegabien.frontend.mapa.fragmento;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;
import static io.realm.Realm.getApplicationContext;

import android.content.Intent;
import android.location.Address;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.llegabien.R;
import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.ruta.directions.Ruta;
import com.example.llegabien.backend.ruta.realm.ruta;
import com.example.llegabien.backend.ruta.realm.rutaDAO;
import com.example.llegabien.backend.ubicacion.UbicacionBusquedaAutocompletada;
import com.example.llegabien.backend.ubicacion.UbicacionDispositivo;
import com.example.llegabien.backend.ubicacion.UbicacionGeocodificacion;
import com.example.llegabien.backend.usuario.usuario;
import com.example.llegabien.frontend.mapa.Mapa;
import com.example.llegabien.frontend.mapa.activity.ActivityMap;
import com.example.llegabien.frontend.rutas.directionhelpers.FetchURL;
import com.example.llegabien.frontend.rutas.directionhelpers.TaskLoadedCallback;
import com.google.android.gms.maps.model.LatLng;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class FragmentoIndicaciones extends Fragment implements View.OnClickListener, TaskLoadedCallback {

    Button mBtnPuntoPartida, mBtnPuntoDestino, mBtnPresionado;
    ConstraintLayout mBtnTiempoBici, mBtnTiempoCaminando;
    TextView mTxtViewTiempoBici, mTxtViewTiempoCaminando, mTxtViewTiempoDetalles, mTxtViewDistanciaDetalles;
    View mViewBici, mViewCaminar;
    String mPuntoPartida, mPuntoDestino, mUbicacionBuscada, mDirectionMode = "walking";

    private UbicacionBusquedaAutocompletada ubicacionBusquedaAutocompletada;
    private final ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onActivityResult(ActivityResult activityResult) {
                            int result = activityResult.getResultCode();
                            Intent data = activityResult.getData();
                            ubicacionBusquedaAutocompletada.verificarResultadoBusqueda((isUbicacionBuscadaObtenida, isUbicacionBuscadaenBD, ubicacionBuscada, ubicacionBuscadaString) -> {
                                if (isUbicacionBuscadaObtenida) {
                                    mBtnPresionado.setText(ubicacionBuscadaString);
                                    tomarDatosRuta();
                                }
                            }, result, data, requireActivity());
                        }

                    }
            );

    public FragmentoIndicaciones() {
    }

    public FragmentoIndicaciones(String ubicacionBuscada) {
        mUbicacionBuscada = ubicacionBuscada;
    }

    public FragmentoIndicaciones(String puntoPartida, String puntoDestino) {
        mPuntoPartida = puntoPartida;
        mPuntoDestino = puntoDestino;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_indicaciones, container, false);

        //wiring up
        ConstraintLayout btnComenzarNavegacion = root.findViewById(R.id.button_comenzar_indicaciones);
        Button btnIntercambiarPuntos = root.findViewById(R.id.button_intercambiarDireccion_partidaDestino);
        mBtnPuntoPartida = root.findViewById(R.id.button_puntoPartida_indicaciones);
        mBtnPuntoDestino = root.findViewById(R.id.button_puntoDestino_indicaciones);
        mBtnTiempoCaminando = root.findViewById(R.id.button_tiempoCaminando_indicaciones);
        mBtnTiempoBici = root.findViewById(R.id.button_tiempoBici_indicaciones);
        mTxtViewTiempoBici = root.findViewById(R.id.textView_btnTiempoBici);
        mTxtViewTiempoCaminando = root.findViewById(R.id.textView_btnTiempoCaminando);
        mTxtViewTiempoDetalles = root.findViewById(R.id.textView_tiempo_detallesRuta);
        mTxtViewDistanciaDetalles = root.findViewById(R.id.textView_km_detallesRuta);
        mViewBici = root.findViewById(R.id.iconBici_btnTimepoBici);
        mViewCaminar = root.findViewById(R.id.iconCaminando_btnTimepoCaminando);
        ConstraintLayout btnCentrarMapa = root.findViewById(R.id.button_centrarMapa_indicaciones);

        //listeners
        mBtnTiempoCaminando.setOnClickListener(this);
        mBtnTiempoBici.setOnClickListener(this);
        mBtnPuntoDestino.setOnClickListener(this);
        mBtnPuntoPartida.setOnClickListener(this);
        btnCentrarMapa.setOnClickListener(this);
        btnComenzarNavegacion.setOnClickListener(this);
        btnIntercambiarPuntos.setOnClickListener(this);

        //Para mostrar la ubicacion del dispositivo en Boton.
        UbicacionDispositivo ubicacionDispositivo = new UbicacionDispositivo();
        ubicacionDispositivo.mostrarStringUbicacionActual(requireActivity(), mBtnPuntoPartida, this);

        // Para mostrar la informacion del lugar seleccionado en Boton
        if (mUbicacionBuscada != null) {
            mBtnPuntoDestino.setText(mUbicacionBuscada);
            tomarDatosRuta();
        }

        if (mPuntoPartida != null && mPuntoDestino != null) {
            mBtnPuntoDestino.setText(mPuntoDestino);
            mBtnPuntoDestino.setText(mPuntoDestino);
        }

        mDirectionMode = "bicycling";
        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_comenzar_indicaciones) {
            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            FragmentoNavegacion fragmentoNavegacion = new FragmentoNavegacion();
            fragmentTransaction.replace(R.id.fragmentContainerView_fragmentoLugares_activityMaps, fragmentoNavegacion).commit();
            fragmentTransaction.addToBackStack(null);
        } else if (view.getId() == R.id.button_puntoPartida_indicaciones || view.getId() == R.id.button_puntoDestino_indicaciones) {
            if (view.getId() == R.id.button_puntoPartida_indicaciones)
                mBtnPresionado = mBtnPuntoPartida;
            else if (view.getId() == R.id.button_puntoDestino_indicaciones)
                mBtnPresionado = mBtnPuntoDestino;

            ubicacionBusquedaAutocompletada = new UbicacionBusquedaAutocompletada();
            ubicacionBusquedaAutocompletada.inicializarIntent(requireActivity());
            activityResultLauncher.launch(ubicacionBusquedaAutocompletada.getIntent());
            //  tomarDatosRuta();
        } else if (view.getId() == R.id.button_tiempoCaminando_indicaciones) {
            mBtnTiempoCaminando.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.morado_oscuro));
            mTxtViewTiempoCaminando.setTextColor(this.requireActivity().getResources().getColor(R.color.blanco));
            mViewCaminar.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.blanco));

            mBtnTiempoBici.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.blanco));
            mTxtViewTiempoBici.setTextColor(this.requireActivity().getResources().getColor(R.color.negro));
            mViewBici.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.negro));
            mDirectionMode = "walking";
            tomarDatosRuta();
        } else if (view.getId() == R.id.button_tiempoBici_indicaciones) {
            mBtnTiempoCaminando.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.blanco));
            mTxtViewTiempoCaminando.setTextColor(this.requireActivity().getResources().getColor(R.color.negro));
            mViewCaminar.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.negro));

            mBtnTiempoBici.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.morado_oscuro));
            mTxtViewTiempoBici.setTextColor(this.requireActivity().getResources().getColor(R.color.blanco));
            mViewBici.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.blanco));
            mDirectionMode = "bicycling";
            tomarDatosRuta();
        } else if (view.getId() == R.id.button_intercambiarDireccion_partidaDestino) {
            if (mBtnPuntoPartida.getText() != null && mBtnPuntoDestino.getText() != null) {
                String puntoDestino = mBtnPuntoDestino.getText().toString();
                mBtnPuntoDestino.setText(mBtnPuntoPartida.getText().toString());
                mBtnPuntoPartida.setText(puntoDestino);
                tomarDatosRuta();
            }
        } else if (view.getId() == R.id.button_centrarMapa_indicaciones) {
            Mapa mapa = new Mapa((ActivityMap) requireActivity());
            mapa.centrarMapa();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void tomarDatosRuta() {
        LatLng origen = obtenerCoordenadas(mBtnPuntoPartida.getText().toString());
        LatLng destino = obtenerCoordenadas(mBtnPuntoDestino.getText().toString());
        if (origen != null && destino != null) {
            anadirRutaBD(origen, destino);
            Toast.makeText(getApplicationContext(), "Creando ruta....", Toast.LENGTH_SHORT).show();
            new FetchURL((TaskLoadedCallback) this, (TaskLoadedCallback) requireActivity(), this.requireActivity().getApplicationContext()).execute(generarUrlRuta(origen, destino), mDirectionMode);
        } else {
            Toast.makeText(getApplicationContext(), "Ingresa punto de origen y destino", Toast.LENGTH_SHORT).show();
            Log.v("QUICKSTART", "wey es nulo");
        }
    }

    private LatLng obtenerCoordenadas(String adress) {
        UbicacionGeocodificacion ubicacionGeodicacion = new UbicacionGeocodificacion(this.requireActivity().getApplicationContext());
        Address ubicacionGeocodificada = ubicacionGeodicacion.geocodificarUbiciacion(adress);
        if (ubicacionGeocodificada != null)
            return new LatLng(ubicacionGeocodificada.getLatitude(), ubicacionGeocodificada.getLongitude());
        return null;
    }

    private String generarUrlRuta(LatLng origen, LatLng dest) {
        // Origen de la ruta
        String str_origen = "origin=" + origen.latitude + "," + origen.longitude;
        // Destino de la ruta
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Modo de viaje
        String mode = "mode=" + mDirectionMode;
        // Construyendo string
        String parameters = str_origen + "&" + str_dest + "&" + mode;
        // Formato de salida
        String output = "json";
        // Construyendo url final
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&alternatives=true" + "&key=" + getString(R.string.api_key);
        Log.v("QUICKSTART", "url: ");
        Log.v("QUICKSTART", url);
        return url;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void anadirRutaBD(LatLng origen, LatLng destino) {
        usuario Usuario = Preferences.getSavedObjectFromPreference(requireActivity().getApplicationContext(), PREFERENCE_USUARIO, usuario.class);
        ruta Ruta = new ruta();
        Ruta.setIdUsuario(Usuario.get_id());
        Ruta.setFUsoRuta(java.util.Date
                .from(LocalDateTime.now().atZone(ZoneId.systemDefault())
                        .toInstant()));
        Ruta.setPuntoInicio(origen);
        Ruta.setPuntoDestino(destino);
        rutaDAO rutaDAO = new rutaDAO(getApplicationContext());

        // Si la ruta ya está dentro de la base de datos, solo se actualiza su atributo "fUsoRuta"
        if (rutaDAO.verificarExistenciaRuta(Ruta.getIdUsuario(), Ruta.getPuntoInicio(), Ruta.getPuntoDestino()))
            rutaDAO.anadirRuta(Ruta);
        else {
            Log.v("QUICKSTART", "RUTA YA AÑADIDA A BASE DE DATOS");
            rutaDAO.actualizarFechaRuta(Ruta);
        }
    }

    // Recibe el objeto Ruta que representa la ruta más segura, toma su información de distancia y tiempo
    // y pone sus valores en los TxtViews correspondientes
    // Luego la manda a la clase ActivityMap para que se pueda imprimir la ruta en sí
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onTaskDone(Object... values) {
        Log.v("QUICKSTART", "ESTOY EN FRAGMENTO");
        Ruta rutaMasSegura = (Ruta) values[0];
        mTxtViewTiempoDetalles.setText(rutaMasSegura.getTiempoTotal().replace("hours", "horas"));
        mTxtViewDistanciaDetalles.setText(rutaMasSegura.getDistanciaTotalDirections());
        TaskLoadedCallback taskLoadedCallbackActivity = (TaskLoadedCallback) values[1];
        taskLoadedCallbackActivity.onTaskDone(rutaMasSegura);
    }
}