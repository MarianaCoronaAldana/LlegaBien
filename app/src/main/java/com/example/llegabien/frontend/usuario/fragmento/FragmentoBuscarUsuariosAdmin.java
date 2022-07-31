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
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.llegabien.R;
import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.usuario.UsuarioDAO;
import com.example.llegabien.backend.usuario.usuario;

import android.widget.TextView;

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
        crearVistaListaUsuarios_admin();

        return root;
    }

    // FUNCIONES LISTENER //
    @Override
    public void onClick(View view) {
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
    private void crearVistaListaUsuarios_admin() {
        ConstraintSet constraintSet = new ConstraintSet();

        for (int i = 0; i < usuarios.size(); i++) {
            // ConstraintLayout principal
            constraintSet.clone(mConsLytScrollView);

            ConstraintLayout consLytPrincipalInfoUsuario = new ConstraintLayout(this.requireActivity());
            consLytPrincipalInfoUsuario.setId(View.generateViewId());

            consLytPrincipalInfoUsuario.setBackground(requireActivity().getResources().getDrawable(R.drawable.bkgd_esquinas_redondeadas));
            consLytPrincipalInfoUsuario.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.morado_claro));
            consLytPrincipalInfoUsuario.setElevation(10f);

            consLytPrincipalInfoUsuario.setClickable(true);
            consLytPrincipalInfoUsuario.setOnClickListener(this);
            consLytPrincipalInfoUsuario.setContentDescription(usuarios.get(i).getCorreoElectronico());

            mConsLytScrollView.addView(consLytPrincipalInfoUsuario);
            constraintSet.connect(consLytPrincipalInfoUsuario.getId(), ConstraintSet.START, mGuideline10Porciento.getId(), ConstraintSet.START, 0);
            constraintSet.connect(consLytPrincipalInfoUsuario.getId(), ConstraintSet.END, mGuideline90Porciente.getId(), ConstraintSet.END, 0);
            constraintSet.connect(consLytPrincipalInfoUsuario.getId(), ConstraintSet.TOP, mViewAuxiliar.getId(), ConstraintSet.BOTTOM, 0);
            constraintSet.connect(consLytPrincipalInfoUsuario.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

            constraintSet.constrainWidth(consLytPrincipalInfoUsuario.getId(), ConstraintSet.PARENT_ID);
            constraintSet.constrainHeight(consLytPrincipalInfoUsuario.getId(), 0);

            constraintSet.setDimensionRatio(consLytPrincipalInfoUsuario.getId(), "7:2");

            constraintSet.setVerticalBias(consLytPrincipalInfoUsuario.getId(), 0.0f);
            //Fin de ConstraintLayout principal

            // Separador
            View viewSeparadorFinal = new View(new ContextThemeWrapper(this.requireActivity(), R.style.ViewSeparadorAuxiliar));

            viewSeparadorFinal.setId(View.generateViewId());

            mConsLytScrollView.addView(viewSeparadorFinal);

            constraintSet.connect(viewSeparadorFinal.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
            constraintSet.connect(viewSeparadorFinal.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
            constraintSet.connect(viewSeparadorFinal.getId(), ConstraintSet.TOP, consLytPrincipalInfoUsuario.getId(), ConstraintSet.BOTTOM, 0);
            constraintSet.connect(viewSeparadorFinal.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

            constraintSet.setDimensionRatio(viewSeparadorFinal.getId(), "15:1");

            constraintSet.setVerticalBias(viewSeparadorFinal.getId(), 0.0f);
            //Fin de Separador

            mViewAuxiliar = viewSeparadorFinal;

            constraintSet.applyTo(mConsLytScrollView);
            // Fin ConstraintLayout principal

            // Se cambia de ConstraintLayout
            constraintSet.clone(consLytPrincipalInfoUsuario);

             // Guideline "4 porciento"
            Guideline guideline4Porciento = new Guideline(this.requireActivity());
            guideline4Porciento.setId(View.generateViewId());

            constraintSet.constrainWidth(guideline4Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.constrainHeight(guideline4Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.setGuidelinePercent(guideline4Porciento.getId(), 0.04f);

            constraintSet.create(guideline4Porciento.getId(), ConstraintSet.VERTICAL_GUIDELINE);
            // Fin de guideline "4 porciento"

            // Guideline "9 porciento"
            Guideline guideline9Porciento = new Guideline(this.requireActivity());
            guideline9Porciento.setId(View.generateViewId());

            constraintSet.constrainWidth(guideline9Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.constrainHeight(guideline9Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.setGuidelinePercent(guideline9Porciento.getId(), 0.09f);

            constraintSet.create(guideline9Porciento.getId(), ConstraintSet.VERTICAL_GUIDELINE);
            // Fin de guideline "9 porciento"

            // Guideline "12 porciento"
            Guideline guideline12Porciento = new Guideline(this.requireActivity());
            guideline12Porciento.setId(View.generateViewId());

            constraintSet.constrainWidth(guideline12Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.constrainHeight(guideline12Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.setGuidelinePercent(guideline12Porciento.getId(), 0.12f);

            constraintSet.create(guideline12Porciento.getId(), ConstraintSet.VERTICAL_GUIDELINE);
            // Fin de guideline "12 porciento"

            // Guideline "90 porciento"
            Guideline guideline90Porciento = new Guideline(this.requireActivity());
            guideline90Porciento.setId(View.generateViewId());

            constraintSet.constrainWidth(guideline90Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.constrainHeight(guideline90Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.setGuidelinePercent(guideline90Porciento.getId(), 0.90f);

            constraintSet.create(guideline90Porciento.getId(), ConstraintSet.VERTICAL_GUIDELINE);
            // Fin de guideline "90 porciento"

            // Guideline "92 porciento"
            Guideline guideline92Porciento = new Guideline(this.requireActivity());
            guideline92Porciento.setId(View.generateViewId());

            constraintSet.constrainWidth(guideline92Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.constrainHeight(guideline92Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.setGuidelinePercent(guideline92Porciento.getId(), 0.92f);

            constraintSet.create(guideline92Porciento.getId(), ConstraintSet.VERTICAL_GUIDELINE);
            // Fin de guideline "92 porciento"

            // Guideline "97 porciento"
            Guideline guideline97Porciento = new Guideline(this.requireActivity());
            guideline97Porciento.setId(View.generateViewId());

            constraintSet.constrainWidth(guideline97Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.constrainHeight(guideline97Porciento.getId(), ConstraintSet.PARENT_ID);
            constraintSet.setGuidelinePercent(guideline97Porciento.getId(), 0.97f);

            constraintSet.create(guideline97Porciento.getId(), ConstraintSet.VERTICAL_GUIDELINE);
            // Fin de guideline "97 porciento"

            // Separador
            View viewSeparadorConsLytInfoUsuario = new View(new ContextThemeWrapper(this.requireActivity(), R.style.ViewSeparador));
            viewSeparadorConsLytInfoUsuario.setId(View.generateViewId());
            viewSeparadorConsLytInfoUsuario.setBackgroundColor(requireActivity().getResources().getColor(R.color.negro));

            consLytPrincipalInfoUsuario.addView(viewSeparadorConsLytInfoUsuario);

            constraintSet.connect(viewSeparadorConsLytInfoUsuario.getId(), ConstraintSet.START, guideline12Porciento.getId(), ConstraintSet.START, 0);
            constraintSet.connect(viewSeparadorConsLytInfoUsuario.getId(), ConstraintSet.END, guideline90Porciento.getId(), ConstraintSet.END, 0);
            constraintSet.connect(viewSeparadorConsLytInfoUsuario.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
            constraintSet.connect(viewSeparadorConsLytInfoUsuario.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

            constraintSet.setDimensionRatio(viewSeparadorConsLytInfoUsuario.getId(), "200:1");

            constraintSet.setVerticalBias(viewSeparadorConsLytInfoUsuario.getId(), 0.5f);
            //Fin de Separador

            // View "Icon usuario"
            View iconUsuario = new TextView(new ContextThemeWrapper(this.requireActivity(), R.style.BkgdIcon));
            iconUsuario.setId(View.generateViewId());

            iconUsuario.setBackground(getResources().getDrawable(R.drawable.bkgd_icon_usuario));

            consLytPrincipalInfoUsuario.addView(iconUsuario);

            constraintSet.connect(iconUsuario.getId(), ConstraintSet.START, guideline4Porciento.getId(), ConstraintSet.START, 0);
            constraintSet.connect(iconUsuario.getId(), ConstraintSet.END, guideline9Porciento.getId(), ConstraintSet.END, 0);
            constraintSet.connect(iconUsuario.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
            constraintSet.connect(iconUsuario.getId(), ConstraintSet.BOTTOM, viewSeparadorConsLytInfoUsuario.getId(), ConstraintSet.TOP, 0);

            constraintSet.setDimensionRatio(iconUsuario.getId(), "1:1");
            // View "Icon usuario"
            
            // View "Icon email"
            View iconEmail = new TextView(new ContextThemeWrapper(this.requireActivity(), R.style.BkgdIcon));
            iconEmail.setId(View.generateViewId());

            iconEmail.setBackground(getResources().getDrawable(R.drawable.bkgd_icon_email));

            consLytPrincipalInfoUsuario.addView(iconEmail);

            constraintSet.connect(iconEmail.getId(), ConstraintSet.START, guideline4Porciento.getId(), ConstraintSet.START, 0);
            constraintSet.connect(iconEmail.getId(), ConstraintSet.END, guideline9Porciento.getId(), ConstraintSet.END, 0);
            constraintSet.connect(iconEmail.getId(), ConstraintSet.TOP, viewSeparadorConsLytInfoUsuario.getId(), ConstraintSet.BOTTOM, 0);
            constraintSet.connect(iconEmail.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

            constraintSet.setDimensionRatio(iconEmail.getId(), "1:1");
            // View "Icon email"

            // Textview "Nombre usuario"
            TextView txtViewNombreUsuario = new TextView(new ContextThemeWrapper(this.requireActivity(), R.style.TxtViewTransparente));
            txtViewNombreUsuario.setId(View.generateViewId());

            String nombre = usuarios.get(i).getNombre()+" "+usuarios.get(i).getApellidos();
            txtViewNombreUsuario.setText(nombre);
            txtViewNombreUsuario.setTypeface(Typeface.DEFAULT_BOLD);
            txtViewNombreUsuario.setGravity(Gravity.START|Gravity.CENTER_VERTICAL);
            txtViewNombreUsuario.setMaxLines(1);

            consLytPrincipalInfoUsuario.addView(txtViewNombreUsuario);

            constraintSet.constrainHeight(txtViewNombreUsuario.getId(), ConstraintSet.PARENT_ID);

            constraintSet.connect(txtViewNombreUsuario.getId(), ConstraintSet.START, guideline12Porciento.getId(), ConstraintSet.START, 0);
            constraintSet.connect(txtViewNombreUsuario.getId(), ConstraintSet.END, guideline90Porciento.getId(), ConstraintSet.END, 0);
            constraintSet.connect(txtViewNombreUsuario.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
            constraintSet.connect(txtViewNombreUsuario.getId(), ConstraintSet.BOTTOM, viewSeparadorConsLytInfoUsuario.getId(), ConstraintSet.TOP, 0);
            // Fin de Textview "Nombre usuario"

            // Textview "Correo usuario"
            TextView txtViewCorreoUsuario = new TextView(new ContextThemeWrapper(this.requireActivity(), R.style.TxtViewTransparente));
            txtViewCorreoUsuario.setId(View.generateViewId());

            txtViewCorreoUsuario.setText(usuarios.get(i).getCorreoElectronico());
            txtViewCorreoUsuario.setGravity(Gravity.START|Gravity.CENTER_VERTICAL);
            txtViewCorreoUsuario.setMaxLines(1);

            consLytPrincipalInfoUsuario.addView(txtViewCorreoUsuario);

            constraintSet.constrainHeight(txtViewCorreoUsuario.getId(), ConstraintSet.PARENT_ID);

            constraintSet.connect(txtViewCorreoUsuario.getId(), ConstraintSet.START, guideline12Porciento.getId(), ConstraintSet.START, 0);
            constraintSet.connect(txtViewCorreoUsuario.getId(), ConstraintSet.END, guideline90Porciento.getId(), ConstraintSet.END, 0);
            constraintSet.connect(txtViewCorreoUsuario.getId(), ConstraintSet.TOP, viewSeparadorConsLytInfoUsuario.getId(), ConstraintSet.BOTTOM, 0);
            constraintSet.connect(txtViewCorreoUsuario.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
            // Fin de Textview "Correo usuario"

            // View "Icon arrow"
            View iconArrow = new TextView(new ContextThemeWrapper(this.requireActivity(), R.style.BkgdIcon));
            iconArrow.setId(View.generateViewId());

            iconArrow.setBackground(getResources().getDrawable(R.drawable.bkgd_icon_right_arrow));

            consLytPrincipalInfoUsuario.addView(iconArrow);

            constraintSet.connect(iconArrow.getId(), ConstraintSet.START, guideline92Porciento.getId(), ConstraintSet.START, 0);
            constraintSet.connect(iconArrow.getId(), ConstraintSet.END, guideline97Porciento.getId(), ConstraintSet.END, 0);
            constraintSet.connect(iconArrow.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
            constraintSet.connect(iconArrow.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

            constraintSet.setDimensionRatio(iconArrow.getId(),"1:1");
            // View "Icon arrow"

            constraintSet.applyTo(consLytPrincipalInfoUsuario);
        }
    }


}