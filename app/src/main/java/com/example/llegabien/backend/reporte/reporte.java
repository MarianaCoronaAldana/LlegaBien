package com.example.llegabien.backend.reporte;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

import java.util.Date;
        import org.bson.types.ObjectId;

import io.realm.RealmObject;
import java.util.Date;
import org.bson.types.ObjectId;

public class reporte extends RealmObject {
    @PrimaryKey
    private ObjectId _id;

    private ObjectId IdUbicacion;

    private ObjectId IdUsuario;

    @Required
    private String _partition;

    private String autor;

    private String comentarios;

    private Date fecha;

    private Date fechaReporte;

    private String tipoDelito;

    private String ubicacion;

    // Standard getters & setters
    public ObjectId get_id() { return _id; }
    public void set_id(ObjectId _id) { this._id = _id; }

    public ObjectId getIdUbicacion() { return IdUbicacion; }
    public void setIdUbicacion(ObjectId IdUbicacion) { this.IdUbicacion = IdUbicacion; }

    public ObjectId getIdUsuario() { return IdUsuario; }
    public void setIdUsuario(ObjectId IdUsuario) { this.IdUsuario = IdUsuario; }

    public String get_partition() { return _partition; }
    public void set_partition(String _partition) { this._partition = _partition; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getComentarios() { return comentarios; }
    public void setComentarios(String comentarios) { this.comentarios = comentarios; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public Date getFechaReporte() { return fechaReporte; }
    public void setFechaReporte(Date fechaReporte) { this.fechaReporte = fechaReporte; }

    public String getTipoDelito() { return tipoDelito; }
    public void setTipoDelito(String tipoDelito) { this.tipoDelito = tipoDelito; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
}