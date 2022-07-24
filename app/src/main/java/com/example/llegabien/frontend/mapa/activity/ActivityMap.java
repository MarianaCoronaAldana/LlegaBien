package com.example.llegabien.frontend.mapa.activity;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_RUTASEGURA;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.llegabien.R;
import com.example.llegabien.backend.app.Permisos;
import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.notificacion.Notificacion;
import com.example.llegabien.backend.poligonos.Poligono;
import com.example.llegabien.backend.ruta.directions.Ruta;
import com.example.llegabien.backend.ubicacion.UbicacionDispositivo;
import com.example.llegabien.databinding.ActivityMapsBinding;
import com.example.llegabien.frontend.mapa.Mapa;
import com.example.llegabien.frontend.mapa.fragmento.FragmentoBuscarLugar;
import com.example.llegabien.frontend.mapa.fragmento.FragmentoLugarSeleccionado;
import com.example.llegabien.frontend.rutas.directionhelpers.TaskLoadedCallback;
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


public class ActivityMap extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnPolygonClickListener, TaskLoadedCallback{

    private static final int DEFAULT_ZOOM = 18;
    private GoogleMap mGoogleMap = null;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Permisos mPermisos;
    private Location mLastKnownLocation;
    private Polygon mPolygonAnterior;
    private Marker mMarkerAnterior;
    private LatLng mLatLngMarkerFavorito = null;
    private Mapa mMapa;

    public int getColorAnterior() {
        return mColorAnterior;
    }

    private int mColorAnterior;

    public GoogleMap getGoogleMap() {
        return mGoogleMap;
    }

    public Location getLastKnownLocation() {
        return mLastKnownLocation;
    }

    public Polygon getPolygonAnterior() {
        return mPolygonAnterior;
    }

    public Marker getMarkerAnterior() {
        return mMarkerAnterior;
    }

    public void setMarkerAnterior(Marker mMarkerAnterior) {
        this.mMarkerAnterior = mMarkerAnterior;
    }

    public void setLatLngMarkerFavorito(LatLng latLngMarkerFavorito) {
        mLatLngMarkerFavorito = latLngMarkerFavorito;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMapa = new Mapa(this);

        // Para saber si la actividad anterior es la de favoritos y abrir el correspondiente fragmento.
        String activityAnterior = getIntent().getStringExtra("ACTIVITY_ANTERIOR");
        if (activityAnterior != null) {
            if (activityAnterior.equals("FAVORITOS")) {
                FragmentoLugarSeleccionado fragmentoLugarSeleccionado = new FragmentoLugarSeleccionado(activityAnterior);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.fragmentContainerView_fragmentoLugares_activityMaps, fragmentoLugarSeleccionado).commit();
            } else
                mMapa.abrirFragmentoBuscarLugar();
        } else
            mMapa.abrirFragmentoBuscarLugar();

        //wiring up
        com.example.llegabien.databinding.ActivityMapsBinding binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SupportMapFragment mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView_mapa_actvityMaps);

        //listeners
        if (mMapFragment != null)
            mMapFragment.getMapAsync(this);

        //Para inicializar a PlacesClient
        Places.initialize(getApplicationContext(), getString(R.string.api_key));
        Places.createClient(this);

        //Para inicializar a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //Para pedir permiso de ubicicación
        mPermisos = new Permisos();
        mPermisos.getPermisoUbicacion(this, true);

        //Para actualizar los datos de usuario cuando se llegue a ésta actividad
//        actualizarPreferencesUsuario();
    }

    @Override
    public void onResume() {
        super.onResume();
        //Se verifica el nivel de bateria del telefono celular
        Notificacion bateria = new Notificacion(getApplicationContext(), this);
        bateria.monitorearBateria();

        if (mGoogleMap != null) {
            //Para activar My Location layer
            actualizarUbicacionUI();
        }
//        actualizarPreferencesUsuario();

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

        if (mLatLngMarkerFavorito != null) {
            mostrarFavorito(mLatLngMarkerFavorito);
            mLatLngMarkerFavorito = null;
        } else
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
        mMapa.removerPolygonAnterior();
        mMapa.removerMarkerAnterior();

        mPolygonAnterior = polygon;
        mColorAnterior = mPolygonAnterior.getFillColor();

        Poligono poligono = new Poligono(this);
        poligono.getInfoPoligono(polygon);
        LatLng centroPoligono = poligono.getCentroPoligono(polygon);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centroPoligono, 16));

        mMapa.abrirFragmentoLugarSeleccionado(false, null, centroPoligono);
    }

    @Override
    public void onBackPressed() {
        FragmentoBuscarLugar fragmentoBuscarLugar = (FragmentoBuscarLugar) getSupportFragmentManager().findFragmentByTag("FragmentoBuscarLugar");
        if (fragmentoBuscarLugar == null) {
            mMapa.abrirFragmentoBuscarLugar();
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
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void mostrarUbicacionDispositivo() {
        UbicacionDispositivo mUbicacionDispositivo = new UbicacionDispositivo();
        mUbicacionDispositivo.getUbicacionDelDispositivo((isUbicacionObtenida, ubicacionObtenida) -> {
            if (isUbicacionObtenida) {
                mLastKnownLocation = ubicacionObtenida;
                mMapa.centrarMapa();
            } else {
                LatLng defaultLocation = new LatLng(20.703027011977582, -103.3884804);
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
            }
        }, mPermisos.getLocationPermissionGranted(), fusedLocationProviderClient, this);
    }

    public void mostrarFavorito(LatLng ubicacionFavorito) {
        mMapa.removerPolygonAnterior();
        mMapa.removerMarkerAnterior();

        // Para mostrar la ubicacion del objecto favorito en el mapa con un marcador.
        mMarkerAnterior = mGoogleMap.addMarker((new MarkerOptions().position(ubicacionFavorito)));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionFavorito, DEFAULT_ZOOM));

    }

    // Para mostrar los poligonos de todas las colonias al momento que se inicia el mapa.
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void mostrarColonias() {
        // Para obetener poligonos.
        Poligono mPoligono = new Poligono(this);
        mPoligono.getColonias(mGoogleMap);

        // listeners
        mGoogleMap.setOnPolygonClickListener(this);
    }
/*
    // Para actualizar los datos de usuario cuando se llegue a ésta actividad
    private void actualizarPreferencesUsuario() {
        UsuarioDAO usuarioDAO = new UsuarioDAO(getApplicationContext());
        usuario Usuario = Preferences.getSavedObjectFromPreference(getApplicationContext(), PREFERENCE_USUARIO, usuario.class);
        Usuario = usuarioDAO.readUsuarioPorCorreo(Usuario.getCorreoElectronico());
        Preferences.savePreferenceObjectRealm(getApplicationContext(), PREFERENCE_USUARIO, Usuario);
    }*/

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onTaskDone(Object... values) {
        Log.v("QUICKSTART", "ESTOY EN ACTIVITY");
        mGoogleMap.clear();
        Toast.makeText(getApplicationContext(), "Ruta creada!", Toast.LENGTH_SHORT).show();
        Ruta rutaMasSegura = (Ruta) values[0];

        Preferences.savePreferenceObject(getApplicationContext(), PREFERENCE_RUTASEGURA, rutaMasSegura);
        rutaMasSegura = Preferences.getSavedObjectFromPreference(this, PREFERENCE_RUTASEGURA, Ruta.class);

        for (int i = 0; i < rutaMasSegura.getNumeroCalles(); i++) {
            if(rutaMasSegura.getCallesRuta().get(i).getmUbicacion().getSeguridad().equals("Seguridad alta")){
                mGoogleMap.addPolyline(rutaMasSegura.getPolyline().get(i)).setColor(R.color.verde_poligono);
                Log.v("QUICKSTART", "Punto : " + i + " seguridad ALTA");
            }
            else if(rutaMasSegura.getCallesRuta().get(i).getmUbicacion().getSeguridad().equals("Seguridad media")) {
                mGoogleMap.addPolyline(rutaMasSegura.getPolyline().get(i)).setColor(R.color.amarillo_poligono);
                Log.v("QUICKSTART", "Punto : " + i + " seguridad MEDIA");
            }
            else if(rutaMasSegura.getCallesRuta().get(i).getmUbicacion().getSeguridad().equals("Seguridad baja")) {
                mGoogleMap.addPolyline(rutaMasSegura.getPolyline().get(i)).setColor(R.color.rojo_claro);
                Log.v("QUICKSTART", "Punto : " + i + " seguridad BAJA");
            }

            if(i == rutaMasSegura.getNumeroCalles() -1 ) {
                Log.v("QUICKSTART", "SIZE PUNTOS : " + rutaMasSegura.getPolyline().get(i).getPoints().size());
                mMapa.removerMarkerAnterior();
                mMarkerAnterior = mGoogleMap.addMarker((new MarkerOptions().position(rutaMasSegura.getPolyline().get(i).getPoints().get(1))));
            }
        }
    }
}