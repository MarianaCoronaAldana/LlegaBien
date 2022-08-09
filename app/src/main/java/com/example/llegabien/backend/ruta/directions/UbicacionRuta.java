package com.example.llegabien.backend.ruta.directions;

import android.location.Address;

import com.example.llegabien.backend.ubicacion.ubicacion;

public class UbicacionRuta {
    private int mDistancia;
    private float mLatPuntoInicio;
    private float mLngPuntoInicio;
    private float mLatPuntoMedio;
    private float mLngPuntoMedio;
    private Address mAddressPuntoMedio;
    private String mNombreCalle;
    private ubicacion mUbicacion;
    private ubicacion mUbicacionColonia;
    private ubicacion mUbicacionCalle;

    public UbicacionRuta(int Distancia, Address Address, String Direccion) {
        this.mDistancia = Distancia;
        this.mAddressPuntoMedio = Address;
        this.mNombreCalle = Direccion;
    }

    public int getmDistancia() {
        return mDistancia;
    }

    public void setmDistancia(int mDistancia) {
        this.mDistancia = mDistancia;
    }

    public Address getmAddressPuntoMedio() {
        return mAddressPuntoMedio;
    }

    public void setmAddressPuntoMedio(Address mAddressPuntoMedio) {
        this.mAddressPuntoMedio = mAddressPuntoMedio;
    }

    public String getmNombreCalle() {
        return mNombreCalle;
    }

    public void setmNombreCalle(String Direccion) {
        this.mNombreCalle = Direccion;
    }

    public ubicacion getmUbicacion() {
        return mUbicacion;
    }

    public void setmUbicacion(ubicacion mUbicacion) {
        this.mUbicacion = mUbicacion;
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

    public float getLatPuntoInicio() {
        return mLatPuntoInicio;
    }

    public void setLatPuntoInicio(float mLatPuntoInicio) {
        this.mLatPuntoInicio = mLatPuntoInicio;
    }

    public float getLngPuntoInicio() {
        return mLngPuntoInicio;
    }

    public void setLngPuntoInicio(float mLngPuntoInicio) {
        this.mLngPuntoInicio = mLngPuntoInicio;
    }

    public float getLatPuntoMedio() {
        return mLatPuntoMedio;
    }

    public void setLatPuntoMedio(float mLatPuntoMedio) {
        this.mLatPuntoMedio = mLatPuntoMedio;
    }

    public float getLngPuntoMedio() {
        return mLngPuntoMedio;
    }

    public void setLngPuntoMedio(float mLngPuntoMedio) {
        this.mLngPuntoMedio = mLngPuntoMedio;
    }

}


