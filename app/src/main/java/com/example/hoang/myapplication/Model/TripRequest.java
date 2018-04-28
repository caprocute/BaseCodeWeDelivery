package com.example.hoang.myapplication.Model;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class TripRequest {
    private String id;
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

    public void setMoney(float money) {
        this.money = money;
    }

    public Date getTimeDrop() {
        return timeDrop;
    }

    public void setTimeDrop(Date timeDrop) {
        this.timeDrop = timeDrop;
    }

    public TripRequest(String id,
                       String tripID,
                       String receiverName,
                       String receiverNumber, LatLng destination,
                       String note,
                       float money,
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
    private float money;
    private Date timeDrop;
}
