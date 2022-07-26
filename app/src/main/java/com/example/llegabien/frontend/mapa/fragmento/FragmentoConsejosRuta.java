package com.example.llegabien.frontend.mapa.fragmento;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;
import static com.example.llegabien.backend.app.Preferences.PREFERENCE_RUTASEGURA;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Guideline;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Html;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.llegabien.R;
import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.ruta.directions.Ruta;

public class FragmentoConsejosRuta extends Fragment implements View.OnClickListener {

    private ConstraintLayout mConsLytScrollView;
    private View mViewAuxiliar;
    private Ruta mRutaMasSegura;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this.requireActivity() fragment
        View root = inflater.inflate(R.layout.fragmento_consejos_ruta, container, false);

        //wiring up
        mConsLytScrollView = root.findViewById(R.id.consLyt_scrollView_consejos);
        mViewAuxiliar = root.findViewById(R.id.view2_consejos);
        Button btnCerrar = root.findViewById(R.id.button_cerrar_consejos);

        //listeners
        btnCerrar.setOnClickListener(this);

        mRutaMasSegura = Preferences.getSavedObjectFromPreference(this.requireActivity(), PREFERENCE_RUTASEGURA, Ruta.class);
        crearVistaConsejosRuta();

        return root;
    }

    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void crearVistaConsejosRuta() {
        ConstraintSet constraintSet = new ConstraintSet();

        for (int i = 0; i < mRutaMasSegura.getDelitosZonasInseguras().size(); i++) {
            // ConstraintLayout principal
            constraintSet.clone(mConsLytScrollView);
            ConstraintLayout consLytPrincipalConsejosRuta = new ConstraintLayout(this.requireActivity());
            consLytPrincipalConsejosRuta.setId(View.generateViewId());

            mConsLytScrollView.addView(consLytPrincipalConsejosRuta);

            constraintSet.connect(consLytPrincipalConsejosRuta.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
            constraintSet.connect(consLytPrincipalConsejosRuta.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
            constraintSet.connect(consLytPrincipalConsejosRuta.getId(), ConstraintSet.TOP, mViewAuxiliar.getId(), ConstraintSet.BOTTOM, 0);
            constraintSet.connect(consLytPrincipalConsejosRuta.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

            constraintSet.constrainWidth(consLytPrincipalConsejosRuta.getId(), ConstraintSet.PARENT_ID);
            constraintSet.constrainHeight(consLytPrincipalConsejosRuta.getId(), ConstraintSet.WRAP_CONTENT);

            constraintSet.setVerticalBias(consLytPrincipalConsejosRuta.getId(), 0.0f);

            mViewAuxiliar = consLytPrincipalConsejosRuta;

            constraintSet.applyTo(mConsLytScrollView);
            // Fin ConstraintLayout principal

            // Se cambia de ConstraintLayout
            constraintSet.clone(consLytPrincipalConsejosRuta);

            // Separador NombreDelito
            View viewSeparadorNombreDelito = new View(new ContextThemeWrapper(this.requireActivity(), R.style.ViewSeparador));
            viewSeparadorNombreDelito.setId(View.generateViewId());
            viewSeparadorNombreDelito.setBackgroundColor(Color.parseColor("#F0A8A8"));

            consLytPrincipalConsejosRuta.addView(viewSeparadorNombreDelito);

            constraintSet.connect(viewSeparadorNombreDelito.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
            constraintSet.connect(viewSeparadorNombreDelito.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
            constraintSet.connect(viewSeparadorNombreDelito.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 55);
            constraintSet.connect(viewSeparadorNombreDelito.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

            constraintSet.setDimensionRatio(viewSeparadorNombreDelito.getId(), "100:1");

            constraintSet.setVerticalBias(viewSeparadorNombreDelito.getId(), 0.0f);
            //Fin de Separador NombreDelito

            // Textview "Nombre Delito"
            TextView txtViewNombreDelito = new TextView(new ContextThemeWrapper(this.requireActivity(), R.style.TxtViewBlanco));
            txtViewNombreDelito.setId(View.generateViewId());

            String stringNumContacto = mRutaMasSegura.getDelitosZonasInseguras().get(i);
            txtViewNombreDelito.setText(stringNumContacto);
            txtViewNombreDelito.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_titulo_small));
            txtViewNombreDelito.setTextColor(this.getResources().getColor(R.color.rojo_oscuro));
            txtViewNombreDelito.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.coral));

            consLytPrincipalConsejosRuta.addView(txtViewNombreDelito);

            constraintSet.constrainWidth(txtViewNombreDelito.getId(), ConstraintSet.WRAP_CONTENT);
            constraintSet.constrainHeight(txtViewNombreDelito.getId(), 0);

            constraintSet.connect(txtViewNombreDelito.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
            constraintSet.connect(txtViewNombreDelito.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
            constraintSet.connect(txtViewNombreDelito.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
            constraintSet.connect(txtViewNombreDelito.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

            constraintSet.setDimensionRatio(txtViewNombreDelito.getId(), "2:1");

            constraintSet.setVerticalBias(txtViewNombreDelito.getId(), 0.0f);
            // Fin de Textview "Nombre Delito"

            // Separador ConsLytPrincipal
            View viewSeparadorConsLytPrincipal1 = new View(new ContextThemeWrapper(this.requireActivity(), R.style.ViewSeparadorAuxiliar));

            viewSeparadorConsLytPrincipal1.setId(View.generateViewId());

            consLytPrincipalConsejosRuta.addView(viewSeparadorConsLytPrincipal1);

            constraintSet.connect(viewSeparadorConsLytPrincipal1.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
            constraintSet.connect(viewSeparadorConsLytPrincipal1.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
            constraintSet.connect(viewSeparadorConsLytPrincipal1.getId(), ConstraintSet.TOP, txtViewNombreDelito.getId(), ConstraintSet.BOTTOM, 0);
            constraintSet.connect(viewSeparadorConsLytPrincipal1.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

            constraintSet.setDimensionRatio(viewSeparadorConsLytPrincipal1.getId(), "20:1");

            constraintSet.setVerticalBias(viewSeparadorConsLytPrincipal1.getId(), 0.0f);
            //Fin de Separador ConsLytPrincipal

            // ConstraintLayout "Consejos"
            ConstraintLayout consLytConsejos = new ConstraintLayout(this.requireActivity());

            consLytConsejos.setId(View.generateViewId());
            consLytConsejos.setBackgroundColor(getResources().getColor(R.color.blanco));

            consLytPrincipalConsejosRuta.addView(consLytConsejos);

            constraintSet.constrainWidth(consLytConsejos.getId(), ConstraintSet.PARENT_ID);
            constraintSet.constrainDefaultHeight(consLytConsejos.getId(), ConstraintSet.MATCH_CONSTRAINT_WRAP);

            constraintSet.connect(consLytConsejos.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
            constraintSet.connect(consLytConsejos.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
            constraintSet.connect(consLytConsejos.getId(), ConstraintSet.TOP, viewSeparadorConsLytPrincipal1.getId(), ConstraintSet.BOTTOM, 0);
            constraintSet.connect(consLytConsejos.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

            constraintSet.setVerticalBias(consLytConsejos.getId(), 0.0f);
            // Fin de ConstraintLayout "Consejos"

            // Separador ConsLytPrincipal
            View viewSeparadorConsLytPrincipal2 = new View(new ContextThemeWrapper(this.requireActivity(), R.style.ViewSeparadorAuxiliar));

            viewSeparadorConsLytPrincipal2.setId(View.generateViewId());

            consLytPrincipalConsejosRuta.addView(viewSeparadorConsLytPrincipal2);

            constraintSet.connect(viewSeparadorConsLytPrincipal2.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
            constraintSet.connect(viewSeparadorConsLytPrincipal2.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
            constraintSet.connect(viewSeparadorConsLytPrincipal2.getId(), ConstraintSet.TOP, consLytConsejos.getId(), ConstraintSet.BOTTOM, 0);
            constraintSet.connect(viewSeparadorConsLytPrincipal2.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

            constraintSet.setDimensionRatio(viewSeparadorConsLytPrincipal2.getId(), "10:1");

            constraintSet.setVerticalBias(viewSeparadorConsLytPrincipal2.getId(), 0.0f);
            //Fin de Separador ConsLytPrincipal

            // Para agregar cambios a "constLytPrincipalContacto"
            constraintSet.applyTo(consLytPrincipalConsejosRuta);

            // Se cambia de ConstraintLayout en el ConstraintSet
            constraintSet.clone(consLytConsejos);

            // Guideline "10 porciento"
            Guideline guideline10Porciento = new Guideline(this.requireActivity());
            guideline10Porciento.setId(View.generateViewId());

            constraintSet.constrainWidth(guideline10Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.constrainHeight(guideline10Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.setGuidelinePercent(guideline10Porciento.getId(), 0.10f);

            constraintSet.create(guideline10Porciento.getId(), ConstraintSet.VERTICAL_GUIDELINE);
            // Fin de guideline "10 porciento"

            // Guideline "90 porciento"
            Guideline guideline90Porciento = new Guideline(this.requireActivity());
            guideline90Porciento.setId(View.generateViewId());

            constraintSet.constrainWidth(guideline90Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.constrainHeight(guideline90Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.setGuidelinePercent(guideline90Porciento.getId(), 0.90f);

            constraintSet.create(guideline90Porciento.getId(), ConstraintSet.VERTICAL_GUIDELINE);
            // Fin de guideline "90 porciento"

            // Separador ConsLytConsejos1
            View viewSeparadorConsLytConsejos1 = new View(new ContextThemeWrapper(this.requireActivity(), R.style.ViewSeparadorAuxiliar));

            viewSeparadorConsLytConsejos1.setId(View.generateViewId());

            consLytConsejos.addView(viewSeparadorConsLytConsejos1);

            constraintSet.connect(viewSeparadorConsLytConsejos1.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
            constraintSet.connect(viewSeparadorConsLytConsejos1.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
            constraintSet.connect(viewSeparadorConsLytConsejos1.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
            constraintSet.connect(viewSeparadorConsLytConsejos1.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

            constraintSet.setDimensionRatio(viewSeparadorConsLytConsejos1.getId(), "10:1");

            constraintSet.setVerticalBias(viewSeparadorConsLytConsejos1.getId(), 0.0f);
            //Fin de Separador ConsLytConsejos1

            // Textview "Consejos"
            TextView txtViewConsejos = new TextView(new ContextThemeWrapper(this.requireActivity(), R.style.TxtViewTransparente));

            txtViewConsejos.setId(View.generateViewId());

            setConsejos(mRutaMasSegura.getDelitosZonasInseguras().get(i), txtViewConsejos);
            txtViewConsejos.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            txtViewConsejos.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);

            consLytConsejos.addView(txtViewConsejos);

            constraintSet.constrainWidth(txtViewConsejos.getId(), ConstraintSet.PARENT_ID);
            constraintSet.constrainDefaultHeight(txtViewConsejos.getId(), ConstraintSet.MATCH_CONSTRAINT_WRAP);

            constraintSet.connect(txtViewConsejos.getId(), ConstraintSet.START, guideline10Porciento.getId(), ConstraintSet.START, 0);
            constraintSet.connect(txtViewConsejos.getId(), ConstraintSet.END, guideline90Porciento.getId(), ConstraintSet.END, 0);
            constraintSet.connect(txtViewConsejos.getId(), ConstraintSet.TOP, viewSeparadorConsLytConsejos1.getId(), ConstraintSet.BOTTOM, 0);
            constraintSet.connect(txtViewConsejos.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

            constraintSet.setVerticalBias(txtViewConsejos.getId(), 0.0f);
            // Fin de textview "Consjeos"

            // Separador ConsLytConsejos2
            View viewSeparadorConsLytConsejos2 = new View(new ContextThemeWrapper(this.requireActivity(), R.style.ViewSeparadorAuxiliar));

            viewSeparadorConsLytConsejos2.setId(View.generateViewId());

            consLytConsejos.addView(viewSeparadorConsLytConsejos2);

            constraintSet.connect(viewSeparadorConsLytConsejos2.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
            constraintSet.connect(viewSeparadorConsLytConsejos2.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
            constraintSet.connect(viewSeparadorConsLytConsejos2.getId(), ConstraintSet.TOP, txtViewConsejos.getId(), ConstraintSet.BOTTOM, 0);
            constraintSet.connect(viewSeparadorConsLytConsejos2.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

            constraintSet.setDimensionRatio(viewSeparadorConsLytConsejos2.getId(), "30:1");

            constraintSet.setVerticalBias(viewSeparadorConsLytConsejos2.getId(), 0.0f);
            //Fin de Separador ConsLytConsejos2

            // Para agregar cambios a "mConstLytPrincipalContacto"
            constraintSet.applyTo(consLytConsejos);
        }
    }

    private void setConsejos(String delito, TextView textView) {
        if (delito.equals("GENERAL") && mRutaMasSegura.getDelitosZonasInseguras().size() == 1)
            textView.setText(Html.fromHtml(getResources().getString(R.string.general_consejos)));
        else {
            switch (delito) {
                case "ROBO":
                    textView.setText(Html.fromHtml(getResources().getString(R.string.robo_consejos)));
                    break;
                case "SECUESTRO":
                    textView.setText(Html.fromHtml(getResources().getString(R.string.secuestro_consejos)));
                    break;
                case "ACOSO SEXUAL":
                    textView.setText(Html.fromHtml(getResources().getString(R.string.acosoSexual_consejos)));
                    break;
                case "ABUSO SEXUAL":
                    textView.setText(Html.fromHtml(getResources().getString(R.string.abusoSexual_consejos)));
                    break;
                case "TIROTEO":
                    textView.setText(Html.fromHtml(getResources().getString(R.string.tiroteo_consejos)));
                    break;
            }
        }
    }

    // LISTENERS//
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_cerrar_consejos) {
            FragmentTransaction fragmentTransaction = this.requireActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.remove(this).commit();
        }


    }
}