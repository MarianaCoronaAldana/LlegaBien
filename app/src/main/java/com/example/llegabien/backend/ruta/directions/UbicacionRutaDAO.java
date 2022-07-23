package com.example.llegabien.backend.ruta.directions;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.List;

public class UbicacionRutaDAO {

    @RequiresApi(api = Build.VERSION_CODES.N)
    public UbicacionRuta obtenerUbicacionRutaConAdress(String calle, List<UbicacionRuta> ruta) {
        return ruta.stream().filter(ubicacionRuta -> ubicacionRuta.getmDireccion().equals(calle)).findAny().orElse(null);
    }

}
