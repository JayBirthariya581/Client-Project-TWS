package com.geartocare.client.model;

public class ModelMyPackage {
    String packageID,name,cost,serviceCost,vehicleCount;
    ModelValidity validity;

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

    public String getVehicleCount() {
        return vehicleCount;
    }

    public void setVehicleCount(String vehicleCount) {
        this.vehicleCount = vehicleCount;
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
