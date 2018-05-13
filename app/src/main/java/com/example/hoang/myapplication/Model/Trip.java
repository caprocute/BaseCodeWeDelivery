package com.example.hoang.myapplication.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Trip implements Parcelable {
    private String id;
    private String driverid;
    private String customerid;
    private String drivingMode;
    private Boolean isExpressMode;
    private String status;

    protected Trip(Parcel in) {
        id = in.readString();
        driverid = in.readString();
        customerid = in.readString();
        drivingMode = in.readString();
        byte tmpIsExpressMode = in.readByte();
        isExpressMode = tmpIsExpressMode == 0 ? null : tmpIsExpressMode == 1;
        status = in.readString();
        pickupTime = in.readLong();
        droffOffTime = in.readLong();
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
        dest.writeString(drivingMode);
        dest.writeByte((byte) (isExpressMode == null ? 0 : isExpressMode ? 1 : 2));
        dest.writeString(status);
        dest.writeLong(pickupTime);
        dest.writeLong(droffOffTime);
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getExpressMode() {
        return isExpressMode;
    }

    public void setExpressMode(Boolean expressMode) {
        isExpressMode = expressMode;
    }

    public Trip() {
        this.drivingMode = "HereBike";
        this.isExpressMode = false;
        this.isUsingLoading = false;
    }

    public String getCustomerid() {
        return customerid;
    }

    public void setCustomerid(String customerid) {
        this.customerid = customerid;
    }

    private long pickupTime;
    private long droffOffTime;

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

    public long getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(long pickupTime) {
        this.pickupTime = pickupTime;
    }

    public long getDroffOffTime() {
        return droffOffTime;
    }

    public void setDroffOffTime(long droffOffTime) {
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


    public String getDrivingMode() {
        return drivingMode;
    }

    public void setDrivingMode(String drivingMode) {
        this.drivingMode = drivingMode;
    }

}
