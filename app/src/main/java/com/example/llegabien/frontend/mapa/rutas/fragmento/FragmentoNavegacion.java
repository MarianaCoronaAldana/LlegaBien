package com.example.llegabien.frontend.mapa.rutas.fragmento;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_RUTA_SEGURA;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.llegabien.R;
import com.example.llegabien.backend.app.Permisos;
import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.ruta.directions.Ruta;
import com.example.llegabien.backend.ruta.directions.UbicacionRuta;
import com.example.llegabien.backend.ubicacion.UbicacionGeocodificacion;
import com.example.llegabien.frontend.app.Utilidades;
import com.example.llegabien.frontend.app.fragmento.FragmentoPermisos;
import com.example.llegabien.frontend.botonEmergencia.fragmento.FragmentoBotonEmergencia;
import com.example.llegabien.frontend.mapa.Mapa;
import com.example.llegabien.frontend.mapa.activity.ActivityMap;
import com.example.llegabien.frontend.reportes.activity.ActivityReportes;
import com.google.android.gms.maps.GoogleMap;

public class FragmentoNavegacion extends Fragment implements View.OnClickListener, View.OnTouchListener, GoogleMap.OnCameraMoveStartedListener{

    private ObjectAnimator mScaleDown = null;
    private ConstraintLayout mBtnSubirReporte, mBtnAdvertencia, mBtnCentrarMapa, mConsLytIndicaciones;
    private TextView mTxtViewCalleActual, mTxtViewCalleSiguiente, mTxtViewUbicacionSeguridad;
    private View mIconSeguridad;
    private FragmentTransaction mFragmentTransaction;
    private UbicacionGeocodificacion mUbicacionGeocodificacion;
    private Ruta mRutaSegura;
    private int mNumCalleActual = -1;
    private Mapa mMapa;
    private GoogleMap mGoogleMap;
    boolean mIsMapaDragged = false;

    public FragmentoNavegacion() {
    }

    @SuppressLint({"ClickableViewAccessibility"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_navegacion, container, false);

        // Se obtiene la ruta segura de Preferences.
        mRutaSegura = Preferences.getSavedObjectFromPreference(this.requireActivity(), PREFERENCE_RUTA_SEGURA, Ruta.class);

        // Se inicializa para utilizarlo en OnLocationChanged.
        mUbicacionGeocodificacion = new UbicacionGeocodificacion(this.requireActivity());

        // Para utilizar el mapa de la actividad ActivityMap.
        ActivityMap activityMap = (ActivityMap) getActivity();
        if (activityMap != null) {
            mMapa = new Mapa(activityMap);
            mGoogleMap = activityMap.getGoogleMap();
        }

        // Para cambiar el color de la status bar.
        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setNavigationBarColor(getResources().getColor(R.color.morado_claro));

        //wiring up
        Button btnEmergencia = root.findViewById(R.id.button_emergencia_navegacion);
        mBtnSubirReporte = root.findViewById(R.id.button_subirReporte_navegacion);
        mBtnAdvertencia = root.findViewById(R.id.button_advertencia_navegacion);
        mBtnCentrarMapa = root.findViewById(R.id.button_centrarMapa_navegacion);
        mTxtViewCalleActual = root.findViewById(R.id.textview_nombreCalleActual_indicaciones);
        mTxtViewCalleSiguiente = root.findViewById(R.id.textview_nombreCalleSiguiente_indicaciones);
        mTxtViewUbicacionSeguridad = root.findViewById(R.id.textView_seguridad_navegacion);
        mConsLytIndicaciones = root.findViewById(R.id.consLyt_indicaciones_navegacion);
        mIconSeguridad = root.findViewById(R.id.icon_seguridad_navegacion);
        TextView mTxtViewTiempoDistanciaRuta = root.findViewById(R.id.textview_tiempo_distancia_navegacion);

        //listeners
        btnEmergencia.setOnTouchListener(this);
        mBtnSubirReporte.setOnClickListener(this);
        mBtnAdvertencia.setOnClickListener(this);
        mBtnCentrarMapa.setOnClickListener(this);
        mGoogleMap.setOnCameraMoveStartedListener(this);

        // Para mostarar el tiempo y distancia total de la ruta.
        String tiempoDistanciaRuta = mRutaSegura.getDistanciaTotalDirections() + " ・ " + mRutaSegura.getTiempoTotalDirections();
        mTxtViewTiempoDistanciaRuta.setText(tiempoDistanciaRuta);

        // Para empezar animación del boton de emergencia.
        this.mScaleDown = ObjectAnimator.ofPropertyValuesHolder(
                btnEmergencia,
                PropertyValuesHolder.ofFloat("scaleX", 0.6f),
                PropertyValuesHolder.ofFloat("scaleY", 0.6f)
        );
        Utilidades.startAnimacionBtnEmergencia(mScaleDown);

        // Para verificar que la ruta pase por zonas inseguras.
        mFragmentTransaction = this.requireActivity().getSupportFragmentManager().beginTransaction();
        if (mRutaSegura.getDelitosZonasInseguras() != null) {
            FragmentoConsejosRuta fragmentoConsejosRuta = new FragmentoConsejosRuta();
            mFragmentTransaction.add(R.id.fragmentContainerView_navegacion, fragmentoConsejosRuta, "FragmentoConsejosRuta").commit();
        }

        return root;
    }

    @SuppressLint({"MissingPermission"})
    @Override
    public void onResume() {
        super.onResume();
        this.mScaleDown.start();
        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,
                1000,
                0,
                locationListener);
    }

    //LISTENERS//

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mFragmentTransaction != null)
            mFragmentTransaction = this.requireActivity().getSupportFragmentManager().beginTransaction();
        // Cuando el boton es presionado.
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Permisos mPermisos = new Permisos();
            mPermisos.getPermisoUbicacion(requireActivity(), false);
            if (mPermisos.getLocationPermissionGranted()) {
                this.mScaleDown.end();

                mBtnAdvertencia.setClickable(false);
                mBtnCentrarMapa.setClickable(false);
                mBtnSubirReporte.setClickable(false);

                // Para mostrar el ProgressCircle.
                FragmentoBotonEmergencia fragmentoBotonEmergencia = new FragmentoBotonEmergencia(mBtnSubirReporte, mBtnCentrarMapa, mBtnAdvertencia);
                mFragmentTransaction.add(R.id.fragmentContainerView_navegacion, fragmentoBotonEmergencia, "FragmentoBotonEmergencia").commit();
            } else {
                FragmentoPermisos fragmentoPermisos = new FragmentoPermisos();
                mFragmentTransaction.add(R.id.fragmentContainerView_reportes, fragmentoPermisos).commit();
            }
        }

        // Cuando el boton no está presionado.
        else if (event.getAction() == MotionEvent.ACTION_UP) {
            this.mScaleDown.start();
            // Para esconder el ProgressCircle.
            Fragment fragment = requireActivity().getSupportFragmentManager().findFragmentByTag("FragmentoBotonEmergencia");
            if (fragment != null)
                mFragmentTransaction.remove(fragment).commit();
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_subirReporte_navegacion)
            startActivity(new Intent(requireActivity(), ActivityReportes.class));
        else if (view.getId() == R.id.button_centrarMapa_navegacion)
            mIsMapaDragged = false;
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            if (mNumCalleActual == mRutaSegura.getCallesRuta().size())
                mNumCalleActual = -1;

            if (!mIsMapaDragged)
                mMapa.actualizarCamaraByUbicacionDispositivo(location);

            UbicacionRuta mCalleSiguiente = mRutaSegura.getCallesRuta().get(mNumCalleActual + 1);

            Location puntoInicioCalleSiguiente = new Location(LocationManager.GPS_PROVIDER);
            puntoInicioCalleSiguiente.setLatitude(mCalleSiguiente.getLatPuntoInicio());
            puntoInicioCalleSiguiente.setLongitude(mCalleSiguiente.getLngPuntoInicio());

            if (location.distanceTo(puntoInicioCalleSiguiente) <= 10) {
                Toast.makeText(requireActivity(), "MENOS DE 10 METROS", Toast.LENGTH_SHORT).show();
                if (mNumCalleActual == -1 && mConsLytIndicaciones.getVisibility() == View.GONE)
                    mConsLytIndicaciones.setVisibility(View.VISIBLE);


                mNumCalleActual++;
                UbicacionRuta mCalleActual = mRutaSegura.getCallesRuta().get(mNumCalleActual);

                mTxtViewCalleActual.setText(mUbicacionGeocodificacion
                        .degeocodificarUbiciacion(mCalleActual.getLatPuntoInicio(), mCalleActual.getLngPuntoInicio())
                        .split("\\d+", 2)[0].trim());

                mTxtViewUbicacionSeguridad.setText(mCalleActual.getmUbicacion().getSeguridad());
                Utilidades.setColoIconSeguridad(mCalleActual.getmUbicacion().getSeguridad(), mIconSeguridad, requireContext());


                if (mNumCalleActual == mRutaSegura.getNumeroCalles() - 1)
                    mTxtViewCalleSiguiente.setText("Punto de destino");
                else {
                    mCalleSiguiente = mRutaSegura.getCallesRuta().get(mNumCalleActual + 1);
                    mTxtViewCalleSiguiente.setText(mUbicacionGeocodificacion
                            .degeocodificarUbiciacion(mCalleSiguiente.getLatPuntoInicio(), mCalleSiguiente.getLngPuntoInicio())
                            .split("\\d+", 2)[0].trim());
                }
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    public void onCameraMoveStarted(int i) {
        if (i ==REASON_GESTURE) {
            mIsMapaDragged = true;
            Toast.makeText(getActivity(), "El usuario arrastró el mapa.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}