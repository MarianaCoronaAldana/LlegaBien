package com.example.llegabien.frontend.mapa.activity;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;

import android.content.pm.PackageManager;
import android.graphics.Color;
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
import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.mapa.poligonos.Poligono;
import com.example.llegabien.backend.mapa.ubicacion.UbicacionDispositivo;
import com.example.llegabien.backend.mapa.ubicacion.UbicacionGeodicacion;
import com.example.llegabien.backend.ruta.directions.rutaDirections;
import com.example.llegabien.backend.usuario.UsuarioDAO;
import com.example.llegabien.backend.usuario.usuario;
import com.example.llegabien.databinding.ActivityMapsBinding;
import com.example.llegabien.frontend.mapa.fragmento.FragmentoBuscarLugar;
import com.example.llegabien.frontend.mapa.fragmento.FragmentoLugarSeleccionado;
import com.example.llegabien.frontend.mapa.Mapa;
import com.example.llegabien.frontend.rutas.directionhelpers.FetchURL;
import com.example.llegabien.frontend.rutas.directionhelpers.TaskLoadedCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;

import java.util.ArrayList;
import java.util.List;


public class ActivityMap extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnPolygonClickListener, TaskLoadedCallback {

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

    private MarkerOptions place1, place2;

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

        //Para pedir permiso de ubicicaci??n
        mPermisos = new Permisos();
        mPermisos.getPermisoUbicacion(this, true);

        //Para actualizar los datos de usuario cuando se llegue a ??sta actividad
        actualizarPreferencesUsuario();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mGoogleMap != null) {
            //Para activar My Location layer
            actualizarUbicacionUI();
        }
        actualizarPreferencesUsuario();

    }

    //FUNCIONES LISTENERS//

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
        //Para mostrar colonias con c??digo de colores
        mostrarColonias();

        //Para activar My Location layer
        actualizarUbicacionUI();


        if (mLatLngMarkerFavorito != null) {
            mostrarFavorito(mLatLngMarkerFavorito);
            mLatLngMarkerFavorito = null;
        }

        else
            //Para obtener la ubicaci??n actual del dispositivo y ubicacion buscada (si existe)
            mostrarUbicacionDispositivo();


        // TODO: UTILIZACION DE API DIRECTIONS
// ->
        place1 = new MarkerOptions().position(new LatLng(20.6674235372583, -103.31179439549422)).title("Location 1");
        place2 = new MarkerOptions().position(new LatLng(20.67097726320246, -103.31441214692855)).title("Location 2");
        mGoogleMap.addMarker(place1);
        mGoogleMap.addMarker(place2);

        PRUEBA();
// ->
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

    // Para actualizar los datos de usuario cuando se llegue a ??sta actividad
    private void actualizarPreferencesUsuario() {
        UsuarioDAO usuarioDAO = new UsuarioDAO(getApplicationContext());
        usuario Usuario = Preferences.getSavedObjectFromPreference(getApplicationContext(), PREFERENCE_USUARIO, usuario.class);
        Usuario = usuarioDAO.readUsuarioPorCorreo(Usuario.getCorreoElectronico());
        Preferences.savePreferenceObjectRealm(getApplicationContext(), PREFERENCE_USUARIO, Usuario);
    }

    //TODO: MOVER FUNCIONES DE AQUI
    private void PRUEBA(){
        // AQUI SE DEBEN DE PONER LOS LatLng de punto d epartida y de destino, as?? como la menra en que se prefiere vijar (a pie o bici)
        new FetchURL(ActivityMap.this).execute(getUrl(place1.getPosition(), place2.getPosition(), "walking"), "walking");
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters +"&alternatives=true" +"&key=" + getString(R.string.api_key);
        Log.v("QUICKSTART", "url: ");
        Log.v("QUICKSTART", url);
        return url;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onTaskDone(Object... values) {
        UbicacionGeodicacion ubicacionGeodicacion = new UbicacionGeodicacion(this);

        rutaDirections directionsObtenidas =  (rutaDirections) values[0];

        List<PolylineOptions> rutasObtenidas = directionsObtenidas.getRutasDirectionsPolylineOptions();
        List<PolylineOptions> ruta = new ArrayList<>();
        List<List<PolylineOptions>> rutas = new ArrayList<>();

        // Para los puntos medios
        List<LatLng> rutaPuntosMedios = new ArrayList<>();
        List<List<LatLng>> rutasPorPuntosMedios = new ArrayList<>();
        List<String> rutaPuntosMediosNombres = new ArrayList();
        List<List<String>> rutasPorPuntosMediosNombres = new ArrayList();

        List<Integer> colores = new ArrayList<>();
        colores.add(Color.BLUE);
        colores.add(Color.WHITE);
        colores.add(Color.GREEN);
        int color=0;

        // Recorre rutas
        for (int i=0; i<rutasObtenidas.size(); i++){
            List<LatLng> points = rutasObtenidas.get(i).getPoints();
            // Recorre los puntos de una ruta
            for (int o=0; o<points.size(); o++) {
                if(color>2)
                    color=0;
                rutaPuntosMediosNombres = new ArrayList();
                PolylineOptions lineOptions = new PolylineOptions();
                lineOptions.add(points.get(o));

                if(o+1<points.size()) {
                    lineOptions.add(points.get(o + 1));

                    //AQUI SE DEBEN DE TOMAR LOS PUNTOS Y SABER SU SEGURIDAD, EN BASE A ESO PONERLE EL COLOR A LA POLYLINE
                    MarkerOptions center = new MarkerOptions().position(LatLngBounds.builder().include(points.get(o)).include(points.get(o+1)).build().getCenter()).title("Location center " + i+","+o);
                    mGoogleMap.addMarker(center);
                    rutaPuntosMedios.add(center.getPosition());
                    String nombreCalle = ubicacionGeodicacion.degeocodificarUbiciacion(center.getPosition().latitude, center.getPosition().longitude);
                    rutaPuntosMediosNombres.add(nombreCalle);
                    Log.v("QUICKSTART", "Nombre calle: " + nombreCalle);
                }

                mGoogleMap.addPolyline(lineOptions).setColor(colores.get(color));

                ruta.add(lineOptions);
                color++;
            }
            rutas.add(ruta);
            rutasPorPuntosMedios.add(rutaPuntosMedios);
            rutasPorPuntosMediosNombres.add(rutaPuntosMediosNombres);
            Log.v("QUICKSTART", "DISTANCIA, TIEMPO: " + directionsObtenidas.getDistancia().get(i) + " , " + directionsObtenidas.getDuracion().get(i));
            //mGoogleMap.addPolyline(routes.get(i)).setColor(Color.BLUE);
        }
        directionsObtenidas.setRutas(rutas);
    }

    //TODO: HASTA AQUI
}