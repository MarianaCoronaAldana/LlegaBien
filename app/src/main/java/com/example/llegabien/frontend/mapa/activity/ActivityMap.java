package com.example.llegabien.frontend.mapa.activity;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;

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
import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.mapa.poligonos.Poligono;
import com.example.llegabien.backend.mapa.ubicacion.UbicacionDispositivo;
import com.example.llegabien.backend.notificacion.Notificacion;
import com.example.llegabien.backend.ruta.EvaluacionRuta;
import com.example.llegabien.backend.ruta.directions.RutaDirections;
import com.example.llegabien.backend.ruta.directions.UbicacionRuta;
import com.example.llegabien.backend.ruta.realm.ruta;
import com.example.llegabien.backend.ruta.realm.rutaDAO;
import com.example.llegabien.backend.usuario.usuario;
import com.example.llegabien.databinding.ActivityMapsBinding;
import com.example.llegabien.frontend.mapa.Mapa;
import com.example.llegabien.frontend.mapa.fragmento.FragmentoBuscarLugar;
import com.example.llegabien.frontend.mapa.fragmento.FragmentoLugarSeleccionado;
import com.example.llegabien.frontend.rutas.directionhelpers.FetchURL;
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

import java.time.LocalDateTime;
import java.time.ZoneId;
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


        // TODO: UTILIZACION DE API DIRECTIONS
// ->
        // JAVIER MINA
        //20.6674235372583, -103.31179439549422
        //20.67097726320246, -103.31441214692855

        //LAS AGUILAS
        //20.624252804065094, -103.40912012122419
        //20.622204544200045, -103.41392667663345

        place1 = new MarkerOptions().position(new LatLng(20.624252804065094, -103.40912012122419)).title("Location 1");
        place2 = new MarkerOptions().position(new LatLng(20.622204544200045, -103.41392667663345)).title("Location 2");
        mGoogleMap.addMarker(place1);
        mGoogleMap.addMarker(place2);

        //PARA AÑADIR RUTA A FAVORITOS
        //añadirRuta();

        //PRUEBA();
// ->
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void añadirRuta() {
        usuario Usuario = Preferences.getSavedObjectFromPreference(getApplicationContext(), PREFERENCE_USUARIO, usuario.class);
        ruta Ruta = new ruta();
        Ruta.setIdUsuario(Usuario.get_id());
        Ruta.setFUsoRuta(java.util.Date
                .from(LocalDateTime.now().atZone(ZoneId.systemDefault())
                        .toInstant()));
        Ruta.setPuntoInicio(place1.getPosition());
        Ruta.setPuntoDestino(place2.getPosition());
        rutaDAO rutaDAO = new rutaDAO(getApplicationContext());
        rutaDAO.anadirRuta(Ruta);
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

    //TODO: MOVER FUNCIONES DE AQUI
    private void PRUEBA() {
        // AQUI SE DEBEN DE PONER LOS LatLng de punto d epartida y de destino, así como la menra en que se prefiere vijar (a pie o bici)
        new FetchURL(ActivityMap.this).execute(generarUrlRuta(place1.getPosition(), place2.getPosition(), "walking"), "walking");
    }

    List<UbicacionRuta> rutaDistancias;
    List<List<UbicacionRuta>> rutasDistancias;

    private String generarUrlRuta(LatLng origen, LatLng dest, String directionMode) {
        // Origen de la ruta
        String str_origen = "origin=" + origen.latitude + "," + origen.longitude;
        // Destino de la ruta
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Modo de viaje
        String mode = "mode=" + directionMode;
        // Construyendo string
        String parameters = str_origen + "&" + str_dest + "&" + mode;
        // Formato de salida
        String output = "json";
        // Construyendo url final
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&alternatives=true" + "&key=" + getString(R.string.api_key);
        Log.v("QUICKSTART", "url: ");
        Log.v("QUICKSTART", url);
        return url;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onTaskDone(Object... values) {
        EvaluacionRuta evaluacionRuta = new EvaluacionRuta(mGoogleMap,this);
        evaluacionRuta.obtenerRuta((RutaDirections) values[0]);
    }

    /*
    private void obtenerUbicacionesRutas(List<List<ubicacionRuta>> rutasDistancias, HashMap<String, ubicacion> coloniasEncontradas, List<String> tipoUbicacion, int rutaMasLarga, boolean seSaltaRutaLarga) {

        List<Integer> cambiarTipo = new ArrayList<>();
        ubicacionRuta ubicacionRutaActual = new ubicacionRuta();

        int u = 0;
        ubicacion calle;
        for (int y = 0; y < rutasDistancias.size(); y++) {
            if (y != rutaMasLarga || seSaltaRutaLarga) {
                for (Map.Entry<String, ubicacionRuta> hashMap : rutasDistancias.get(y).entrySet()) {
                    ubicacionRutaActual = hashMap.getValue();
                    if (tipoUbicacion.get(u).equals("calle")) {
                        // LO DE LA CALLE ALLÁ
                        calle = null;
                        if (calle != null) {
                            ubicacionRutaActual.setmUbicacion(calle);
                        } else {
                            tipoUbicacion.set(u, "colonia");
                            cambiarTipo.add(u);
                            cambiarTipo(rutasDistancias, coloniasEncontradas, tipoUbicacion, rutaMasLarga, true);
                            //obtenerUbicacionesRutas(rutasDistancias, coloniasEncontradas, tipoUbicacion, rutaMasLarga, true);
                        }
                    }
                    if (tipoUbicacion.get(u).equals("colonia")) {
                        ubicacionRutaActual.setmUbicacion(coloniasEncontradas.get(ubicacionRutaActual.getmAddress().getSubLocality()));
                    }
                    hashMap.setValue(ubicacionRutaActual);
                    u++;
                }
            }
        }
    }

    private void cambiarTipo(List<List<ubicacionRuta>> rutasDistancias, HashMap<String, ubicacion> coloniasEncontradas, List<String> tipoUbicacion, int rutaMasLarga, boolean seSaltaRutaLarga) {

        UbicacionGeodicacion ubicacionGeodicacion = new UbicacionGeodicacion(this);
        UbicacionDAO mUbicacionDAO = new UbicacionDAO(this);
        List<ubicacion> colonias = mUbicacionDAO.obtenerColonias();
        ubicacion colonia;
        List<Integer> rutasInservibles = new ArrayList<>();
        HashMap<String, Integer> ubicacionesInservibles = new HashMap<>();

        List<Integer> cambiarTipo = new ArrayList<>();
        ubicacionRuta ubicacionRutaActual = new ubicacionRuta();

        ubicacion calle;
        for (int y = 0; y < rutasDistancias.size(); y++) {
            for (int o = 0; o < cambiarTipo.size(); o++) {

                ubicacionRutaActual = rutasDistancias.get(y).get().getValue();

                for (Map.Entry<String, ubicacionRuta> hashMap : rutasDistancias.get(y).entrySet()) {
                    ubicacionRutaActual = hashMap.getValue();
                    if (tipoUbicacion.get(o).equals("calle")) {
                        // LO DE LA CALLE ALLÁ
                        calle = null;
                        if (calle != null) {
                            ubicacionRutaActual.setmUbicacion(calle);
                        } else {
                            tipoUbicacion.set(o, "colonia");
                            cambiarTipo.add(o);
                            cambiarTipo();
                            //obtenerUbicacionesRutas(rutasDistancias, coloniasEncontradas, tipoUbicacion, rutaMasLarga, true);
                        }
                    }
                    if (tipoUbicacion.get(o).equals("colonia")) {
                        ubicacionRutaActual.setmUbicacion(coloniasEncontradas.get(ubicacionRutaActual.getmAddress().getSubLocality()));
                    }
                    hashMap.setValue(ubicacionRutaActual);
                }
            }
        }
    }*/
}