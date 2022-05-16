package com.example.llegabien.frontend.mapa.activity;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_UBICACION_BUSQUEDA_AUTOCOMPLETADA;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.llegabien.R;
import com.example.llegabien.backend.app.Permisos;
import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.mapa.poligonos.Poligono;
import com.example.llegabien.backend.mapa.ubicacion.UbicacionDispositivo;
import com.example.llegabien.backend.mapa.ubicacion.UbicacionGeodicacion;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.libraries.places.api.Places;

import java.util.List;


public class ActivityMap extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnPolygonClickListener {

    private GoogleMap mGoogleMap;
    private String mStringAddress;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Permisos mPermisos;
    private Location mLastKnownLocation;
    private Button mBtnCentrarEnMapa, mBtnDirecciones;
    private static final int DEFAULT_ZOOM = 18;
    SupportMapFragment mMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Para inicializar a PlacesClient
        Places.initialize(getApplicationContext(), getString(R.string.api_key));
        Places.createClient(this);

        //Para inicializar a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //Para pedir permiso de ubicicación
        mPermisos = new Permisos();
        mPermisos.getPermisoUbicacion(this);

        //wiring up
        com.example.llegabien.databinding.ActivityMapsBinding binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView_map);
    }


    @Override
    public void onResume(){
        super.onResume();
        //listener
        mMapFragment.getMapAsync(this);
    }

    //FUNCIONES LISTENERS//

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mStringAddress = Preferences.getSavedStringFromPreference(this, PREFERENCE_UBICACION_BUSQUEDA_AUTOCOMPLETADA);
        LatLng defaultLocation = new LatLng(20.703027011977582, -103.3884804);
        //LatLng prueba = new LatLng(20.766993955497846, -103.5243485882069);

        //para obetener poligonos
        Poligono poligono= new Poligono();
        poligono.getPoligonos(mGoogleMap,this);

        //OnPolygonClickListener
        mGoogleMap.setOnPolygonClickListener(this);

        // Para activar My Location layer  y el control relacionado en el mapa.
        actualizarUbicacionUI();

        // Para obetener la ubicación actual del dispositivo y establezca la posición del mapa.
        UbicacionDispositivo mUbicacionDispositivo = new UbicacionDispositivo();
        mUbicacionDispositivo.getUbicacionDelDispositivo(new UbicacionDispositivo.OnUbicacionObtenida() {
            @Override
            public void isUbicacionObtenida(boolean isUbicacionObtenida, Location ubicacionObtenida) {
                if (!mStringAddress.equals("no hay string"))
                    mostrarLugarBuscado();
                else {
                    if (isUbicacionObtenida) {
                        mLastKnownLocation = ubicacionObtenida;
                        centrarEnUbicacionDispositivo();
                    }
                    else {
                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                }
            }
        }, mPermisos.getmLocationPermissionGranted(),fusedLocationProviderClient, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mPermisos.setmLocationPermissionGranted(false);
        if (requestCode == 0) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mPermisos.setmLocationPermissionGranted(true);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        actualizarUbicacionUI();
    }

    @Override
    public void onPolygonClick(Polygon polygon) {
        Poligono poligono = new Poligono();
        poligono.getInfoPoligono(polygon, this);
        FragmentoLugarSeleccionado fragmentoLugarSeleccionado = new FragmentoLugarSeleccionado();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerView_map_1, fragmentoLugarSeleccionado).commit();
        fragmentTransaction.addToBackStack(null);
    }

    //OTRAS FUNCIONES//

    private void actualizarUbicacionUI() {
        if (mGoogleMap == null) {
            return;
        }
        try {
            if (mPermisos.getmLocationPermissionGranted()) {
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mGoogleMap.setMyLocationEnabled(false);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                mPermisos.getPermisoUbicacion(this);
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void mostrarLugarBuscado(){
        UbicacionGeodicacion ubicacionGeodicacion = new UbicacionGeodicacion();
        ubicacionGeodicacion.geocodificarUbiciacion(this, mStringAddress);
        LatLng mLatLng = new LatLng(ubicacionGeodicacion.getmAddress().getLatitude(), ubicacionGeodicacion.getmAddress().getLongitude());
        mGoogleMap.addMarker((new MarkerOptions().position(mLatLng)));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, DEFAULT_ZOOM));
        Preferences.savePreferenceString(this,"no hay string",PREFERENCE_UBICACION_BUSQUEDA_AUTOCOMPLETADA);
    }

    private void centrarEnUbicacionDispositivo(){
        if (mLastKnownLocation != null)
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
    }

}