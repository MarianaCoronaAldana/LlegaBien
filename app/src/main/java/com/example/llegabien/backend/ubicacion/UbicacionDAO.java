package com.example.llegabien.backend.ubicacion;

import static com.example.llegabien.backend.app.Preferences.PREFERENCE_UBICACION;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.llegabien.backend.app.Preferences;
import com.example.llegabien.backend.poligonos.Poligono;
import com.example.llegabien.backend.mongoDB.ConectarBD;
import com.google.android.gms.maps.model.LatLng;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import org.bson.types.ObjectId;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class UbicacionDAO {
    Realm realm;
    ConectarBD conectarBD = new ConectarBD();
    private final Context mContext;

    public UbicacionDAO(Context context) {
        mContext = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ubicacion obtenerUbicacionConIdDeLista(ObjectId IdUbicacion, List<ubicacion> ubicaciones) {
        return ubicaciones.stream().filter(ubicacion -> ubicacion.get_id().equals(IdUbicacion)).findAny().orElse(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ubicacion obtenerUbicacionConNombreDeLista(String nombre, List<ubicacion> ubicaciones) {
        return ubicaciones.stream().filter(ubi -> ubi.getNombre().equals(nombre)).findAny().orElse(null);
    }

    public ubicacion obtenerUbicacionConNombre(String nombre) {
        if (realm == null)
            realm = conectarBD.conseguirUsuarioMongoDB();
        if (realm != null) {
            ubicacion ubicacion = realm.where(ubicacion.class).equalTo("nombre", nombre).findFirst();
            if (ubicacion != null)
                return realm.copyFromRealm(ubicacion);
        } else
            errorConexion();
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ubicacion obtenerColonia(String nombreColonia, List<ubicacion> colonias) {
        ubicacion colonia = obtenerUbicacionConNombre(nombreColonia);
        if (colonia != null)
            return colonia;

        UbicacionGeocodificacion ubicacionGeodicacion = new UbicacionGeocodificacion(mContext);
        Address addressColonia = ubicacionGeodicacion.geocodificarUbiciacion(nombreColonia);
        if (addressColonia != null) {
            Poligono poligono = new Poligono();
            colonia = poligono.isUbicacionEnPoligono(colonias, addressColonia.getLatitude(), addressColonia.getLongitude());
            return colonia;

        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ubicacion obtenerColonia(String nombreColonia, List<ubicacion> colonias, LatLng ubicacionColonia) {
        ubicacion colonia = obtenerUbicacionConNombre(nombreColonia);
        if (colonia != null)
            return colonia;

        Poligono poligono = new Poligono();
        colonia = poligono.isUbicacionEnPoligono(colonias, ubicacionColonia.latitude, ubicacionColonia.longitude);
        return colonia;
    }

    public List<ubicacion> obtenerColonias() {
        if (realm == null)
            realm = conectarBD.conseguirUsuarioMongoDB();

        if (realm != null)
            return realm.copyFromRealm(realm.where(ubicacion.class).equalTo("tipo", "colonia").findAll());
        else
            errorConexion();

        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<ubicacion> obtenerColoniasConIdMunicipio(ObjectId IdMunicipio) {
        if (realm == null)
            realm = conectarBD.conseguirUsuarioMongoDB();

        if (realm != null)
            return realm.copyFromRealm(realm.where(ubicacion.class).equalTo("tipo", "colonia")
                    .equalTo("IdMunicipio", IdMunicipio).findAll());
        else
            errorConexion();

        return null;
    }

    public List<ubicacion> obtenerUbicaciones() {
        if (realm == null)
            realm = conectarBD.conseguirUsuarioMongoDB();

        if (realm != null)
            return realm.copyFromRealm(realm.where(ubicacion.class).findAll());
        else
            errorConexion();

        return null;
    }

    public void obetenerUbicacionConPoligono(String coordenadasPoligono, Context c) {
        if (realm == null)
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

        if (realm == null)
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

        Toast.makeText(mContext, "Esta ubicación no está dentro del Área Metropolitana de Guadalajara", Toast.LENGTH_SHORT);
        return false;
    }

    public void updateUbicaciones(List<ubicacion> ubicaciones) {
        Log.v("QUICKSTART", "NUMERO DE UBICACIONES: " + ubicaciones.size());
        if (realm == null)
            realm = conectarBD.conseguirUsuarioMongoDB();

        if (realm != null) {
            realm.executeTransactionAsync(realm -> {
                for (ubicacion ubicacion : ubicaciones) {
                    realm.insertOrUpdate(ubicacion);
                }
            }, () -> Log.v("QUICKSTART", "SÍ SE ACTUALIZARON UBICACIONES, POR FIN PTM."), error -> Log.v("QUICKSTART", error.toString() + "NO SE ACTUALIZARON UBICACIONES, PTM PTM PTM PTM PTM PTM."));

        } else
            errorConexion();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void anadirUbicacionesBD(Intent data) {
        if (data != null) {
            if (realm == null)
                realm = conectarBD.conseguirUsuarioMongoDB();

            if (realm != null) {
                realm.executeTransactionAsync(realm -> {
                            try {
                                InputStreamReader inputStreamReader = new InputStreamReader(mContext.getContentResolver().openInputStream(data.getData()));
                                CSVReader csvReader = new CSVReader(inputStreamReader);
                                String[] nextLine = csvReader.readNext();

                                ubicacion ubicacion = new ubicacion();

                                while ((nextLine = csvReader.readNext()) != null) {
                                    if (!nextLine[0].equals("") && !nextLine[0].equals("null"))
                                        ubicacion.setIdColonia(new ObjectId(nextLine[0].trim()));
                                    if (!nextLine[1].equals("") && !nextLine[1].equals("null"))
                                        ubicacion.setIdMunicipio(new ObjectId(nextLine[1].trim()));
                                    if (!nextLine[2].equals(""))
                                        ubicacion.set_id(new ObjectId(nextLine[2].trim()));
                                    ubicacion.set_partition("LlegaBien");
                                    ubicacion.setCoordenadas_string(nextLine[4]);
                                    ubicacion.setDelito_mas_frecuente(nextLine[5]);
                                    ubicacion.setDelitos_semana(Integer.valueOf(nextLine[6]));
                                    ubicacion.setMedia_historica_double(Double.valueOf(nextLine[7]));
                                    ubicacion.setMeta_reduccion_double(Double.valueOf(nextLine[8]));
                                    ubicacion.setNombre(nextLine[9]);
                                    ubicacion.setSeguridad(nextLine[10]);
                                    ubicacion.setSuma_delitos(Integer.valueOf(nextLine[11]));
                                    ubicacion.setTipo(nextLine[12]);
                                    realm.insert(ubicacion);
                                }
                            } catch (IOException | CsvException e) {
                                Toast.makeText(mContext, "Error: " + e.toString(), Toast.LENGTH_LONG).show();
                            }
                        },
                        () -> Log.v("QUICKSTART", "SÍ SE SUBIERON UBICACIONES, POR FIN PTM."),
                        error -> Log.v("QUICKSTART", error.toString() + "NO SE SEUBIERON UBICACIONES, PTM PTM PTM PTM PTM PTM."));
            } else
                errorConexion();
        }
    }

    private void errorConexion() {
        Toast.makeText(mContext, "Hubo un problema en conectarse, intenta mas tarde", Toast.LENGTH_SHORT).show();
    }
}
