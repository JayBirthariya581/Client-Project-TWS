package com.g2c.clientP.Helpers;

public class ReferralHelper {
    String code,points;

    public ReferralHelper(String code, String points) {
        this.code = code;
        this.points = points;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }
}
