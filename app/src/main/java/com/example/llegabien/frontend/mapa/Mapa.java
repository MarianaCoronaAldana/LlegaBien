package com.example.llegabien.frontend.mapa;

import android.location.Location;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentTransaction;

import com.example.llegabien.R;
import com.example.llegabien.backend.poligonos.Poligono;
import com.example.llegabien.frontend.mapa.activity.ActivityMap;
import com.example.llegabien.frontend.mapa.fragmento.FragmentoBuscarLugar;
import com.example.llegabien.frontend.mapa.fragmento.FragmentoLugarSeleccionado;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Mapa {

    private GoogleMap mGoogleMap;
    private final ActivityMap mActivityMap;
    private static final int DEFAULT_ZOOM = 18;

    public Mapa(ActivityMap activityMap) {
        mActivityMap = activityMap;
    }

    // Para mostrar la ubicación que se buscó en el fragmento "BuscarLugar" o "LugarSeleccionado"
    public void mostrarUbicacionBuscada(boolean isUbicacionBuscadaEnBD, boolean isFragmentoBuscarLugar, LatLng ubicacionBuscada, String ubicacionBuscadaString) {
        if (mActivityMap != null) {
            mGoogleMap = mActivityMap.getGoogleMap();
            if (mGoogleMap != null) {
                removerPolygonAnterior();
                removerMarkerAnterior();

                // Para mostrar la ubicacion buscada en el mapa con un marcador.
                mActivityMap.setMarkerAnterior(mGoogleMap.addMarker((new MarkerOptions().position(ubicacionBuscada))));

                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionBuscada, DEFAULT_ZOOM));

                // Para verificar que si se encontró la ubicación buscada en la BD.
                if (isUbicacionBuscadaEnBD) {
                    // Para abrir el fragmento "LugarSeleccionado" cuando se obtenga un resultado.
                    abrirFragmentoLugarSeleccionado(true, ubicacionBuscadaString, ubicacionBuscada);
                } else {
                    // Si no se encuentra la ubicacion el BD, se muestra el fragmento "BuscarLugar".
                    // Se verifica que el fragmento mostrado no sea "BuscarLugar".
                    if (!isFragmentoBuscarLugar) {
                        FragmentoBuscarLugar fragmentoBuscarLugar = new FragmentoBuscarLugar();
                        FragmentTransaction fragmentTransaction = mActivityMap.getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragmentContainerView_fragmentoLugares_activityMaps, fragmentoBuscarLugar).commit();
                    }

                }
            }
        }
    }

    public void establecerLatLngFavorito(LatLng ubicacionFavorito){
        if(mActivityMap !=null){
            mActivityMap.setLatLngMarkerFavorito(ubicacionFavorito);
        }
    }

    public void abrirFragmentoBuscarLugar() {
        if (mActivityMap != null) {
            FragmentoBuscarLugar fragmentoBuscarLugar = new FragmentoBuscarLugar();
            FragmentTransaction fragmentTransaction = mActivityMap.getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainerView_fragmentoLugares_activityMaps, fragmentoBuscarLugar, "FragmentoBuscarLugar").commit();
            removerPolygonAnterior();
            removerMarkerAnterior();
        }
    }

    // Para abrir el fragmento "LugarSeleccionado".
    public void abrirFragmentoLugarSeleccionado(boolean hasUbicacionBuscada, String address, LatLng coordenadasParaFavorito) {
        if (mActivityMap != null) {
            FragmentoLugarSeleccionado fragmentoLugarSeleccionado = new FragmentoLugarSeleccionado(null);

            // Para pasar al fragmento "LugarSeleccionado" las coordenadas que serviran para guardar el lugar en favoritos.
            fragmentoLugarSeleccionado.setCoordenadasParaFavorito(coordenadasParaFavorito);

            // Para verificar que si se buscó un lugar o si se hizo click en un poligono.
            if (hasUbicacionBuscada) {
                fragmentoLugarSeleccionado.setUbicacionBuscada(address);
            }

            FragmentTransaction fragmentTransaction = mActivityMap.getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainerView_fragmentoLugares_activityMaps, fragmentoLugarSeleccionado).commit();
        }
    }

    // Para centrar en mapa la ubicación actual del disposistivo.
    public void centrarMapa() {
        if (mActivityMap != null) {
            if (mActivityMap.getLastKnownLocation() != null) {
                mGoogleMap = mActivityMap.getGoogleMap();
                if (mGoogleMap != null)
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mActivityMap.getLastKnownLocation().getLatitude(), mActivityMap.getLastKnownLocation().getLongitude()), DEFAULT_ZOOM));
            }
        }
    }

    public void actualizarCamaraByUbicacionDispositivo(Location deviceLocation) {
        if (mActivityMap != null) {
            mGoogleMap = mActivityMap.getGoogleMap();
            if (mGoogleMap != null) {
                CameraPosition currentPlace = new CameraPosition.Builder()
                        .target(new LatLng(deviceLocation.getLatitude(), deviceLocation.getLongitude()))
                        .bearing(deviceLocation.getBearing())
                        .zoom(18f)
                        .build();
                mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(currentPlace));

            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void mostrarColonias() {
        if (mActivityMap != null) {
            if (mActivityMap.getLastKnownLocation() != null) {
                mGoogleMap = mActivityMap.getGoogleMap();
                if (mGoogleMap != null) {
                    mGoogleMap.clear();
                    abrirFragmentoBuscarLugar();
                    centrarMapa();
                    mActivityMap.mostrarColonias();
                }
            }
        }
    }

    // Para cambiar el color del poligono anterior que se seleccionó.
    public void removerPolygonAnterior() {
        if (mActivityMap != null) {
            if (mActivityMap.getPolygonAnterior() != null)
                mActivityMap.getPolygonAnterior().setFillColor(mActivityMap.getColorAnterior());
        }
    }

    // Para remover el marcador anterior que se seleccionó.
    public void removerMarkerAnterior() {
        if (mActivityMap != null) {
            if (mActivityMap.getMarkerAnterior() != null)
                mActivityMap.getMarkerAnterior().remove();
        }
    }
}
