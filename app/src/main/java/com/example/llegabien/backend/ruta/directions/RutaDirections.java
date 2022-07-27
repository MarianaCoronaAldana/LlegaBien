package com.example.llegabien.backend.ruta.directions;

import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RutaDirections {

    private List <String> duracion;
    private List <String> distancia;
    private List<List<HashMap<String, String>>> rutasDirectionsJSON;
    private List<PolylineOptions> rutasDirectionsPolylineOptions;

    public RutaDirections() {
        duracion = new ArrayList<>();
        distancia = new ArrayList<>();
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
