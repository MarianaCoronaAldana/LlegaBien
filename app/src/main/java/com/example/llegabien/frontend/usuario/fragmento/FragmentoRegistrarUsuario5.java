package com.example.llegabien.frontend.usuario.fragmento;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.llegabien.R;
import com.example.llegabien.frontend.rutas.activity.MapsActivity;
import com.example.llegabien.frontend.contactos.fragmento.FragmentoRegistrarContacto;

public class FragmentoRegistrarUsuario5 extends Fragment implements View.OnClickListener, FragmentManager.OnBackStackChangedListener{

    private Button mBtnSiguiente;
    private FragmentManager mFragmentManager;
    private int mNumContacto = 1, mBackStackCount = 0, mSiguienteCount = 1;

    public FragmentoRegistrarUsuario5() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_registrar_usuario5, container, false);

        //wiring up
        mBtnSiguiente= (Button) root.findViewById(R.id.button_siguiente_registro_5);
        mFragmentManager = getActivity().getSupportFragmentManager();

        //listeners
        mBtnSiguiente.setOnClickListener(this);
        mFragmentManager.addOnBackStackChangedListener(this);

        return root;
    }

    //listener functions
    @Override
    public void onClick(View view) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        switch (view.getId()) {
            case R.id.button_siguiente_registro_5:
                if (mNumContacto == 5)
                    startActivity(new Intent(getActivity(), MapsActivity.class));
                else {
                    mNumContacto++;
                    mSiguienteCount++;
                    Fragment fragmentoRegistrarContacto = FragmentoRegistrarContacto.newInstance(mNumContacto);
                    fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                    fragmentTransaction.add(R.id.fragmentContainerView_registrarContactos_registro_5, fragmentoRegistrarContacto).commit();
                    fragmentTransaction.addToBackStack(null);
                    break;
                }
        }
    }

    @Override
    public void onBackStackChanged() {
        if(mBackStackCount == mSiguienteCount)
            mNumContacto--;
        mBackStackCount = mSiguienteCount;
    }
}