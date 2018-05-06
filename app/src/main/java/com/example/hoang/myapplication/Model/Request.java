package com.example.hoang.myapplication.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class Request implements Parcelable {
    private String id;
    private String destinationName;

    protected Request(Parcel in) {
        id = in.readString();
        destinationName = in.readString();
        tripID = in.readString();
        receiverName = in.readString();
        receiverNumber = in.readString();
        destination = in.readParcelable(LatLng.class.getClassLoader());
        note = in.readString();
        money = in.readLong();
    }

    public static final Creator<Request> CREATOR = new Creator<Request>() {
        @Override
        public Request createFromParcel(Parcel in) {
            return new Request(in);
        }

        @Override
        public Request[] newArray(int size) {
            return new Request[size];
        }
    };

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    private String tripID;
    private String receiverName;
    private String receiverNumber;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTripID() {
        return tripID;
    }

    public void setTripID(String tripID) {
        this.tripID = tripID;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverNumber() {
        return receiverNumber;
    }

    public void setReceiverNumber(String receiverNumber) {
        this.receiverNumber = receiverNumber;
    }

    public LatLng getDestination() {
        return destination;
    }

    public void setDestination(LatLng destination) {
        this.destination = destination;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public float getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }

    public Date getTimeDrop() {
        return timeDrop;
    }

    public void setTimeDrop(Date timeDrop) {
        this.timeDrop = timeDrop;
    }

    public Request() {
    }

    public Request(String id,
                   String tripID,
                   String receiverName,
                   String receiverNumber, LatLng destination,
                   String note,
                   long money,
                   Date timeDrop) {
        this.id = id;
        this.tripID = tripID;
        this.receiverName = receiverName;
        this.receiverNumber = receiverNumber;
        this.destination = destination;
        this.note = note;
        this.money = money;
        this.timeDrop = timeDrop;

    }

    private LatLng destination;
    private String note;
    private long money;
    private Date timeDrop;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(destinationName);
        dest.writeString(tripID);
        dest.writeString(receiverName);
        dest.writeString(receiverNumber);
        dest.writeParcelable(destination, flags);
        dest.writeString(note);
        dest.writeFloat(money);
    }

    public boolean isRequestFilled() {
        if (this.receiverName == null || this.receiverName.isEmpty()) return false;
        if (this.receiverNumber == null || this.receiverNumber.isEmpty()) return false;
        return true;
    }

    public boolean isStartPointAndItDone() {
        if (this.destinationName == null || this.destinationName.isEmpty()) return false;
        if (this.destination == null) return false;
        return true;
    }

    public boolean isRequestDone() {
        if (this.id == null || this.id.isEmpty()) return false;
        if (this.destinationName == null || this.destinationName.isEmpty()) return false;
        if (this.tripID == null || this.tripID.isEmpty()) return false;
        if (this.receiverName == null || this.receiverName.isEmpty()) return false;
        if (this.receiverNumber == null || this.receiverNumber.isEmpty()) return false;
        if (this.destination == null) return false;
        return true;
    }
}
