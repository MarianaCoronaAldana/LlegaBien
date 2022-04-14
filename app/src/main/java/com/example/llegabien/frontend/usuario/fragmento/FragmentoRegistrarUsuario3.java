package com.example.llegabien.frontend.usuario.fragmento;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.llegabien.R;

public class FragmentoRegistrarUsuario3 extends Fragment implements View.OnClickListener{

    private Button mBtnVerificar, mBtnRegresar;

    public FragmentoRegistrarUsuario3() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_registrar_usuario3, container, false);

        //wiring up
        mBtnVerificar= (Button) root.findViewById(R.id.button_verificar_registro_3);
        mBtnRegresar = (Button) root.findViewById(R.id.button_regresar_registro_3);

        //listeners
        mBtnVerificar.setOnClickListener(this);
        mBtnRegresar.setOnClickListener(this);

        return root;
    }

    //listener function
    @Override
    public void onClick(View view) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        switch (view.getId()) {
            case R.id.button_verificar_registro_3:
                FragmentoRegistrarUsuario4 fragmentoRegistrarUsuario4 = new FragmentoRegistrarUsuario4();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_right);
                fragmentTransaction.replace(R.id.fragment_pantallaPrincipal, fragmentoRegistrarUsuario4).commit();
                fragmentTransaction.addToBackStack(null);
                break;
            case R.id.button_regresar_registro_3:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }
    }
}