package com.example.llegabien.frontend.mapa.rutas.fragmento;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_RUTA_SEGURA;
import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;
import static io.realm.Realm.getApplicationContext;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.llegabien.R;
import com.example.llegabien.backend.app.Permisos;
import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.ruta.directions.Ruta;
import com.example.llegabien.backend.ruta.realm.ruta;
import com.example.llegabien.backend.ruta.realm.rutaDAO;
import com.example.llegabien.backend.ubicacion.UbicacionBusquedaAutocompletada;
import com.example.llegabien.backend.ubicacion.UbicacionDispositivo;
import com.example.llegabien.backend.ubicacion.UbicacionGeocodificacion;
import com.example.llegabien.backend.usuario.usuario;
import com.example.llegabien.frontend.app.fragmento.FragmentoPermisos;
import com.example.llegabien.frontend.mapa.Mapa;
import com.example.llegabien.frontend.mapa.activity.ActivityMap;
import com.example.llegabien.frontend.mapa.rutas.directionhelpers.FetchURL;
import com.example.llegabien.frontend.mapa.rutas.directionhelpers.TaskLoadedCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.SquareCap;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class FragmentoIndicaciones extends Fragment implements View.OnClickListener, TaskLoadedCallback {

    private Button mBtnPuntoPartida, mBtnPuntoDestino, mBtnPresionado;
    private ConstraintLayout mBtnTiempoBici, mBtnTiempoCaminando, mBtnComenzarNavegacion, mConsLytRutaDetalles;
    private TextView mTxtViewTiempoBici, mTxtViewTiempoCaminando, mTxtViewTiempoDetalles, mTxtViewDistanciaDetalles;
    private View mViewBici, mViewCaminar;
    private String mUbicacionBuscada;
    private String mDirectionMode = "bicycling";
    private final String mActivityAnterior;
    private GoogleMap mGoogleMap;
    private Mapa mMapa;

    private UbicacionBusquedaAutocompletada ubicacionBusquedaAutocompletada;
    private final ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onActivityResult(ActivityResult activityResult) {
                            int result = activityResult.getResultCode();
                            Intent data = activityResult.getData();
                            ubicacionBusquedaAutocompletada.verificarResultadoBusqueda((isUbicacionBuscadaObtenida, isUbicacionBuscadaenBD, ubicacionBuscada, ubicacionBuscadaString) -> {
                                if (isUbicacionBuscadaObtenida) {
                                    mBtnPresionado.setText(ubicacionBuscadaString);
                                    tomarDatosRuta();
                                }
                            }, result, data);
                        }
                    }
            );

    public FragmentoIndicaciones(String activityAnterior, String ubicacionBuscada) {
        mActivityAnterior = activityAnterior;
        if(activityAnterior == null)
            mUbicacionBuscada = ubicacionBuscada;
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_indicaciones, container, false);

        //wiring up
        mBtnComenzarNavegacion = root.findViewById(R.id.button_comenzar_indicaciones);
        Button btnIntercambiarPuntos = root.findViewById(R.id.button_intercambiarDireccion_partidaDestino);
        Button btnRegresar = root.findViewById(R.id.button_regresar_partidaDestino);
        mBtnPuntoPartida = root.findViewById(R.id.button_puntoPartida_indicaciones);
        mBtnPuntoDestino = root.findViewById(R.id.button_puntoDestino_indicaciones);
        mBtnTiempoCaminando = root.findViewById(R.id.button_tiempoCaminando_indicaciones);
        mBtnTiempoBici = root.findViewById(R.id.button_tiempoBici_indicaciones);
        mTxtViewTiempoBici = root.findViewById(R.id.textView_btnTiempoBici);
        mTxtViewTiempoCaminando = root.findViewById(R.id.textView_btnTiempoCaminando);
        mTxtViewTiempoDetalles = root.findViewById(R.id.textView_tiempo_detallesRuta);
        mTxtViewDistanciaDetalles = root.findViewById(R.id.textView_km_detallesRuta);
        mViewBici = root.findViewById(R.id.iconBici_btnTimepoBici);
        mViewCaminar = root.findViewById(R.id.iconCaminando_btnTimepoCaminando);
        ConstraintLayout btnCentrarMapa = root.findViewById(R.id.button_centrarMapa_indicaciones);
        mConsLytRutaDetalles = root.findViewById(R.id.consLyt_detallesRuta_indicaciones);

        //listeners
        mBtnTiempoCaminando.setOnClickListener(this);
        mBtnTiempoBici.setOnClickListener(this);
        mBtnPuntoDestino.setOnClickListener(this);
        mBtnPuntoPartida.setOnClickListener(this);
        btnCentrarMapa.setOnClickListener(this);
        mBtnComenzarNavegacion.setOnClickListener(this);
        btnIntercambiarPuntos.setOnClickListener(this);
        btnRegresar.setOnClickListener(this);

        // Para inicializar objeto de clase Mapa.
        if (getActivity() != null)
            mMapa = new Mapa((ActivityMap) requireActivity());

        // Para ver si la actividad anterior no es la de historial ruta.
        if (mActivityAnterior != null) {
            if (mActivityAnterior.equals("HISTORIAL_RUTAS")) {
                ruta ruta = Preferences.getSavedObjectFromPreference(requireActivity(), PREFERENCE_RUTA_SEGURA, ruta.class);
                UbicacionGeocodificacion ubicacionGeocodificacion = new UbicacionGeocodificacion(requireActivity());

                mBtnPuntoPartida.setText(ubicacionGeocodificacion.degeocodificarUbiciacion(Double.parseDouble(ruta.getPuntoInicio().get(0)), Double.parseDouble(ruta.getPuntoInicio().get(1))));
                mBtnPuntoDestino.setText(ubicacionGeocodificacion.degeocodificarUbiciacion(Double.parseDouble(ruta.getPuntoDestino().get(0)), Double.parseDouble(ruta.getPuntoDestino().get(1))));
                tomarDatosRuta();
            }
        }
        else {
           crearRutaConUbiBuscada();
        }
        return root;
    }

    // LISTENERS //
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_comenzar_indicaciones) {

            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            FragmentoNavegacion fragmentoNavegacion = new FragmentoNavegacion();
            fragmentTransaction.replace(R.id.fragmentContainerView_fragmentoLugares_activityMaps, fragmentoNavegacion, "FragmentoNavegacion").commit();
            fragmentTransaction.addToBackStack(null);

        } else if (view.getId() == R.id.button_puntoPartida_indicaciones || view.getId() == R.id.button_puntoDestino_indicaciones) {

            if (view.getId() == R.id.button_puntoPartida_indicaciones)
                mBtnPresionado = mBtnPuntoPartida;
            else if (view.getId() == R.id.button_puntoDestino_indicaciones)
                mBtnPresionado = mBtnPuntoDestino;

            ubicacionBusquedaAutocompletada = new UbicacionBusquedaAutocompletada();
            ubicacionBusquedaAutocompletada.inicializarIntent(requireActivity());
            activityResultLauncher.launch(ubicacionBusquedaAutocompletada.getIntent());

        } else if (view.getId() == R.id.button_tiempoCaminando_indicaciones) {

            mBtnTiempoCaminando.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.morado_oscuro));
            mTxtViewTiempoCaminando.setTextColor(this.requireActivity().getResources().getColor(R.color.blanco));
            mViewCaminar.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.blanco));

            mBtnTiempoBici.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.blanco));
            mTxtViewTiempoBici.setTextColor(this.requireActivity().getResources().getColor(R.color.negro));
            mViewBici.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.negro));
            mDirectionMode = "walking";
            tomarDatosRuta();

        } else if (view.getId() == R.id.button_tiempoBici_indicaciones) {

            mBtnTiempoCaminando.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.blanco));
            mTxtViewTiempoCaminando.setTextColor(this.requireActivity().getResources().getColor(R.color.negro));
            mViewCaminar.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.negro));

            mBtnTiempoBici.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.morado_oscuro));
            mTxtViewTiempoBici.setTextColor(this.requireActivity().getResources().getColor(R.color.blanco));
            mViewBici.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.blanco));
            mDirectionMode = "bicycling";
            tomarDatosRuta();

        } else if (view.getId() == R.id.button_intercambiarDireccion_partidaDestino) {

            if (mBtnPuntoPartida.getText() != null && mBtnPuntoDestino.getText() != null) {
                String puntoDestino = mBtnPuntoDestino.getText().toString();
                mBtnPuntoDestino.setText(mBtnPuntoPartida.getText().toString());
                mBtnPuntoPartida.setText(puntoDestino);
                tomarDatosRuta();
            }

        }else if(view.getId() == R.id.button_regresar_partidaDestino) {
            mMapa.abrirFragmentoBuscarLugar();
            mMapa.mostrarColonias();
        }

        else if (view.getId() == R.id.button_centrarMapa_indicaciones)
            mMapa.centrarMapa();

    }

    // Recibe el objeto Ruta que representa la ruta más segura, toma su información de distancia y tiempo.
    // Pone sus valores en los TxtViews correspondientes
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onTaskDone(Object... values) {
        Log.v("QUICKSTART", "ESTOY EN FRAGMENTO");
        Ruta rutaMasSegura = (Ruta) values[0];

        mBtnTiempoCaminando.setEnabled(true);
        mBtnTiempoBici.setEnabled(true);
        mBtnPuntoDestino.setEnabled(true);
        mBtnPuntoPartida.setEnabled(true);

        if (rutaMasSegura != null) {
            mTxtViewTiempoDetalles.setText(rutaMasSegura.getTiempoTotalDirections()
                    .replace("hours", "horas")
                    .replace("mins", "min")
                    .replace("hour", "hora"));
            mTxtViewDistanciaDetalles.setText(rutaMasSegura.getDistanciaTotalDirections());
            if (this.getActivity() != null) {
                ActivityMap mActivityMap = (ActivityMap) requireActivity();
                this.mGoogleMap = mActivityMap.getGoogleMap();

                mGoogleMap.clear();
                Toast.makeText(getApplicationContext(), "Ruta creada!", Toast.LENGTH_SHORT).show();

                // Se guarda ruta en Preferences.
                Preferences.savePreferenceObject(this.requireActivity(), PREFERENCE_RUTA_SEGURA, rutaMasSegura);
                rutaMasSegura = Preferences.getSavedObjectFromPreference(this.requireActivity(), PREFERENCE_RUTA_SEGURA, Ruta.class);

                if (rutaMasSegura != null) {
                    setValoresRutaPolylines(rutaMasSegura);
                    centrarRutaEnMapa(rutaMasSegura);
                    mBtnComenzarNavegacion.setVisibility(View.VISIBLE);
                    mConsLytRutaDetalles.setVisibility(View.VISIBLE);
                }
            }
        }
        else
            Toast.makeText(this.requireActivity(), "¡No hay rutas disponibles!", Toast.LENGTH_SHORT).show();
    }

    // OTRAS FUNCIONES //
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void tomarDatosRuta() {
        if(mBtnPuntoPartida.getText() != null || mBtnPuntoDestino.getText() != null) {
            LatLng origen = obtenerCoordenadas(mBtnPuntoPartida.getText().toString());
            LatLng destino = obtenerCoordenadas(mBtnPuntoDestino.getText().toString());
            if (origen != null && destino != null) {
                if (!origen.equals(destino)) {
                    anadirRutaBD(origen, destino);
                    Toast.makeText(getApplicationContext(), "Creando ruta....", Toast.LENGTH_SHORT).show();

                    mBtnTiempoCaminando.setEnabled(false);
                    mBtnTiempoBici.setEnabled(false);
                    mBtnPuntoDestino.setEnabled(false);
                    mBtnPuntoPartida.setEnabled(false);

                    new FetchURL(this, this.requireActivity().getApplicationContext()).execute(generarUrlRuta(origen, destino), mDirectionMode);
                } else
                    Toast.makeText(getApplicationContext(), "Punto de origen y partida no pueden ser los mismos.", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "Ingresa punto de origen y punto destino.", Toast.LENGTH_SHORT).show();
        }
    }


    private LatLng obtenerCoordenadas(String adress) {
        UbicacionGeocodificacion ubicacionGeodicacion = new UbicacionGeocodificacion(this.requireActivity().getApplicationContext());
        Address ubicacionGeocodificada = ubicacionGeodicacion.geocodificarUbiciacion(adress);
        if (ubicacionGeocodificada != null)
            return new LatLng(ubicacionGeocodificada.getLatitude(), ubicacionGeocodificada.getLongitude());
        return null;
    }

    private String generarUrlRuta(LatLng origen, LatLng dest) {
        // Origen de la ruta
        String str_origen = "origin=" + origen.latitude + "," + origen.longitude;
        // Destino de la ruta
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Modo de viaje
        String mode = "mode=" + mDirectionMode;
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
    private void anadirRutaBD(LatLng origen, LatLng destino) {
        usuario Usuario = Preferences.getSavedObjectFromPreference(requireActivity().getApplicationContext(), PREFERENCE_USUARIO, usuario.class);
        ruta Ruta = new ruta();
        Ruta.setIdUsuario(Usuario.get_id());
        Ruta.setFUsoRuta(java.util.Date
                .from(LocalDateTime.now().atZone(ZoneId.systemDefault())
                        .toInstant()));
        Ruta.setPuntoInicio(origen);
        Ruta.setPuntoDestino(destino);
        rutaDAO rutaDAO = new rutaDAO(getApplicationContext());
        rutaDAO.verificarExistenciaRuta(Ruta);
    }

    private void mostrarRutaEnMapa(int i, int color, Ruta ruta) {
        if (i == 0) {
            this.mGoogleMap.addPolyline(ruta.getPolyline().get(i)
                    .color(color)
                    .startCap(new SquareCap())
                    .width(10));
        }
        else {
            this.mGoogleMap.addPolyline(ruta.getPolyline().get(i)
                    .color(color)
                    .width(10));
        }
    }

    private void setValoresRutaPolylines(Ruta rutaMasSegura) {
        // Para establecer el color de cada calle dependiendo seguridad.
        for (int i = 0; i < rutaMasSegura.getNumeroCalles(); i++) {
            if (i == 0) {
                Drawable vectorDrawable = ContextCompat.getDrawable(this.requireActivity(), R.drawable.bkgd_icon_puntopartida);
                vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
                Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                vectorDrawable.draw(canvas);
               this.mGoogleMap.addMarker((new MarkerOptions()
                       .position(rutaMasSegura.getPolyline().get(0).getPoints().get(0))
                       .icon(BitmapDescriptorFactory.fromBitmap(bitmap))));
            }

            switch (rutaMasSegura.getCallesRuta().get(i).getmUbicacion().getSeguridad()) {
                case "Seguridad alta":
                    mostrarRutaEnMapa(i, getResources().getColor(R.color.verde_oscuro_poligono), rutaMasSegura);
                    Log.v("QUICKSTART", "Punto " + i + ": seguridad ALTA.");
                    break;
                case "Seguridad media":
                    mostrarRutaEnMapa(i, getResources().getColor(R.color.amarillo_oscuro_poligono), rutaMasSegura);
                    Log.v("QUICKSTART", "Punto " + i + ": seguridad MEDIA.");
                    break;
                case "Seguridad baja":
                    mostrarRutaEnMapa(i, getResources().getColor(R.color.rojo_oscuro_poligono), rutaMasSegura);
                    Log.v("QUICKSTART", "Punto " + i + ": seguridad BAJA.");
                    break;
            }

            if (i == rutaMasSegura.getNumeroCalles() - 1)
                this.mGoogleMap.addMarker((new MarkerOptions().position(rutaMasSegura.getPolyline().get(i).getPoints().get(1))));
        }
    }

    private void centrarRutaEnMapa(Ruta rutaMasSegura){
        LatLng latLngPuntoPartida = rutaMasSegura.getPolyline().get(0).getPoints().get(0);
        LatLng latLngPuntoDestino = rutaMasSegura.getPolyline().get(rutaMasSegura.getNumeroCalles() - 1).getPoints()
                .get(rutaMasSegura.getPolyline().get(rutaMasSegura.getNumeroCalles() - 1).getPoints().size() - 1);

        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                .include((new LatLng(latLngPuntoPartida.latitude, latLngPuntoPartida.longitude)))
                .include((new LatLng(latLngPuntoDestino.latitude, latLngPuntoDestino.longitude)))
                .build();

        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 250));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void crearRutaConUbiBuscada() {
        Permisos permisos = new Permisos();
        permisos.getPermisoUbicacion(requireActivity(), false);
        if (permisos.getLocationPermissionGranted()) {
            FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

            UbicacionDispositivo mUbicacionDispositivo = new UbicacionDispositivo();
            mUbicacionDispositivo.getUbicacionDelDispositivo((isUbicacionObtenida, ubicacionObtenida) -> {
                if (isUbicacionObtenida) {
                    UbicacionGeocodificacion ubicacionGeocodificacion = new UbicacionGeocodificacion(requireActivity());
                    String Ubicacion = ubicacionGeocodificacion.degeocodificarUbiciacion(ubicacionObtenida.getLatitude(),
                            ubicacionObtenida.getLongitude());
                    
                    mBtnPuntoPartida.setText(Ubicacion);
                    // Para mostrar la direccion del lugar que se buscó en Botón.
                    if (mUbicacionBuscada != null) {
                        mBtnPuntoDestino.setText(mUbicacionBuscada);
                        tomarDatosRuta();
                    }
                }
            }, true, fusedLocationProviderClient, requireActivity());
        }else {
            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            FragmentoPermisos fragmentoPermisos = new FragmentoPermisos();
            fragmentTransaction.add(R.id.fragmentContainerView_reportes, fragmentoPermisos).commit();
        }
    }


}