package com.example.hoang.myapplication.Model;

import java.util.Date;

public class Trip {
    private String id;
    private String driverid;
    private String customerid;

    public String getCustomerid() {
        return customerid;
    }

    public void setCustomerid(String customerid) {
        this.customerid = customerid;
    }

    private Date pickupTime;
    private Date droffOffTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDriverid() {
        return driverid;
    }

    public void setDriverid(String driverid) {
        this.driverid = driverid;
    }

    public Date getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(Date pickupTime) {
        this.pickupTime = pickupTime;
    }

    public Date getDroffOffTime() {
        return droffOffTime;
    }

    public void setDroffOffTime(Date droffOffTime) {
        this.droffOffTime = droffOffTime;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public float getDriverRating() {
        return driverRating;
    }

    public void setDriverRating(float driverRating) {
        this.driverRating = driverRating;
    }

    public float getCustomerRating() {
        return customerRating;
    }

    public void setCustomerRating(float customerRating) {
        this.customerRating = customerRating;
    }

    public float getMoneySum() {
        return moneySum;
    }

    public void setMoneySum(float moneySum) {
        this.moneySum = moneySum;
    }

    private String vehicleId;
    private float driverRating;
    private float customerRating;
    private float moneySum;

}
