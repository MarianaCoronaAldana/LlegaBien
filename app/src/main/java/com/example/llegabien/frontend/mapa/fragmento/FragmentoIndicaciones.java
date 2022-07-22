package com.example.llegabien.frontend.mapa.fragmento;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.llegabien.R;
import com.example.llegabien.backend.mapa.ubicacion.UbicacionBusquedaAutocompletada;
import com.example.llegabien.backend.mapa.ubicacion.UbicacionDispositivo;
import com.example.llegabien.backend.mapa.ubicacion.UbicacionGeocodificacion;
import com.example.llegabien.frontend.mapa.Mapa;
import com.example.llegabien.frontend.mapa.activity.ActivityMap;
import com.example.llegabien.frontend.rutas.directionhelpers.FetchURL;
import com.example.llegabien.frontend.rutas.directionhelpers.TaskLoadedCallback;
import com.google.android.gms.maps.model.LatLng;

public class FragmentoIndicaciones extends Fragment implements View.OnClickListener {

    Button mBtnPuntoPartida, mBtnPuntoDestino, mBtnPresionado;
    ConstraintLayout mBtnTiempoBici, mBtnTiempoCaminando;
    TextView mTxtViewTiempoBici, mTxtViewTiempoCaminando;
    View mViewBici, mViewCaminar;
    String mUbicacionBuscada, mDirectionMode = "walking";

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
                                    mBtnPresionado.setText(ubicacionBuscadaString);
                            }, result, data, requireActivity());
                        }

                    }
            );

    public FragmentoIndicaciones(){
    }

    public FragmentoIndicaciones(String ubicacionBuscada){
        mUbicacionBuscada = ubicacionBuscada;
    }

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
        if(mUbicacionBuscada != null){
            mBtnPuntoDestino.setText(mUbicacionBuscada);
            tomarDatosRuta();
        }

        mDirectionMode = "bicycling";
        return root;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_comenzar_indicaciones) {
            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            FragmentoNavegacion fragmentoNavegacion = new FragmentoNavegacion();
            fragmentTransaction.replace(R.id.fragmentContainerView_fragmentoLugares_activityMaps, fragmentoNavegacion).commit();
            fragmentTransaction.addToBackStack(null);
        }
        else if (view.getId() == R.id.button_puntoPartida_indicaciones || view.getId() == R.id.button_puntoDestino_indicaciones) {
            if (view.getId() == R.id.button_puntoPartida_indicaciones)
                mBtnPresionado = mBtnPuntoPartida;
            else if (view.getId() == R.id.button_puntoDestino_indicaciones)
                mBtnPresionado = mBtnPuntoDestino;

            ubicacionBusquedaAutocompletada = new UbicacionBusquedaAutocompletada();
            ubicacionBusquedaAutocompletada.inicializarIntent(requireActivity());
            activityResultLauncher.launch(ubicacionBusquedaAutocompletada.getIntent());
            tomarDatosRuta();
        }
        else if (view.getId() == R.id.button_tiempoCaminando_indicaciones) {
            mBtnTiempoCaminando.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.morado_oscuro));
            mTxtViewTiempoCaminando.setTextColor(this.requireActivity().getResources().getColor(R.color.blanco));
            mViewCaminar.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.blanco));

            mBtnTiempoBici.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.blanco));
            mTxtViewTiempoBici.setTextColor(this.requireActivity().getResources().getColor(R.color.negro));
            mViewBici.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.negro));
            mDirectionMode = "walking";
            tomarDatosRuta();
        }
        else if (view.getId() == R.id.button_tiempoBici_indicaciones) {
            mBtnTiempoCaminando.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.blanco));
            mTxtViewTiempoCaminando.setTextColor(this.requireActivity().getResources().getColor(R.color.negro));
            mViewCaminar.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.negro));

            mBtnTiempoBici.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.morado_oscuro));
            mTxtViewTiempoBici.setTextColor(this.requireActivity().getResources().getColor(R.color.blanco));
            mViewBici.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.blanco));
            mDirectionMode = "bicycling";
            tomarDatosRuta();
        }
        else if (view.getId() == R.id.button_intercambiarDireccion_partidaDestino) {
            if(mBtnPuntoPartida.getText() != null && mBtnPuntoDestino.getText() != null){
                String puntoDestino = mBtnPuntoDestino.getText().toString();
                mBtnPuntoDestino.setText(mBtnPuntoPartida.getText().toString());
                mBtnPuntoPartida.setText(puntoDestino);
                tomarDatosRuta();
            }
        }
        else if (view.getId() == R.id.button_centrarMapa_indicaciones) {
            Mapa mapa = new Mapa((ActivityMap) requireActivity());
            mapa.centrarMapa();
        }
    }

    private void tomarDatosRuta(){
        LatLng origen = obtenerCoordenadas(mBtnPuntoPartida.getText().toString());
        LatLng destino = obtenerCoordenadas(mBtnPuntoDestino.getText().toString());
        if (origen!=null && destino != null)
            new FetchURL((TaskLoadedCallback) requireActivity()).execute(generarUrlRuta(origen, destino), mDirectionMode);
        else
            Log.v("QUICKSTART", "wey es nulo");
/*
        MarkerOptions place1, place2;
        place1 = new MarkerOptions().position(new LatLng(20.6674235372583, -103.31179439549422)).title("Location 1");
        place2 = new MarkerOptions().position(new LatLng(20.67097726320246, -103.31441214692855)).title("Location 2");
        new FetchURL((TaskLoadedCallback) requireActivity()).execute(generarUrlRuta(place1.getPosition(), place2.getPosition()), "walking", "walking");*/
    }

    private LatLng obtenerCoordenadas(String adress){
        UbicacionGeocodificacion ubicacionGeodicacion = new UbicacionGeocodificacion(this.requireActivity().getApplicationContext());
        Address ubicacionGeocodificada = ubicacionGeodicacion.geocodificarUbiciacion(adress);
        if(ubicacionGeocodificada!=null)
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
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters +"&alternatives=true" +"&key=" + getString(R.string.api_key);
        Log.v("QUICKSTART", "url: ");
        Log.v("QUICKSTART", url);
        return url;
    }

}