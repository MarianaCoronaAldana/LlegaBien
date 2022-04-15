package com.example.llegabien.frontend.usuario.fragmento;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.llegabien.R;
import com.example.llegabien.backend.usuario.UsuarioInputValidaciones;
import com.example.llegabien.backend.usuario.UsuarioRegistro;

public class FragmentoRegistrarUsuario3 extends Fragment implements View.OnClickListener{

    private Button mBtnVerificar, mBtnRegresar;
    private EditText mEditTxtCodigo1, mEditTxtCodigo2, mEditTxtCodigo3, mEditTxtCodigo4, mEditTxtCodigo5, mEditTxtCodigo6;

    //PARAMETROS DE INICALIZACIÓN DEL FRAGMENTO
    private static final String mCodigoNumTelefonico_PARAM1 = "param1"; //etiqueta
    private String mCodigoNumTelefonico_param1 = ""; //tipo y valor

    public FragmentoRegistrarUsuario3() {
        // Required empty public constructor
    }

    //para inicalizar el fragmento con parametros y guardarlos en un bundle
    public static FragmentoRegistrarUsuario3 newInstance(String param1) {
        FragmentoRegistrarUsuario3 fragment = new FragmentoRegistrarUsuario3();
        Bundle args = new Bundle();
        args.putString(mCodigoNumTelefonico_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    //para obtener los parametros que se guardan en el bundle
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCodigoNumTelefonico_param1 = getArguments().getString(mCodigoNumTelefonico_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_registrar_usuario3, container, false);

        //wiring up
        mBtnVerificar= (Button) root.findViewById(R.id.button_verificar_registro_3);
        mBtnRegresar = (Button) root.findViewById(R.id.button_regresar_registro_3);
        mEditTxtCodigo1 = (EditText) root.findViewById(R.id.editText1_codigo_registro_3);
        mEditTxtCodigo2 = (EditText) root.findViewById(R.id.editText2_codigo_registro_3);
        mEditTxtCodigo3 = (EditText) root.findViewById(R.id.editText3_codigo_registro_3);
        mEditTxtCodigo4 = (EditText) root.findViewById(R.id.editText4_codigo_registro_3);
        mEditTxtCodigo5 = (EditText) root.findViewById(R.id.editText5_codigo_registro_3);
        mEditTxtCodigo6 = (EditText) root.findViewById(R.id.editText6_codigo_registro_3);

        //listeners
        mBtnVerificar.setOnClickListener(this);
        mBtnRegresar.setOnClickListener(this);

        return root;
    }

    //listener function
    @Override
    public void onClick(View view) {
        //FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        switch (view.getId()) {
            case R.id.button_verificar_registro_3:
                if (validarAllInputs()){
                    String codigo =
                            mEditTxtCodigo1.getText().toString() +
                                    mEditTxtCodigo2.getText().toString() +
                                    mEditTxtCodigo3.getText().toString() +
                                    mEditTxtCodigo4.getText().toString() +
                                    mEditTxtCodigo5.getText().toString() +
                                    mEditTxtCodigo6.getText().toString();
                    UsuarioRegistro usuarioRegistro = new UsuarioRegistro();
                    usuarioRegistro.verificarCodigoNumTelefonico(mCodigoNumTelefonico_param1,codigo, this);
                }
                break;
            case R.id.button_regresar_registro_3:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }
    }

    //otras funciones
    private boolean validarAllInputs(){
        UsuarioInputValidaciones usuarioInputValidaciones = new UsuarioInputValidaciones();
        boolean esInputValido = true;
        if (!usuarioInputValidaciones.validarStringVacia(this.getActivity(),mEditTxtCodigo1))
            esInputValido = false;
        if ( !usuarioInputValidaciones.validarStringVacia(this.getActivity(),mEditTxtCodigo2))
            esInputValido = false;
        if (!usuarioInputValidaciones.validarStringVacia(this.getActivity(),mEditTxtCodigo3))
            esInputValido = false;
        if ( !usuarioInputValidaciones.validarStringVacia(this.getActivity(),mEditTxtCodigo4))
            esInputValido = false;
        if (!usuarioInputValidaciones.validarStringVacia(this.getActivity(),mEditTxtCodigo5))
            esInputValido = false;
        if ( !usuarioInputValidaciones.validarStringVacia(this.getActivity(),mEditTxtCodigo6))
            esInputValido = false;

        return esInputValido;
    }
}