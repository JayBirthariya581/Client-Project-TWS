package com.geartocare.client.Helpers;

public class LocationHelper {

    String lat,lng,txt;

    public LocationHelper(String lat, String lng, String txt) {
        this.lat = lat;
        this.lng = lng;
        this.txt = txt;
    }


    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }
}
