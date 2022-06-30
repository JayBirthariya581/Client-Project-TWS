package com.g2c.clientP.Helpers;

import java.io.Serializable;

public class FinalServiceHelper implements Serializable {

    String serviceID,date,time,phone,status,mechanicID,note;

    LocationHelper location;
    PaymentHelper payment;



    public FinalServiceHelper(String serviceID,String phone, String date, String time, LocationHelper location, PaymentHelper payment,String status,String mechanicID) {
        this.serviceID = serviceID;
        this.date = date;
        this.time = time;

        this.location = location;
        this.payment = payment;
        this.phone = phone;
        this.status = status;
        this.mechanicID = mechanicID;

    }


    public String getMechanicID() {
        return mechanicID;
    }

    public void setMechanicID(String mechanicID) {
        this.mechanicID = mechanicID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocationHelper getLocation() {
        return location;
    }

    public void setLocation(LocationHelper location) {
        this.location = location;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getServiceID() {
        return serviceID;
    }

    public void setServiceID(String serviceID) {
        this.serviceID = serviceID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }





    public PaymentHelper getPayment() {
        return payment;
    }

    public void setPayment(PaymentHelper payment) {
        this.payment = payment;
    }
}
