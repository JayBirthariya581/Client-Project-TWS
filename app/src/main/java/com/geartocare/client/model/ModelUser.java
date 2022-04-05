package com.geartocare.client.model;


public class ModelUser {

    String uid,fullName,phone,email;
    ModelLocation location;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ModelLocation getLocation() {
        return location;
    }

    public void setLocation(ModelLocation location) {
        this.location = location;
    }
}
