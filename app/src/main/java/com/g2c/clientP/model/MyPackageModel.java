package com.g2c.clientP.model;

import java.io.Serializable;

public class MyPackageModel implements Serializable {
    String packageID,name,cost,serviceCost,serviceCount;
    ModelValidity validity;
    ModelPayment payment;

    public ModelPayment getPayment() {
        return payment;
    }

    public void setPayment(ModelPayment payment) {
        this.payment = payment;
    }

    public ModelValidity getValidity() {
        return validity;
    }

    public void setValidity(ModelValidity validity) {
        this.validity = validity;
    }

    public String getPackageID() {
        return packageID;
    }

    public void setPackageID(String packageID) {
        this.packageID = packageID;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServiceCount() {
        return serviceCount;
    }

    public void setServiceCount(String serviceCount) {
        this.serviceCount = serviceCount;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getServiceCost() {
        return serviceCost;
    }

    public void setServiceCost(String serviceCost) {
        this.serviceCost = serviceCost;
    }


}
