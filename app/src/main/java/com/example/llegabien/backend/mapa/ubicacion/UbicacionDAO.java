package com.example.llegabien.backend.mapa.ubicacion;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_CALLE;
import static com.example.llegabien.backend.app.Preferences.PREFERENCE_UBICACION;

import android.content.Context;
import android.location.Address;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.mapa.poligonos.Poligono;
import com.example.llegabien.backend.mongoDB.ConectarBD;

import org.bson.types.ObjectId;

import java.util.List;
import java.util.Objects;

import io.realm.ImportFlag;
import io.realm.Realm;
import io.realm.RealmResults;

public class UbicacionDAO {
    Realm realm;
    ConectarBD conectarBD = new ConectarBD();
    UbicacionGeodicacion ubicacionGeodicacion = new UbicacionGeodicacion();
    private final Context mContext;

    public UbicacionDAO(Context context) {
        mContext = context;
    }

    public ubicacion obetenerUbicacionConId(ObjectId IdUbicacion){
        realm = conectarBD.conseguirUsuarioMongoDB();

        if (realm != null) {
            return realm.copyFromRealm(Objects.requireNonNull(realm.where(ubicacion.class).equalTo("_id", IdUbicacion).findFirst()));
        } else
            errorConexion();

        return null;
    }

    public boolean obetenerCalle(String calleString) {
        // TODO: cambiar esto de acuerdo a si la linea sera una linea o poligono.
        Poligono poligono = new Poligono();
        realm = conectarBD.conseguirUsuarioMongoDB();

        Address addressCalle = ubicacionGeodicacion.geocodificarUbiciacion(mContext, calleString);

        if (realm != null) {
            RealmResults<ubicacion> resultadosCalles = realm.where(ubicacion.class).equalTo("tipo", "calle").findAll();
            if (resultadosCalles != null) {
                Preferences.savePreferenceObjectRealm(mContext, PREFERENCE_CALLE,
                        poligono.isUbicacionEnPoligono(resultadosCalles, addressCalle.getLatitude(), addressCalle.getLongitude()));
                return true;
            }
        } else
            errorConexion();
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ubicacion obetenerColoniaDeLista(String coloniaString, List<ubicacion> colonias) {
        ubicacion colonia = colonias.stream().filter(c -> c.getNombre().equals(coloniaString)).findAny().orElse(null);
        if (colonia!= null)
            return  colonia;

        UbicacionGeodicacion ubicacionGeodicacion = new UbicacionGeodicacion();
        Address addressColonia = ubicacionGeodicacion.geocodificarUbiciacion(mContext, coloniaString);
        if (addressColonia != null) {
            Poligono poligono = new Poligono();
            colonia = poligono.isUbicacionEnPoligono(colonias, addressColonia.getLatitude(), addressColonia.getLongitude());
            return colonia;

        }
        return null;
    }

    public List<ubicacion> obetenerColonias() {
        realm = conectarBD.conseguirUsuarioMongoDB();

        if (realm != null)
            return realm.copyFromRealm(realm.where(ubicacion.class).equalTo("tipo", "colonia").findAll());
        else
            errorConexion();

        return null;
    }

    public ubicacion obtenerMunicipio(String municipio) {
        realm = conectarBD.conseguirUsuarioMongoDB();

        if (realm != null)
            return realm.copyFromRealm(Objects.requireNonNull(realm.where(ubicacion.class).equalTo("nombre", municipio).findFirst()));
        else
            errorConexion();

        return null;
    }

    public void obetenerUbicacionConPoligono(String coordenadasPoligono, Context c) {
        realm = conectarBD.conseguirUsuarioMongoDB();

        if (realm != null) {

            ubicacion task = realm.where(ubicacion.class).equalTo("coordenadas_string", coordenadasPoligono).findFirst();

            if (task != null) {
                // Se guarda la ubicacion en Shared Preferences
                Preferences.savePreferenceObjectRealm(c, PREFERENCE_UBICACION, task);
            }
        } else
            errorConexion();

    }

    public boolean obtenerUbicacionBuscada(double latitude, double longitude) {
        Poligono poligono = new Poligono();
        RealmResults<ubicacion> resultadosUbicaciones;
        ubicacion ubicacion;

        realm = conectarBD.conseguirUsuarioMongoDB();

        if (realm != null) {
            resultadosUbicaciones = realm.where(ubicacion.class).equalTo("tipo", "calle").findAll();
            if (resultadosUbicaciones != null) {
            }

            resultadosUbicaciones = realm.where(ubicacion.class).equalTo("tipo", "colonia").findAll();
            if (resultadosUbicaciones != null) {
                ubicacion = poligono.isUbicacionEnPoligono(resultadosUbicaciones, latitude, longitude);
                if (ubicacion != null) {
                    Preferences.savePreferenceObjectRealm(mContext, PREFERENCE_UBICACION, ubicacion);
                    return true;
                }
            }

            resultadosUbicaciones = realm.where(ubicacion.class).equalTo("tipo", "municipio").findAll();
            if (resultadosUbicaciones != null) {
                ubicacion = poligono.isUbicacionEnPoligono(resultadosUbicaciones, latitude, longitude);
                if (ubicacion != null) {
                    Preferences.savePreferenceObjectRealm(mContext, PREFERENCE_UBICACION, ubicacion);
                    return true;
                }
            }
        } else
            errorConexion();

        return false;
    }

    public void updateUbicacion(ubicacion ubicacion) {
        realm = conectarBD.conseguirUsuarioMongoDB();
        if (realm != null) {
            realm.executeTransactionAsync(transactionRealm ->
                    transactionRealm.copyToRealmOrUpdate(ubicacion, ImportFlag.CHECK_SAME_VALUES_BEFORE_SET));
            realm.close();
        } else
            errorConexion();
    }

    public void updateUbicaciones(List <ubicacion> ubicaciones) {
        Log.v("QUICKSTART", "NUMERO DE UBICACIONES: " + ubicaciones);
        realm = conectarBD.conseguirUsuarioMongoDB();
        if (realm != null) {
            realm.executeTransactionAsync(transactionRealm -> {
                for (ubicacion ubicacion: ubicaciones) {
                    transactionRealm.copyToRealmOrUpdate(ubicacion, ImportFlag.CHECK_SAME_VALUES_BEFORE_SET);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    Log.v("QUICKSTART", "SÍ SE ACTUALIZARON UBICACIONES, POR FIN PTM.");
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    Log.v("QUICKSTART", error.toString() + "NO SE ACTUALIZARON UBICACIONES, PTM PTM PTM PTM PTM PTM.");
                }
            });
            realm.close();
        } else
            errorConexion();
    }

    public void sumaDelitosCeroUbicaciones(){
        realm = conectarBD.conseguirUsuarioMongoDB();
        List <ubicacion> ubicaciones = realm.copyFromRealm(realm.where(ubicacion.class).findAll());
        if (realm != null) {
            realm.executeTransactionAsync(realm -> {
                for(ubicacion ubicacion : ubicaciones){
                    ubicacion.setSuma_delitos(0);
                    realm.insertOrUpdate(ubicacion);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    Log.v("QUICKSTART", "SÍ SE ACTUALIZARON UBICACIONES, POR FIN PTM.");
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    Log.v("QUICKSTART", error.toString() + "NO SE ACTUALIZARON UBICACIONES, PTM PTM PTM PTM PTM PTM.");
                }
            });
            realm.close();
        } else
            errorConexion();
    }

    private void errorConexion() {
        Toast.makeText(mContext, "Hubo un problema en conectarse, intenta mas tarde", Toast.LENGTH_SHORT).show();
    }
}
