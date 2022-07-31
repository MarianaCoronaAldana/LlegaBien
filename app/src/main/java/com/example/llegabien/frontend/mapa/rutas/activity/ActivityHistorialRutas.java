package com.example.llegabien.frontend.mapa.rutas.activity;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Guideline;
import androidx.core.content.ContextCompat;

import com.example.llegabien.R;
import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.ruta.realm.ruta;
import com.example.llegabien.backend.ruta.realm.rutaDAO;
import com.example.llegabien.backend.ubicacion.UbicacionGeocodificacion;
import com.example.llegabien.backend.usuario.usuario;
import com.example.llegabien.frontend.mapa.activity.ActivityMap;

import org.bson.types.ObjectId;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;

import io.realm.RealmResults;

public class ActivityHistorialRutas extends AppCompatActivity implements View.OnClickListener {
    private ConstraintLayout mConsLytScrollView;
    private View mViewAuxiliar;
    private Guideline mGuideline10Porciento, mGuideline90Porciente;
    private RealmResults<ruta> rutas;
    private UbicacionGeocodificacion mUbicacionGeocodificacion;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_rutas);

        mUbicacionGeocodificacion = new UbicacionGeocodificacion(this);

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
        if (view.getId() == R.id.button_regresar_mostrar_rutas) {
            finish();
        } else {
            //TODO: AQUI SE HACE LO QUE SE TIENE QUE HACER CUANDO SE ELIGE UNA RUTA

            rutaDAO RutaDao = new rutaDAO(this);
            ObjectId idRuta = new ObjectId(String.valueOf(view.getContentDescription()));
            RutaDao.obtenerRutaPorId(idRuta);

            // Para abrir fragmento "Lugar seleccionado".
            Intent intent = new Intent(this, ActivityMap.class);
            intent.putExtra("ACTIVITY_ANTERIOR","HISTORIAL_RUTAS");
            startActivity(intent);
        }

    }

    // OTRAS FUNCIONES //

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void crearVistaRutas() {
        int constrainBottomForConsLytPrincipal;
        LocalDate fechaUsoRuta = null;
        ConstraintSet constraintSet = new ConstraintSet();

        for (int i = 0; i < rutas.size(); i++) {
            // ConstraintLayout principal
            constraintSet.clone(mConsLytScrollView);

            if (i == 0 || !fechaUsoRuta.toString().equals(rutas.get(i).getFUsoRuta().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString())) {
                fechaUsoRuta = rutas.get(i).getFUsoRuta().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                // Separador
                View viewSeparador = new View(new ContextThemeWrapper(this, R.style.ViewSeparador));
                viewSeparador.setId(View.generateViewId());
                viewSeparador.setBackgroundColor(Color.parseColor("#AFBED8"));

                mConsLytScrollView.addView(viewSeparador);

                constraintSet.connect(viewSeparador.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
                constraintSet.connect(viewSeparador.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
                constraintSet.connect(viewSeparador.getId(), ConstraintSet.TOP, mViewAuxiliar.getId(), ConstraintSet.BOTTOM, 45);
                constraintSet.connect(viewSeparador.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

                constraintSet.setDimensionRatio(viewSeparador.getId(), "100:1");

                constraintSet.setVerticalBias(viewSeparador.getId(), 0.0f);
                //Fin de Separador

                // Textview "Número de contacto"
                TextView txtViewFecha = new TextView(new ContextThemeWrapper(this, R.style.TxtViewBlanco));
                txtViewFecha.setId(View.generateViewId());

                @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                txtViewFecha.setText(dateFormat.format(rutas.get(i).getFUsoRuta()));
                txtViewFecha.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_titulo_small));
                txtViewFecha.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.azul_claro));

                mConsLytScrollView.addView(txtViewFecha);

                constraintSet.constrainWidth(txtViewFecha.getId(), ConstraintSet.WRAP_CONTENT);
                constraintSet.constrainHeight(txtViewFecha.getId(), 0);

                constraintSet.connect(txtViewFecha.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
                constraintSet.connect(txtViewFecha.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
                constraintSet.connect(txtViewFecha.getId(), ConstraintSet.TOP, mViewAuxiliar.getId(), ConstraintSet.BOTTOM, 0);
                constraintSet.connect(txtViewFecha.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

                constraintSet.setDimensionRatio(txtViewFecha.getId(), "5:1");

                constraintSet.setVerticalBias(txtViewFecha.getId(), 0.0f);
                // Fin de Textview "Número de contacto"

                // Separador
                View viewSeparadorFinal = new View(new ContextThemeWrapper(this, R.style.ViewSeparadorAuxiliar));

                viewSeparadorFinal.setId(View.generateViewId());

                mConsLytScrollView.addView(viewSeparadorFinal);

                constraintSet.connect(viewSeparadorFinal.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
                constraintSet.connect(viewSeparadorFinal.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
                constraintSet.connect(viewSeparadorFinal.getId(), ConstraintSet.TOP, txtViewFecha.getId(), ConstraintSet.BOTTOM, 0);
                constraintSet.connect(viewSeparadorFinal.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

                constraintSet.setDimensionRatio(viewSeparadorFinal.getId(), "15:1");

                constraintSet.setVerticalBias(viewSeparadorFinal.getId(), 0.0f);
                //Fin de Separador

                constrainBottomForConsLytPrincipal = viewSeparadorFinal.getId();
            } else
                constrainBottomForConsLytPrincipal = mViewAuxiliar.getId();

            ConstraintLayout consLytPrincipalHistorialRutas = new ConstraintLayout(this);
            consLytPrincipalHistorialRutas.setId(View.generateViewId());
            consLytPrincipalHistorialRutas.setBackground(this.getResources().getDrawable(R.drawable.bkgd_esquinas_redondeadas));
            consLytPrincipalHistorialRutas.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.morado_claro));

            consLytPrincipalHistorialRutas.setClickable(true);
            consLytPrincipalHistorialRutas.setOnClickListener(this);
            consLytPrincipalHistorialRutas.setContentDescription(Objects.requireNonNull(rutas.get(i)).get_id().toString());
            consLytPrincipalHistorialRutas.setElevation(10f);

            mConsLytScrollView.addView(consLytPrincipalHistorialRutas);
            constraintSet.connect(consLytPrincipalHistorialRutas.getId(), ConstraintSet.START, mGuideline10Porciento.getId(), ConstraintSet.START, 0);
            constraintSet.connect(consLytPrincipalHistorialRutas.getId(), ConstraintSet.END, mGuideline90Porciente.getId(), ConstraintSet.END, 0);
            constraintSet.connect(consLytPrincipalHistorialRutas.getId(), ConstraintSet.TOP, constrainBottomForConsLytPrincipal, ConstraintSet.BOTTOM, 0);
            constraintSet.connect(consLytPrincipalHistorialRutas.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

            constraintSet.constrainWidth(consLytPrincipalHistorialRutas.getId(), ConstraintSet.PARENT_ID);
            constraintSet.constrainHeight(consLytPrincipalHistorialRutas.getId(), ConstraintSet.PARENT_ID);

            constraintSet.setDimensionRatio(consLytPrincipalHistorialRutas.getId(), "7:2");

            constraintSet.setVerticalBias(consLytPrincipalHistorialRutas.getId(), 0.0f);
            //Fin de ConstraintLayout principal


            // Separador
            View viewSeparadorFinal = new View(new ContextThemeWrapper(this, R.style.ViewSeparadorAuxiliar));

            viewSeparadorFinal.setId(View.generateViewId());

            mConsLytScrollView.addView(viewSeparadorFinal);

            constraintSet.connect(viewSeparadorFinal.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
            constraintSet.connect(viewSeparadorFinal.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
            constraintSet.connect(viewSeparadorFinal.getId(), ConstraintSet.TOP, consLytPrincipalHistorialRutas.getId(), ConstraintSet.BOTTOM, 0);
            constraintSet.connect(viewSeparadorFinal.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

            constraintSet.setDimensionRatio(viewSeparadorFinal.getId(), "15:1");

            constraintSet.setVerticalBias(viewSeparadorFinal.getId(), 0.0f);
            //Fin de Separador

            mViewAuxiliar = viewSeparadorFinal;

            constraintSet.applyTo(mConsLytScrollView);

            // Se cambia de ConstraintLayout
            constraintSet.clone(consLytPrincipalHistorialRutas);

            // Guideline "5 porciento"
            Guideline guideline5Porciento = new Guideline(this);
            guideline5Porciento.setId(View.generateViewId());

            constraintSet.constrainWidth(guideline5Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.constrainHeight(guideline5Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.setGuidelinePercent(guideline5Porciento.getId(), 0.05f);

            constraintSet.create(guideline5Porciento.getId(), ConstraintSet.VERTICAL_GUIDELINE);
            // Fin de guideline "5 porciento"

            // Guideline "9 porciento"
            Guideline guideline9Porciento = new Guideline(this);
            guideline9Porciento.setId(View.generateViewId());

            constraintSet.constrainWidth(guideline9Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.constrainHeight(guideline9Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.setGuidelinePercent(guideline9Porciento.getId(), 0.09f);

            constraintSet.create(guideline9Porciento.getId(), ConstraintSet.VERTICAL_GUIDELINE);
            // Fin de guideline "9 porciento"

            // Guideline "12 porciento"
            Guideline guideline12Porciento = new Guideline(this);
            guideline12Porciento.setId(View.generateViewId());

            constraintSet.constrainWidth(guideline12Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.constrainHeight(guideline12Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.setGuidelinePercent(guideline12Porciento.getId(), 0.12f);

            constraintSet.create(guideline12Porciento.getId(), ConstraintSet.VERTICAL_GUIDELINE);
            // Fin de guideline "12 porciento"

            // Guideline "90 porciento"
            Guideline guideline90Porciento = new Guideline(this);
            guideline90Porciento.setId(View.generateViewId());

            constraintSet.constrainWidth(guideline90Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.constrainHeight(guideline90Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.setGuidelinePercent(guideline90Porciento.getId(), 0.90f);

            constraintSet.create(guideline90Porciento.getId(), ConstraintSet.VERTICAL_GUIDELINE);
            // Fin de guideline "90 porciento"

            // Guideline "92 porciento"
            Guideline guideline92Porciento = new Guideline(this);
            guideline92Porciento.setId(View.generateViewId());

            constraintSet.constrainWidth(guideline92Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.constrainHeight(guideline92Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.setGuidelinePercent(guideline92Porciento.getId(), 0.92f);

            constraintSet.create(guideline92Porciento.getId(), ConstraintSet.VERTICAL_GUIDELINE);
            // Fin de guideline "92 porciento"

            // Guideline "97 porciento"
            Guideline guideline97Porciento = new Guideline(this);
            guideline97Porciento.setId(View.generateViewId());

            constraintSet.constrainWidth(guideline97Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.constrainHeight(guideline97Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.setGuidelinePercent(guideline97Porciento.getId(), 0.97f);

            constraintSet.create(guideline97Porciento.getId(), ConstraintSet.VERTICAL_GUIDELINE);
            // Fin de guideline "97 porciento"

            // Separador viewSeparadorConsLytPrincipal
            View viewSeparadorConsLytPrincipal = new View(new ContextThemeWrapper(this, R.style.ViewSeparador));
            viewSeparadorConsLytPrincipal.setId(View.generateViewId());

            viewSeparadorConsLytPrincipal.setBackgroundColor(this.getResources().getColor(R.color.negro));

            consLytPrincipalHistorialRutas.addView(viewSeparadorConsLytPrincipal);

            constraintSet.connect(viewSeparadorConsLytPrincipal.getId(), ConstraintSet.START, guideline12Porciento.getId(), ConstraintSet.START, 0);
            constraintSet.connect(viewSeparadorConsLytPrincipal.getId(), ConstraintSet.END, guideline90Porciento.getId(), ConstraintSet.END, 0);
            constraintSet.connect(viewSeparadorConsLytPrincipal.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 10);
            constraintSet.connect(viewSeparadorConsLytPrincipal.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

            constraintSet.setDimensionRatio(viewSeparadorConsLytPrincipal.getId(), "200:1");

            constraintSet.setVerticalBias(viewSeparadorConsLytPrincipal.getId(), 0.5f);
            //Fin de Separador viewSeparadorConsLytPrincipal

            // View "Icon ubicación"
            View iconUbicacion = new TextView(new ContextThemeWrapper(this, R.style.BkgdIcon));
            iconUbicacion.setId(View.generateViewId());

            iconUbicacion.setBackground(getResources().getDrawable(R.drawable.bkgd_icon_partida_destino));

            consLytPrincipalHistorialRutas.addView(iconUbicacion);

            constraintSet.connect(iconUbicacion.getId(), ConstraintSet.START, guideline5Porciento.getId(), ConstraintSet.START, 0);
            constraintSet.connect(iconUbicacion.getId(), ConstraintSet.END, guideline9Porciento.getId(), ConstraintSet.END, 0);
            constraintSet.connect(iconUbicacion.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
            constraintSet.connect(iconUbicacion.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

            constraintSet.setDimensionRatio(iconUbicacion.getId(), "2:8");
            // View "Icon ubicación"

            // Textview "PuntoOrigen"
            TextView txtViewPuntoOrigen = new TextView(new ContextThemeWrapper(this, R.style.TxtViewTransparente));
            txtViewPuntoOrigen.setId(View.generateViewId());
            txtViewPuntoOrigen.setText(mUbicacionGeocodificacion.degeocodificarUbiciacion(Double.parseDouble(rutas.get(i).getPuntoInicio().get(0)), Double.parseDouble(rutas.get(i).getPuntoInicio().get(1))));
            txtViewPuntoOrigen.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            txtViewPuntoOrigen.setMaxLines(1);
            txtViewPuntoOrigen.setEllipsize(TextUtils.TruncateAt.END);

            consLytPrincipalHistorialRutas.addView(txtViewPuntoOrigen);

            constraintSet.constrainHeight(txtViewPuntoOrigen.getId(), ConstraintSet.PARENT_ID);

            constraintSet.connect(txtViewPuntoOrigen.getId(), ConstraintSet.START, guideline12Porciento.getId(), ConstraintSet.START, 0);
            constraintSet.connect(txtViewPuntoOrigen.getId(), ConstraintSet.END, guideline90Porciento.getId(), ConstraintSet.END, 0);
            constraintSet.connect(txtViewPuntoOrigen.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 10);
            constraintSet.connect(txtViewPuntoOrigen.getId(), ConstraintSet.BOTTOM, viewSeparadorConsLytPrincipal.getId(), ConstraintSet.TOP, 0);
            // Fin de Textview "PuntoOrigen"

            // Textview "PuntoDestino"
            TextView txtViewPuntoDestino = new TextView(new ContextThemeWrapper(this, R.style.TxtViewTransparente));
            txtViewPuntoDestino.setId(View.generateViewId());

            txtViewPuntoDestino.setText(mUbicacionGeocodificacion.degeocodificarUbiciacion(Double.parseDouble(rutas.get(i).getPuntoDestino().get(0)), Double.parseDouble(rutas.get(i).getPuntoDestino().get(1))));
            txtViewPuntoDestino.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            txtViewPuntoDestino.setMaxLines(1);
            txtViewPuntoDestino.setEllipsize(TextUtils.TruncateAt.END);

            consLytPrincipalHistorialRutas.addView(txtViewPuntoDestino);

            constraintSet.constrainHeight(txtViewPuntoDestino.getId(), ConstraintSet.PARENT_ID);

            constraintSet.connect(txtViewPuntoDestino.getId(), ConstraintSet.START, guideline12Porciento.getId(), ConstraintSet.START, 0);
            constraintSet.connect(txtViewPuntoDestino.getId(), ConstraintSet.END, guideline90Porciento.getId(), ConstraintSet.END, 0);
            constraintSet.connect(txtViewPuntoDestino.getId(), ConstraintSet.TOP, viewSeparadorConsLytPrincipal.getId(), ConstraintSet.BOTTOM, 10);
            constraintSet.connect(txtViewPuntoDestino.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 10);
            // Fin de Textview "PuntoDestino"

            // View "Icon arrow"
            View iconArrow = new TextView(new ContextThemeWrapper(this, R.style.BkgdIcon));
            iconArrow.setId(View.generateViewId());

            iconArrow.setBackground(getResources().getDrawable(R.drawable.bkgd_icon_right_arrow));

            consLytPrincipalHistorialRutas.addView(iconArrow);

            constraintSet.connect(iconArrow.getId(), ConstraintSet.START, guideline92Porciento.getId(), ConstraintSet.START, 0);
            constraintSet.connect(iconArrow.getId(), ConstraintSet.END, guideline97Porciento.getId(), ConstraintSet.END, 0);
            constraintSet.connect(iconArrow.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
            constraintSet.connect(iconArrow.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

            constraintSet.setDimensionRatio(iconArrow.getId(),"1:1");
            // View "Icon arrow"

            constraintSet.applyTo(consLytPrincipalHistorialRutas);
        }
    }
}