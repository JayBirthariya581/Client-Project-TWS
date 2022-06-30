package com.g2c.clientP.model;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class ModelArea implements Serializable {

    LatLng bottomLeft;
    LatLng topRight;
    LatLng landMark;
    String name,landMarkName;


    public LatLng getLandMark() {
        return landMark;
    }

    public void setLandMark(LatLng landMark) {
        this.landMark = landMark;
    }

    public String getLandMarkName() {
        return landMarkName;
    }

    public void setLandMarkName(String landMarkName) {
        this.landMarkName = landMarkName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getBottomLeft() {
        return bottomLeft;
    }

    public void setBottomLeft(LatLng bottomLeft) {
        this.bottomLeft = bottomLeft;
    }

    public LatLng getTopRight() {
        return topRight;
    }

    public void setTopRight(LatLng topRight) {
        this.topRight = topRight;
    }
}