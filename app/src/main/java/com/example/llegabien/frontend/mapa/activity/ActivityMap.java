package com.example.llegabien.frontend.mapa.activity;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.llegabien.R;
import com.example.llegabien.backend.app.Permisos;
import com.example.llegabien.backend.mapa.poligonos.Poligono;
import com.example.llegabien.backend.mapa.ubicacion.UbicacionDispositivo;
import com.example.llegabien.backend.notificacion.Notificacion;
import com.example.llegabien.databinding.ActivityMapsBinding;
import com.example.llegabien.frontend.mapa.fragmento.FragmentoBuscarLugar;
import com.example.llegabien.frontend.mapa.fragmento.FragmentoLugarSeleccionado;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.libraries.places.api.Places;


public class ActivityMap extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnPolygonClickListener{

    private static final int DEFAULT_ZOOM = 18;
    private GoogleMap mGoogleMap = null;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Permisos mPermisos;
    private Location mLastKnownLocation;
    private SupportMapFragment mMapFragment;
    private Poligono mPoligono;
    private Polygon mPolygonAnterior;
    private Marker mMarkerAnterior;
    private int mColorAnterior;
    public static boolean clickOnEmergenciaDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //wiring up
        com.example.llegabien.databinding.ActivityMapsBinding binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView_mapa_actvityMaps);

        //listeners
        mMapFragment.getMapAsync(this);

        //Para inicializar a PlacesClient
        Places.initialize(getApplicationContext(), getString(R.string.api_key));
        Places.createClient(this);

        //Para inicializar a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //Para pedir permiso de ubicicación
        mPermisos = new Permisos();
        mPermisos.getPermisoUbicacion(this,true);

        //Se verifica el nivel de bateria del telefono celular
        Notificacion bateria = new Notificacion(this);
    }

    @Override
    public void onResume(){
        super.onResume();

        //Se verifica el nivel de bateria del telefono celular
        Notificacion bateria = new Notificacion(this);

        if (mGoogleMap!=null){
            //Para activar My Location layer
            actualizarUbicacionUI();

            if (clickOnEmergenciaDialog){
                mPermisos.getPermisoUbicacion(this,true);
                clickOnEmergenciaDialog = false;
            }
        }

    }

    //FUNCIONES LISTENERS//

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
        //Para mostrar colonias con código de colores
        mostrarColonias();

        //Para activar My Location layer
        actualizarUbicacionUI();

        //Para obtener la ubicación actual del dispositivo y ubicacion buscada (si existe)
        mostrarUbicacionDispositivo();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mPermisos.setLocationPermissionGranted(false);
        if (requestCode == 0) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mPermisos.setLocationPermissionGranted(true);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        actualizarUbicacionUI();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onPolygonClick(@NonNull Polygon polygon) {
        removerPolygonAnterior();
        removerMarkerAnterior();

        mPolygonAnterior = polygon;
        mColorAnterior = mPolygonAnterior.getFillColor();

        Poligono poligono = new Poligono(this);
        poligono.getInfoPoligono(polygon);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(poligono.getCentroPoligono(polygon), 16));

        abrirFragmentoLugarBuscado(false, null);

    }

    @Override
    public void onBackPressed() {
        FragmentoLugarSeleccionado fragmentoLugarSeleccionado = (FragmentoLugarSeleccionado) getSupportFragmentManager().findFragmentByTag("FragmentoLugarSeleccionado");
        if (fragmentoLugarSeleccionado != null && fragmentoLugarSeleccionado.isVisible()) {
            abrirFragmentoBuscarLugar();
        }
    }


    //OTRAS FUNCIONES//

    private void actualizarUbicacionUI() {
        if (mGoogleMap == null) {
            return;
        }
        try {
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false); //boton de centrar en mapa default de GM
            if (mPermisos.getLocationPermissionGranted()) {
                mGoogleMap.setMyLocationEnabled(true);

            } else {
                mGoogleMap.setMyLocationEnabled(false);
                mLastKnownLocation = null;
                //mPermisos.getPermisoUbicacion(this);
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void mostrarUbicacionDispositivo(){
        UbicacionDispositivo mUbicacionDispositivo = new UbicacionDispositivo();
        mUbicacionDispositivo.getUbicacionDelDispositivo(new UbicacionDispositivo.OnUbicacionObtenida() {
            @Override
            public void isUbicacionObtenida(boolean isUbicacionObtenida, Location ubicacionObtenida) {
                if (isUbicacionObtenida) {
                    mLastKnownLocation = ubicacionObtenida;
                    centrarMapa();
                }
                else {
                    LatLng defaultLocation = new LatLng(20.703027011977582, -103.3884804);
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                    mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                }
            }
        }, mPermisos.getLocationPermissionGranted(),fusedLocationProviderClient, this);
    }

    public void mostrarUbicacionBuscada(boolean isUbicacionBuscadaEnBD, boolean isFragmentoBuscarLugar, LatLng ubicacionBuscada, String ubicacionBuscadaString){
        removerPolygonAnterior();
        removerMarkerAnterior();

        // Para mostrar la ubicacion buscada en el mapa con un marcador.
        mMarkerAnterior  = mGoogleMap.addMarker((new MarkerOptions().position(ubicacionBuscada)));

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionBuscada, DEFAULT_ZOOM));

        // Para verificar que si se encontró la ubicación buscada en la BD.
        if(isUbicacionBuscadaEnBD) {
            // Para abrir el fragmento "LugarSeleccionado" cuando se obtenga un resultado.
            abrirFragmentoLugarBuscado(true, ubicacionBuscadaString);
        }
        else{
            if(!isFragmentoBuscarLugar){
                FragmentoBuscarLugar fragmentoBuscarLugar = new FragmentoBuscarLugar();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainerView1_fragemntoBuscarLugar_activityMaps, fragmentoBuscarLugar).commit();
            }

        }
    }

    public void centrarMapa(){
        if (mLastKnownLocation != null)
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void mostrarColonias(){
        // Para obetener poligonos.
        mPoligono = new Poligono(this);
        mPoligono.getColonias(mGoogleMap);

        // OnPolygonClickListener
        mGoogleMap.setOnPolygonClickListener(this);
    }

    private void abrirFragmentoLugarBuscado(boolean hasUbicacionBuscada, String address){
        FragmentoLugarSeleccionado fragmentoLugarSeleccionado = new FragmentoLugarSeleccionado();
        if (hasUbicacionBuscada){
            fragmentoLugarSeleccionado.setUbicacionBuscada(address);
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerView1_fragemntoBuscarLugar_activityMaps, fragmentoLugarSeleccionado, "FragmentoLugarSeleccionado").commit();
    }

    public void abrirFragmentoBuscarLugar(){
        FragmentoBuscarLugar fragmentoBuscarLugar = new FragmentoBuscarLugar();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerView1_fragemntoBuscarLugar_activityMaps, fragmentoBuscarLugar).commit();
        removerPolygonAnterior();
        removerMarkerAnterior();
    }

    // Para cambiar el color del poligono anterior que se seleccionó.
    public void removerPolygonAnterior(){
        if (mPolygonAnterior != null)
            mPolygonAnterior.setFillColor(mColorAnterior);
    }

    // Para remover el marcador anterior que se seleccionó.
    public void removerMarkerAnterior(){
        if (mMarkerAnterior != null)
            mMarkerAnterior.remove();
    }

}