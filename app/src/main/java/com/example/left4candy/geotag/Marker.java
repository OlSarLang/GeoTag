package com.example.left4candy.geotag;


public class Marker {
    public int markerId;
    private String markerColor;
    private String markerType;
    private double markerLong;

    public int getMarkerId() {
        return markerId;
    }
    public String getMarkerColor() {
        return markerColor;
    }
    public void setMarkerColor(String markerColor) {
        this.markerColor = markerColor;
    }
    public String getMarkerType() {
        return markerType;
    }
    public void setMarkerType(String markerType) {
        this.markerType = markerType;
    }
    public double getMarkerLong() {
        return markerLong;
    }
    public void setMarkerLong(double markerLong) {
        this.markerLong = markerLong;
    }
    public double getMarkerLat() {
        return markerLat;
    }
    public void setMarkerLat(double markerLat) {
        this.markerLat = markerLat;
    }
    private double markerLat;

}
