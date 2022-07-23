package com.example.llegabien.frontend.rutas;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Guideline;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.example.llegabien.R;
import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.mapa.ubicacion.UbicacionGeocodificacion;
import com.example.llegabien.backend.ruta.realm.ruta;
import com.example.llegabien.backend.ruta.realm.rutaDAO;
import com.example.llegabien.backend.usuario.usuario;
import com.example.llegabien.frontend.mapa.fragmento.FragmentoIndicaciones;

import org.bson.types.ObjectId;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Objects;

import io.realm.RealmResults;

public class ActivityMostrarRutas extends AppCompatActivity implements View.OnClickListener {
    private ConstraintLayout mConsLytScrollView;
    private View mViewAuxiliar;
    private Guideline mGuideline10Porciento, mGuideline90Porciente;
    private RealmResults<ruta> rutas;
   // private final UbicacionGeocodificacion ubicacionGeocodificacion = new UbicacionGeocodificacion(getApplicationContext());

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_rutas);

        //wiring up
        mConsLytScrollView = findViewById(R.id.consLyt_scrollView_mostrar_rutas);
        mViewAuxiliar = findViewById(R.id.view2_mostrar_rutas);
        mGuideline10Porciento = findViewById(R.id.guideline1_textView_editView_scrollView_mostrar_rutas);
        mGuideline90Porciente = findViewById(R.id.guideline2_textView_editView_scrollView_mostrar_rutas);
        Button mBtnRegresar = findViewById(R.id.button_regresar_mostrar_rutas);

        //listeners
        mBtnRegresar.setOnClickListener(this);

        usuario usuario = Preferences.getSavedObjectFromPreference(this, PREFERENCE_USUARIO, com.example.llegabien.backend.usuario.usuario.class);
        rutaDAO rutaDAO = new rutaDAO(this);
        rutas = rutaDAO.obtenerRutasPorUsuario(usuario.get_id());

        // Para crear la vista de las rutas creadas por el usuario
        crearVistaRutas();
    }

    // FUNCIONES LISTENER //

    @SuppressLint("ResourceType")
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_regresar_mostrar_rutas){
            finish();
        }
        else{
            //TODO: AQUI SE HACE LO QUE SE TIENE QUE HACER CUANDO SE ELIGE UNA RUTA
            Log.v("QUICKSTART", "ME HICIERON CLICK :D " + view.getContentDescription());

            rutaDAO RutaDao =  new rutaDAO(this);
            ObjectId idRuta = new ObjectId(String.valueOf(view.getContentDescription()));
            ruta RutaElegida = RutaDao.obtenerRutaPorId(idRuta);
            UbicacionGeocodificacion ubicacionGeocodificacion = new UbicacionGeocodificacion(this);

            String puntoPartida = ubicacionGeocodificacion.degeocodificarUbiciacion(
                    Double.parseDouble(RutaElegida.getPuntoInicio().get(0).toString()),
                    Double.parseDouble(RutaElegida.getPuntoInicio().get(1).toString()));

            String puntoDestino = ubicacionGeocodificacion.degeocodificarUbiciacion(
                    Double.parseDouble(RutaElegida.getPuntoDestino().get(0).toString()),
                    Double.parseDouble(RutaElegida.getPuntoDestino().get(1).toString()));

            Log.v("QUICKSTART", "pUNTO PARTIDA: " + puntoPartida);
            Log.v("QUICKSTART", "PUNTO DESTINO: " + puntoDestino);

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            FragmentoIndicaciones fragmentoIndicaciones = new FragmentoIndicaciones(puntoPartida, puntoDestino);
            fragmentTransaction.add(R.id.fragmentContainerView_fragmentoLugares_activityMaps, fragmentoIndicaciones).commit();

            // Para abrir fragmento "Lugar seleccionado".
            Intent intent = new Intent(this, FragmentoIndicaciones.class);
            startActivity(intent);
        }

    }

    // OTRAS FUNCIONES //

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void crearVistaRutas() {
        ConstraintSet constraintSet = new ConstraintSet();
        int size = rutas.size();
        UbicacionGeocodificacion ubicacionGeocodificacion = new UbicacionGeocodificacion(this);
        if(size > 0 ) {
            for (int i = 0; i < size; i++) {
                // ConstraintLayout principal
                constraintSet.clone(mConsLytScrollView);
                ConstraintLayout consLytPrincipalReporte = new ConstraintLayout(this);
                consLytPrincipalReporte.setId(View.generateViewId());
                consLytPrincipalReporte.setBackground(this.getResources().getDrawable(R.drawable.bkgd_esquinas_redondeadas));
                consLytPrincipalReporte.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.morado_claro));

                consLytPrincipalReporte.setClickable(true);
                consLytPrincipalReporte.setOnClickListener(this);
                consLytPrincipalReporte.setContentDescription(Objects.requireNonNull(rutas.get(i)).get_id().toString());

                mConsLytScrollView.addView(consLytPrincipalReporte);
                constraintSet.connect(consLytPrincipalReporte.getId(), ConstraintSet.START, mGuideline10Porciento.getId(), ConstraintSet.START, 0);
                constraintSet.connect(consLytPrincipalReporte.getId(), ConstraintSet.END, mGuideline90Porciente.getId(), ConstraintSet.END, 0);
                constraintSet.connect(consLytPrincipalReporte.getId(), ConstraintSet.TOP, mViewAuxiliar.getId(), ConstraintSet.BOTTOM, 0);
                constraintSet.connect(consLytPrincipalReporte.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

                constraintSet.constrainWidth(consLytPrincipalReporte.getId(), ConstraintSet.PARENT_ID);
                constraintSet.constrainHeight(consLytPrincipalReporte.getId(), ConstraintSet.PARENT_ID);

                constraintSet.setDimensionRatio(consLytPrincipalReporte.getId(), "7:2");

                constraintSet.setVerticalBias(consLytPrincipalReporte.getId(), 0.0f);
                //Fin de ConstraintLayout principal

                // Separador
                View viewSeparadorFinal = new View(new ContextThemeWrapper(this, R.style.ViewSeparadorAuxiliar));

                viewSeparadorFinal.setId(View.generateViewId());

                mConsLytScrollView.addView(viewSeparadorFinal);

                constraintSet.connect(viewSeparadorFinal.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
                constraintSet.connect(viewSeparadorFinal.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
                constraintSet.connect(viewSeparadorFinal.getId(), ConstraintSet.TOP, consLytPrincipalReporte.getId(), ConstraintSet.BOTTOM, 0);
                constraintSet.connect(viewSeparadorFinal.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

                constraintSet.setDimensionRatio(viewSeparadorFinal.getId(), "15:1");

                constraintSet.setVerticalBias(viewSeparadorFinal.getId(), 0.0f);
                //Fin de Separador

                mViewAuxiliar = viewSeparadorFinal;

                constraintSet.applyTo(mConsLytScrollView);
                // Fin ConstraintLayout principal

                // Se cambia de ConstraintLayout
                constraintSet.clone(consLytPrincipalReporte);

                // Guideline "5 porciento"
                Guideline guideline5Porciento = new Guideline(this);
                guideline5Porciento.setId(View.generateViewId());

                constraintSet.constrainWidth(guideline5Porciento.getId(), ConstraintSet.PARENT_ID);
                constraintSet.constrainHeight(guideline5Porciento.getId(), ConstraintSet.PARENT_ID);
                constraintSet.setGuidelinePercent(guideline5Porciento.getId(), 0.05f);

                constraintSet.create(guideline5Porciento.getId(), ConstraintSet.VERTICAL_GUIDELINE);
                // Fin de guideline "5 porciento"

                // Guideline "95 porciento"
                Guideline guideline95Porciento = new Guideline(this);
                guideline95Porciento.setId(View.generateViewId());

                constraintSet.constrainWidth(guideline95Porciento.getId(), ConstraintSet.PARENT_ID);
                constraintSet.constrainHeight(guideline95Porciento.getId(), ConstraintSet.PARENT_ID);
                constraintSet.setGuidelinePercent(guideline95Porciento.getId(), 0.95f);

                constraintSet.create(guideline95Porciento.getId(), ConstraintSet.VERTICAL_GUIDELINE);
                // Fin de guideline "95 porciento"

                // Guideline "45 porciento"
                Guideline guideline45Porciento = new Guideline(this);
                guideline45Porciento.setId(View.generateViewId());

                constraintSet.constrainWidth(guideline45Porciento.getId(), ConstraintSet.PARENT_ID);
                constraintSet.constrainHeight(guideline45Porciento.getId(), ConstraintSet.PARENT_ID);
                constraintSet.setGuidelinePercent(guideline45Porciento.getId(), 0.45f);

                constraintSet.create(guideline45Porciento.getId(), ConstraintSet.VERTICAL_GUIDELINE);
                // Fin de guideline "45 porciento"

                // Guideline "50 porciento"
                Guideline guideline50Porciento = new Guideline(this);
                guideline50Porciento.setId(View.generateViewId());

                constraintSet.constrainWidth(guideline50Porciento.getId(), ConstraintSet.PARENT_ID);
                constraintSet.constrainHeight(guideline50Porciento.getId(), ConstraintSet.PARENT_ID);
                constraintSet.setGuidelinePercent(guideline50Porciento.getId(), 0.50f);

                constraintSet.create(guideline50Porciento.getId(), ConstraintSet.VERTICAL_GUIDELINE);
                // Fin de guideline "50 porciento"


                View viewSeparador_2 = new View(new ContextThemeWrapper(this, R.style.ViewSeparador));
                viewSeparador_2.setId(View.generateViewId());

                // Separador 1
                View viewSeparador_1 = new View(new ContextThemeWrapper(this, R.style.ViewSeparador));
                viewSeparador_1.setId(View.generateViewId());
                viewSeparador_1.setBackgroundColor(this.getResources().getColor(R.color.morado_claro));

                consLytPrincipalReporte.addView(viewSeparador_1);

                constraintSet.connect(viewSeparador_1.getId(), ConstraintSet.START, guideline5Porciento.getId(), ConstraintSet.START, 0);
                constraintSet.connect(viewSeparador_1.getId(), ConstraintSet.END, guideline95Porciento.getId(), ConstraintSet.END, 0);
                constraintSet.connect(viewSeparador_1.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
                constraintSet.connect(viewSeparador_1.getId(), ConstraintSet.BOTTOM, viewSeparador_2.getId(), ConstraintSet.BOTTOM, 0);

                constraintSet.setDimensionRatio(viewSeparador_1.getId(), "200:1");

                constraintSet.setVerticalBias(viewSeparador_1.getId(), 0.5f);
                //Fin de Separador 1


                // Separador 2
                viewSeparador_2.setBackgroundColor(this.getResources().getColor(R.color.negro));

                consLytPrincipalReporte.addView(viewSeparador_2);

                constraintSet.connect(viewSeparador_2.getId(), ConstraintSet.START, guideline5Porciento.getId(), ConstraintSet.START, 0);
                constraintSet.connect(viewSeparador_2.getId(), ConstraintSet.END, guideline95Porciento.getId(), ConstraintSet.END, 0);
                constraintSet.connect(viewSeparador_2.getId(), ConstraintSet.TOP, viewSeparador_1.getId(), ConstraintSet.TOP, 10);
                constraintSet.connect(viewSeparador_2.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

                constraintSet.setDimensionRatio(viewSeparador_2.getId(), "200:1");

                constraintSet.setVerticalBias(viewSeparador_2.getId(), 0.5f);
                //Fin de Separador 2


                // Textview "FechaDeUso"
                TextView txtViewFechaDeUso = new TextView(new ContextThemeWrapper(this, R.style.TxtViewTransparente));
                txtViewFechaDeUso.setId(View.generateViewId());

                @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
                txtViewFechaDeUso.setText(dateFormat.format(rutas.get(i).getFUsoRuta()));
                txtViewFechaDeUso.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_parrafo_medium));
                txtViewFechaDeUso.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
                txtViewFechaDeUso.setMaxLines(1);

                txtViewFechaDeUso.setTextColor(this.getResources().getColor(R.color.morado_oscuro));
                txtViewFechaDeUso.setTypeface(Typeface.DEFAULT_BOLD);

                consLytPrincipalReporte.addView(txtViewFechaDeUso);

                constraintSet.constrainHeight(txtViewFechaDeUso.getId(), ConstraintSet.PARENT_ID);

                constraintSet.connect(txtViewFechaDeUso.getId(), ConstraintSet.START, guideline5Porciento.getId(), ConstraintSet.START, 0);
                constraintSet.connect(txtViewFechaDeUso.getId(), ConstraintSet.END, guideline95Porciento.getId(), ConstraintSet.END, 0);
                constraintSet.connect(txtViewFechaDeUso.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
                constraintSet.connect(txtViewFechaDeUso.getId(), ConstraintSet.BOTTOM, viewSeparador_1.getId(), ConstraintSet.TOP, 0);
                // Fin de Textview "FechaDeUso"

                // Textview "PuntoOrigen"
                TextView txtViewPuntoOrigen = new TextView(new ContextThemeWrapper(this, R.style.TxtViewTransparente));
                txtViewPuntoOrigen.setId(View.generateViewId());
                txtViewPuntoOrigen.setText(new StringBuilder().append("De: ").append(ubicacionGeocodificacion.degeocodificarUbiciacion(Double.parseDouble(rutas.get(i).getPuntoInicio().get(0)), Double.parseDouble(rutas.get(i).getPuntoInicio().get(1).toString()))).toString());
                txtViewPuntoOrigen.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                txtViewPuntoOrigen.setMaxLines(1);

                Log.v("QUICKSTART", txtViewPuntoOrigen.getText().toString());

                consLytPrincipalReporte.addView(txtViewPuntoOrigen);

                constraintSet.constrainHeight(txtViewPuntoOrigen.getId(), ConstraintSet.PARENT_ID);

                constraintSet.connect(txtViewPuntoOrigen.getId(), ConstraintSet.START, guideline5Porciento.getId(), ConstraintSet.START, 0);
                constraintSet.connect(txtViewPuntoOrigen.getId(), ConstraintSet.END, guideline95Porciento.getId(), ConstraintSet.END, 0);
                constraintSet.connect(txtViewPuntoOrigen.getId(), ConstraintSet.TOP, viewSeparador_1.getId(), ConstraintSet.TOP, 10);
                constraintSet.connect(txtViewPuntoOrigen.getId(), ConstraintSet.BOTTOM, viewSeparador_2.getId(), ConstraintSet.TOP, 0);
                // Fin de Textview "PuntoOrigen"

                // Textview "PuntoDestino"
                TextView txtViewPuntoDestino = new TextView(new ContextThemeWrapper(this, R.style.TxtViewTransparente));
                txtViewPuntoDestino.setId(View.generateViewId());

                txtViewPuntoDestino.setText(new StringBuilder().append("A:  ").append(ubicacionGeocodificacion.degeocodificarUbiciacion(Double.valueOf(rutas.get(i).getPuntoDestino().get(0)), Double.parseDouble(rutas.get(i).getPuntoDestino().get(1).toString()))).toString());
                txtViewPuntoDestino.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                txtViewPuntoDestino.setMaxLines(1);

                Log.v("QUICKSTART", txtViewPuntoDestino.getText().toString());

                consLytPrincipalReporte.addView(txtViewPuntoDestino);

                constraintSet.constrainHeight(txtViewPuntoDestino.getId(), ConstraintSet.PARENT_ID);

                constraintSet.connect(txtViewPuntoDestino.getId(), ConstraintSet.START, guideline5Porciento.getId(), ConstraintSet.START, 0);
                constraintSet.connect(txtViewPuntoDestino.getId(), ConstraintSet.END, guideline95Porciento.getId(), ConstraintSet.END, 0);
                constraintSet.connect(txtViewPuntoDestino.getId(), ConstraintSet.TOP, viewSeparador_2.getId(), ConstraintSet.BOTTOM, 10);
                constraintSet.connect(txtViewPuntoDestino.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 10);
                // Fin de Textview "PuntoDestino"

                constraintSet.applyTo(consLytPrincipalReporte);
            }
        }
        else
            Toast.makeText(getApplicationContext(), "No has buscado ninguna ruta!", Toast.LENGTH_LONG).show();
    }
}