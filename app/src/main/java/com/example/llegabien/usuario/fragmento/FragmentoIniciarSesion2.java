package com.example.llegabien.usuario.fragmento;

import static com.example.llegabien.permisos.Preferences.PREFERENCE_ESTADO_BUTTON_SESION;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;

import com.example.llegabien.FragmentoAuxiliar;
import com.example.llegabien.R;
import com.example.llegabien.Transacciones;
import com.example.llegabien.permisos.Preferences;
import com.example.llegabien.rutas.MapsActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentoIniciarSesion2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentoIniciarSesion2 extends Fragment implements View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentoIniciarSesion2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentoIniciarSesion2.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentoIniciarSesion2 newInstance(String param1, String param2) {
        FragmentoIniciarSesion2 fragment = new FragmentoIniciarSesion2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    private Button mBtnCerrar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_iniciar_sesion2, container, false);

        //wiring up
        mBtnCerrar = (Button) root.findViewById(R.id.button_cerrar_inicia_sesion_2);

        //listeners
        mBtnCerrar.setOnClickListener(this);

        return root;
    }

    //listener function
    @Override
    public void onClick(View view) {
        FragmentoAuxiliar fragmentoAuxiliar = new FragmentoAuxiliar();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        switch (view.getId()) {
            case R.id.button_cerrar_inicia_sesion_2:
                Transacciones transacciones = new Transacciones();
                transacciones.cerrarFragmento(fragmentTransaction,fragmentoAuxiliar);
                break;

        }
    }
}