package com.g2c.clientP.Helpers;

import com.g2c.clientP.model.PaymentBoxModel;

import java.io.Serializable;
import java.util.ArrayList;

public class PaymentCalculator implements Serializable {
    ArrayList<PaymentBoxModel> paymentList, originalList, offerList, otherOfferList, packageList;
    Integer total;


    public PaymentCalculator() {
        paymentList = new ArrayList<>();
        originalList = new ArrayList<>();
        offerList = new ArrayList<>();
        otherOfferList = new ArrayList<>();
        packageList = new ArrayList<>();
        total = 0;


    }


    public ArrayList<PaymentBoxModel> getPaymentList() {
        return paymentList;
    }

    public void setPaymentList(ArrayList<PaymentBoxModel> paymentList) {
        this.paymentList = paymentList;
    }

    public ArrayList<PaymentBoxModel> getOriginalList() {
        return originalList;
    }

    public void setOriginalList(ArrayList<PaymentBoxModel> originalList) {
        this.originalList = originalList;
    }

    public ArrayList<PaymentBoxModel> getOfferList() {
        return offerList;
    }

    public void setOfferList(ArrayList<PaymentBoxModel> offerList) {
        this.offerList = offerList;
    }

    public ArrayList<PaymentBoxModel> getOtherOfferList() {
        return otherOfferList;
    }

    public void setOtherOfferList(ArrayList<PaymentBoxModel> otherOfferList) {
        this.otherOfferList = otherOfferList;
    }

    public ArrayList<PaymentBoxModel> getPackageList() {
        return packageList;
    }

    public void setPackageList(ArrayList<PaymentBoxModel> packageList) {
        this.packageList = packageList;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }


    public void addToOriginalList(String type, String field, String value) {
        for (int i = 0; i < originalList.size(); i++) {
            if (originalList.get(i).getField().equals(field)) {
                originalList.remove(i);
            }
        }
        PaymentBoxModel pbm = new PaymentBoxModel();
        pbm.setField(field);
        pbm.setType(type);
        pbm.setValue(value);

        originalList.add(pbm);

    }

    public void addToPaymentList(String type, String field, String value) {

        for (int i = 0; i < paymentList.size(); i++) {
            if (paymentList.get(i).getField().equals(field)) {
                paymentList.remove(i);
            }
        }

        PaymentBoxModel pbm = new PaymentBoxModel();
        pbm.setField(field);
        pbm.setType(type);
        pbm.setValue(value);

        paymentList.add(pbm);
    }

    public void addToOfferList(String type, String field, String value) {

        offerList.clear();

        PaymentBoxModel pbm = new PaymentBoxModel();
        pbm.setField(field);
        pbm.setType(type);
        pbm.setValue(value);

        offerList.add(pbm);
    }

    public void addToOtherOfferList(String type, String field, String value) {

        for (int i = 0; i < otherOfferList.size(); i++) {
            if (otherOfferList.get(i).getField().equals(field)) {
                otherOfferList.remove(i);
            }
        }

        PaymentBoxModel pbm = new PaymentBoxModel();
        pbm.setField(field);
        pbm.setType(type);
        pbm.setValue(value);

        otherOfferList.add(pbm);
    }

    public void addToPackageList(String type, String field, String value) {

        packageList.clear();

        PaymentBoxModel pbm = new PaymentBoxModel();
        pbm.setField(field);
        pbm.setType(type);
        pbm.setValue(value);

        packageList.add(pbm);
    }


    public Integer calculateTotal() {
        paymentList.clear();
        total = 0;
        for (PaymentBoxModel p : originalList) {
            paymentList.add(p);
            total += Integer.valueOf(p.getValue());

        }

        for (PaymentBoxModel p : packageList) {

            paymentList.add(p);
            total += Integer.valueOf(p.getValue());

        }


        for (PaymentBoxModel p : otherOfferList) {

            paymentList.add(p);
            total -= Integer.valueOf(p.getValue());

        }


        for (PaymentBoxModel p : offerList) {

            paymentList.add(p);
            total -= Integer.valueOf(p.getValue());

        }

        return total;

    }


}
