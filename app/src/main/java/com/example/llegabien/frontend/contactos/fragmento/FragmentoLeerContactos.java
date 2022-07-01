package com.example.llegabien.frontend.contactos.fragmento;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;

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

import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.llegabien.R;
import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.usuario.usuario;
import com.example.llegabien.frontend.usuario.fragmento.FragmentoEditarPerfilUsuario;

public class FragmentoLeerContactos extends Fragment implements View.OnClickListener {

    private ConstraintLayout mConsLytScrollView, mConsLytPrincipalContacto;
    private View mViewAuxiliar;
    private Button mBtnRegersar;
    private usuario Usuario;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this.getActivity() fragment
        View root = inflater.inflate(R.layout.fragmento_leer_contactos, container, false);

        //wiring up
        mConsLytScrollView = root.findViewById(R.id.consLyt_scrollView_leerContactos);
        mViewAuxiliar = root.findViewById(R.id.view2_leerContactos);
        mBtnRegersar = root.findViewById(R.id.button_regresar_leerContactos);

        //listeners
        mBtnRegersar.setOnClickListener(this);

        Usuario = Preferences.getSavedObjectFromPreference(getActivity(), PREFERENCE_USUARIO, usuario.class);
        crearVistaContacto();

        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void crearVistaContacto() {
        ConstraintSet constraintSet = new ConstraintSet();

        for (int i = 0; i < Usuario.getContacto().size(); i++) {
            String sI = String.valueOf(i);
            Log.v("QUICKSTART", "Estoy en LEER contactos, id 11: " + Integer.valueOf(String.valueOf(i)+String.valueOf(i)));

            // ConstraintLayout principal
            constraintSet.clone(mConsLytScrollView);
            mConsLytPrincipalContacto = new ConstraintLayout(this.getActivity());
            mConsLytPrincipalContacto.setId(View.generateViewId());

            mConsLytScrollView.addView(mConsLytPrincipalContacto);

            mConsLytScrollView.setClickable(true);
            mConsLytScrollView.setOnClickListener(this);
            mConsLytScrollView.setContentDescription(String.valueOf(i));

            constraintSet.connect(mConsLytPrincipalContacto.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
            constraintSet.connect(mConsLytPrincipalContacto.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
            constraintSet.connect(mConsLytPrincipalContacto.getId(), ConstraintSet.TOP, mViewAuxiliar.getId(), ConstraintSet.BOTTOM, 0);
            constraintSet.connect(mConsLytPrincipalContacto.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

            constraintSet.constrainWidth(mConsLytPrincipalContacto.getId(), ConstraintSet.PARENT_ID);
            constraintSet.constrainHeight(mConsLytPrincipalContacto.getId(), 0);

            constraintSet.setDimensionRatio(mConsLytPrincipalContacto.getId(), "12:10");

            constraintSet.setVerticalBias(mConsLytPrincipalContacto.getId(), 0.0f);

            mViewAuxiliar = mConsLytPrincipalContacto;

            constraintSet.applyTo(mConsLytScrollView);
            // Fin ConstraintLayout principal

            // Se cambia de ConstraintLayout
            constraintSet.clone(mConsLytPrincipalContacto);

            // Separador
            View viewSeparador = new View(new ContextThemeWrapper(this.getActivity(), R.style.ViewSeparador));
            viewSeparador.setId(View.generateViewId());
            viewSeparador.setBackgroundColor(Color.parseColor("#AFBED8"));

            mConsLytPrincipalContacto.addView(viewSeparador);

            constraintSet.connect(viewSeparador.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
            constraintSet.connect(viewSeparador.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
            constraintSet.connect(viewSeparador.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
            constraintSet.connect(viewSeparador.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

            constraintSet.setDimensionRatio(viewSeparador.getId(), "100:1");

            constraintSet.setVerticalBias(viewSeparador.getId(), 0.1f);
            //Fin de Separador

            // Textview "Número de contacto"
            TextView txtViewNumContacto = new TextView(new ContextThemeWrapper(this.getActivity(), R.style.TxtViewBlanco));
            txtViewNumContacto.setId(View.generateViewId());

            String stringNumContacto = getResources().getString(R.string.contactoEmergencia_registro4) + " " + (i);
            txtViewNumContacto.setText(stringNumContacto);
            txtViewNumContacto.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_titulo_small));
            txtViewNumContacto.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.azul_claro));

            mConsLytPrincipalContacto.addView(txtViewNumContacto);

            constraintSet.constrainWidth(txtViewNumContacto.getId(), ConstraintSet.WRAP_CONTENT);
            constraintSet.constrainHeight(txtViewNumContacto.getId(), 0);

            constraintSet.connect(txtViewNumContacto.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
            constraintSet.connect(txtViewNumContacto.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
            constraintSet.connect(txtViewNumContacto.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
            constraintSet.connect(txtViewNumContacto.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

            constraintSet.setDimensionRatio(txtViewNumContacto.getId(), "5:1");

            constraintSet.setVerticalBias(txtViewNumContacto.getId(), 0.04f);
            // Fin de Textview "Número de contacto"

            // ConstraintLayout "Información del contacto"
            ConstraintLayout consLytInformacionContacto = new ConstraintLayout(this.getActivity());

            consLytInformacionContacto.setId(View.generateViewId());
            consLytInformacionContacto.setBackgroundColor(getResources().getColor(R.color.blanco));
            consLytInformacionContacto.setClickable(true);
            consLytInformacionContacto.setOnClickListener(this);
            consLytInformacionContacto.setContentDescription(String.valueOf(i));

            mConsLytPrincipalContacto.addView(consLytInformacionContacto);

            constraintSet.constrainWidth(consLytInformacionContacto.getId(), ConstraintSet.PARENT_ID);
            constraintSet.constrainHeight(consLytInformacionContacto.getId(), 0);

            constraintSet.connect(consLytInformacionContacto.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
            constraintSet.connect(consLytInformacionContacto.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
            constraintSet.connect(consLytInformacionContacto.getId(), ConstraintSet.TOP, txtViewNumContacto.getId(), ConstraintSet.BOTTOM, 0);
            constraintSet.connect(consLytInformacionContacto.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

            constraintSet.setDimensionRatio(consLytInformacionContacto.getId(), "7:4");

            constraintSet.setVerticalBias(consLytInformacionContacto.getId(), 0.5f);
            // Fin de ConstraintLayout "Información del contacto"

            // Para agregar cambios a "mConstLytPrincipalContacto"
            constraintSet.applyTo(mConsLytPrincipalContacto);

            // Se cambia de ConstraintLayout en el ConstraintSet
            constraintSet.clone(consLytInformacionContacto);

            // Guideline "10 porciento"
            Guideline guideline10Porciento = new Guideline(this.getActivity());
            guideline10Porciento.setId(View.generateViewId());

            constraintSet.constrainWidth(guideline10Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.constrainHeight(guideline10Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.setGuidelinePercent(guideline10Porciento.getId(), 0.10f);

            constraintSet.create(guideline10Porciento.getId(), ConstraintSet.VERTICAL_GUIDELINE);
            // Fin de guideline "10 porciento"

            // Guideline "33 porciento"
            Guideline guideline33Porciento = new Guideline(this.getActivity());
            guideline33Porciento.setId(View.generateViewId());

            constraintSet.constrainWidth(guideline33Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.constrainHeight(guideline33Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.setGuidelinePercent(guideline33Porciento.getId(), 0.33f);

            constraintSet.create(guideline33Porciento.getId(), ConstraintSet.VERTICAL_GUIDELINE);
            // Fin de guideline "33 porciento"

            // Guideline "35 porciento"
            Guideline guideline35Porciento = new Guideline(this.getActivity());
            guideline35Porciento.setId(View.generateViewId());

            constraintSet.constrainWidth(guideline35Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.constrainHeight(guideline35Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.setGuidelinePercent(guideline35Porciento.getId(), 0.35f);

            constraintSet.create(guideline35Porciento.getId(), ConstraintSet.VERTICAL_GUIDELINE);
            // Fin de guideline "35 porciento"

            // Guideline "90 porciento"
            Guideline guideline90Porciento = new Guideline(this.getActivity());
            guideline90Porciento.setId(View.generateViewId());

            constraintSet.constrainWidth(guideline90Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.constrainHeight(guideline90Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.setGuidelinePercent(guideline90Porciento.getId(), 0.90f);

            constraintSet.create(guideline90Porciento.getId(), ConstraintSet.VERTICAL_GUIDELINE);
            // Fin de guideline "90 porciento"

            // Textview "Nombre del contacto"
            TextView txtViewNombreContacto = new TextView(new ContextThemeWrapper(this.getActivity(), R.style.TxtViewTransparente_LetraAzulOscuro));

            //        txtViewNombreContacto.setId(View.generateViewId());
            txtViewNombreContacto.setId(View.generateViewId());

            txtViewNombreContacto.setText(getResources().getString(R.string.nombreCompletoTextView));
            txtViewNombreContacto.setClickable(true);
            txtViewNombreContacto.setFocusable(true);
            txtViewNombreContacto.setOnClickListener(this);
            txtViewNombreContacto.setContentDescription(String.valueOf(i));

            consLytInformacionContacto.addView(txtViewNombreContacto);

            constraintSet.connect(txtViewNombreContacto.getId(), ConstraintSet.START, guideline10Porciento.getId(), ConstraintSet.START, 0);
            constraintSet.connect(txtViewNombreContacto.getId(), ConstraintSet.END, guideline90Porciento.getId(), ConstraintSet.END, 0);
            constraintSet.connect(txtViewNombreContacto.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
            constraintSet.connect(txtViewNombreContacto.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

            constraintSet.setDimensionRatio(txtViewNombreContacto.getId(), "10:1");

            constraintSet.setVerticalBias(txtViewNombreContacto.getId(), 0.1f);
            // Fin de textview "Nombre del contacto"

            // TextView "Dato nombre del contacto"
            TextView txtViewDatoNombreContacto = new TextView(new ContextThemeWrapper(this.getActivity(), R.style.EditTextTransparente));

            txtViewDatoNombreContacto.setId(View.generateViewId());
            txtViewDatoNombreContacto.setText(Usuario.getContacto().get(i).getNombre());
            txtViewDatoNombreContacto.setClickable(true);
            txtViewDatoNombreContacto.setFocusable(true);
            txtViewDatoNombreContacto.setOnClickListener(this);
            txtViewDatoNombreContacto.setContentDescription(String.valueOf(i));

            consLytInformacionContacto.addView(txtViewDatoNombreContacto);

            constraintSet.connect(txtViewDatoNombreContacto.getId(), ConstraintSet.START, guideline10Porciento.getId(), ConstraintSet.START, 0);
            constraintSet.connect(txtViewDatoNombreContacto.getId(), ConstraintSet.END, guideline90Porciento.getId(), ConstraintSet.END, 0);
            constraintSet.connect(txtViewDatoNombreContacto.getId(), ConstraintSet.TOP, txtViewNombreContacto.getId(), ConstraintSet.BOTTOM, 0);
            constraintSet.connect(txtViewDatoNombreContacto.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

            constraintSet.setDimensionRatio(txtViewDatoNombreContacto.getId(), "6:1");

            constraintSet.setVerticalBias(txtViewDatoNombreContacto.getId(), 0.0f);
            // Fin de textView "Dato nombre del contacto"

            // Textview "Numero telefonico del contacto"
            TextView txtViewNumTelefonicoContacto = new TextView(new ContextThemeWrapper(this.getActivity(), R.style.TxtViewTransparente_LetraAzulOscuro));

            txtViewNumTelefonicoContacto.setId(View.generateViewId());

            txtViewNumTelefonicoContacto.setText(getResources().getString(R.string.numTelefónicoTextView));

            txtViewNumTelefonicoContacto.setClickable(true);
            txtViewNumTelefonicoContacto.setFocusable(View.FOCUSABLE_AUTO);
            txtViewNumTelefonicoContacto.setOnClickListener(this);
            txtViewNumTelefonicoContacto.setContentDescription(String.valueOf(i));

            consLytInformacionContacto.addView(txtViewNumTelefonicoContacto);

            constraintSet.connect(txtViewNumTelefonicoContacto.getId(), ConstraintSet.START, guideline10Porciento.getId(), ConstraintSet.START, 0);
            constraintSet.connect(txtViewNumTelefonicoContacto.getId(), ConstraintSet.END, guideline90Porciento.getId(), ConstraintSet.END, 0);
            constraintSet.connect(txtViewNumTelefonicoContacto.getId(), ConstraintSet.TOP, txtViewDatoNombreContacto.getId(), ConstraintSet.BOTTOM, 0);
            constraintSet.connect(txtViewNumTelefonicoContacto.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

            constraintSet.setDimensionRatio(txtViewNumTelefonicoContacto.getId(), "10:1");

            constraintSet.setVerticalBias(txtViewNumTelefonicoContacto.getId(), 0.1f);
            // Fin de textview "Numero telefonico del contacto"

            // TextView "Dato número telefónico del contacto"
            TextView txtViewDatoNumTelefonicoContacto = new TextView(this.getActivity(), null, 0, R.style.EditTextTransparente);

            txtViewDatoNumTelefonicoContacto.setId(View.generateViewId());
            txtViewDatoNumTelefonicoContacto.setText(Usuario.getContacto().get(i).getTelCelular().substring(2));
            txtViewDatoNumTelefonicoContacto.setClickable(true);
            txtViewDatoNumTelefonicoContacto.setFocusable(true);
            txtViewDatoNumTelefonicoContacto.setOnClickListener(this);
            txtViewDatoNumTelefonicoContacto.setContentDescription(String.valueOf(i));

            consLytInformacionContacto.addView(txtViewDatoNumTelefonicoContacto);

            constraintSet.connect(txtViewDatoNumTelefonicoContacto.getId(), ConstraintSet.START, guideline35Porciento.getId(), ConstraintSet.START, 0);
            constraintSet.connect(txtViewDatoNumTelefonicoContacto.getId(), ConstraintSet.END, guideline90Porciento.getId(), ConstraintSet.END, 0);
            constraintSet.connect(txtViewDatoNumTelefonicoContacto.getId(), ConstraintSet.TOP, txtViewNumTelefonicoContacto.getId(), ConstraintSet.BOTTOM, 0);
            constraintSet.connect(txtViewDatoNumTelefonicoContacto.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

            constraintSet.setDimensionRatio(txtViewDatoNumTelefonicoContacto.getId(), "42:10");

            constraintSet.setVerticalBias(txtViewDatoNumTelefonicoContacto.getId(), 0.0f);
            // Fin de textView "Dato número telefónico del contacto"

            // TextView "Dato country code"
            TextView txtViewDatoCountryCode = new TextView(new ContextThemeWrapper(this.getActivity(), R.style.EditTextTransparente));

            txtViewDatoCountryCode.setId(View.generateViewId());
            txtViewDatoCountryCode.setText(Usuario.getContacto().get(i).getTelCelular().substring(0, 2));
            txtViewDatoCountryCode.setClickable(true);
            txtViewDatoCountryCode.setFocusable(true);
            txtViewDatoCountryCode.setOnClickListener(this);
            txtViewDatoCountryCode.setContentDescription(String.valueOf(i));

            consLytInformacionContacto.addView(txtViewDatoCountryCode);

            constraintSet.connect(txtViewDatoCountryCode.getId(), ConstraintSet.START, guideline10Porciento.getId(), ConstraintSet.START, 0);
            constraintSet.connect(txtViewDatoCountryCode.getId(), ConstraintSet.END, guideline33Porciento.getId(), ConstraintSet.END, 0);
            constraintSet.connect(txtViewDatoCountryCode.getId(), ConstraintSet.TOP, txtViewNumTelefonicoContacto.getId(), ConstraintSet.BOTTOM, 0);
            constraintSet.connect(txtViewDatoCountryCode.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

            constraintSet.setDimensionRatio(txtViewDatoCountryCode.getId(), "35:20");

            constraintSet.setVerticalBias(txtViewDatoCountryCode.getId(), 0.0f);
            // Fin de textView "Daro country code"

            // Para agregar cambios a "mConstLytPrincipalContacto"
            constraintSet.applyTo(consLytInformacionContacto);
        }

        // Se cambia de ConstraintLayout
        constraintSet.clone(mConsLytScrollView);

        // Separador
        View viewSeparadorFinal = new View(new ContextThemeWrapper(this.getActivity(), R.style.ViewSeparadorAuxiliar));

        viewSeparadorFinal.setId(View.generateViewId());

        mConsLytScrollView.addView(viewSeparadorFinal);

        constraintSet.connect(viewSeparadorFinal.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
        constraintSet.connect(viewSeparadorFinal.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
        constraintSet.connect(viewSeparadorFinal.getId(), ConstraintSet.TOP, mConsLytPrincipalContacto.getId(), ConstraintSet.BOTTOM, 0);
        constraintSet.connect(viewSeparadorFinal.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

        constraintSet.setDimensionRatio(viewSeparadorFinal.getId(), "4:1");

        constraintSet.setVerticalBias(viewSeparadorFinal.getId(), 0.0f);
        //Fin de Separador

        // Para agregar cambios a "mConstLytPrincipalContacto"
        constraintSet.applyTo(mConsLytScrollView);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_regresar_leerContactos)
            getActivity().finish();
        else {
            TextView txtViewDatoNombreContacto = new TextView(new ContextThemeWrapper(this.getActivity(), R.style.EditTextTransparente));
            Log.v("QUICKSTART", "Estoy en LEER contactos, ID: " + view.getId());

            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            FragmentoEditarContacto fragmentoEditarContacto = new FragmentoEditarContacto(Integer.valueOf(String.valueOf(view.getContentDescription())), Usuario);
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
            fragmentTransaction.replace(R.id.fragmentContainerView_leerEditarContactos, fragmentoEditarContacto).commit();
            fragmentTransaction.addToBackStack(null);
        }
    }
}