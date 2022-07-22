package com.example.llegabien.backend.ruta.directions;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.List;

public class ubicacionRutaDAO {

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ubicacionRuta obtenerUbicacionRutaConAdress(String calle, List<ubicacionRuta> ruta) {
        return ruta.stream().filter(ubicacionRuta -> ubicacionRuta.getmDireccion().equals(calle)).findAny().orElse(null);
    }

}
