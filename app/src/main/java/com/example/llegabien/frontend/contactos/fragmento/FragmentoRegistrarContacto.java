package com.example.llegabien.frontend.contactos.fragmento;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Guideline;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.llegabien.R;
import com.example.llegabien.backend.usuario.UsuarioInputValidaciones;
import com.example.llegabien.frontend.usuario.fragmento.FragmentoIniciarSesion1;

public class FragmentoRegistrarContacto extends Fragment implements View.OnClickListener, FragmentManager.OnBackStackChangedListener{

    private EditText mEditTxtNombre, mEditTxtNumTelefonico;
    private Button mBtnSiguiente, mBtnFinalizar;
    private Guideline mGuideline1_Btn1, mGuideline2_Btn1;
    ConstraintLayout mConstraintLayout;
    private int mNumContacto = 1, mBackStackCount = 0, mSiguienteCount = 1;


    public FragmentoRegistrarContacto(){
        // Required empty public constructor
    }

    public FragmentoRegistrarContacto(int numContacto, int siguienteCount) {
        mNumContacto = numContacto;
        mSiguienteCount = siguienteCount;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_registrar_contacto, container, false);

        //wiring up
        TextView mTxtTitulo = (TextView) root.findViewById(R.id.textView_titulo_registroContactos);
        FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
        mEditTxtNombre = (EditText) root.findViewById(R.id.editText_nombre_registroContactos);
        mEditTxtNumTelefonico = (EditText) root.findViewById(R.id.editText_celular_registroContactos);

        //views de fragmento padre "FragmentoRegistrarUsuario4"
        Fragment parent = (Fragment) this.getParentFragment();
        mBtnSiguiente = parent.getView().findViewById(R.id.button1_siguiente_registro_4);
        mBtnFinalizar = parent.getView().findViewById(R.id.button2_finalizar_registro_4);
        mGuideline1_Btn1 = parent.getView().findViewById(R.id.guideline1_textView_editView_registro_4);
        mGuideline2_Btn1 = parent.getView().findViewById(R.id.guideline2_button1_registro_4);
        mConstraintLayout = parent.getView().findViewById(R.id.consLyt_parentPrincipal_registro_4);


        //para cambiar el titulo segun el numero de contacto
        String tituloRegistroContacto = getResources().getString(R.string.contactoEmergencia_registro4) + " " + String.valueOf(mNumContacto);
        mTxtTitulo.setText(tituloRegistroContacto);

        mBtnSiguiente.setOnClickListener(this);
        mFragmentManager.addOnBackStackChangedListener(this);

        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button1_siguiente_registro_4:
                if (validarAllInputs()) {
                    if (mNumContacto >= 2){
                        ConstraintSet constraintSet = new ConstraintSet();
                        constraintSet.clone(mConstraintLayout);
                        constraintSet.connect(mBtnSiguiente.getId(),ConstraintSet.START,mGuideline1_Btn1.getId(),ConstraintSet.START,0);
                        constraintSet.connect(mBtnSiguiente.getId(),ConstraintSet.END,mGuideline2_Btn1.getId(),ConstraintSet.END,0);
                        constraintSet.setDimensionRatio(mBtnSiguiente.getId(),"5:2");
                        constraintSet.applyTo(mConstraintLayout);
                        mBtnFinalizar.setVisibility(View.VISIBLE);
                        mBtnFinalizar.setClickable(true);
                    }
                    if (mNumContacto == 5){
                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        FragmentoIniciarSesion1 fragmentoIniciarSesion1 = new FragmentoIniciarSesion1();
                        fragmentTransaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);
                        fragmentTransaction.replace(R.id.fragment_pagina_principal, fragmentoIniciarSesion1).commit();
                    }
                    else {
                        mNumContacto++;
                        mSiguienteCount++;
                        abrirInicioSesion1();
                    }
                }
                break;
            case R.id.button2_finalizar_registro_4:
                abrirInicioSesion1();
                break;
        }
    }

    //otras funciones
    @Override
    public void onBackStackChanged() {
        if (mBackStackCount == mSiguienteCount)
            mNumContacto--;
        mBackStackCount = mSiguienteCount;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean validarAllInputs(){
        UsuarioInputValidaciones usuarioInputValidaciones = new UsuarioInputValidaciones();
        boolean esInputValido = true;
        if (!usuarioInputValidaciones.validarNombre(getActivity(),mEditTxtNombre))
            esInputValido = false;
        if ( !usuarioInputValidaciones.validarNumTelefonico(getActivity(),mEditTxtNumTelefonico))
            esInputValido = false;

        return esInputValido;
    }

    private void abrirInicioSesion1 (){
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        FragmentoRegistrarContacto fragmentoRegistrarContacto = new FragmentoRegistrarContacto(mNumContacto, mSiguienteCount);
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        fragmentTransaction.add(R.id.fragmentContainerView_registrarContactos_registro_4, fragmentoRegistrarContacto).commit();
        fragmentTransaction.addToBackStack(null);
    }

}