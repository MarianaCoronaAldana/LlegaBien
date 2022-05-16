package com.example.llegabien.backend.mapa.ubicacion;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

import org.bson.types.ObjectId;

public class ubicacion extends RealmObject {
    @PrimaryKey
    private ObjectId _id;

    @Required
    private String _partition;

    @Required
    private RealmList<Double> coordenadas;

    private Integer delitos_semana;

    private Integer media_historica;

    private Integer meta_reduccion;

    private String nombre;

    private String poligono;

    private String seguridad;

    private Integer suma_delitos;

    // Standard getters & setters
    public ObjectId get_id() { return _id; }
    public void set_id(ObjectId _id) { this._id = _id; }

    public String get_partition() { return _partition; }
    public void set_partition(String _partition) { this._partition = _partition; }

    public RealmList<Double> getCoordenadas() { return coordenadas; }
    public void setCoordenadas(RealmList<Double> coordenadas) { this.coordenadas = coordenadas; }

    public Integer getDelitos_semana() { return delitos_semana; }
    public void setDelitos_semana(Integer delitos_semana) { this.delitos_semana = delitos_semana; }

    public Integer getMedia_historica() { return media_historica; }
    public void setMedia_historica(Integer media_historica) { this.media_historica = media_historica; }

    public Integer getMeta_reduccion() { return meta_reduccion; }
    public void setMeta_reduccion(Integer meta_reduccion) { this.meta_reduccion = meta_reduccion; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPoligono() { return poligono; }
    public void setPoligono(String poligono) { this.poligono = poligono; }

    public String getSeguridad() { return seguridad; }
    public void setSeguridad(String seguridad) { this.seguridad = seguridad; }

    public Integer getSuma_delitos() { return suma_delitos; }
    public void setSuma_delitos(Integer suma_delitos) { this.suma_delitos = suma_delitos; }
}

