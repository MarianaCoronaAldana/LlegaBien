package com.example.llegabien.usuario.fragmento;

import static com.example.llegabien.permisos.Preferences.PREFERENCE_ESTADO_BUTTON_SESION;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;

import com.example.llegabien.permisos.Preferences;
import com.example.llegabien.R;
import com.example.llegabien.rutas.MapsActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentoIniciarSesion1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentoIniciarSesion1 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentoIniciarSesion1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragmento_IniciarSesion_1.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentoIniciarSesion1 newInstance(String param1, String param2) {
        FragmentoIniciarSesion1 fragment = new FragmentoIniciarSesion1();
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
    private RadioButton mBtnRecoradrSesion;
    private Button mBtnIniciarSesion, mBtnContraseñaOlvidada;
    private boolean isActivateRadioButton;

    public void iniciarActividadSiguiente(){
        FragmentoIniciarSesion2 fragmentoIniciarSesion2 = new FragmentoIniciarSesion2();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_pantallaPrincipal,fragmentoIniciarSesion2);
    }

    //listener function
    private final View.OnClickListener mListener = new View.OnClickListener() {
        public void onClick(View view) {
            FragmentoIniciarSesion2 fragmentoIniciarSesion2 = new FragmentoIniciarSesion2();
            switch (view.getId()) {
                case R.id.radioBtn_recordar_inicia_sesion_1:
                    //ACTIVADO
                    if (isActivateRadioButton) {
                        mBtnRecoradrSesion.setChecked(false);
                    }
                    isActivateRadioButton = mBtnRecoradrSesion.isChecked();
                    break;
                case R.id.button_inicia_inicia_sesion_1:
                    Preferences.savePreferenceBoolean(FragmentoIniciarSesion1.this,mBtnRecoradrSesion.isChecked(), PREFERENCE_ESTADO_BUTTON_SESION);
                    startActivity(new Intent(getActivity(), MapsActivity.class));
                    break;
                case R.id.button_contraseña_olvidada_inicia_sesion_1:
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_pantallaPrincipal,fragmentoIniciarSesion2);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_iniciar_sesion1, container, false);

        //para verificar si el boton de recodar contraseña fue presionado
        if(Preferences.obtenerPreference(this,PREFERENCE_ESTADO_BUTTON_SESION)){
            iniciarActividadSiguiente();
        }

        //wiring up
        mBtnRecoradrSesion = (RadioButton) root.findViewById(R.id.radioBtn_recordar_inicia_sesion_1);
        mBtnIniciarSesion = (Button) root.findViewById(R.id.button_inicia_inicia_sesion_1);
        mBtnContraseñaOlvidada = (Button) root.findViewById(R.id.button_contraseña_olvidada_inicia_sesion_1);

        isActivateRadioButton = mBtnRecoradrSesion.isChecked(); //DESACTIVADO

        //listeners
        mBtnRecoradrSesion.setOnClickListener(mListener);
        mBtnIniciarSesion.setOnClickListener(mListener);
        mBtnContraseñaOlvidada.setOnClickListener(mListener);

        return root;
    }
}