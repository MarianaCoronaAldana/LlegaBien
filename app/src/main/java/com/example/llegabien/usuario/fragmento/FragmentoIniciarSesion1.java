package com.example.llegabien.usuario.fragmento;

import static com.example.llegabien.permisos.Preferences.PREFERENCE_ESTADO_BUTTON_SESION;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.llegabien.R;
import com.example.llegabien.mongoDB.Conectar;
import com.example.llegabien.permisos.Preferences;
import com.example.llegabien.rutas.MapsActivity;
import com.example.llegabien.usuario.clases.usuario;
import com.example.llegabien.usuario.clases.usuario_contacto;

import org.bson.types.ObjectId;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.mongodb.sync.SyncConfiguration;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentoIniciarSesion1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentoIniciarSesion1 extends Fragment implements View.OnClickListener{

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
    private RadioButton mBtnRecordarSesion;
    private Button mBtnIniciarSesion, mBtnContraseñaOlvidada, mBtnCerrar;
    private EditText ET_correo, ET_contraseña;

    private boolean isActivateRadioButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_iniciar_sesion1, container, false);

        //para verificar si el boton de recordar contraseña fue presionado
        if(Preferences.obtenerPreference(this,PREFERENCE_ESTADO_BUTTON_SESION)){
            startActivity(new Intent(getActivity(), MapsActivity.class));
        }

        //wiring up
        mBtnRecordarSesion = (RadioButton) root.findViewById(R.id.radioBtn_recordar_inicia_sesion_1);
        mBtnIniciarSesion = (Button) root.findViewById(R.id.button_inicia_inicia_sesion_1);
        mBtnContraseñaOlvidada = (Button) root.findViewById(R.id.button_contraseña_olvidada_inicia_sesion_1);
        mBtnCerrar = (Button) root.findViewById(R.id.button_cerrar_inicia_sesion_1);
        ET_contraseña = (EditText) root.findViewById(R.id.editText_contraseña_inicia_sesion_1);
        ET_correo = (EditText) root.findViewById(R.id.editText_correo_inicia_sesion_1);

        //listeners
        mBtnRecordarSesion.setOnClickListener(this);
        mBtnIniciarSesion.setOnClickListener(this);
        mBtnContraseñaOlvidada.setOnClickListener(this);
        mBtnCerrar.setOnClickListener(this);

        isActivateRadioButton = mBtnRecordarSesion.isChecked(); //DESACTIVADO

        return root;
    }

    //listener function
    @Override
    public void onClick(View view) {
        FragmentoIniciarSesion2 fragmentoIniciarSesion2 = new FragmentoIniciarSesion2();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack("text");
        switch (view.getId()) {
            case R.id.radioBtn_recordar_inicia_sesion_1:
                //ACTIVADO
                if (isActivateRadioButton) {
                    mBtnRecordarSesion.setChecked(false);
                }
                isActivateRadioButton = mBtnRecordarSesion.isChecked();
                break;
            case R.id.button_inicia_inicia_sesion_1:
                AñadirUser();

                Preferences.savePreferenceBoolean(FragmentoIniciarSesion1.this,mBtnRecordarSesion.isChecked(), PREFERENCE_ESTADO_BUTTON_SESION);
                startActivity(new Intent(getActivity(), MapsActivity.class));
                break;
            case R.id.button_contraseña_olvidada_inicia_sesion_1:
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                fragmentTransaction.replace(R.id.fragment_pantallaPrincipal,fragmentoIniciarSesion2).commit();
                break;
            case R.id.button_cerrar_inicia_sesion_1:
                getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                break;

        }
    }



    private void AñadirUser() {

        RealmList<usuario_contacto> Contacto =  new  RealmList <usuario_contacto>();
        Contacto.add(new usuario_contacto("Aurora", "432432432"));
        Contacto.add(new usuario_contacto("Bella", "432432432"));
        Contacto.add(new usuario_contacto("Jasmine", "432432432"));
        Contacto.add(new usuario_contacto("Cenicienta", "432432432"));

        usuario Usuario = new usuario(new ObjectId(), "Princesa", Contacto,"abcd", "a@gmail.com", "Ariel", "3321707532");

        Conectar conectar = new Conectar();
        SyncConfiguration config = conectar.ConectarAMongoDB(getActivity());
        Realm realm = Realm.getInstance(config);

        realm.executeTransactionAsync(transactionRealm -> {
            transactionRealm.insert(Usuario);
        });

        realm.close();
    }
}