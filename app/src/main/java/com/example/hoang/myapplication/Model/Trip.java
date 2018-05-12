package com.example.hoang.myapplication.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Trip implements Parcelable {
    private String id;
    private String driverid;
    private String customerid;

    public Trip() {
    }


    protected Trip(Parcel in) {
        id = in.readString();
        driverid = in.readString();
        customerid = in.readString();
        vehicleId = in.readString();
        driverRating = in.readFloat();
        customerRating = in.readFloat();
        moneySum = in.readFloat();
        byte tmpIsUsingLoading = in.readByte();
        isUsingLoading = tmpIsUsingLoading == 0 ? null : tmpIsUsingLoading == 1;
        distanceSum = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(driverid);
        dest.writeString(customerid);
        dest.writeString(vehicleId);
        dest.writeFloat(driverRating);
        dest.writeFloat(customerRating);
        dest.writeFloat(moneySum);
        dest.writeByte((byte) (isUsingLoading == null ? 0 : isUsingLoading ? 1 : 2));
        dest.writeFloat(distanceSum);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Trip> CREATOR = new Creator<Trip>() {
        @Override
        public Trip createFromParcel(Parcel in) {
            return new Trip(in);
        }

        @Override
        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };

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

    public Boolean getUsingLoading() {
        return isUsingLoading;
    }

    public void setUsingLoading(Boolean usingLoading) {
        isUsingLoading = usingLoading;
    }

    private Boolean isUsingLoading;

    public float getDistanceSum() {
        return distanceSum;
    }

    public void setDistanceSum(float distanceSum) {
        this.distanceSum = distanceSum;
    }

    private float distanceSum;


}
