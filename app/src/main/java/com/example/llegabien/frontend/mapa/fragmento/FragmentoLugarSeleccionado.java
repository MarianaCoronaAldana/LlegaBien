package com.example.llegabien.frontend.mapa.fragmento;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_UBICACION;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.llegabien.R;
import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.mapa.ubicacion.ubicacion;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.Arrays;
import java.util.stream.Collectors;

public class FragmentoLugarSeleccionado extends Fragment implements View.OnClickListener {

    private TextView mTxtNombre1Lugar, mTxtNombre2Lugar, mTxtDelitosSemana, mTxtMediaHistorica, mTxtSeguridad;
    private View mIconSeguridad;
    private BottomSheetBehavior<ConstraintLayout> mBottomSheetBehavior;
    private Button mBtnRegresar;

    public FragmentoLugarSeleccionado() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_lugar_seleccionado, container, false);

        //wirirng up
        mBottomSheetBehavior = BottomSheetBehavior.from(root.findViewById(R.id.bottomSheet_detallesLugar));
        mBtnRegresar = root.findViewById(R.id.button_regresar_barraBusqueda_lugarSeleccionado);
        mTxtNombre1Lugar = (TextView) root.findViewById(R.id.textView1_nombreLugar_detallesLugar);
        mTxtNombre2Lugar = (TextView) root.findViewById(R.id.textView2_nombreLugar_detallesLugar);
        mTxtSeguridad = (TextView) root.findViewById(R.id.textView_seguridad_detallesLugar);
        mTxtMediaHistorica = (TextView) root.findViewById(R.id.textView_mediHistorica_detallesLugar);
        mTxtDelitosSemana = (TextView) root.findViewById(R.id.textView_numDelitos_detallesLugar);
        mIconSeguridad = (View) root.findViewById(R.id.icon_seguridad_detallesLugar);

        //para que la bottomSheet se abra en STATE.EXPANDED
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        //para establecer valores dependiendo ubicacion
        setValoresBotttomSheet();

        //listeners
        mBtnRegresar.setOnClickListener(this);
        mBottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == BottomSheetBehavior.STATE_EXPANDED)
                    Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_EXPANDED");
                if(newState == BottomSheetBehavior.STATE_COLLAPSED)
                    Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_COLLAPSED");
                else
                    Log.i("BottomSheetCallback", "BottomSheetBehavior.OTHER");
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.i("BottomSheetCallback", "slideOffset: " + slideOffset);
            }
        });



        return root;
    }

    //FUNCIONES LISTENERS//
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_regresar_barraBusqueda_lugarSeleccionado:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }
    }

    //OTRAS FUNCIONES//
    private void setValoresBotttomSheet(){
        ubicacion Ubicacion = Preferences.getSavedObjectFromPreference(getActivity(), PREFERENCE_UBICACION, ubicacion.class);

        if (Ubicacion != null) {
            String[] nombreLugar = Ubicacion.getNombre().split(",", 2);
            String seguridad = Ubicacion.getSeguridad();
            mTxtNombre1Lugar.setText(nombreLugar[0]);
            mTxtNombre2Lugar.setText("Zapopan, Jalisco, México");
            mTxtDelitosSemana.setText("Delitos ultima semana = " + Ubicacion.getDelitos_semana());
            mTxtMediaHistorica.setText("Media Histórica Semanal = " + Ubicacion.getMedia_historica());
            mTxtSeguridad.setText(seguridad);
            if (seguridad.equals("Seguridad baja"))
                mIconSeguridad.setBackgroundColor(Color.RED);
            if (seguridad.equals("Seguridad media"))
                mIconSeguridad.setBackgroundColor(Color.YELLOW);
            if (seguridad.equals("Seguridad alta"))
                mIconSeguridad.setBackgroundColor(Color.GREEN);
        }
    }

}