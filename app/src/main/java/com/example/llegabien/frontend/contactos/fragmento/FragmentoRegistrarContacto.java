package com.example.llegabien.frontend.contactos.fragmento;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
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
import com.example.llegabien.frontend.rutas.activity.MapsActivity;
import com.example.llegabien.frontend.usuario.fragmento.FragmentoRegistrarUsuario2;

public class FragmentoRegistrarContacto extends Fragment implements View.OnClickListener, FragmentManager.OnBackStackChangedListener{

    private TextView mTxtTitulo;
    private FragmentManager mFragmentManager;
    private EditText mEditTxtNombre, mEditTxtNumTelefonico;
    private Button mBtnSiguiente;
    private int mNumContacto = 1, mBackStackCount = 0, mSiguienteCount = 1;
    private Fragment parent;

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
        mTxtTitulo = (TextView) root.findViewById(R.id.textView_titulo_registroContactos);
        mFragmentManager = getActivity().getSupportFragmentManager();
        mEditTxtNombre = (EditText) root.findViewById(R.id.editText_nombre_registroContactos);
        mEditTxtNumTelefonico = (EditText) root.findViewById(R.id.editText_celular_registroContactos);

        parent = (Fragment) this.getParentFragment();
        mBtnSiguiente = parent.getView().findViewById(R.id.button_siguiente_registro_5);

        //para cambiar el titulo segun el numero de contacto
        String tituloRegistroContacto = getResources().getString(R.string.contactoEmergencia_registro5) + " " + String.valueOf(mNumContacto);
        mTxtTitulo.setText(tituloRegistroContacto);

        mBtnSiguiente.setOnClickListener(this);
        mFragmentManager.addOnBackStackChangedListener(this);

        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        switch (view.getId()) {
            case R.id.button_siguiente_registro_5:
                if (validarAllInputs()) {
                    if (mNumContacto == 5)
                        startActivity(new Intent(getActivity(), MapsActivity.class));
                    else {
                        mNumContacto++;
                        mSiguienteCount++;
                        FragmentoRegistrarContacto fragmentoRegistrarContacto = new FragmentoRegistrarContacto(mNumContacto, mSiguienteCount);
                        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                        fragmentTransaction.add(R.id.fragmentContainerView_registrarContactos_registro_5, fragmentoRegistrarContacto).commit();
                        fragmentTransaction.addToBackStack(null);
                    }
                }
                break;
        }
    }

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

}