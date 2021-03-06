package com.example.llegabien.backend.mapa.ubicacion;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class UbicacionGeodicacion {

    private Geocoder mGeocoder;

    public UbicacionGeodicacion (Context context){
        mGeocoder = new Geocoder(context, Locale.getDefault());
    }

    public Address geocodificarUbiciacion(String stringAddress){
        try{
            List<Address> addressList = mGeocoder.getFromLocationName(stringAddress,1);
            if(addressList.size()>0) {
                return addressList.get(0);
            }

        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Address geocodificarUbiciacionPrueba(String stringAddress){
        try{
            List<Address> addressList = mGeocoder.getFromLocationName(stringAddress,5);
            if(addressList.size()>0) {
                return addressList.get(0);
            }

        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String degeocodificarUbiciacion(double latitude, double longitude){
        try{
            List<Address> addressList = mGeocoder.getFromLocation(latitude, longitude, 1);
            if(addressList.size()>0) {
                return addressList.get(0).getAddressLine(0);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
