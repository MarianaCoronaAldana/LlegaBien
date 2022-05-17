package com.example.llegabien.frontend.contactos.fragmento;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Guideline;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.llegabien.R;
import com.example.llegabien.backend.contactos.usuario_contacto;
import com.example.llegabien.backend.usuario.UsuarioInputValidaciones;
import com.example.llegabien.backend.usuario.usuario;
import com.example.llegabien.backend.usuario.UsuarioSharedViewModel;
import com.example.llegabien.frontend.usuario.fragmento.FragmentoIniciarSesion1;
import com.example.llegabien.backend.mongoDB.ConectarBD;
import com.example.llegabien.backend.usuario.UsuarioBD_CRUD;

import io.realm.RealmList;

public class FragmentoRegistrarContacto extends Fragment implements View.OnClickListener{

    private TextView mTxtTitulo;
    private EditText mEditTxtNombre, mEditTxtNumTelefonico;
    private Button mBtnSiguiente, mBtnFinalizar;;
    private Guideline mGuideline1_Btn1, mGuideline2_Btn1;
    private  ConstraintLayout mConstraintLayout;
    private int mNumContacto = 1, mBackStackCount = 0, mSiguienteCount = 1;
    private Fragment parent;

    private UsuarioSharedViewModel SharedViewModel;
    private usuario Usuario;
    private usuario_contacto Contacto =  new  usuario_contacto();
    private ConectarBD conectarBD = new ConectarBD();
    private UsuarioBD_CRUD usuarioBD_CRUD = new UsuarioBD_CRUD();

    public FragmentoRegistrarContacto(){}

    public FragmentoRegistrarContacto(int numContacto, int siguienteCount) {
        mNumContacto = numContacto;
        mSiguienteCount = siguienteCount;
    }


    //para obtener los parametros que se guardan en el bundle
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedViewModel = new ViewModelProvider(requireActivity()).get(UsuarioSharedViewModel.class);

        final Observer<usuario> nameObserver = new Observer<usuario>() {
            @Override
            public void onChanged(@Nullable final usuario user) {
                Usuario = user;

                Log.v("QUICKSTART", "nombre: " + Usuario.getNombre()
                        + "apellido: " + Usuario.getApellidos() + "ESTOY DENTRO DE CONTACTO");
            }
        };

        //para usar el mismo ViewModel que los otros fragmentos y compartir informacion
        SharedViewModel.getUsuario().observe(this, nameObserver);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragmento_registrar_contacto, container, false);

        //wiring up
        mTxtTitulo = (TextView) root.findViewById(R.id.textView_titulo_registroContactos);
        mEditTxtNombre = (EditText) root.findViewById(R.id.editText_nombre_registroContactos);
        mEditTxtNumTelefonico = (EditText) root.findViewById(R.id.editText_celular_registroContactos);

        //views de fragmento padre "FragmentoRegistrarUsuario4"
        parent = (Fragment) this.getParentFragment();
        mBtnSiguiente = parent.getView().findViewById(R.id.button1_siguiente_registro_4);
        mBtnFinalizar = parent.getView().findViewById(R.id.button2_finalizar_registro_4);
        mGuideline1_Btn1 = parent.getView().findViewById(R.id.guideline1_textView_editView_registro_4);
        mGuideline2_Btn1 = parent.getView().findViewById(R.id.guideline2_button1_registro_4);
        mConstraintLayout = parent.getView().findViewById(R.id.consLyt_parentPrincipal_registro_4);

        //para cambiar el titulo segun el numero de contacto
        String tituloRegistroContacto = getResources().getString(R.string.contactoEmergencia_registro4) + " " + String.valueOf(mNumContacto);
        mTxtTitulo.setText(tituloRegistroContacto);

        //listeners
        mBtnSiguiente.setOnClickListener(this);
        mBtnFinalizar.setOnClickListener(this);

        return root;
    }

    //FUNCIONES LISTENERS//

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        FragmentTransaction fragmentTransaction;
        switch (view.getId()) {
            case R.id.button1_siguiente_registro_4:
                if (validarAllInputs()) {
                    tomarDatosContacto();
                    if (mNumContacto >= 1){
                        ConstraintSet constraintSet = new ConstraintSet();
                        constraintSet.clone(mConstraintLayout);
                        constraintSet.connect(mBtnSiguiente.getId(),ConstraintSet.START,mGuideline1_Btn1.getId(),ConstraintSet.START,0);
                        constraintSet.connect(mBtnSiguiente.getId(),ConstraintSet.END,mGuideline2_Btn1.getId(),ConstraintSet.END,0);
                        constraintSet.setDimensionRatio(mBtnSiguiente.getId(),"6:2");
                        constraintSet.applyTo(mConstraintLayout);
                        mBtnFinalizar.setVisibility(View.VISIBLE);
                        mBtnFinalizar.setClickable(true);
                    }
                    if (mNumContacto == 5){
                        //Se integra al usuario a la BD
                        terminarRegistro();
                    }
                    else {
                        mNumContacto++;
                        mSiguienteCount++;
                        fragmentTransaction = getParentFragmentManager().beginTransaction();
                        FragmentoRegistrarContacto fragmentoRegistrarContacto = new FragmentoRegistrarContacto(mNumContacto, mSiguienteCount);
                        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                        fragmentTransaction.add(R.id.fragmentContainerView_registrarContactos_registro_4, fragmentoRegistrarContacto).commit();
                        fragmentTransaction.addToBackStack(null);
                    }
                }
                break;
            case R.id.button2_finalizar_registro_4:
                if (validarAllInputs()) {
                    //Se integra al usuario a la BD
                    tomarDatosContacto();
                    terminarRegistro();
                }
                break;
        }
    }


    //OTRAS FUNCIONES//

    private boolean validarAllInputs(){
        UsuarioInputValidaciones usuarioInputValidaciones = new UsuarioInputValidaciones();
        boolean esInputValido = true;
        if (!usuarioInputValidaciones.validarNombre(getActivity(),mEditTxtNombre))
            esInputValido = false;
        if (!usuarioInputValidaciones.validarNumTelefonico(getActivity(),mEditTxtNumTelefonico))
            esInputValido = false;

        return esInputValido;
    }


    //Funcion para tomar datos del contacto y añadirlo al objeto Usuario
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void tomarDatosContacto(){
        Contacto.setNombre(mEditTxtNombre.getText().toString());
        Contacto.setTelCelular(mEditTxtNumTelefonico.getText().toString());

        if(mNumContacto==1) {
            RealmList<usuario_contacto> Contactos =  new  RealmList <usuario_contacto>();
            Contactos.add(Contacto);
            Usuario.setContacto(Contactos);
        }

        else
            Usuario.getContacto().add(Usuario.getContacto().size(),Contacto);

        SharedViewModel.setUsuario(Usuario);
    }

    private void terminarRegistro(){
        usuarioBD_CRUD.añadirUser(Usuario);
        conectarBD.registrarCuentaCorreo(Usuario.getCorreoElectronico(), Usuario.getContrasena());
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        FragmentoIniciarSesion1 fragmentoIniciarSesion1 = new FragmentoIniciarSesion1();
        fragmentTransaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);
        fragmentTransaction.replace(R.id.fragment_pagina_principal, fragmentoIniciarSesion1).commit();
    }

}