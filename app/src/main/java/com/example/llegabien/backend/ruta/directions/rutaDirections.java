package com.example.llegabien.backend.ruta.directions;

import com.google.android.gms.maps.model.LatLng;
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
    private List<List<PolylineOptions>> rutasEnPolylines;
    private List<List<LatLng>> rutasPuntosMedios;
    private List<List<String>> rutasNombresPuntosMedios;

    public rutaDirections() {
        duracion = new ArrayList<>();
        distancia = new ArrayList<>();
    }

    public ObjectId getIdUsuario() { return idUsuario; }

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

    public List<List<PolylineOptions>> getRutasEnPolylines() {
        return rutasEnPolylines;
    }

    public void setRutasEnPolylines(List<List<PolylineOptions>> rutasEnPolylines) {
        this.rutasEnPolylines = rutasEnPolylines;
    }

    public List<List<LatLng>> getRutasPuntosMedios() {
        return rutasPuntosMedios;
    }

    public void setRutasPuntosMedios(List<List<LatLng>> rutasPuntosMedios) {
        this.rutasPuntosMedios = rutasPuntosMedios;
    }

    public List<List<String>> getRutasNombresPuntosMedios() {
        return rutasNombresPuntosMedios;
    }

    public void setRutasNombresPuntosMedios(List<List<String>> rutasNombresPuntosMedios) {
        this.rutasNombresPuntosMedios = rutasNombresPuntosMedios;
    }


}
