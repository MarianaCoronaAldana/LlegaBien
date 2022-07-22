package com.example.llegabien.backend.ruta.directions;

import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class Ruta {

    private List<UbicacionRuta> mCallesRuta;
    private int mNumeroCalles;
    private String mTiempoTotal;
    private String mDistanciaTotalDirections;
    private int mDistanciaTotal;
    private double mMediaHistorica;
    private double mPorcentajeZonasSeguras;
    private double mPorcentajeDistanciaSegura;
    private PolylineOptions mPolyline;

    public Ruta() {
    }

    public List<UbicacionRuta> getmCallesRuta() {
        return mCallesRuta;
    }

    public void setmCallesRuta(List<UbicacionRuta> mCallesRuta) {
        this.mCallesRuta = mCallesRuta;
    }

    public int getmNumeroCalles() {
        return mNumeroCalles;
    }

    public void setmNumeroCalles(int mNumeroCalles) {
        this.mNumeroCalles = mNumeroCalles;
    }

    public String getmTiempoTotal() {
        return mTiempoTotal;
    }

    public void setmTiempoTotal(String mTiempoTotal) {
        this.mTiempoTotal = mTiempoTotal;
    }

    public String getmDistanciaTotalDirections() {
        return mDistanciaTotalDirections;
    }

    public void setmDistanciaTotalDirections(String mDistanciaTotalDirections) {
        this.mDistanciaTotalDirections = mDistanciaTotalDirections;
    }

    public int getmDistanciaTotal() {
        return mDistanciaTotal;
    }

    public void setmDistanciaTotal(int mDistanciaTotal) {
        this.mDistanciaTotal = mDistanciaTotal;
    }

    public double getmMediaHistorica() {
        return mMediaHistorica;
    }

    public void setmMediaHistorica(double mMediaHistorica) {
        this.mMediaHistorica = mMediaHistorica;
    }

    public double getmPorcentajeZonasSeguras() {
        return mPorcentajeZonasSeguras;
    }

    public void setmPorcentajeZonasSeguras(double mPorcentajeZonasSeguras) {
        this.mPorcentajeZonasSeguras = mPorcentajeZonasSeguras;
    }

    public double getmPorcentajeDistanciaSegura() {
        return mPorcentajeDistanciaSegura;
    }

    public void setmPorcentajeDistanciaSegura(double mPorcentajeDistanciaSegura) {
        this.mPorcentajeDistanciaSegura = mPorcentajeDistanciaSegura;
    }

    public PolylineOptions getmPolyline() {
        return mPolyline;
    }

    public void setmPolyline(PolylineOptions mPolyline) {
        this.mPolyline = mPolyline;
    }
}
