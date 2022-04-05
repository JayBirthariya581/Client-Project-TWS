package com.geartocare.client.Helpers;

import com.geartocare.client.model.ModelValidity;

public class MyPackageHelper {
    String packageID,name,cost,serviceCost,vehicleCount;
    ValidityHelper validity;


    public MyPackageHelper(String packageID, String name, String cost, String serviceCost, String vehicleCount, ValidityHelper validity) {
        this.packageID = packageID;
        this.name = name;
        this.cost = cost;
        this.serviceCost = serviceCost;
        this.vehicleCount = vehicleCount;
        this.validity = validity;
    }

    public void setValidity(ValidityHelper validity) {
        this.validity = validity;
    }

    public String getPackageID() {
        return packageID;
    }

    public void setPackageID(String packageID) {
        this.packageID = packageID;
    }

    public ValidityHelper getValidity() {
        return validity;
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
