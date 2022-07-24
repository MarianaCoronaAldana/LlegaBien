package com.example.llegabien.backend.ubicacion;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

import org.bson.types.ObjectId;

public class ubicacion extends RealmObject {
    @PrimaryKey
    private ObjectId _id;

    private ObjectId IdColonia;

    private ObjectId IdMunicipio;

    @Required
    private String _partition;

    private String coordenadas_string;

    private String delito_mas_frecuente;

    private Integer delitos_semana;

    private Double media_historica_double;

    private Double meta_reduccion_double;

    private String nombre;

    private String seguridad;

    private Integer suma_delitos;

    private String tipo;

    // Standard getters & setters
    public ObjectId get_id() { return _id; }
    public void set_id(ObjectId _id) { this._id = _id; }

    public ObjectId getIdColonia() { return IdColonia; }
    public void setIdColonia(ObjectId IdColonia) { this.IdColonia = IdColonia; }

    public ObjectId getIdMunicipio() { return IdMunicipio; }
    public void setIdMunicipio(ObjectId IdMunicipio) { this.IdMunicipio = IdMunicipio; }

    public String get_partition() { return _partition; }
    public void set_partition(String _partition) { this._partition = _partition; }

    public String getCoordenadas_string() { return coordenadas_string; }
    public void setCoordenadas_string(String coordenadas) { this.coordenadas_string = coordenadas; }

    public String getDelito_mas_frecuente() { return delito_mas_frecuente; }
    public void setDelito_mas_frecuente(String delito_mas_frecuente) { this.delito_mas_frecuente = delito_mas_frecuente; }

    public Integer getDelitos_semana() { return delitos_semana; }
    public void setDelitos_semana(Integer delitos_semana) { this.delitos_semana = delitos_semana; }

    public Double getMedia_historica_double() { return media_historica_double; }
    public void setMedia_historica_double(Double media_historica) { this.media_historica_double = media_historica; }

    public Double getMeta_reduccion_double() { return meta_reduccion_double; }
    public void setMeta_reduccion_double(Double meta_reduccion) { this.meta_reduccion_double = meta_reduccion; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getSeguridad() { return seguridad; }
    public void setSeguridad(String seguridad) { this.seguridad = seguridad; }

    public Integer getSuma_delitos() { return suma_delitos; }
    public void setSuma_delitos(Integer suma_delitos) { this.suma_delitos = suma_delitos; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}
