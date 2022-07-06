package com.example.llegabien.backend.ruta.directions;

import com.google.android.gms.maps.model.PolylineOptions;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class rutaDirections  {

    private ObjectId idUsuario;
    private List <String> duracion;
    private List <String> distancia;
    private List<List<HashMap<String, String>>> rutasDirectionsJSON;
    private List<PolylineOptions> rutasDirectionsPolylineOptions;

    public rutaDirections() {
        duracion = new ArrayList();
        distancia = new ArrayList();
    }

    public ObjectId getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(ObjectId idUsuario) {
        this.idUsuario = idUsuario;
    }

    public List<String> getDuracion() {
        return duracion;
    }

    public void setDuracion(List<String> duracion) {
        this.duracion = duracion;
    }

    public List<String> getDistancia() {
        return distancia;
    }

    public void setDistancia(List<String> distancia) {
        this.distancia = distancia;
    }

    public List<List<HashMap<String, String>>> getRutasDirectionsJSON() {
        return rutasDirectionsJSON;
    }

    public void setRutasDirectionsJSON(List<List<HashMap<String, String>>> rutasDirectionsJSON) {
        this.rutasDirectionsJSON = rutasDirectionsJSON;
    }

    public List<PolylineOptions> getRutasDirectionsPolylineOptions() {
        return rutasDirectionsPolylineOptions;
    }

    public void setRutasDirectionsPolylineOptions(List<PolylineOptions> rutasDirectionsPolylineOptions) {
        this.rutasDirectionsPolylineOptions = rutasDirectionsPolylineOptions;
    }
}
