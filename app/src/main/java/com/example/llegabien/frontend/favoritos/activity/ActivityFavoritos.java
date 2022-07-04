package com.example.llegabien.frontend.favoritos.activity;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Guideline;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.llegabien.R;
import com.example.llegabien.backend.mapa.favoritos.Favorito_DAO;
import com.example.llegabien.backend.mapa.favoritos.favorito;
import com.example.llegabien.backend.mapa.ubicacion.UbicacionDAO;

import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.usuario.usuario;
import com.example.llegabien.frontend.mapa.activity.ActivityMap;

import org.bson.types.ObjectId;

import io.realm.RealmResults;

public class ActivityFavoritos extends AppCompatActivity implements View.OnClickListener {
    private ConstraintLayout mConsLytScrollView;
    private View mViewAuxiliar;
    private Guideline mGuideline10Porciento, mGuideline90Porciente;
    private  RealmResults<favorito> favoritos;
    private Favorito_DAO favoritoDAO;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos);

        //wiring up
        mConsLytScrollView = findViewById(R.id.consLyt_scrollView_favoritos);
        mViewAuxiliar = findViewById(R.id.view2_favoritos);
        mGuideline10Porciento = findViewById(R.id.guideline1_textView_editView_scrollView_favoritos);
        mGuideline90Porciente = findViewById(R.id.guideline2_textView_editView_scrollView_favoritos);
        Button mBtnRegresar = findViewById(R.id.button_regresar_favoritos);

        //listeners
        mBtnRegresar.setOnClickListener(this);

        usuario usuario = Preferences.getSavedObjectFromPreference(this, PREFERENCE_USUARIO, com.example.llegabien.backend.usuario.usuario.class);
        favoritoDAO = new Favorito_DAO(this);
        favoritos = favoritoDAO.obtenerFavoritosDeUsuario(usuario);

        // Para crear la vista de favoritos
        crearVistaFavoritos();
    }

    // FUNCIONES LISTENER //

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_regresar_favoritos){
            finish();
        }
        else{
            UbicacionDAO ubicacionDAO = new UbicacionDAO(this);
            ObjectId idFavorito = new ObjectId(String.valueOf(view.getContentDescription()));
            favorito Favorito = favoritoDAO.obtenerFavoritoPorId(idFavorito);
            ubicacionDAO.obtenerUbicacionBuscada(Favorito.getUbicacion().getCoordinates().get(0),Favorito.getUbicacion().getCoordinates().get(1));

            // Para abrir fragmento "Lugar seleccionado".
            Intent intent = new Intent(this, ActivityMap.class);
            intent.putExtra("ACTIVITY_ANTERIOR","FAVORITOS");
            startActivity(intent);
        }

    }

    // OTRAS FUNCIONES //

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void crearVistaFavoritos() {
        ConstraintSet constraintSet = new ConstraintSet();

        for (int i = 0; i < favoritos.size(); i++) {
            // ConstraintLayout principal
            constraintSet.clone(mConsLytScrollView);
            ConstraintLayout consLytPrincipalFavorito = new ConstraintLayout(this);
            consLytPrincipalFavorito.setId(View.generateViewId());

            consLytPrincipalFavorito.setBackground(getResources().getDrawable(R.drawable.bkgd_esquinas_redondeadas));
            consLytPrincipalFavorito.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.morado_claro));
            consLytPrincipalFavorito.setElevation(10);

            consLytPrincipalFavorito.setClickable(true);
            consLytPrincipalFavorito.setOnClickListener(this);
            consLytPrincipalFavorito.setContentDescription(favoritos.get(i).get_id().toString());

            mConsLytScrollView.addView(consLytPrincipalFavorito);
            constraintSet.connect(consLytPrincipalFavorito.getId(), ConstraintSet.START, mGuideline10Porciento.getId(), ConstraintSet.START, 0);
            constraintSet.connect(consLytPrincipalFavorito.getId(), ConstraintSet.END, mGuideline90Porciente.getId(), ConstraintSet.END, 0);
            constraintSet.connect(consLytPrincipalFavorito.getId(), ConstraintSet.TOP, mViewAuxiliar.getId(), ConstraintSet.BOTTOM, 0);
            constraintSet.connect(consLytPrincipalFavorito.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

            constraintSet.constrainWidth(consLytPrincipalFavorito.getId(), ConstraintSet.PARENT_ID);
            constraintSet.constrainHeight(consLytPrincipalFavorito.getId(), 0);

            constraintSet.setDimensionRatio(consLytPrincipalFavorito.getId(), "7:1");

            constraintSet.setVerticalBias(consLytPrincipalFavorito.getId(), 0.0f);
            //Fin de ConstraintLayout principal

            // Separador
            View viewSeparadorFinal = new View(new ContextThemeWrapper(this, R.style.ViewSeparadorAuxiliar));

            viewSeparadorFinal.setId(View.generateViewId());

            mConsLytScrollView.addView(viewSeparadorFinal);

            constraintSet.connect(viewSeparadorFinal.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
            constraintSet.connect(viewSeparadorFinal.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
            constraintSet.connect(viewSeparadorFinal.getId(), ConstraintSet.TOP, consLytPrincipalFavorito.getId(), ConstraintSet.BOTTOM, 0);
            constraintSet.connect(viewSeparadorFinal.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

            constraintSet.setDimensionRatio(viewSeparadorFinal.getId(), "15:1");

            constraintSet.setVerticalBias(viewSeparadorFinal.getId(), 0.0f);
            //Fin de Separador

            mViewAuxiliar = viewSeparadorFinal;

            constraintSet.applyTo(mConsLytScrollView);
            // Fin ConstraintLayout principal

            // Se cambia de ConstraintLayout
            constraintSet.clone(consLytPrincipalFavorito);

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

            // Guideline "10 porciento"
            Guideline guideline10Porciento = new Guideline(this);
            guideline10Porciento.setId(View.generateViewId());

            constraintSet.constrainWidth(guideline10Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.constrainHeight(guideline10Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.setGuidelinePercent(guideline10Porciento.getId(), 0.10f);

            constraintSet.create(guideline10Porciento.getId(), ConstraintSet.VERTICAL_GUIDELINE);
            // Fin de guideline "10 porciento"

            // Guideline "12 porciento"
            Guideline guideline12Porciento = new Guideline(this);
            guideline12Porciento.setId(View.generateViewId());

            constraintSet.constrainWidth(guideline12Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.constrainHeight(guideline12Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.setGuidelinePercent(guideline12Porciento.getId(), 0.12f);

            constraintSet.create(guideline12Porciento.getId(), ConstraintSet.VERTICAL_GUIDELINE);
            // Fin de guideline "12 porciento"

            // View "Icon ubicación"
            View iconUbicacion = new TextView(new ContextThemeWrapper(this, R.style.BkgdIcon));
            iconUbicacion.setId(View.generateViewId());

            iconUbicacion.setBackground(getResources().getDrawable(R.drawable.bkgd_icon_ubicacion));

            consLytPrincipalFavorito.addView(iconUbicacion);

            constraintSet.connect(iconUbicacion.getId(), ConstraintSet.START, guideline5Porciento.getId(), ConstraintSet.START, 0);
            constraintSet.connect(iconUbicacion.getId(), ConstraintSet.END, guideline10Porciento.getId(), ConstraintSet.END, 0);
            constraintSet.connect(iconUbicacion.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
            constraintSet.connect(iconUbicacion.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

            constraintSet.setDimensionRatio(iconUbicacion.getId(),"17:22");
            // View "Icon ubicación"

            // Textview "Ubicacion"
            TextView txtViewUbicacion = new TextView(new ContextThemeWrapper(this, R.style.TxtViewTransparente));
            txtViewUbicacion.setId(View.generateViewId());

            txtViewUbicacion.setText(favoritos.get(i).getNombre());
            txtViewUbicacion.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_parrafo));
            txtViewUbicacion.setGravity(Gravity.START|Gravity.CENTER_VERTICAL);
            txtViewUbicacion.setMaxLines(1);

            consLytPrincipalFavorito.addView(txtViewUbicacion);

            constraintSet.constrainHeight(txtViewUbicacion.getId(), ConstraintSet.PARENT_ID);

            constraintSet.connect(txtViewUbicacion.getId(), ConstraintSet.START, guideline12Porciento.getId(), ConstraintSet.START, 0);
            constraintSet.connect(txtViewUbicacion.getId(), ConstraintSet.END, guideline95Porciento.getId(), ConstraintSet.END, 0);
            constraintSet.connect(txtViewUbicacion.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
            constraintSet.connect(txtViewUbicacion.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
            // Fin de Textview "Ubicacion"

            constraintSet.applyTo(consLytPrincipalFavorito);

        }
    }
}