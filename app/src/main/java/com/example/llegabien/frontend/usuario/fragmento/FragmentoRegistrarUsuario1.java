package com.example.llegabien.frontend.usuario.fragmento;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.llegabien.R;
import com.example.llegabien.backend.mongoDB.ConectarBD;
import com.example.llegabien.backend.usuario.UsuarioInputValidaciones;
import com.example.llegabien.backend.usuario.usuario;
import com.example.llegabien.backend.usuario.UsuarioSharedViewModel;
import com.example.llegabien.frontend.app.fragmento.FragmentoAuxiliar;
import com.example.llegabien.frontend.usuario.dialog.DialogDatePicker;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
public class FragmentoRegistrarUsuario1 extends Fragment implements View.OnClickListener{

    private UsuarioSharedViewModel SharedViewModel;

    private Button mBtnSiguiente, mBtnIniciarSesion, mBtnCerrar;
    private EditText mEditTxtNombres, mEditTxtApellidos, mEditTxtFechaNacimiento;
    ConectarBD conectarBD = new ConectarBD();

    public FragmentoRegistrarUsuario1() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_registrar_usuario1, container, false);

        //wiring up
        mBtnSiguiente= (Button) root.findViewById(R.id.button_siguiente_registro_1);
        mBtnIniciarSesion = (Button) root.findViewById(R.id.button_iniciarSesion_registro_1);
        mBtnCerrar = (Button) root.findViewById(R.id.button_cerrar_registro_1);
        mEditTxtNombres = (EditText) root.findViewById(R.id.editText_nombres_registro_1);
        mEditTxtApellidos = (EditText) root.findViewById(R.id.editText_apellidos_registro_1);
        mEditTxtFechaNacimiento = (EditText) root.findViewById(R.id.editText_fechaNacimiento_registro_1);

        //listeners
        mBtnSiguiente.setOnClickListener(this);
        mBtnIniciarSesion.setOnClickListener(this);
        mBtnCerrar.setOnClickListener(this);
        mEditTxtFechaNacimiento.setOnClickListener(this);

        //para usar el mismo ViewModel que los otros fragmentos y compartir informacion
        SharedViewModel = new ViewModelProvider(requireActivity()).get(UsuarioSharedViewModel.class);

        conectarBD.ConectarAnonimoMongoDB();
        return root;
    }

    //FUNCIONES LISTENER//
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        FragmentoAuxiliar fragmentoAuxiliar = new FragmentoAuxiliar();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        switch (view.getId()) {
            case R.id.button_siguiente_registro_1:
                if (validarAllInputs()) {
                    //Para obtener los datos del formulario y hacer una instancia de Usuario
                    SharedViewModel.setUsuario(usuarioConDatos());

                    // Para pasar al siguiente fragmento
                    FragmentoRegistrarUsuario2 fragmentoRegistrarUsuario2 = new FragmentoRegistrarUsuario2();
                    fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                    fragmentTransaction.replace(R.id.fragment_pagina_principal, fragmentoRegistrarUsuario2).commit();
                    fragmentTransaction.addToBackStack(null);
                }
                break;
            case R.id.button_iniciarSesion_registro_1:
                FragmentoIniciarSesion1 fragmentoIniciarSesion1 = new FragmentoIniciarSesion1();
                fragmentTransaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);
                fragmentTransaction.replace(R.id.fragment_pagina_principal, fragmentoIniciarSesion1).commit();
                break;
            case R.id.button_cerrar_registro_1:
                fragmentTransaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);
                fragmentTransaction.replace(R.id.fragment_pagina_principal,fragmentoAuxiliar).commit();
                fragmentTransaction.remove(fragmentoAuxiliar);
                break;
            case R.id.editText_fechaNacimiento_registro_1:
                DialogDatePicker dialogDatePicker = new DialogDatePicker();
                dialogDatePicker.mostrarDatePickerDialog(mEditTxtFechaNacimiento, this);
                mEditTxtFechaNacimiento.setError(null);
                break;
        }

    }

    //OTRAS FUNCIONES//

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean validarAllInputs(){
        UsuarioInputValidaciones usuarioInputValidaciones = new UsuarioInputValidaciones();
        boolean esInputValido = true;
        if (!usuarioInputValidaciones.validarNombre(getActivity(),mEditTxtNombres))
            esInputValido = false;
        if ( !usuarioInputValidaciones.validarNombre(getActivity(),mEditTxtApellidos))
            esInputValido = false;
        if (!usuarioInputValidaciones.validarFechaNacimiento(getActivity(),mEditTxtFechaNacimiento))
            esInputValido = false;

        return esInputValido;
    }

    //Funcion para crear objeto usuario e inicializarlo con los datos obtenidos
    @RequiresApi(api = Build.VERSION_CODES.O)
    private usuario usuarioConDatos(){
       usuario Usuario = new usuario();
       Usuario.setNombre(mEditTxtNombres.getText().toString().trim());
       Usuario.setApellidos(mEditTxtApellidos.getText().toString().trim());

       DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
       LocalDate localDate =  LocalDate.parse(mEditTxtFechaNacimiento.getText().toString(), dateTimeFormatter);
       Usuario.setFNacimiento(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));

       return Usuario;
    }

}