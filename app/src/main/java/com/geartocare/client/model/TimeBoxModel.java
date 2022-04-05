package com.geartocare.client.model;

public class TimeBoxModel {
    String time,status;


    public String getTime() {
        return time;
    }

    public TimeBoxModel(String time, String status) {
        this.time = time;
        this.status = status;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
