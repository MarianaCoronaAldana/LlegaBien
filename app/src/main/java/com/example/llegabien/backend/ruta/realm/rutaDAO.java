package com.example.llegabien.backend.ruta.realm;

import static io.realm.Realm.getApplicationContext;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.llegabien.backend.mongoDB.ConectarBD;
import com.example.llegabien.backend.usuario.usuario;

import org.bson.types.ObjectId;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import io.realm.ImportFlag;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

public class rutaDAO {

    private final ConectarBD conectarBD = new ConectarBD();
    private Realm realm;
    private final Context mContext;

    public rutaDAO(Context mContext) {
        this.mContext = mContext;
    }

    public void anadirRuta(ruta Ruta) {
        Ruta.set_id(new ObjectId());
        Ruta.set_partition("LlegaBien");
        realm = conectarBD.conseguirUsuarioMongoDB();
        if(realm!=null){
            realm.executeTransactionAsync(transactionRealm -> transactionRealm.insert(Ruta));
            realm.close();
        }
        else
            errorConexion();
    }

    // Actualiza el atributo "fUsoRuta", que se refiere a la ultima vez que la ruta ha sido usada
    public boolean actualizarFechaRuta(ruta Ruta) {
        Ruta.set_partition("LlegaBien");
        realm = conectarBD.conseguirUsuarioMongoDB();

        if(realm!=null){
            realm.executeTransactionAsync(transactionRealm -> {
                transactionRealm.copyToRealmOrUpdate(Ruta, ImportFlag.CHECK_SAME_VALUES_BEFORE_SET);
            });
            realm.close();
            return true;
        }
        else
            errorConexion();
        return false;
    }

    // Obtene todas las rutas relacionadas al id de un usuario
    public RealmResults<ruta> obtenerRutasPorUsuario(ObjectId usuario) {
        realm = conectarBD.conseguirUsuarioMongoDB();
        if(realm!=null){
            return realm.where(ruta.class).equalTo("idUsuario",usuario)
                    .sort("fUsoRuta", Sort.DESCENDING).findAll();
        }
        else
            errorConexion();
        return null;
    }

    // Devuelve un objeto RUTA basado en el id
    public ruta obtenerRutaPorId(ObjectId id) {
        realm = conectarBD.conseguirUsuarioMongoDB();
        if(realm!=null){
            ruta task = realm.where(ruta.class).equalTo("_id", id)
                    .findFirst();
            if (task != null) {
                Log.v("QUICKSTART", "estoy en conseguir ruta por id");
                return task;
            }
        }
        else
            errorConexion();
        return null;
    }

    // Devuelve un booleano, depensiendo de si el objeto favorito ya existe
    public boolean verificarExistenciaRuta(ObjectId idUsuario, RealmList<String> puntoPartida,RealmList<String> puntoDestino) {
        realm = conectarBD.conseguirUsuarioMongoDB();

        Set<String> coordenadasPuntoPartida = new LinkedHashSet<>(puntoPartida);
        String[] arrayCoordenadasPuntoPartida = coordenadasPuntoPartida.toArray(new String[0]);

        Set<String> coordenadasPuntoDestino = new LinkedHashSet<>(puntoDestino);
        String[] arrayCoordenadasPuntoDestino = coordenadasPuntoDestino.toArray(new String[0]);

        if(realm!=null){
            ruta task = realm.where(ruta.class).equalTo("idUsuario", idUsuario)
                    .and()
                    .in("puntoInicio", arrayCoordenadasPuntoPartida)
                    .and()
                    .in("puntoDestino", arrayCoordenadasPuntoDestino)
                    .findFirst();

            return task == null;
        }
        else
            errorConexion();
        return false;
    }


    public void deleteRutasPorUsuario(usuario Usuario) {
        realm = conectarBD.conseguirUsuarioMongoDB();
        if(realm!=null){
            realm.executeTransactionAsync(transactionRealm -> {
                RealmResults<ruta> task = transactionRealm.where(ruta.class).equalTo("idUsuario",Usuario.get_id()).findAll();
                Objects.requireNonNull(task).deleteAllFromRealm();
            });
            Toast.makeText(getApplicationContext(), "Cuenta eliminada con exito", Toast.LENGTH_LONG).show();
            realm.close();
        }
        else
            errorConexion();
    }


    private void errorConexion(){
        Toast.makeText(mContext, "Hubo un problema en conectarse, intenta mas tarde", Toast.LENGTH_SHORT).show();
    }
}
