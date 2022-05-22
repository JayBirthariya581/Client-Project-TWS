package com.geartocare.client.model;

import java.io.Serializable;

public class ModelPackage implements Serializable {
    String name,cost,serviceCost,description,vehicleCount,validity,id;
    //ModelValidity validity;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVehicleCount() {
        return vehicleCount;
    }

    public void setVehicleCount(String vehicleCount) {
        this.vehicleCount = vehicleCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
