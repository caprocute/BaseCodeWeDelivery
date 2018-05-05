package com.example.hoang.myapplication.Model;

public class Driver {
    private float rating;
    private int trip_count;
    private int trip_accept;
    private int trip_cancel;
    private int status;
    private String userID;
    private String mName;

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getTrip_count() {
        return trip_count;
    }

    public void setTrip_count(int trip_count) {
        this.trip_count = trip_count;
    }

    public int getTrip_accept() {
        return trip_accept;
    }

    public void setTrip_accept(int trip_accept) {
        this.trip_accept = trip_accept;
    }

    public int getTrip_cancel() {
        return trip_cancel;
    }

    public void setTrip_cancel(int trip_cancel) {
        this.trip_cancel = trip_cancel;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmPhone() {
        return mPhone;
    }

    public void setmPhone(String mPhone) {
        this.mPhone = mPhone;
    }

    public String getmCar() {
        return mCar;
    }

    public void setmCar(String mCar) {
        this.mCar = mCar;
    }

    public String getmService() {
        return mService;
    }

    public void setmService(String mService) {
        this.mService = mService;
    }

    public String getmProfileImageUrl() {
        return mProfileImageUrl;
    }

    public void setmProfileImageUrl(String mProfileImageUrl) {
        this.mProfileImageUrl = mProfileImageUrl;
    }

    private String mPhone;
    private String mCar;
    private String mService;
    private String mProfileImageUrl;

    public boolean isTrueDriver() {
        if (this.mName == null || this.mName.isEmpty()) return false;
        if (this.mCar == null || this.mCar.isEmpty()) return false;
        if (this.mPhone == null || this.mPhone.isEmpty()) return false;
        if (this.mProfileImageUrl == null || this.mProfileImageUrl.isEmpty()) return false;
        if (this.mService == null || this.mService.isEmpty()) return false;
        if (this.userID == null || this.userID.isEmpty()) return false;
        return true;
    }
}
