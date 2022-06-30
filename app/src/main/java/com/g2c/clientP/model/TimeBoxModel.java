package com.g2c.clientP.model;

public class TimeBoxModel {
    Long timeStamp;
    String status;



    public TimeBoxModel(Long timeStamp, String status) {
        this.timeStamp = timeStamp;
        this.status = status;
    }



    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
