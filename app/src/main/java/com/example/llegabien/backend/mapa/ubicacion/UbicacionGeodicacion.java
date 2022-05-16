package com.example.llegabien.backend.mapa.ubicacion;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class UbicacionGeodicacion {

    private Geocoder mGeocoder;
    private Address mAddress;

    public Address getmAddress() { return mAddress; }

    public void geocodificarUbiciacion(Context c, String stringAddress){
        mGeocoder = new Geocoder(c, Locale.getDefault());
        try{
            List<Address> addressList = mGeocoder.getFromLocationName(stringAddress,1);
            if(addressList.size()>0)
                mAddress = addressList.get(0);

        }catch (IOException e) {
            e.printStackTrace();
        }

    }
}
