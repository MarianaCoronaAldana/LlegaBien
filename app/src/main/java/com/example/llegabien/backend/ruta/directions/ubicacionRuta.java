package com.example.llegabien.backend.ruta.directions;

import android.location.Address;

import com.example.llegabien.backend.mapa.ubicacion.ubicacion;

import java.util.Locale;

public class ubicacionRuta {
    int mDistancia;
    Address mAddress;
    String mDireccion;
    ubicacion mUbicacion;
    ubicacion mUbicacionColonia;
    ubicacion mUbicacionCalle;

    public ubicacionRuta() {
    }

    public ubicacionRuta(int Distancia, Address Address, String Direccion) {
        this.mDistancia = Distancia;
        this.mAddress = Address;
        this.mDireccion = Direccion;
    }

    public int getmDistancia() {
        return mDistancia;
    }

    public void setmDistancia(int mDistancia) {
        this.mDistancia = mDistancia;
    }

    public Address getmAddress() {
        return mAddress;
    }

    public void setmAddress(Address mAddress) {
        this.mAddress = mAddress;
    }

    public String getmDireccion() {
        return mDireccion;
    }

    public void setmDireccion(String Direccion) {
        this.mDireccion = Direccion;
    }

    public ubicacion getmUbicacion() {
        return mUbicacion;
    }

    public void setmUbicacion(ubicacion mUbicacion) {
        this.mUbicacion = mUbicacion;
    }

    public String construirDireccion(){
        return (mAddress.getThoroughfare() + ", " + mAddress.getSubLocality() + ", " + mAddress.getLocality() + ", " + mAddress.getAdminArea()
                + ", " + mAddress.getCountryName()).toUpperCase(Locale.ROOT);
    }

    public ubicacion getmUbicacionColonia() {
        return mUbicacionColonia;
    }

    public void setmUbicacionColonia(ubicacion mUbicacionColonia) {
        this.mUbicacionColonia = mUbicacionColonia;
    }

    public ubicacion getmUbicacionCalle() {
        return mUbicacionCalle;
    }

    public void setmUbicacionCalle(ubicacion mUbicacionCalle) {
        this.mUbicacionCalle = mUbicacionCalle;
    }
}


