package com.example.llegabien.backend.usuario;

import com.example.llegabien.backend.contactos.usuario_contacto;

import org.bson.types.ObjectId;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class usuario extends RealmObject {
    @PrimaryKey
    private ObjectId _id;

    @Required
    private String _partition;

    private String apellidos;

    private RealmList<usuario_contacto> contacto;

    private String contrasena;

    private String correoElectronico;

    private Date fNacimiento;

    private String nombre;

    private String telCelular;

    private Boolean status;

    public usuario() {
    }

    // Standard getters & setters
    public ObjectId get_id() { return _id; }
    public void set_id(ObjectId _id) { this._id = _id; }

    public String get_partition() { return _partition; }
    public void set_partition(String _partition) { this._partition = _partition; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public RealmList<usuario_contacto> getContacto() { return contacto; }
    public void setContacto(RealmList<usuario_contacto> contacto) { this.contacto = contacto; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getCorreoElectronico() { return correoElectronico; }
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico = correoElectronico; }

    public Date getFNacimiento() { return fNacimiento; }
    public void setFNacimiento(Date fNacimiento) { this.fNacimiento = fNacimiento; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelCelular() { return telCelular; }
    public void setTelCelular(String telCelular) { this.telCelular = telCelular; }

}