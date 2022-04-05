package com.example.llegabien;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class Transacciones {

    public void cerrarFragmento(FragmentTransaction fragmentTransaction, FragmentoAuxiliar fragmentoAuxiliar){
        fragmentTransaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);
        fragmentTransaction.replace(R.id.fragment_pantallaPrincipal,fragmentoAuxiliar).commit();
        fragmentTransaction.remove(fragmentoAuxiliar);
    }
}
