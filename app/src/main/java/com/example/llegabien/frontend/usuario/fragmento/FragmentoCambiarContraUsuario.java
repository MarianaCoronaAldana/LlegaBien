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

public class FragmentoCambiarContraUsuario extends Fragment implements View.OnClickListener{

    private Button mBtnAceptar, mBtnRegresar;
    private EditText mEditTxtActualContraseña, mEditTxtNuevaContraseña, mEditTxtConfirmarContraseña;

    public FragmentoCambiarContraUsuario() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_cambiar_contra_usuario, container, false);

        //wiring up
        mBtnAceptar = (Button) root.findViewById(R.id.button_aceptar_cambiarContra);
        mBtnRegresar = (Button) root.findViewById(R.id.button_regresar_cambiarContra);
        mEditTxtActualContraseña = (EditText) root.findViewById(R.id.editText_actualContra_cambiarContra);
        mEditTxtNuevaContraseña = (EditText) root.findViewById(R.id.editText_nuevaContra_cambiarContra);
        mEditTxtConfirmarContraseña = (EditText) root.findViewById(R.id.editText_confirmarContra_cambiarContra);

        //listeners
        mBtnAceptar.setOnClickListener(this);
        mBtnRegresar.setOnClickListener(this);

        return root;
    }

    //FUNCIONES LISTENERS//
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_aceptar_cambiarContra:
                if(validarAllInputs()) {
                    FragmentoEditarPerfilUsuario fragmentoEditarPerfilUsuario = new FragmentoEditarPerfilUsuario();
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                    fragmentTransaction.replace(R.id.fragment_configuracion, fragmentoEditarPerfilUsuario).commit();
                    fragmentTransaction.addToBackStack(null);
                }
                break;
            case R.id.button_regresar_cambiarContra:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }
    }

    //OTRAS FUNCIONES//
    private boolean validarAllInputs() {
        UsuarioInputValidaciones usuarioInputValidaciones = new UsuarioInputValidaciones();
        boolean esInputValido = true;
        if (usuarioInputValidaciones.validarContraseña(getActivity(),mEditTxtNuevaContraseña)){
             if (!usuarioInputValidaciones.validarConfirmarContraseña(mEditTxtNuevaContraseña.getText().toString(),getActivity(), mEditTxtConfirmarContraseña))
                 esInputValido = false;
        }
        else
            esInputValido = false;

        return esInputValido;
    }
}