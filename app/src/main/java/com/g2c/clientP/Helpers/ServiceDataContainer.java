package com.g2c.clientP.Helpers;

import com.g2c.clientP.model.ModelFinalService;
import com.g2c.clientP.model.ModelLocation;
import com.g2c.clientP.model.ModelPayment;
import com.g2c.clientP.model.ModelVehicle;
import com.g2c.clientP.model.MyPackageModel;
import com.g2c.clientP.model.PaymentBoxModel;

import java.io.Serializable;

public class ServiceDataContainer implements Serializable {

    ModelFinalService serviceData;
    ModelVehicle vehicle;
    String serviceName;
    String servicePrice;
    String serviceID;
    PaymentCalculator paymentCalculator;
    PaymentBoxModel currentDayDiscount;
    MyPackageModel packageDetails;


    public ServiceDataContainer() {
        this.serviceData = new ModelFinalService();
        this.vehicle = new ModelVehicle();
        serviceData.setLocation(new ModelLocation());
        serviceData.setPayment(new ModelPayment());

    }


    public PaymentCalculator getPaymentCalculator() {
        return paymentCalculator;
    }

    public void setPaymentCalculator(PaymentCalculator paymentCalculator) {
        this.paymentCalculator = paymentCalculator;
    }

    public String getServiceID() {
        return serviceID;
    }

    public void setServiceID(String serviceID) {
        this.serviceID = serviceID;
    }

    public MyPackageModel getPackageDetails() {
        return packageDetails;
    }

    public void setPackageDetails(MyPackageModel packageDetails) {
        this.packageDetails = packageDetails;
    }

    public PaymentBoxModel getCurrentDayDiscount() {
        return currentDayDiscount;
    }

    public void setCurrentDayDiscount(PaymentBoxModel currentDayDiscount) {
        this.currentDayDiscount = currentDayDiscount;
    }

    public String getServicePrice() {
        return servicePrice;
    }

    public void setServicePrice(String servicePrice) {
        this.servicePrice = servicePrice;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public ModelFinalService getServiceData() {
        return serviceData;
    }

    public void setServiceData(ModelFinalService serviceData) {
        this.serviceData = serviceData;
    }

    public ModelVehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(ModelVehicle vehicle) {
        this.vehicle = vehicle;
    }
}
