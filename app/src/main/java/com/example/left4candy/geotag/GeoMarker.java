package com.example.left4candy.geotag;


public class GeoMarker {
    private int geoMarkerId;
    private String geoMarkerName;
    private String geoMarkerColor;
    private float geoMarkerLat;
    private float geoMarkerLong;
    private String firstField;
    private String secondField;
    private String thirdField;

    public GeoMarker(){
    }

    public GeoMarker(String geoMarkerName, String geoMarkerColor, float geoMarkerLat, float geoMarkerLong){
        this.geoMarkerName = geoMarkerName;
        this.geoMarkerColor = geoMarkerColor;
        this.geoMarkerLat = geoMarkerLat;
        this.geoMarkerLong = geoMarkerLong;
        this.firstField = "Field One";
        this.secondField = "Field Two";
        this.thirdField = "Field Three";

    }

    public int getGeoMarkerId() {
        return geoMarkerId;
    }

    public String getGepMarkerColor() {
        return geoMarkerColor;
    }
    public void setGeoMarkerColor(String geoMarkerColor) {
        this.geoMarkerColor = geoMarkerColor;
    }

    public String geoMarkerName(){return geoMarkerName;}
    public void setGeoMarkerName(String geoMarkerName){
        this.geoMarkerName = geoMarkerName;
    }

    public double getGeoMarkerLong() {return geoMarkerLong;}
    public void setGeoMarkerLong(float geoMarkerLong) {
        this.geoMarkerLong = geoMarkerLong;
    }

    public double getGeoMarkerLat() {return geoMarkerLat;}
    public void setGeoMarkerLat(float geoMarkerLat) {
        this.geoMarkerLat = geoMarkerLat;
    }

    public String getFirstField() { return firstField; }
    public void setFirstField(String firstField) {
        this.firstField = firstField;
    }

    public String getSecondField() { return secondField; }
    public void setSecondField(String secondField) {
        this.secondField = secondField;
    }

    public String getThirdField() { return thirdField; }
    public void setThirdField(String thirdField) {
        this.thirdField = thirdField;
    }
}
