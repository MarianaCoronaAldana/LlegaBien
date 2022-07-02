package com.example.llegabien.frontend.mapa.fragmento;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_FAVORITO;
import static com.example.llegabien.backend.app.Preferences.PREFERENCE_UBICACION;

import android.content.Intent;
import android.os.Build;
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
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.llegabien.R;
import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.mapa.favoritos.favorito;
import com.example.llegabien.backend.mapa.ubicacion.UbicacionBusquedaAutocompletada;
import com.example.llegabien.backend.mapa.ubicacion.UbicacionGeodicacion;
import com.example.llegabien.backend.mapa.ubicacion.ubicacion;
import com.example.llegabien.frontend.mapa.activity.ActivityMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class FragmentoLugarSeleccionado extends Fragment implements View.OnClickListener {

    private TextView mTxtNombre1Lugar, mTxtNombre2Lugar, mTxtDelitosSemana, mTxtMediaHistorica, mTxtSeguridad;
    ConstraintLayout mBtnGuardarEnFavoritos;
    private View mIconSeguridad;
    private String mUbicacionBuscada = null, mActividadAnterior = null;
    private LatLng mCoordenadasParaFavorito = null;
    private UbicacionBusquedaAutocompletada ubicacionBusquedaAutocompletada;
    private final ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult activityResult) {
                            int result = activityResult.getResultCode();
                            Intent data = activityResult.getData();
                            ubicacionBusquedaAutocompletada.verificarResultadoBusqueda(new UbicacionBusquedaAutocompletada.OnUbicacionBuscadaObtenida() {
                                @Override
                                public void isUbicacionBuscadaObtenida(boolean isUbicacionBuscadaObtenida, boolean isUbicacionBuscadaEnBD, LatLng ubicacionBuscada, String ubicacionBuscadaString) {
                                    if (isUbicacionBuscadaObtenida)
                                        ((ActivityMap) getActivity()).mostrarUbicacionBuscada(isUbicacionBuscadaEnBD, false, ubicacionBuscada, ubicacionBuscadaString);
                                }
                            }, result, data, getActivity());
                        }
                    }
            );

    public FragmentoLugarSeleccionado(String actividadAnterior) {
        mActividadAnterior = actividadAnterior;
    }

    public void setUbicacionBuscada(String ubicacionBuscada) {
        mUbicacionBuscada = ubicacionBuscada;
    }

    public void setCoordenadasParaFavorito(LatLng coordenadasParaFavorito) {
        mCoordenadasParaFavorito= coordenadasParaFavorito;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_lugar_seleccionado, container, false);

        //wirirng up
        BottomSheetBehavior<ConstraintLayout> mBottomSheetBehavior = BottomSheetBehavior.from(root.findViewById(R.id.bottomSheet_detallesLugar));
        Button mBtnRegresar = root.findViewById(R.id.button_regresar_barraBusqueda_lugarSeleccionado);
        Button mBtnBarraBusqueda = root.findViewById(R.id.button_titulo_barraBusqueda_lugarSeleccionado);
        ConstraintLayout btnCentrarMapa = root.findViewById(R.id.button_centrarMapa_lugarSeleccionado);
        mBtnGuardarEnFavoritos = root.findViewById(R.id.button_añadirFavorito_detallesLugar);
        mTxtNombre1Lugar = root.findViewById(R.id.textView1_nombreLugar_detallesLugar);
        mTxtNombre2Lugar = root.findViewById(R.id.textView2_nombreLugar_detallesLugar);
        mTxtSeguridad = root.findViewById(R.id.textView_seguridad_detallesLugar);
        mTxtMediaHistorica = root.findViewById(R.id.textView_mediHistorica_detallesLugar);
        mTxtDelitosSemana = root.findViewById(R.id.textView_numDelitos_detallesLugar);
        mIconSeguridad = root.findViewById(R.id.icon_seguridad_detallesLugar);

        //para que la bottomSheet se abra en STATE.EXPANDED
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        //listeners
        mBtnRegresar.setOnClickListener(this);
        mBtnBarraBusqueda.setOnClickListener(this);
        btnCentrarMapa.setOnClickListener(this);
        mBtnGuardarEnFavoritos.setOnClickListener(this);
        mBottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED)
                    Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_EXPANDED");
                if (newState == BottomSheetBehavior.STATE_COLLAPSED)
                    Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_COLLAPSED");
                else
                    Log.i("BottomSheetCallback", "BottomSheetBehavior.OTHER");
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.i("BottomSheetCallback", "slideOffset: " + slideOffset);
            }
        });

        //para establecer valores dependiendo ubicacion
        setValoresBotttomSheet();

        return root;
    }

    //FUNCIONES LISTENERS//
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_titulo_barraBusqueda_lugarSeleccionado:
                ubicacionBusquedaAutocompletada = new UbicacionBusquedaAutocompletada();
                ubicacionBusquedaAutocompletada.inicializarIntent(getActivity());
                activityResultLauncher.launch(ubicacionBusquedaAutocompletada.getIntent());
                break;
            case R.id.button_añadirFavorito_detallesLugar:
                if (mCoordenadasParaFavorito != null) {
                    UbicacionGeodicacion ubicacionGeodicacion = new UbicacionGeodicacion();
                    String nombreLugar = ubicacionGeodicacion.degeocodificarUbiciacion(getActivity(),
                                            mCoordenadasParaFavorito.latitude, mCoordenadasParaFavorito.longitude);
                    // TODO: Añadir datos a la BD, el nombre del lugar y el objecto geojson con la latitud y longitud de "mCoordenadasParaFavorito".
                    // TODO: Modificar los campos de la colección "favoritos" para que no exita relacion con la colección "ubicacion".
                }
                break;
            case R.id.button_regresar_barraBusqueda_lugarSeleccionado:
                ((ActivityMap) getActivity()).abrirFragmentoBuscarLugar();
                break;
            case R.id.button_centrarMapa_lugarSeleccionado:
                ((ActivityMap) getActivity()).centrarMapa();
                break;
        }
    }


    //OTRAS FUNCIONES//

    private void setValoresBotttomSheet() {
        ubicacion ubicacion = Preferences.getSavedObjectFromPreference(getActivity(), PREFERENCE_UBICACION, ubicacion.class);

        String[] nombreLugar = null;

        if (ubicacion != null) {
            if (mActividadAnterior.equals("FAVORITOS")){
                mBtnGuardarEnFavoritos.setVisibility(View.GONE);
                // TODO: obtener latitud y longitud del ebjecto geojson
                favorito favorito = Preferences.getSavedObjectFromPreference(getActivity(), PREFERENCE_FAVORITO, favorito.class);
                nombreLugar = favorito.getNombre().split(",", 2);
                //LatLng ubicacionFavorito = favorito.getUbicacion();
                //((ActivityMap) getActivity()).mostrarFavorito(ubicacionFavorito);
            }
            else if (mUbicacionBuscada != null)
                nombreLugar = mUbicacionBuscada.split(",", 2);
            else
                nombreLugar = ubicacion.getNombre().split(",", 2);

            mTxtNombre1Lugar.setText(nombreLugar[0].trim());
            mTxtNombre2Lugar.setText(nombreLugar[1].trim());

            String delitosSemana = mTxtDelitosSemana.getText().toString() + " " + ubicacion.getDelitos_semana();
            String mediaHistorica = mTxtMediaHistorica.getText().toString() + " " + ubicacion.getMedia_historica_double();
            mTxtDelitosSemana.setText(delitosSemana);
            mTxtMediaHistorica.setText(mediaHistorica);

            String seguridad = ubicacion.getSeguridad();
            mTxtSeguridad.setText(seguridad);
            if (seguridad.equals("Seguridad baja"))
                mIconSeguridad.setBackgroundTintList(ContextCompat.getColorStateList(this.getContext(), R.color.rojo_icon));
            if (seguridad.equals("Seguridad media"))
                mIconSeguridad.setBackgroundTintList(ContextCompat.getColorStateList(this.getContext(), R.color.amarillo_icon));
            if (seguridad.equals("Seguridad alta"))
                mIconSeguridad.setBackgroundTintList(ContextCompat.getColorStateList(this.getContext(), R.color.verde_icon));
        }
    }

}