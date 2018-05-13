package com.example.hoang.myapplication.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Driver implements Parcelable {
    private float rating;
    private int trip_count;
    private int trip_accept;
    private int trip_cancel;
    private int status;
    private String userID;
    private String mName;

    public Driver() {
    }

    protected Driver(Parcel in) {
        rating = in.readFloat();
        trip_count = in.readInt();
        trip_accept = in.readInt();
        trip_cancel = in.readInt();
        status = in.readInt();
        userID = in.readString();
        mName = in.readString();
        mPhone = in.readString();
        mCar = in.readString();
        mService = in.readString();
        mProfileImageUrl = in.readString();
    }

    public static final Creator<Driver> CREATOR = new Creator<Driver>() {
        @Override
        public Driver createFromParcel(Parcel in) {
            return new Driver(in);
        }

        @Override
        public Driver[] newArray(int size) {
            return new Driver[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(rating);
        dest.writeInt(trip_count);
        dest.writeInt(trip_accept);
        dest.writeInt(trip_cancel);
        dest.writeInt(status);
        dest.writeString(userID);
        dest.writeString(mName);
        dest.writeString(mPhone);
        dest.writeString(mCar);
        dest.writeString(mService);
        dest.writeString(mProfileImageUrl);
    }
}
