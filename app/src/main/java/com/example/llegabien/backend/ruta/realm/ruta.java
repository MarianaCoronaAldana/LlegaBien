package com.example.llegabien.backend.ruta.realm;

import com.google.android.gms.maps.model.LatLng;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

import org.bson.types.ObjectId;

import java.util.Date;

public class ruta extends RealmObject {

    @PrimaryKey
    private ObjectId _id;

    @Required
    private String _partition;

    private Date fUsoRuta;

    private ObjectId idUsuario;

    @Required
    private RealmList<String> puntoDestino;

    @Required
    private RealmList<String> puntoInicio;

    // Standard getters & setters
    public ObjectId get_id() { return _id; }
    public void set_id(ObjectId _id) { this._id = _id; }

    public String get_partition() { return _partition; }
    public void set_partition(String _partition) { this._partition = _partition; }

    public Date getFUsoRuta() { return fUsoRuta; }
    public void setFUsoRuta(Date fUsoRuta) { this.fUsoRuta = fUsoRuta; }

    public ObjectId getIdUsuario() { return idUsuario; }
    public void setIdUsuario(ObjectId idUsuario) { this.idUsuario = idUsuario; }

    public RealmList<String> getPuntoDestino() { return puntoDestino; }
    public void setPuntoDestino(RealmList<String> puntoDestino) { this.puntoDestino = puntoDestino; }
    public void setPuntoDestino(LatLng coordenadas) {
        RealmList<String> puntoDestino = new RealmList<>();
        puntoDestino.add(String.valueOf(coordenadas.latitude));
        puntoDestino.add(String.valueOf(coordenadas.longitude));
        this.puntoDestino = puntoDestino;
    }

    public RealmList<String> getPuntoInicio() { return puntoInicio; }
    public void setPuntoInicio(RealmList<String> puntoInicio) { this.puntoInicio = puntoInicio; }
    public void setPuntoInicio(LatLng coordenadas) {
        RealmList<String> puntoInicio = new RealmList<>();
        puntoInicio.add(String.valueOf(coordenadas.latitude));
        puntoInicio.add(String.valueOf(coordenadas.longitude));
        this.puntoInicio = puntoInicio;
    }
}

