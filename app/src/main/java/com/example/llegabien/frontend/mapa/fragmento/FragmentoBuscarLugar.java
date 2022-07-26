package com.example.llegabien.frontend.mapa.fragmento;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_ES_ADMIN;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.example.llegabien.R;
import com.example.llegabien.backend.ubicacion.UbicacionBusquedaAutocompletada;
import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.frontend.mapa.activity.ActivityMap;
import com.example.llegabien.frontend.mapa.Mapa;
import com.example.llegabien.frontend.usuario.activity.ActivityConfiguracionUsuario;
import com.example.llegabien.frontend.usuario.dialog.DialogTipoConfiguracion;
import com.google.android.libraries.places.api.Places;

public class FragmentoBuscarLugar extends Fragment implements View.OnClickListener {

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
                                if (isUbicacionBuscadaObtenida) {
                                    Mapa mapa = new Mapa((ActivityMap) requireActivity());
                                    mapa.mostrarUbicacionBuscada(isUbicacionBuscadaenBD, true, ubicacionBuscada, ubicacionBuscadaString);
                                }
                            }, result, data, requireActivity());
                        }
                    }
            );


    public FragmentoBuscarLugar() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_buscar_lugar, container, false);
        String apiKey = getString(R.string.api_key);
        if (!Places.isInitialized()) {
            Places.initialize(this.requireActivity().getApplicationContext(), apiKey);
        }

        //wiring up
        Button btnBusqueda = (Button) root.findViewById(R.id.button_titulo_barraBusqueda_buscarLugar);
        Button btnConfiguracion = (Button) root.findViewById(R.id.button_configuracion_barraBusqueda);
        ConstraintLayout btnCentrarMapa = (ConstraintLayout) root.findViewById(R.id.button_centrarMapa_buscarLugar);
        ConstraintLayout btnIndicaciones = root.findViewById(R.id.button_indicaciones_buscarLugar);

        //listeners
        btnBusqueda.setOnClickListener(this);
        btnConfiguracion.setOnClickListener(this);
        btnCentrarMapa.setOnClickListener(this);
        btnIndicaciones.setOnClickListener(this);

        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setNavigationBarColor(getResources().getColor(R.color.morado_oscuro));

        return root;
    }

    //FUNCIONES LISTENERS//
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_titulo_barraBusqueda_buscarLugar) {
            ubicacionBusquedaAutocompletada = new UbicacionBusquedaAutocompletada();
            ubicacionBusquedaAutocompletada.inicializarIntent(requireActivity());
            activityResultLauncher.launch(ubicacionBusquedaAutocompletada.getIntent());
        }
        else if (view.getId() == R.id.button_configuracion_barraBusqueda) {
            // Si el usuario es del tipo Administrador, se manda a un lugar distinto que a un usuario normal
            if (Preferences.getSavedBooleanFromPreference(this.requireActivity(), PREFERENCE_ES_ADMIN)) {
                DialogTipoConfiguracion dialogTipoConfiguracion = new DialogTipoConfiguracion(this.requireActivity());
                dialogTipoConfiguracion.show();
            } else
                startActivity(new Intent(this.requireActivity(), ActivityConfiguracionUsuario.class));
        }
        else if (view.getId() == R.id.button_indicaciones_buscarLugar) {
            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            FragmentoIndicaciones fragmentoIndicaciones = new FragmentoIndicaciones(null);
            fragmentTransaction.replace(R.id.fragmentContainerView_fragmentoLugares_activityMaps, fragmentoIndicaciones,"FragmentoIndicaciones").commit();
        }
        else if (view.getId() == R.id.button_centrarMapa_buscarLugar) {
            Mapa mapa = new Mapa ((ActivityMap) requireActivity());
            mapa.centrarMapa();
        }
    }

}