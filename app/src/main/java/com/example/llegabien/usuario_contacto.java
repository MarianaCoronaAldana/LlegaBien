package com.example.llegabien;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

@RealmClass(embedded = true)
public class usuario_contacto extends RealmObject {
    private String nombre;
    private String telCelular;
    // Standard getters & setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTelCelular() { return telCelular; }
    public void setTelCelular(String telCelular) { this.telCelular = telCelular; }
}
