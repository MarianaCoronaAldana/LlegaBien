package com.example.llegabien.backend.mapa.favoritos;

import com.example.llegabien.backend.usuario.usuario;

import org.bson.types.ObjectId;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
import io.realm.RealmObject;
import org.bson.types.ObjectId;
import io.realm.RealmObject;
import org.bson.types.ObjectId;

public class favorito extends RealmObject {
    @PrimaryKey
    private ObjectId _id;

    private ObjectId IdUsuario;

    @Required
    private String _partition;

    private String nombre;

    private favorito_ubicacion ubicacion;

    // Standard getters & setters
    public ObjectId get_id() { return _id; }
    public void set_id(ObjectId _id) { this._id = _id; }

    public ObjectId getIdUsuario() { return IdUsuario; }
    public void setIdUsuario(ObjectId IdUsuario) { this.IdUsuario = IdUsuario; }

    public String get_partition() { return _partition; }
    public void set_partition(String _partition) { this._partition = _partition; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public favorito_ubicacion getUbicacion() { return ubicacion; }
    public void setUbicacion(favorito_ubicacion ubicacion) { this.ubicacion = ubicacion; }
}

