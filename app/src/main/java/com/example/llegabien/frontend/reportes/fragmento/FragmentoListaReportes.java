package com.example.llegabien.frontend.reportes.fragmento;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Guideline;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.llegabien.R;

public class FragmentoListaReportes extends Fragment implements View.OnClickListener {

    private ConstraintLayout mConsLytScrollView, mConsLytPrincipalReporte;
    private View mViewAuxiliar;
    private Guideline mGuideline10Porciento, mGuideline90Porciente;
    private Button mBtnRegresar;

    public FragmentoListaReportes() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_lista_reportes, container, false);

        //wiring up
        mConsLytScrollView = root.findViewById(R.id.consLyt_scrollView_listaReportes);
        mViewAuxiliar = root.findViewById(R.id.view2_listaReportes);
        mGuideline10Porciento = root.findViewById(R.id.guideline1_textView_editView_scrollView_listaReportes);
        mGuideline90Porciente = root.findViewById(R.id.guideline2_textView_editView_scrollView_listaReportes);
        mBtnRegresar = root.findViewById(R.id.button_regresar_listaReportes);

        //listeners
        mBtnRegresar.setOnClickListener(this);

        crearVistaContacto();


        return root;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void crearVistaContacto() {
        ConstraintSet constraintSet = new ConstraintSet();
        String tipoDelito = "Acoso sexual";
        String fechaDelito = "12/12/2021, 09:47";
        String ubicacionDelito = "Calle Paseo de los Abetos 339 9";

        for (int i = 0; i < 5; i++) {

            // ConstraintLayout principal
            constraintSet.clone(mConsLytScrollView);
            mConsLytPrincipalReporte = new ConstraintLayout(this.getActivity());
            mConsLytPrincipalReporte.setId(View.generateViewId());
            mConsLytPrincipalReporte.setBackground(getActivity().getResources().getDrawable(R.drawable.bkgd_esquinas_redondeadas));
            mConsLytPrincipalReporte.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.morado_claro));

            mConsLytScrollView.addView(mConsLytPrincipalReporte);

            constraintSet.connect(mConsLytPrincipalReporte.getId(), ConstraintSet.START, mGuideline10Porciento.getId(), ConstraintSet.START, 0);
            constraintSet.connect(mConsLytPrincipalReporte.getId(), ConstraintSet.END, mGuideline90Porciente.getId(), ConstraintSet.END, 0);
            constraintSet.connect(mConsLytPrincipalReporte.getId(), ConstraintSet.TOP, mViewAuxiliar.getId(), ConstraintSet.BOTTOM, 0);
            constraintSet.connect(mConsLytPrincipalReporte.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

            constraintSet.constrainWidth(mConsLytPrincipalReporte.getId(), ConstraintSet.PARENT_ID);
            constraintSet.constrainHeight(mConsLytPrincipalReporte.getId(), 0);

            constraintSet.setDimensionRatio(mConsLytPrincipalReporte.getId(), "7:2");

            constraintSet.setVerticalBias(mConsLytPrincipalReporte.getId(), 0.0f);
            //Fin de ConstraintLayout principal

            // Separador
            View viewSeparadorFinal = new View(new ContextThemeWrapper(this.getActivity(), R.style.ViewSeparadorAuxiliar));

            viewSeparadorFinal.setId(View.generateViewId());

            mConsLytScrollView.addView(viewSeparadorFinal);

            constraintSet.connect(viewSeparadorFinal.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
            constraintSet.connect(viewSeparadorFinal.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
            constraintSet.connect(viewSeparadorFinal.getId(), ConstraintSet.TOP, mConsLytPrincipalReporte.getId(), ConstraintSet.BOTTOM, 0);
            constraintSet.connect(viewSeparadorFinal.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

            constraintSet.setDimensionRatio(viewSeparadorFinal.getId(), "15:1");

            constraintSet.setVerticalBias(viewSeparadorFinal.getId(), 0.0f);
            //Fin de Separador

            mViewAuxiliar = viewSeparadorFinal;

            constraintSet.applyTo(mConsLytScrollView);
            // Fin ConstraintLayout principal

            // Se cambia de ConstraintLayout
            constraintSet.clone(mConsLytPrincipalReporte);

            // Guideline "5 porciento"
            Guideline guideline5Porciento = new Guideline(this.getActivity());
            guideline5Porciento.setId(View.generateViewId());

            constraintSet.constrainWidth(guideline5Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.constrainHeight(guideline5Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.setGuidelinePercent(guideline5Porciento.getId(), 0.05f);

            constraintSet.create(guideline5Porciento.getId(), ConstraintSet.VERTICAL_GUIDELINE);
            // Fin de guideline "5 porciento"

            // Guideline "95 porciento"
            Guideline guideline95Porciento = new Guideline(this.getActivity());
            guideline95Porciento.setId(View.generateViewId());

            constraintSet.constrainWidth(guideline95Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.constrainHeight(guideline95Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.setGuidelinePercent(guideline95Porciento.getId(), 0.95f);

            constraintSet.create(guideline95Porciento.getId(), ConstraintSet.VERTICAL_GUIDELINE);
            // Fin de guideline "95 porciento"

            // Guideline "45 porciento"
            Guideline guideline45Porciento = new Guideline(this.getActivity());
            guideline45Porciento.setId(View.generateViewId());

            constraintSet.constrainWidth(guideline45Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.constrainHeight(guideline45Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.setGuidelinePercent(guideline45Porciento.getId(), 0.45f);

            constraintSet.create(guideline45Porciento.getId(), ConstraintSet.VERTICAL_GUIDELINE);
            // Fin de guideline "45 porciento"

            // Guideline "50 porciento"
            Guideline guideline50Porciento = new Guideline(this.getActivity());
            guideline50Porciento.setId(View.generateViewId());

            constraintSet.constrainWidth(guideline50Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.constrainHeight(guideline50Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.setGuidelinePercent(guideline50Porciento.getId(), 0.50f);

            constraintSet.create(guideline50Porciento.getId(), ConstraintSet.VERTICAL_GUIDELINE);
            // Fin de guideline "50 porciento"

            // Separador
            View viewSeparador = new View(new ContextThemeWrapper(this.getActivity(), R.style.ViewSeparador));
            viewSeparador.setId(View.generateViewId());
            viewSeparador.setBackgroundColor(getActivity().getResources().getColor(R.color.negro));

            mConsLytPrincipalReporte.addView(viewSeparador);

            constraintSet.connect(viewSeparador.getId(), ConstraintSet.START, guideline5Porciento.getId(), ConstraintSet.START, 0);
            constraintSet.connect(viewSeparador.getId(), ConstraintSet.END, guideline95Porciento.getId(), ConstraintSet.END, 0);
            constraintSet.connect(viewSeparador.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
            constraintSet.connect(viewSeparador.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

            constraintSet.setDimensionRatio(viewSeparador.getId(), "200:1");

            constraintSet.setVerticalBias(viewSeparador.getId(), 0.5f);
            //Fin de Separador

            // Textview "TipoDelito"
            TextView txtViewTipoDelito = new TextView(new ContextThemeWrapper(this.getActivity(), R.style.TxtViewTransparente));
            txtViewTipoDelito.setId(View.generateViewId());

            txtViewTipoDelito.setText(tipoDelito);
            txtViewTipoDelito.setTypeface(Typeface.DEFAULT_BOLD);
            txtViewTipoDelito.setGravity(Gravity.START|Gravity.CENTER_VERTICAL);

            mConsLytPrincipalReporte.addView(txtViewTipoDelito);

            constraintSet.constrainHeight(txtViewTipoDelito.getId(), ConstraintSet.PARENT_ID);

            constraintSet.connect(txtViewTipoDelito.getId(), ConstraintSet.START, guideline5Porciento.getId(), ConstraintSet.START, 0);
            constraintSet.connect(txtViewTipoDelito.getId(), ConstraintSet.END, guideline45Porciento.getId(), ConstraintSet.END, 0);
            constraintSet.connect(txtViewTipoDelito.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
            constraintSet.connect(txtViewTipoDelito.getId(), ConstraintSet.BOTTOM, viewSeparador.getId(), ConstraintSet.TOP, 0);
            // Fin de Textview "TipoDelito"

            // Textview "FechaReporte"
            TextView txtViewFechaReporte = new TextView(new ContextThemeWrapper(this.getActivity(), R.style.TxtViewTransparente));
            txtViewFechaReporte.setId(View.generateViewId());

            txtViewFechaReporte.setText(fechaDelito);
            txtViewFechaReporte.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_parrafo_medium));
            txtViewFechaReporte.setGravity(Gravity.END|Gravity.CENTER_VERTICAL);

            mConsLytPrincipalReporte.addView(txtViewFechaReporte);

            constraintSet.constrainHeight(txtViewFechaReporte.getId(), ConstraintSet.PARENT_ID);

            constraintSet.connect(txtViewFechaReporte.getId(), ConstraintSet.START, guideline50Porciento.getId(), ConstraintSet.START, 0);
            constraintSet.connect(txtViewFechaReporte.getId(), ConstraintSet.END, guideline95Porciento.getId(), ConstraintSet.END, 0);
            constraintSet.connect(txtViewFechaReporte.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
            constraintSet.connect(txtViewFechaReporte.getId(), ConstraintSet.BOTTOM, viewSeparador.getId(), ConstraintSet.TOP, 0);
            // Fin de Textview "FechaReporte"

            // Textview "UbicacionDelito"
            TextView txtViewUbicacionDelito = new TextView(new ContextThemeWrapper(this.getActivity(), R.style.TxtViewTransparente));
            txtViewUbicacionDelito.setId(View.generateViewId());

            txtViewUbicacionDelito.setText(ubicacionDelito);
            txtViewFechaReporte.setGravity(Gravity.START|Gravity.CENTER_VERTICAL);

            mConsLytPrincipalReporte.addView(txtViewUbicacionDelito);

            constraintSet.constrainHeight(txtViewUbicacionDelito.getId(), ConstraintSet.PARENT_ID);

            constraintSet.connect(txtViewUbicacionDelito.getId(), ConstraintSet.START, guideline5Porciento.getId(), ConstraintSet.START, 0);
            constraintSet.connect(txtViewUbicacionDelito.getId(), ConstraintSet.END, guideline95Porciento.getId(), ConstraintSet.END, 0);
            constraintSet.connect(txtViewUbicacionDelito.getId(), ConstraintSet.TOP, viewSeparador.getId(), ConstraintSet.BOTTOM, 0);
            constraintSet.connect(txtViewUbicacionDelito.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
            // Fin de Textview "UbicacionDelito"

            constraintSet.applyTo(mConsLytPrincipalReporte);

        }
    }

    @Override
    public void onClick(View view) {

        getActivity().getSupportFragmentManager().popBackStack();
    }
}