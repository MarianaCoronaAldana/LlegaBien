package com.example.llegabien.frontend.usuario.fragmento;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.llegabien.R;

public class FragmentoRegistrarUsuario4 extends Fragment implements View.OnClickListener{

    private Button mBtnVerificar, mBtnCerrar;
    private FrameLayout mFrameLytPaginaPrincipal;

    public FragmentoRegistrarUsuario4() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_registrar_usuario4, container, false);

        //wiring up
        mBtnVerificar= (Button) root.findViewById(R.id.button_verificar_registro_4);
        mBtnCerrar = (Button) root.findViewById(R.id.button_regresar_registro_4);
        mFrameLytPaginaPrincipal = (FrameLayout) getActivity().findViewById(R.id.fragment_pantallaPrincipal);

        //listeners
        mBtnVerificar.setOnClickListener(this);
        mBtnCerrar.setOnClickListener(this);

        return root;
    }

    //listener function
    @Override
    public void onClick(View view) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        switch (view.getId()) {
            case R.id.button_verificar_registro_4:
                FragmentoRegistrarUsuario5 fragmentoRegistrarUsuario5 = new FragmentoRegistrarUsuario5();
                mFrameLytPaginaPrincipal.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_right);
                fragmentTransaction.replace(R.id.fragment_pantallaPrincipal, fragmentoRegistrarUsuario5).commit();
                fragmentTransaction.addToBackStack(null);
                break;
            case R.id.button_regresar_registro_4:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }
    }
}