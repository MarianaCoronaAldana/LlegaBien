package com.example.llegabien.backend.mapa.ubicacion;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.example.llegabien.backend.app.Permisos;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class UbicacionGeodicacion {

    private Geocoder mGeocoder;
    private Address mAddress;
    private String mAddressString;

    public Address geocodificarUbiciacion(Context c, String stringAddress){
        mGeocoder = new Geocoder(c, Locale.getDefault());
        try{
            List<Address> addressList = mGeocoder.getFromLocationName(stringAddress,1);
            if(addressList.size()>0) {
                mAddress = addressList.get(0);
                return mAddress;
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
                mAddressString = addressList.get(0).getAddressLine(0); ;
                return mAddressString;
            }

        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
