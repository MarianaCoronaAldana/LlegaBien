package com.example.llegabien.frontend.mapa.fragmento;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_ES_ADMIN;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.llegabien.R;
import com.example.llegabien.backend.mapa.ubicacion.UbicacionBusquedaAutocompletada;
import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.frontend.usuario.activity.ActivityConfiguracionAdmin;
import com.example.llegabien.frontend.usuario.activity.ActivityConfiguracionUsuario;
import com.example.llegabien.frontend.usuario.dialog.DialogTipoConfiguracion;
import com.google.android.libraries.places.api.Places;

public class FragmentoBuscarLugar extends Fragment implements View.OnClickListener{

    private UbicacionBusquedaAutocompletada ubicacionBusquedaAutocompletada;
    private Button mBtnBusqueda, mBtnConfiguracion;
    private ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult activityResult) {
                            int result = activityResult.getResultCode();
                            Intent data = activityResult.getData();
                            ubicacionBusquedaAutocompletada.verificarResultadoBusqueda(result, data);
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
            Places.initialize(this.getActivity().getApplicationContext(), apiKey);
        }

        //wiring up
        mBtnBusqueda = (Button) root.findViewById(R.id.button_titulo_barraBusqueda);
        mBtnConfiguracion = (Button) root.findViewById(R.id.button_configuracion_barraBusqueda);

        //listeners
        mBtnBusqueda.setOnClickListener(this);
        mBtnConfiguracion.setOnClickListener(this);

        return root;
    }

    //FUNCIONES LISTENERS//
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_titulo_barraBusqueda:
                ubicacionBusquedaAutocompletada = new UbicacionBusquedaAutocompletada(this);
                ubicacionBusquedaAutocompletada.inicializarIntent();
                activityResultLauncher.launch(ubicacionBusquedaAutocompletada.getmIntent());
                break;
            case R.id.button_configuracion_barraBusqueda:
                // Si el usuario es del tipo Administrador, se manda a un lugar distinto que a un usuario normal
                if(Preferences.getSavedBooleanFromPreference(this.getActivity(), PREFERENCE_ES_ADMIN)){
                    DialogTipoConfiguracion dialogTipoConfiguracion = new DialogTipoConfiguracion(this.getActivity());
                    dialogTipoConfiguracion.show();
                }
                else
                    startActivity(new Intent(this.getActivity(), ActivityConfiguracionUsuario.class));

                break;
        }
    }

}