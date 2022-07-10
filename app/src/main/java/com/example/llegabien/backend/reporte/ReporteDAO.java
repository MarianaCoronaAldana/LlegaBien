package com.example.llegabien.backend.reporte;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.llegabien.backend.mapa.ubicacion.ubicacion;
import com.example.llegabien.backend.mongoDB.ConectarBD;
import com.example.llegabien.backend.usuario.usuario;

import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

import io.realm.ImportFlag;
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
        realm = conectarBD.conseguirUsuarioMongoDB();

        if (realm != null) {
            realm.executeTransactionAsync(transactionRealm -> transactionRealm.insert(Reporte));
            realm.close();
        } else
            errorConexion();
    }

    public void anadirReportes(List<reporte> reportes) {
        realm = conectarBD.conseguirUsuarioMongoDB();
        Log.v("QUICKSTART", "TAMAÑO LISTA REPORTES: " + reportes.size());

        if (realm != null) {
            realm.executeTransactionAsync(realm -> {
                for (reporte reporte : reportes) {
                    reporte.set_id(new ObjectId());
                    reporte.set_partition("LlegaBien");
                    realm.insert(reporte);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    Log.v("QUICKSTART", "SÍ SE SUBIERON REPORTES, POR FIN PTM.");
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    Log.v("QUICKSTART", error.toString() + "NO SE SEUBIERON REPORTES, PTM PTM PTM PTM PTM PTM.");
                }
            });
        } else
            errorConexion();
    }

    public RealmResults<reporte> obtenerReportesPorUsuario(reporte Reporte) {
        realm = conectarBD.conseguirUsuarioMongoDB();

        if (realm != null)
            return realm.where(reporte.class).equalTo("IdUsuario", Reporte.getIdUsuario()).findAll();
        else
            errorConexion();

        return null;
    }

    public List<reporte> obtenerReportesPorFecha(Date fechaInicio, Date fechaTermino) {
        realm = conectarBD.conseguirUsuarioMongoDB();

        if (realm != null)
            return realm.copyFromRealm(realm.where(reporte.class).between("fecha", fechaInicio, fechaTermino).findAll());
        else
            errorConexion();

        return null;
    }


    public RealmResults<reporte> obtenerReportesPorUsuario(usuario Usuario) {
        realm = conectarBD.conseguirUsuarioMongoDB();

        if (realm != null) {
            RealmResults<reporte> realmResults = realm.where(reporte.class).equalTo("IdUsuario", Usuario.get_id()).findAll();
            Log.v("QUICKSTART", "Estoy en REPORTE DAO, size: " + realmResults.size());
            return realmResults;
        } else
            errorConexion();

        return null;
    }

    public void elimarReportes(){
        realm = conectarBD.conseguirUsuarioMongoDB();
        if (realm != null) {
            realm.executeTransactionAsync(realm -> {
                if (realm.where(reporte.class).findAll().deleteAllFromRealm())
                    Log.v("QUICKSTART", "Sí se borraron todas los reportes.");
                else
                    Log.v("QUICKSTART", "No se borraron todas lops reportes.");
            });
            realm.close();
        } else
            errorConexion();
    }

    public void mostrarReportesLog(){
        realm = conectarBD.conseguirUsuarioMongoDB();
        if (realm != null) {
            List <reporte> reportes = realm.copyFromRealm(realm.where(reporte.class).findAll());
            for (reporte reporte : reportes){
                Log.v("QUICKSTART", reporte.getAutor());
            }
            realm.close();
        } else
            errorConexion();

    }

    private void errorConexion() {
        Toast.makeText(mContext, "Hubo un problema en conectarse, intenta mas tarde", Toast.LENGTH_SHORT).show();
    }
}
