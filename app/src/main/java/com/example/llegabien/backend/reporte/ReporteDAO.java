package com.example.llegabien.backend.reporte;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.llegabien.backend.mongoDB.ConectarBD;
import com.example.llegabien.backend.usuario.usuario;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import org.bson.types.ObjectId;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class ReporteDAO {

    private final ConectarBD conectarBD = new ConectarBD();
    private Realm realm;
    private final Context mContext;

    public ReporteDAO(Context context) {
        mContext = context;
    }

    public void anadirReporte(reporte Reporte) {
        Reporte.set_id(new ObjectId());
        Reporte.set_partition("LlegaBien");
        if (realm == null)
            realm = conectarBD.conseguirUsuarioMongoDB();

        if (realm != null) {
            realm.executeTransactionAsync(transactionRealm -> transactionRealm.insert(Reporte));
            realm.close();
            Toast.makeText(mContext, "Tu reporte será verificado el siguiente fin de semana", Toast.LENGTH_LONG).show();
        } else
            errorConexion();
    }

    public void anadirReportes(List<reporte> reportes) {
        List<reporte> reportesTemp = new ArrayList<>();

        if(reportes.size() <= 10000)
            anadirReportesPorPartes(reportes);

        else {
            for (int i = 0; i < reportes.size(); i++) {
                // Para a{adir reporets a la lista temporal mientras no se completen 10,000 reportes.
                reportesTemp.add(reportes.get(i));

                if (i % 10000 == 0) {
                    // Para subir los primeros 10000 reportes.
                    anadirReportesPorPartes(reportesTemp);

                    // Para esperar 10 min a que la BD se actualice.
                    try {
                        Thread.sleep(600000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Para limpiar la lista e iniciar de nuevo la cuenta.
                    reportesTemp.clear();
                }
            }
        }

    }

    public void anadirReportesPorPartes(List<reporte> reportes) {
        if (realm == null)
            realm = conectarBD.conseguirUsuarioMongoDB();

        if (realm != null) {
            realm.executeTransactionAsync(transactionRealm -> {
                        for (int i = 0; i < reportes.size(); i++) {
                            reportes.get(i).set_id(new ObjectId());
                            reportes.get(i).set_partition("LlegaBien");
                            transactionRealm.insert(reportes.get(i));
                        }
                    },
                    () -> Log.v("QUICKSTART", "SÍ SE SUBIERON REPORTES, POR FIN PTM."),
                    error -> Log.v("QUICKSTART", error.toString() + "NO SE SEUBIERON REPORTES, PTM PTM PTM PTM PTM PTM."));
        } else
            errorConexion();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void anadirReportesIIEG(Intent data) {
        if (data != null) {
            if (realm == null)
                realm = conectarBD.conseguirUsuarioMongoDB();

            if (realm != null) {
                realm.executeTransactionAsync(realm -> {
                            try {
                                InputStreamReader inputStreamReader = new InputStreamReader(mContext.getContentResolver().openInputStream(data.getData()));
                                CSVReader csvReader = new CSVReader(inputStreamReader);
                                String[] nextLine = csvReader.readNext();

                                reporte reporte = new reporte();

                                while ((nextLine = csvReader.readNext()) != null) {
                                    reporte.set_id(new ObjectId());
                                    if (!nextLine[0].equals(""))
                                        reporte.setIdUbicacion(new ObjectId(nextLine[0].trim()));
                                    if (!nextLine[1].equals(""))
                                        reporte.setIdUsuario(new ObjectId(nextLine[1].trim()));
                                    reporte.set_partition("LlegaBien");
                                    reporte.setAutor(nextLine[3]);
                                    reporte.setCantidad(Integer.valueOf(nextLine[4]));
                                    reporte.setComentarios(nextLine[5]);
                                    reporte.setFecha(Date.from(Instant.from(DateTimeFormatter.ISO_INSTANT.parse(nextLine[6]))));
                                    reporte.setFechaReporte(Date.from(Instant.from(DateTimeFormatter.ISO_INSTANT.parse(nextLine[7]))));
                                    reporte.setTipoDelito(nextLine[8]);
                                    reporte.setUbicacion(nextLine[9]);
                                    realm.insert(reporte);
                                }

                            } catch (IOException | CsvException e) {
                                Toast.makeText(mContext, "Error: " + e.toString(), Toast.LENGTH_LONG).show();
                            }
                        },
                        () -> Log.v("QUICKSTART", "SÍ SE SUBIERON REPORTES, POR FIN PTM."),
                        error -> Log.v("QUICKSTART", error.toString() + "NO SE SEUBIERON REPORTES, PTM PTM PTM PTM PTM PTM."));
            } else
                errorConexion();
        }
    }

    public RealmResults<reporte> obtenerReportesPorUsuario(reporte Reporte) {
        if (realm == null)
            realm = conectarBD.conseguirUsuarioMongoDB();

        if (realm != null)
            return realm.where(reporte.class).equalTo("IdUsuario", Reporte.getIdUsuario()).findAll();
        else
            errorConexion();

        return null;
    }

    public List<reporte> obtenerReportesPorFecha(Date fechaInicio, Date fechaTermino) {
        if (realm == null)
            realm = conectarBD.conseguirUsuarioMongoDB();

        if (realm != null)
            return realm.copyFromRealm(realm.where(reporte.class).between("fecha", fechaInicio, fechaTermino).findAll());
        else
            errorConexion();

        return null;
    }


    public RealmResults<reporte> obtenerReportesPorUsuario(usuario Usuario) {
        if (realm == null)
            realm = conectarBD.conseguirUsuarioMongoDB();

        if (realm != null) {
            RealmResults<reporte> realmResults = realm.where(reporte.class).equalTo("IdUsuario", Usuario.get_id()).findAll();
            Log.v("QUICKSTART", "Estoy en REPORTE DAO, size: " + realmResults.size());
            return realmResults;
        } else
            errorConexion();

        return null;
    }

    private void errorConexion() {
        Toast.makeText(mContext, "Hubo un problema en conectarse, intenta mas tarde", Toast.LENGTH_SHORT).show();
    }
}
