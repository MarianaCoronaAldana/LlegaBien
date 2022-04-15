package com.example.llegabien.frontend.usuario.fragmento;

import static com.example.llegabien.backend.permisos.Preferences.PREFERENCE_ESTADO_BUTTON_SESION;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.llegabien.R;
import com.example.llegabien.backend.contactos.usuario_contacto;
import com.example.llegabien.backend.permisos.Preferences;
import com.example.llegabien.backend.usuario.usuario;
import com.example.llegabien.frontend.FragmentoAuxiliar;
import com.example.llegabien.frontend.rutas.activity.MapsActivity;
import com.example.llegabien.mongoDB.Conectar;

import org.bson.types.ObjectId;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.mongodb.sync.SyncConfiguration;

public class FragmentoIniciarSesion1 extends Fragment implements View.OnClickListener{
    private RadioButton mBtnRecordarSesion;
    private Button mBtnIniciarSesion, mBtnContraseñaOlvidada, mBtnCerrar, mBtnRegistrarse;
    private EditText ET_contraseña, ET_correo;
    private boolean isActivateRadioButton;

    public FragmentoIniciarSesion1() {
        // Required empty public constructor
    }

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
        mBtnRegistrarse = (Button) root.findViewById(R.id.button_registrarse_inicia_sesion_1);

        //listeners
        mBtnRecordarSesion.setOnClickListener(this);
        mBtnIniciarSesion.setOnClickListener(this);
        mBtnContraseñaOlvidada.setOnClickListener(this);
        mBtnCerrar.setOnClickListener(this);
        mBtnRegistrarse.setOnClickListener(this);

        isActivateRadioButton = mBtnRecordarSesion.isChecked(); //DESACTIVADO

        return root;
    }

    //listener function
    @Override
    public void onClick(View view) {
        FragmentoAuxiliar fragmentoAuxiliar = new FragmentoAuxiliar();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
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
            case R.id.button_registrarse_inicia_sesion_1:
                FragmentoRegistrarUsuario1 fragmentoRegistrarUsuario1 = new FragmentoRegistrarUsuario1();
                fragmentTransaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);
                fragmentTransaction.replace(R.id.fragment_pantallaPrincipal,fragmentoRegistrarUsuario1).commit();
                break;
            case R.id.button_contraseña_olvidada_inicia_sesion_1:
                FragmentoIniciarSesion2 fragmentoIniciarSesion2 = new FragmentoIniciarSesion2();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_right);
                fragmentTransaction.replace(R.id.fragment_pantallaPrincipal,fragmentoIniciarSesion2).commit();
                fragmentTransaction.addToBackStack(null);
                break;
            case R.id.button_cerrar_inicia_sesion_1:
                fragmentTransaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);
                fragmentTransaction.replace(R.id.fragment_pantallaPrincipal,fragmentoAuxiliar).commit();
                fragmentTransaction.remove(fragmentoAuxiliar);
                break;

        }
    }



    private void AñadirUser() {

        RealmList<usuario_contacto> Contacto =  new  RealmList <usuario_contacto>();
        Contacto.add(new usuario_contacto("Aurora", "432432432"));
        Contacto.add(new usuario_contacto("Bella", "432432432"));
        Contacto.add(new usuario_contacto("Jasmine", "432432432"));
        Contacto.add(new usuario_contacto("Cenicienta", "432432432"));

        usuario Usuario = new usuario(new ObjectId(), "Princesa", Contacto,"abcd", "a@gmail.com", new Date(),"Ariel", "3321707532");

        Conectar conectar = new Conectar();
        SyncConfiguration config = conectar.ConectarAMongoDB();
        Realm realm = Realm.getInstance(config);

        realm.executeTransactionAsync(transactionRealm -> {
            transactionRealm.insert(Usuario);
        });

        realm.close();
    }
}