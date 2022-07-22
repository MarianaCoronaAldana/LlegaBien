package com.example.llegabien.frontend.mapa.fragmento;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_FAVORITO;
import static com.example.llegabien.backend.app.Preferences.PREFERENCE_UBICACION;
import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.llegabien.R;
import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.mapa.favoritos.Favorito_DAO;
import com.example.llegabien.backend.mapa.favoritos.favorito;
import com.example.llegabien.backend.mapa.favoritos.favorito_ubicacion;
import com.example.llegabien.backend.mapa.ubicacion.UbicacionBusquedaAutocompletada;
import com.example.llegabien.backend.mapa.ubicacion.UbicacionGeocodificacion;
import com.example.llegabien.backend.mapa.ubicacion.ubicacion;
import com.example.llegabien.backend.usuario.usuario;
import com.example.llegabien.frontend.mapa.activity.ActivityMap;
import com.example.llegabien.frontend.mapa.Mapa;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import io.realm.RealmList;

public class FragmentoLugarSeleccionado extends Fragment implements View.OnClickListener {

    private TextView mTxtNombre1Lugar, mTxtNombre2Lugar, mTxtDelitosSemana, mTxtMediaHistorica, mTxtSeguridad;
    ConstraintLayout mBtnGuardarEnFavoritos;
    private View mIconSeguridad;
    private String mUbicacionBuscada = null;
    private final String mActividadAnterior;
    private LatLng mCoordenadasParaFavorito = null;
    private String[] mNombreLugar = null;
    private UbicacionBusquedaAutocompletada ubicacionBusquedaAutocompletada;
    private Mapa mMapa;
    private final ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult activityResult) {
                            int result = activityResult.getResultCode();
                            Intent data = activityResult.getData();
                            ubicacionBusquedaAutocompletada.verificarResultadoBusqueda((isUbicacionBuscadaObtenida, isUbicacionBuscadaEnBD, ubicacionBuscada, ubicacionBuscadaString) -> {
                                if (isUbicacionBuscadaObtenida){
                                    mMapa.mostrarUbicacionBuscada(isUbicacionBuscadaEnBD, false, ubicacionBuscada, ubicacionBuscadaString);
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
        mCoordenadasParaFavorito = coordenadasParaFavorito;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_lugar_seleccionado, container, false);

        mMapa = new Mapa((ActivityMap) requireActivity());

        //wirirng up
        BottomSheetBehavior<ConstraintLayout> mBottomSheetBehavior = BottomSheetBehavior.from(root.findViewById(R.id.bottomSheet_detallesLugar));
        Button mBtnRegresar = root.findViewById(R.id.button_regresar_barraBusqueda_lugarSeleccionado);
        Button mBtnBarraBusqueda = root.findViewById(R.id.button_titulo_barraBusqueda_lugarSeleccionado);
        ConstraintLayout btnCentrarMapa = root.findViewById(R.id.button_centrarMapa_lugarSeleccionado);
        ConstraintLayout btnIndicaciones = root.findViewById(R.id.button_indicaciones_detallesLugar);
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
        btnIndicaciones.setOnClickListener(this);
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

        // Para cambiar el color de statusBar y NavigationBar.
        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setNavigationBarColor(getResources().getColor(R.color.morado_claro));

        //para establecer valores dependiendo ubicacion
        setValoresBotttomSheet();

        return root;
    }

    //FUNCIONES LISTENERS//
    @Override
    public void onClick(View view) {
        Mapa mapa = new Mapa((ActivityMap) requireActivity());
        if (view.getId() == R.id.button_titulo_barraBusqueda_lugarSeleccionado) {
            ubicacionBusquedaAutocompletada = new UbicacionBusquedaAutocompletada();
            ubicacionBusquedaAutocompletada.inicializarIntent(getActivity());
            activityResultLauncher.launch(ubicacionBusquedaAutocompletada.getIntent());
        }

        else if (view.getId() == R.id.button_añadirFavorito_detallesLugar) {
            if (mCoordenadasParaFavorito != null) {
                UbicacionGeocodificacion ubicacionGeodicacion = new UbicacionGeocodificacion(getActivity());
                String nombreLugar = ubicacionGeodicacion.degeocodificarUbiciacion(mCoordenadasParaFavorito.latitude,
                                                                                    mCoordenadasParaFavorito.longitude);
                anadirFavoritoaBD(nombreLugar);
            }
        }

        else if (view.getId() == R.id.button_regresar_barraBusqueda_lugarSeleccionado) {
            if (mActividadAnterior != null) {
                if (mActividadAnterior.equals("FAVORITOS"))
                    requireActivity().finish();
            } else
                mapa.abrirFragmentoBuscarLugar();

        }

        else if (view.getId() == R.id.button_indicaciones_detallesLugar){
            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            FragmentoIndicaciones fragmentoIndicaciones = new FragmentoIndicaciones(mNombreLugar[0].trim() + ", " + mNombreLugar[1].trim());
            fragmentTransaction.replace(R.id.fragmentContainerView_fragmentoLugares_activityMaps, fragmentoIndicaciones).commit();
        }

        else if (view.getId() == R.id.button_centrarMapa_lugarSeleccionado)
            mapa.centrarMapa();
    }

    //OTRAS FUNCIONES//

    private void setValoresBotttomSheet() {
        ubicacion ubicacion = Preferences.getSavedObjectFromPreference(requireActivity(), PREFERENCE_UBICACION, ubicacion.class);

        if (ubicacion != null) {
            if (mActividadAnterior != null) {
                if (mActividadAnterior.equals("FAVORITOS")) {
                    favorito favorito = Preferences.getSavedObjectFromPreference(requireActivity(), PREFERENCE_FAVORITO, favorito.class);
                    if (favorito != null) {
                        mNombreLugar = favorito.getNombre().split(",", 2);
                        mMapa.establecerLatLngFavorito(new LatLng(favorito.getUbicacion().getCoordinates().get(0), favorito.getUbicacion().getCoordinates().get(1)));
                    }
                }
            } else if (mUbicacionBuscada != null)
                mNombreLugar = mUbicacionBuscada.split(",", 2);
            else
                mNombreLugar = ubicacion.getNombre().split(",", 2);

            if (mNombreLugar != null) {
                mTxtNombre1Lugar.setText(mNombreLugar[0].trim());
                mTxtNombre2Lugar.setText(mNombreLugar[1].trim());
            }

            String delitosSemana = mTxtDelitosSemana.getText().toString() + " " + ubicacion.getDelitos_semana();
            String mediaHistorica = mTxtMediaHistorica.getText().toString() + " " + Math.round(ubicacion.getMedia_historica_double());
            mTxtDelitosSemana.setText(delitosSemana);
            mTxtMediaHistorica.setText(mediaHistorica);

            String seguridad = ubicacion.getSeguridad();
            mTxtSeguridad.setText(seguridad);
            if (seguridad.equals("Seguridad baja"))
                mIconSeguridad.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.rojo_icon));
            if (seguridad.equals("Seguridad media"))
                mIconSeguridad.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.amarillo_icon));
            if (seguridad.equals("Seguridad alta"))
                mIconSeguridad.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.verde_icon));
        }
    }


    private void anadirFavoritoaBD(String nombreLugar) {
        RealmList<Double> coordenadas = new RealmList<>();
        usuario Usuario = Preferences.getSavedObjectFromPreference(requireActivity(), PREFERENCE_USUARIO, usuario.class);
        favorito_ubicacion Favorito_ubicacion = new favorito_ubicacion();
        favorito Favorito = new favorito();
        Favorito_DAO favoritoDAO = new Favorito_DAO(this.getContext());

        coordenadas.add(mCoordenadasParaFavorito.latitude);
        coordenadas.add(mCoordenadasParaFavorito.longitude);

        Favorito_ubicacion.setCoordinates(coordenadas);

        if (Usuario != null) {
            Favorito.setIdUsuario(Usuario.get_id());

            Favorito.setNombre(nombreLugar);
            Favorito.setUbicacion(Favorito_ubicacion);

            if (!favoritoDAO.obtenerFavoritoPorNombre_Id(Usuario.get_id(), Favorito.getNombre())) {
                favoritoDAO.anadirFavorito(Favorito);
                Toast.makeText(this.getContext(), "Ubicacion añadida a favoritos.", Toast.LENGTH_LONG).show();
            } else
                Toast.makeText(this.getContext(), "¡Ya tienes esta ubicación guardada!", Toast.LENGTH_LONG).show();
        }

    }



}