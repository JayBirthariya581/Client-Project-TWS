package com.geartocare.client.model;

public class ModelFinalService {

    String serviceID,date,time,phone,status,mechanicID;

    ModelLocation location;
    ModelPayment payment;

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



    public ModelLocation getLocation() {
        return location;
    }

    public void setLocation(ModelLocation location) {
        this.location = location;
    }

    public ModelPayment getPayment() {
        return payment;
    }

    public void setPayment(ModelPayment payment) {
        this.payment = payment;
    }
}
