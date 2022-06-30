package com.g2c.clientP.Helpers;

public class VehicleHelper {
    String Company,Model,VehicleNo,vehicleID;



    public VehicleHelper(String company, String model, String vehicleNo,String vehicleID) {
        Company = company;
        Model = model;
        VehicleNo = vehicleNo;
        this.vehicleID = vehicleID;

    }


    public String getVehicleID() {
        return vehicleID;
    }

    public void setVehicleID(String vehicleID) {
        this.vehicleID = vehicleID;
    }

    public String getVehicleNo() {
        return VehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        VehicleNo = vehicleNo;
    }

    public String getCompany() {
        return Company;
    }

    public void setCompany(String company) {
        Company = company;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        Model = model;
    }
}
