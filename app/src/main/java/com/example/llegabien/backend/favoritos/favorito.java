package com.example.llegabien.backend.favoritos;

import com.example.llegabien.backend.usuario.usuario;

import org.bson.types.ObjectId;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
public class favorito extends RealmObject {

    @PrimaryKey
    private ObjectId _id;

    @Required
    private String _partition;
    private usuario idUsuario;
    private String nombre;
    private favorito_ubicacion ubicacion;

    // Standard getters & setters
    public ObjectId get_id() { return _id; }
    public void set_id(ObjectId _id) { this._id = _id; }
    public String get_partition() { return _partition; }
    public void set_partition(String _partition) { this._partition = _partition; }
    public usuario getIdUsuario() { return idUsuario; }
    public void setIdUsuario(usuario idUsuario) { this.idUsuario = idUsuario; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public favorito_ubicacion getUbicacion() { return ubicacion; }
    public void setUbicacion(favorito_ubicacion ubicacion) { this.ubicacion = ubicacion; }
}