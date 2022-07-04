package com.example.llegabien.frontend.usuario.fragmento;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_ADMIN;
import static com.example.llegabien.backend.app.Preferences.PREFERENCE_EDITANDO_USUARIO_CON_ADMIN;
import static com.example.llegabien.backend.app.Preferences.PREFERENCE_USUARIO;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.llegabien.R;
import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.reporte.reporte;
import com.example.llegabien.backend.usuario.UsuarioDAO;
import com.example.llegabien.backend.usuario.usuario;
import com.example.llegabien.frontend.contactos.fragmento.FragmentoEditarContacto;

import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import io.realm.RealmResults;

public class FragmentoBuscarUsuariosAdmin extends Fragment implements View.OnClickListener{

    private ConstraintLayout mConsLytScrollView;
    private View mViewAuxiliar;
    private Guideline mGuideline10Porciento, mGuideline90Porciente;
    private RealmResults<usuario> usuarios;
    private usuario UsuarioAdmin;

    public FragmentoBuscarUsuariosAdmin() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragmento_buscar_usuarios_admin, container, false);

        //wiring up
        mConsLytScrollView = root.findViewById(R.id.consLyt_scrollView_listaUsuarios_admin);
        mViewAuxiliar = root.findViewById(R.id.view2_listaUsuarios_admin);
        mGuideline10Porciento = root.findViewById(R.id.guideline1_textView_editView_scrollView_listaUsuarios_admin);
        mGuideline90Porciente = root.findViewById(R.id.guideline2_textView_editView_scrollView_listaUsuarios_admin);
        Button mBtnRegresar = root.findViewById(R.id.button_regresar_listaUsuarios_admin);

        //listeners
        mBtnRegresar.setOnClickListener(this);

        UsuarioAdmin = Preferences.getSavedObjectFromPreference(requireActivity(), PREFERENCE_USUARIO, usuario.class);
        UsuarioDAO usuario_DAO = new UsuarioDAO(this.getContext());
        usuarios = usuario_DAO.obtenerTodosUsuarios();
        crearVistalistaUsuarios_admin();

        return root;
    }

    // FUNCIONES LISTENER //
    @Override
    public void onClick(View view) {
        Log.v("QUICKSTART", "ME HICIERON CLICK :D");
        if (view.getId() == R.id.button_regresar_listaUsuarios_admin)
            requireActivity().getSupportFragmentManager().popBackStack();
        else
            editarUsuario(view);
    }

    private void editarUsuario(View view) {
        UsuarioDAO usuarioDAO = new UsuarioDAO(this.getContext());

        Preferences.savePreferenceBoolean(this.requireActivity(), true, PREFERENCE_EDITANDO_USUARIO_CON_ADMIN);

        UsuarioAdmin = usuarioDAO.readUsuarioPorCorreo(UsuarioAdmin.getCorreoElectronico());
        Preferences.savePreferenceObjectRealm(requireActivity(), PREFERENCE_ADMIN, UsuarioAdmin);

        usuario Usuario = usuarioDAO.readUsuarioPorCorreo(view.getContentDescription().toString());
        Preferences.savePreferenceObjectRealm(requireActivity(), PREFERENCE_USUARIO, Usuario);

        // Pasar al gragmento donde se puede editar
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        FragmentoEditarPerfilUsuario fragmentoEditarPerfilUsuario = new FragmentoEditarPerfilUsuario();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.fragment_configuracion, fragmentoEditarPerfilUsuario).commit();
        fragmentTransaction.addToBackStack(null);
    }

    //OTRAS FUNCIONES//

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void crearVistalistaUsuarios_admin() {
        ConstraintSet constraintSet = new ConstraintSet();

        for (int i = 0; i < usuarios.size(); i++) {
            // ConstraintLayout principal
            constraintSet.clone(mConsLytScrollView);
            ConstraintLayout consLytPrincipalReporte = new ConstraintLayout(this.requireActivity());
            consLytPrincipalReporte.setId(View.generateViewId());
            consLytPrincipalReporte.setBackground(requireActivity().getResources().getDrawable(R.drawable.bkgd_esquinas_redondeadas));
            consLytPrincipalReporte.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.morado_claro));

            consLytPrincipalReporte.setClickable(true);
            consLytPrincipalReporte.setOnClickListener(this);
            consLytPrincipalReporte.setContentDescription(usuarios.get(i).getCorreoElectronico().toString());

            mConsLytScrollView.addView(consLytPrincipalReporte);
            constraintSet.connect(consLytPrincipalReporte.getId(), ConstraintSet.START, mGuideline10Porciento.getId(), ConstraintSet.START, 0);
            constraintSet.connect(consLytPrincipalReporte.getId(), ConstraintSet.END, mGuideline90Porciente.getId(), ConstraintSet.END, 0);
            constraintSet.connect(consLytPrincipalReporte.getId(), ConstraintSet.TOP, mViewAuxiliar.getId(), ConstraintSet.BOTTOM, 0);
            constraintSet.connect(consLytPrincipalReporte.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

            constraintSet.constrainWidth(consLytPrincipalReporte.getId(), ConstraintSet.PARENT_ID);
            constraintSet.constrainHeight(consLytPrincipalReporte.getId(), 0);

            constraintSet.setDimensionRatio(consLytPrincipalReporte.getId(), "7:2");

            constraintSet.setVerticalBias(consLytPrincipalReporte.getId(), 0.0f);
            //Fin de ConstraintLayout principal

            // Separador
            View viewSeparadorFinal = new View(new ContextThemeWrapper(this.requireActivity(), R.style.ViewSeparadorAuxiliar));

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
            Guideline guideline5Porciento = new Guideline(this.requireActivity());
            guideline5Porciento.setId(View.generateViewId());

            constraintSet.constrainWidth(guideline5Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.constrainHeight(guideline5Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.setGuidelinePercent(guideline5Porciento.getId(), 0.05f);

            constraintSet.create(guideline5Porciento.getId(), ConstraintSet.VERTICAL_GUIDELINE);
            // Fin de guideline "5 porciento"

            // Guideline "95 porciento"
            Guideline guideline95Porciento = new Guideline(this.requireActivity());
            guideline95Porciento.setId(View.generateViewId());

            constraintSet.constrainWidth(guideline95Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.constrainHeight(guideline95Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.setGuidelinePercent(guideline95Porciento.getId(), 0.95f);

            constraintSet.create(guideline95Porciento.getId(), ConstraintSet.VERTICAL_GUIDELINE);
            // Fin de guideline "95 porciento"

            // Guideline "45 porciento"
            Guideline guideline45Porciento = new Guideline(this.requireActivity());
            guideline45Porciento.setId(View.generateViewId());

            constraintSet.constrainWidth(guideline45Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.constrainHeight(guideline45Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.setGuidelinePercent(guideline45Porciento.getId(), 0.45f);

            constraintSet.create(guideline45Porciento.getId(), ConstraintSet.VERTICAL_GUIDELINE);
            // Fin de guideline "45 porciento"

            // Guideline "50 porciento"
            Guideline guideline50Porciento = new Guideline(this.requireActivity());
            guideline50Porciento.setId(View.generateViewId());

            constraintSet.constrainWidth(guideline50Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.constrainHeight(guideline50Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.setGuidelinePercent(guideline50Porciento.getId(), 0.50f);

            constraintSet.create(guideline50Porciento.getId(), ConstraintSet.VERTICAL_GUIDELINE);
            // Fin de guideline "50 porciento"

            // Separador
            View viewSeparador = new View(new ContextThemeWrapper(this.requireActivity(), R.style.ViewSeparador));
            viewSeparador.setId(View.generateViewId());
            viewSeparador.setBackgroundColor(requireActivity().getResources().getColor(R.color.negro));

            consLytPrincipalReporte.addView(viewSeparador);

            constraintSet.connect(viewSeparador.getId(), ConstraintSet.START, guideline5Porciento.getId(), ConstraintSet.START, 0);
            constraintSet.connect(viewSeparador.getId(), ConstraintSet.END, guideline95Porciento.getId(), ConstraintSet.END, 0);
            constraintSet.connect(viewSeparador.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
            constraintSet.connect(viewSeparador.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

            constraintSet.setDimensionRatio(viewSeparador.getId(), "200:1");

            constraintSet.setVerticalBias(viewSeparador.getId(), 0.5f);
            //Fin de Separador

            // Textview "TipoDelito"
            TextView txtViewTipoDelito = new TextView(new ContextThemeWrapper(this.requireActivity(), R.style.TxtViewTransparente));
            txtViewTipoDelito.setId(View.generateViewId());

            txtViewTipoDelito.setText(usuarios.get(i).getNombre()+" "+usuarios.get(i).getApellidos());
            txtViewTipoDelito.setTypeface(Typeface.DEFAULT_BOLD);
            txtViewTipoDelito.setGravity(Gravity.START|Gravity.CENTER_VERTICAL);
            txtViewTipoDelito.setMaxLines(1);

            consLytPrincipalReporte.addView(txtViewTipoDelito);

            constraintSet.constrainHeight(txtViewTipoDelito.getId(), ConstraintSet.PARENT_ID);

            constraintSet.connect(txtViewTipoDelito.getId(), ConstraintSet.START, guideline5Porciento.getId(), ConstraintSet.START, 0);
            constraintSet.connect(txtViewTipoDelito.getId(), ConstraintSet.END, guideline45Porciento.getId(), ConstraintSet.END, 0);
            constraintSet.connect(txtViewTipoDelito.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
            constraintSet.connect(txtViewTipoDelito.getId(), ConstraintSet.BOTTOM, viewSeparador.getId(), ConstraintSet.TOP, 0);
            // Fin de Textview "TipoDelito"

            // Textview "UbicacionDelito"
            TextView txtViewUbicacionDelito = new TextView(new ContextThemeWrapper(this.requireActivity(), R.style.TxtViewTransparente));
            txtViewUbicacionDelito.setId(View.generateViewId());

            txtViewUbicacionDelito.setText(usuarios.get(i).getCorreoElectronico());
            txtViewUbicacionDelito.setGravity(Gravity.START|Gravity.CENTER_VERTICAL);
            txtViewUbicacionDelito.setMaxLines(1);

            consLytPrincipalReporte.addView(txtViewUbicacionDelito);

            constraintSet.constrainHeight(txtViewUbicacionDelito.getId(), ConstraintSet.PARENT_ID);

            constraintSet.connect(txtViewUbicacionDelito.getId(), ConstraintSet.START, guideline5Porciento.getId(), ConstraintSet.START, 0);
            constraintSet.connect(txtViewUbicacionDelito.getId(), ConstraintSet.END, guideline95Porciento.getId(), ConstraintSet.END, 0);
            constraintSet.connect(txtViewUbicacionDelito.getId(), ConstraintSet.TOP, viewSeparador.getId(), ConstraintSet.BOTTOM, 0);
            constraintSet.connect(txtViewUbicacionDelito.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
            // Fin de Textview "UbicacionDelito"

            constraintSet.applyTo(consLytPrincipalReporte);
        }
    }


}