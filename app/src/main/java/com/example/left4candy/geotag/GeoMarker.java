package com.example.left4candy.geotag;


public class GeoMarker{
    private static int geoMarkerId;
    private String geoMarkerName;
    private String geoMarkerColor;
    private double geoMarkerLat;
    private double geoMarkerLong;
    private String firstName;
    private String secondName;
    private String thirdName;
    private String firstField;
    private String secondField;
    private String thirdField;

    public GeoMarker(){
        this("name", "color", 0, 0);
        firstName = "Name One";
        secondName = "Name Two";
        thirdName = "Name Three";
        firstField = "Field One";
        secondField = "Field Two";
        thirdField = "Field Three";
        geoMarkerId++;
    }

    public GeoMarker(String geoMarkerName, String geoMarkerColor, double geoMarkerLat, double geoMarkerLong){
        this.geoMarkerName = geoMarkerName;
        this.geoMarkerColor = geoMarkerColor;
        this.geoMarkerLat = geoMarkerLat;
        this.geoMarkerLong = geoMarkerLong;
        firstName = "Name One";
        secondName = "Name Two";
        thirdName = "Name Three";
        firstField = "Field One";
        secondField = "Field Two";
        thirdField = "Field Three";
        geoMarkerId++;
    }

    public int getGeoMarkerId() {
        return geoMarkerId;
    }
    public void setGeoMarkerId(int geoMarkerId){ this.geoMarkerId = geoMarkerId; }

    public String getGeoMarkerColor() {
        return geoMarkerColor;
    }
    public void setGeoMarkerColor(String geoMarkerColor) {
        this.geoMarkerColor = geoMarkerColor;
    }

    public String getGeoMarkerName(){return geoMarkerName;}
    public void setGeoMarkerName(String geoMarkerName){
        this.geoMarkerName = geoMarkerName;
    }

    public double getGeoMarkerLong() {return geoMarkerLong;}
    public void setGeoMarkerLong(double geoMarkerLong) {
        this.geoMarkerLong = geoMarkerLong;
    }

    public double getGeoMarkerLat() {return geoMarkerLat;}
    public void setGeoMarkerLat(double geoMarkerLat) {
        this.geoMarkerLat = geoMarkerLat;
    }

    public String getFirstName() {return firstName; }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {return secondName; }
    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getThirdName() {return thirdName; }
    public void setThirdName(String thirdName) {
        this.thirdName = thirdName;
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
