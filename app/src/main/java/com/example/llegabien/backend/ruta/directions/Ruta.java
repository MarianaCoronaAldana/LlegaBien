package com.example.llegabien.backend.ruta.directions;

import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class Ruta {

    private List<UbicacionRuta> mCallesRuta;
    private List<PolylineOptions> mPolyline;

    private List<String> mDelitosZonasInseguras;

    private int mNumeroCalles;
    private String mTiempoTotalDirections;
    private String mDistanciaTotalDirections;
    private int mDistanciaTotal;
    private double mMediaHistorica;
    private double mPorcentajeZonasSeguras;
    private double mPorcentajeDistanciaSegura;
    private int mNumeroDeRuta;


    private boolean mHasMenorMediaHistorica;
    private boolean mHasMayorPorcentajeZonasSeguras;
    private boolean mHasMayorPorcentajeDistanciaSegura;

    public Ruta() {
        mPolyline = new ArrayList<>();
    }

    public List<UbicacionRuta> getCallesRuta() {
        return mCallesRuta;
    }

    public void setCallesRuta(List<UbicacionRuta> mCallesRuta) {
        this.mCallesRuta = mCallesRuta;
    }

    public int getNumeroCalles() {
        return mNumeroCalles;
    }

    public void setNumeroCalles(int mNumeroCalles) {
        this.mNumeroCalles = mNumeroCalles;
    }

    public String getTiempoTotalDirections() {
        return mTiempoTotalDirections;
    }

    public void setTiempoTotalDirections(String mTiempoTotalDirections) {
        this.mTiempoTotalDirections = mTiempoTotalDirections;
    }

    public String getDistanciaTotalDirections() {
        return mDistanciaTotalDirections;
    }

    public void setDistanciaTotalDirections(String mDistanciaTotalDirections) {
        this.mDistanciaTotalDirections = mDistanciaTotalDirections;
    }

    public int getDistanciaTotal() {
        return mDistanciaTotal;
    }

    public void setDistanciaTotal(int mDistanciaTotal) {
        this.mDistanciaTotal = mDistanciaTotal;
    }

    public double getMediaHistorica() {
        return mMediaHistorica;
    }

    public void setMediaHistorica(double mMediaHistorica) {
        this.mMediaHistorica = mMediaHistorica;
    }

    public double getPorcentajeZonasSeguras() {
        return mPorcentajeZonasSeguras;
    }

    public void setPorcentajeZonasSeguras(double mPorcentajeZonasSeguras) {
        this.mPorcentajeZonasSeguras = mPorcentajeZonasSeguras;
    }

    public double getPorcentajeDistanciaSegura() {
        return mPorcentajeDistanciaSegura;
    }

    public void setPorcentajeDistanciaSegura(double mPorcentajeDistanciaSegura) {
        this.mPorcentajeDistanciaSegura = mPorcentajeDistanciaSegura;
    }

    public List<PolylineOptions> getPolyline() {
        return mPolyline;
    }

    public void setPolyline(List<PolylineOptions> mPolyline) {
        this.mPolyline = mPolyline;
    }

    public int getNumeroDeRuta() {
        return mNumeroDeRuta;
    }

    public void setNumeroDeRuta(int mNumeroDeRuta) {
        this.mNumeroDeRuta = mNumeroDeRuta;
    }

    public boolean getHasMenorMediaHistorica() {
        return mHasMenorMediaHistorica;
    }

    public void setHasMenorMediaHistorica(boolean mHasMenorMediaHistorica) {
        this.mHasMenorMediaHistorica = mHasMenorMediaHistorica;
    }

    public boolean getHasMayorPorcentajeZonasSeguras() {
        return mHasMayorPorcentajeZonasSeguras;
    }

    public void setHasMayorPorcentajeZonasSeguras(boolean mHasMayorPorcentajeZonasSeguras) {
        this.mHasMayorPorcentajeZonasSeguras = mHasMayorPorcentajeZonasSeguras;
    }

    public boolean getHasMayorPorcentajeDistanciaSegura() {
        return mHasMayorPorcentajeDistanciaSegura;
    }

    public void setHasMayorPorcentajeDistanciaSegura(boolean mHasMayorPorcentajeDistanciaSegura) {
        this.mHasMayorPorcentajeDistanciaSegura = mHasMayorPorcentajeDistanciaSegura;
    }

    public List<String> getDelitosZonasInseguras() {
        return mDelitosZonasInseguras;
    }

    public void setDelitosZonasInseguras(List<String> mDelitosZonasInseguras) {
        this.mDelitosZonasInseguras = mDelitosZonasInseguras;
    }
}
