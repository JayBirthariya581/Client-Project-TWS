package com.g2c.clientP.model;

public class ModelTimeSlot {

    String key;

    Long timeStamp;

    public ModelTimeSlot(String key, Long timeStamp) {
        this.key = key;
        this.timeStamp = timeStamp;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
