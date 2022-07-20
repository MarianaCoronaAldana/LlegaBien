package com.example.llegabien.frontend.favoritos.activity;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.example.llegabien.backend.mapa.favoritos.Favorito_DAO;
import com.example.llegabien.backend.mapa.favoritos.favorito;
import com.example.llegabien.backend.mapa.ubicacion.UbicacionDAO;
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

            // Guideline "4 porciento"
            Guideline guideline4Porciento = new Guideline(this);
            guideline4Porciento.setId(View.generateViewId());

            constraintSet.constrainWidth(guideline4Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.constrainHeight(guideline4Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.setGuidelinePercent(guideline4Porciento.getId(), 0.04f);

            constraintSet.create(guideline4Porciento.getId(), ConstraintSet.VERTICAL_GUIDELINE);
            // Fin de guideline "4 porciento"

            // Guideline "9 porciento"
            Guideline guideline9Porciento = new Guideline(this);
            guideline9Porciento.setId(View.generateViewId());

            constraintSet.constrainWidth(guideline9Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.constrainHeight(guideline9Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.setGuidelinePercent(guideline9Porciento.getId(), 0.09f);

            constraintSet.create(guideline9Porciento.getId(), ConstraintSet.VERTICAL_GUIDELINE);
            // Fin de guideline "9 porciento"

            // Guideline "11 porciento"
            Guideline guideline11Porciento = new Guideline(this);
            guideline11Porciento.setId(View.generateViewId());

            constraintSet.constrainWidth(guideline11Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.constrainHeight(guideline11Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.setGuidelinePercent(guideline11Porciento.getId(), 0.11f);

            constraintSet.create(guideline11Porciento.getId(), ConstraintSet.VERTICAL_GUIDELINE);
            // Fin de guideline "11 porciento"
            
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
            

            // View "Icon ubicación"
            View iconUbicacion = new TextView(new ContextThemeWrapper(this, R.style.BkgdIcon));
            iconUbicacion.setId(View.generateViewId());

            iconUbicacion.setBackground(getResources().getDrawable(R.drawable.bkgd_icon_ubicacion));

            consLytPrincipalFavorito.addView(iconUbicacion);

            constraintSet.connect(iconUbicacion.getId(), ConstraintSet.START, guideline4Porciento.getId(), ConstraintSet.START, 0);
            constraintSet.connect(iconUbicacion.getId(), ConstraintSet.END, guideline9Porciento.getId(), ConstraintSet.END, 0);
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
            txtViewUbicacion.setEllipsize(TextUtils.TruncateAt.END);

            consLytPrincipalFavorito.addView(txtViewUbicacion);

            constraintSet.constrainHeight(txtViewUbicacion.getId(), ConstraintSet.PARENT_ID);

            constraintSet.connect(txtViewUbicacion.getId(), ConstraintSet.START, guideline11Porciento.getId(), ConstraintSet.START, 0);
            constraintSet.connect(txtViewUbicacion.getId(), ConstraintSet.END, guideline92Porciento.getId(), ConstraintSet.END, 0);
            constraintSet.connect(txtViewUbicacion.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
            constraintSet.connect(txtViewUbicacion.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
            // Fin de Textview "Ubicacion"

            // View "Icon arrow"
            View iconArrow = new TextView(new ContextThemeWrapper(this, R.style.BkgdIcon));
            iconArrow.setId(View.generateViewId());

            iconArrow.setBackground(getResources().getDrawable(R.drawable.bkgd_icon_right_arrow));

            consLytPrincipalFavorito.addView(iconArrow);

            constraintSet.connect(iconArrow.getId(), ConstraintSet.START, guideline92Porciento.getId(), ConstraintSet.START, 0);
            constraintSet.connect(iconArrow.getId(), ConstraintSet.END, guideline97Porciento.getId(), ConstraintSet.END, 0);
            constraintSet.connect(iconArrow.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
            constraintSet.connect(iconArrow.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

            constraintSet.setDimensionRatio(iconArrow.getId(),"1:1");
            // View "Icon arrow"

            constraintSet.applyTo(consLytPrincipalFavorito);

        }
    }
}