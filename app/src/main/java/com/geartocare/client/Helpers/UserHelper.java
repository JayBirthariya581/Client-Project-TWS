package com.geartocare.client.Helpers;

public class UserHelper {

    String uid,fullName,phone,email;
    LocationHelper location;

    public UserHelper(String uid, String fullName, String phone, String email, LocationHelper location) {
        this.uid = uid;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.location = location;
    }

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

    public LocationHelper getLocation() {
        return location;
    }

    public void setLocation(LocationHelper location) {
        this.location = location;
    }
}
