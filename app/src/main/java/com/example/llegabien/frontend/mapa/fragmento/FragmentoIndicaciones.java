package com.example.llegabien.frontend.mapa.fragmento;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.llegabien.R;
import com.example.llegabien.backend.mapa.ubicacion.UbicacionBusquedaAutocompletada;
import com.example.llegabien.backend.mapa.ubicacion.UbicacionDispositivo;
import com.example.llegabien.frontend.mapa.Mapa;
import com.example.llegabien.frontend.mapa.activity.ActivityMap;

public class FragmentoIndicaciones extends Fragment implements View.OnClickListener {

    Button mBtnPuntoPartida, mBtnPuntoDestino, mBtnPresionado;
    ConstraintLayout mBtnTiempoBici, mBtnTiempoCaminando;
    TextView mTxtViewTiempoBici, mTxtViewTiempoCaminando;
    View mViewBici, mViewCaminar;
    String mUbicacionBuscada;
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

        // Para
        if(mUbicacionBuscada != null){
            mBtnPuntoDestino.setText(mUbicacionBuscada);
        }

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
        }
        else if (view.getId() == R.id.button_tiempoCaminando_indicaciones) {
            mBtnTiempoCaminando.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.morado_oscuro));
            mTxtViewTiempoCaminando.setTextColor(this.requireActivity().getResources().getColor(R.color.blanco));
            mViewCaminar.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.blanco));

            mBtnTiempoBici.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.blanco));
            mTxtViewTiempoBici.setTextColor(this.requireActivity().getResources().getColor(R.color.negro));
            mViewBici.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.negro));
        }
        else if (view.getId() == R.id.button_tiempoBici_indicaciones) {
            mBtnTiempoCaminando.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.blanco));
            mTxtViewTiempoCaminando.setTextColor(this.requireActivity().getResources().getColor(R.color.negro));
            mViewCaminar.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.negro));

            mBtnTiempoBici.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.morado_oscuro));
            mTxtViewTiempoBici.setTextColor(this.requireActivity().getResources().getColor(R.color.blanco));
            mViewBici.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.blanco));

        }

        else if (view.getId() == R.id.button_intercambiarDireccion_partidaDestino) {
            if(mBtnPuntoPartida.getText() != null && mBtnPuntoDestino.getText() != null){
            String puntoDestino = mBtnPuntoDestino.getText().toString();
            mBtnPuntoDestino.setText(mBtnPuntoPartida.getText().toString());
            mBtnPuntoPartida.setText(puntoDestino);
            }
        }
        else if (view.getId() == R.id.button_centrarMapa_indicaciones) {
            Mapa mapa = new Mapa((ActivityMap) requireActivity());
            mapa.centrarMapa();
        }
    }
}