package com.example.llegabien.backend.favoritos;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.RealmClass;
import io.realm.annotations.Required;

@RealmClass(embedded = true)
public class favorito_ubicacion extends RealmObject {
    @Required
    private RealmList<Double> coordinates;

    private String type;

    // Standard getters & setters
    public RealmList<Double> getCoordinates() { return coordinates; }
    public void setCoordinates(RealmList<Double> coordinates) { this.coordinates = coordinates; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public favorito_ubicacion(){}
}
