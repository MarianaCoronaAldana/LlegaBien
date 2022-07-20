package com.example.llegabien.backend.mapa.ubicacion;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class UbicacionGeodicacion {
    private Geocoder mGeocoder;

    public Address geocodificarUbiciacion(Context c, String stringAddress){
        mGeocoder = new Geocoder(c, Locale.getDefault());
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

    public String degeocodificarUbiciacion(Context c, double latitude, double longitude){
        mGeocoder = new Geocoder(c, Locale.getDefault());
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

    public String degeocodificarUbiciacionSinNumero(Context c, double latitude, double longitude){
        mGeocoder = new Geocoder(c, Locale.getDefault());
        try{
            List<Address> addressList = mGeocoder.getFromLocation(latitude, longitude, 1);
            if(addressList.size()>0) {
                String calle;
                if(addressList.get(0).getThoroughfare()==null)
                    calle = addressList.get(0).getFeatureName();
                else
                    calle = addressList.get(0).getThoroughfare();
                if(addressList.get(0).getSubLocality()!=null) {
                    Address direccion = new Address(Locale.getDefault());
                    direccion.setThoroughfare(calle);
                    direccion.setSubLocality(addressList.get(0).getSubLocality());
                    direccion.setLocality(addressList.get(0).getLocality());
                    return calle
                            + ", " + addressList.get(0).getSubLocality()
                            + ", " + addressList.get(0).getLocality();
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
