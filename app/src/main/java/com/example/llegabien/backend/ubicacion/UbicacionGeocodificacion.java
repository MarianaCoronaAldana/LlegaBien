package com.example.llegabien.backend.ubicacion;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class UbicacionGeocodificacion {

    private final Geocoder mGeocoder;

    public UbicacionGeocodificacion(Context context){
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

    public Address geocodificarUbiciacion(double latitude, double longitude){
        try{
            List<Address> addressList = mGeocoder.getFromLocation(latitude, longitude,1);
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

    public static String establecerNombreUbicacion(Address addressGoogle, ubicacion colonia, ubicacion municipio) {
        // Para establecer el nombre de la calle se establece con Thoroughfare o FeatureName.
        // En caso de que Thoroughfare o FeatureName sean nulos, quiere decir que la ubicacion no se encontró como calle o vía pública.
        String calleNombreGoogle = addressGoogle.getThoroughfare();
        if (calleNombreGoogle == null)
            calleNombreGoogle = addressGoogle.getFeatureName();

        if (calleNombreGoogle != null) {
            // Para establecer el nombre de la colonia se establece con SubLocality.
            // En caso de que SubLocality sea nulo, se tomara el nombre del objeto ubicacion que corresponde a la colonia de la calle.
            String coloniaNombreGoogle = addressGoogle.getSubLocality();
            if (coloniaNombreGoogle == null) {
                coloniaNombreGoogle = colonia.getNombre().split(",", 2)[0].trim();
            }

            // Se retorna el nombre completo de la calle.
            return (calleNombreGoogle + ", " + coloniaNombreGoogle + ", " + municipio.getNombre().split(",", 2)[0].trim() + ", " + addressGoogle.getAdminArea()
                    + ", " + addressGoogle.getCountryName()).toUpperCase(Locale.ROOT);
        }
        return null;
    }
}
