package com.g2c.clientP.Helpers;

public class MyPackageHelper {
    String packageID,name,cost,serviceCost,serviceCount;
    ValidityHelper validity;


    public MyPackageHelper(String packageID, String name, String cost, String serviceCost, String serviceCount, ValidityHelper validity) {
        this.packageID = packageID;
        this.name = name;
        this.cost = cost;
        this.serviceCost = serviceCost;
        this.serviceCount = serviceCount;
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

    public String getServiceCount() {
        return serviceCount;
    }

    public void setServiceCount(String serviceCount) {
        this.serviceCount = serviceCount;
    }

    public ValidityHelper getValidity() {
        return validity;
    }

    public void setValidity(ValidityHelper validity) {
        this.validity = validity;
    }
}
