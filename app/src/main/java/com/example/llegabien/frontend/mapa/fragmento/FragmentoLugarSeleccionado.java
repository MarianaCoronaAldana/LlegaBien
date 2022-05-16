package com.example.llegabien.frontend.mapa.fragmento;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.llegabien.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class FragmentoLugarSeleccionado extends Fragment implements View.OnClickListener {

    private BottomSheetBehavior<FrameLayout> mBottomSheetBehavior;
    private Button mBtnRegresar;

    public FragmentoLugarSeleccionado() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_lugar_seleccionado, container, false);


        //wirirng up
        mBottomSheetBehavior = BottomSheetBehavior.from(root.findViewById(R.id.bottomSheet_detallesLugar));
        mBtnRegresar = root.findViewById(R.id.button_regresar_barraBusqueda_lugarSeleccionado);

        //para que la bottomSheet se abra en STATE.EXPANDED
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        mBtnRegresar.setOnClickListener(this);

        //listeners
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

}