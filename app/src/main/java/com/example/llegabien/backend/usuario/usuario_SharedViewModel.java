package com.example.llegabien.backend.usuario;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class usuario_SharedViewModel extends ViewModel {

    private MutableLiveData<usuario> Usuario = new MutableLiveData<>();
    private usuario UsuariO = new usuario();

    public void setUsuario(usuario user) {
        Usuario.setValue(user);
    }

    public MutableLiveData<usuario> getUsuario() {
        if (Usuario == null) {
            Usuario = new MutableLiveData<>();
        }
        return Usuario;
    }

}