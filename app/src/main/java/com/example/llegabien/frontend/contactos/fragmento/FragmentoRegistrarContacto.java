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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.llegabien.R;
import com.example.llegabien.backend.contactos.usuario_contacto;
import com.example.llegabien.mongoDB.usuario_BD;
import com.example.llegabien.backend.usuario.UsuarioInputValidaciones;
import com.example.llegabien.backend.usuario.usuario;
import com.example.llegabien.backend.usuario.usuario_SharedViewModel;
import com.example.llegabien.frontend.usuario.fragmento.FragmentoIniciarSesion1;

import io.realm.RealmList;

public class FragmentoRegistrarContacto extends Fragment implements View.OnClickListener, FragmentManager.OnBackStackChangedListener{

    private TextView mTxtTitulo;
    private FragmentManager mFragmentManager;
    private EditText mEditTxtNombre, mEditTxtNumTelefonico;
    private Button mBtnSiguiente;
    private int mNumContacto = 1, mBackStackCount = 0, mSiguienteCount = 1;
    private Fragment parent;

    //PARAMETROS DE INICALIZACIÓN DEL FRAGMENTO
    private static final String parametro_usuario = "usuario"; //etiqueta

    private usuario_SharedViewModel SharedViewModel;
    usuario Usuario;
    usuario_contacto Contacto =  new  usuario_contacto();

    //para inicalizar el fragmento con parametros y guardarlos en un bundle
    public static FragmentoRegistrarContacto newInstance(usuario Usuario) {
        FragmentoRegistrarContacto fragment = new FragmentoRegistrarContacto();
        Bundle args = new Bundle();
        args.putSerializable(parametro_usuario, Usuario);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentoRegistrarContacto(){
    }

    public FragmentoRegistrarContacto(int numContacto, int siguienteCount) {
        mNumContacto = numContacto;
        mSiguienteCount = siguienteCount;
    }


    //para obtener los parametros que se guardan en el bundle
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedViewModel = new ViewModelProvider(requireActivity()).get(usuario_SharedViewModel.class);

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
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_registrar_contacto, container, false);

        //wiring up
        mTxtTitulo = (TextView) root.findViewById(R.id.textView_titulo_registroContactos);
        mFragmentManager = getActivity().getSupportFragmentManager();
        mEditTxtNombre = (EditText) root.findViewById(R.id.editText_nombre_registroContactos);
        mEditTxtNumTelefonico = (EditText) root.findViewById(R.id.editText_celular_registroContactos);

        parent = (Fragment) this.getParentFragment();
        mBtnSiguiente = parent.getView().findViewById(R.id.button_siguiente_registro_4);

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
        FragmentTransaction fragmentTransaction;
        switch (view.getId()) {
            case R.id.button_siguiente_registro_4:
                if (validarAllInputs()) {

                    tomarDatosContacto();

                    if (mNumContacto == 5){
                        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        FragmentoIniciarSesion1 fragmentoIniciarSesion1 = new FragmentoIniciarSesion1();
                        fragmentTransaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);
                        fragmentTransaction.replace(R.id.fragment_pantallaPrincipal,fragmentoIniciarSesion1).commit();


                        Log.v("QUICKSTART", "nombre: " + Usuario.getNombre()
                                + "contacto 1 nombre: " + Usuario.getContacto().first().getNombre()+
                                "ultimo contacto numero: " + Usuario.getContacto().last().getTelCelular());

                        //Se integra al usuario a la BD
                        usuario_BD.AñadirUser(Usuario);

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

}