package com.example.llegabien.usuario.fragmento;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.llegabien.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentoRegistrarUsuario4#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentoRegistrarUsuario4 extends Fragment implements View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentoRegistrarUsuario4() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentoRegistroUsuario4.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentoRegistrarUsuario4 newInstance(String param1, String param2) {
        FragmentoRegistrarUsuario4 fragment = new FragmentoRegistrarUsuario4();
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
    private Button mBtnVerificar, mBtnCerrar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_registrar_usuario4, container, false);

        //wiring up
        mBtnVerificar= (Button) root.findViewById(R.id.button_verificar_registro_4);
        mBtnCerrar = (Button) root.findViewById(R.id.button_cerrar_registro_4);

        //listeners
        mBtnVerificar.setOnClickListener(this);
        mBtnCerrar.setOnClickListener(this);

        return root;
    }

    //listener function
    @Override
    public void onClick(View view) {
        FragmentoRegistrarUsuario5 fragmentoRegistrarUsuario5 = new FragmentoRegistrarUsuario5();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack("text");
        switch (view.getId()) {
            case R.id.button_verificar_registro_4:
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                fragmentTransaction.replace(R.id.fragment_pantallaPrincipal, fragmentoRegistrarUsuario5).commit();
                break;
            case R.id.button_cerrar_registro_4:
                getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                break;
        }
    }
}