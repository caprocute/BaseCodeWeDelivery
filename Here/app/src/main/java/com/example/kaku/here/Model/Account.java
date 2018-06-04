package com.example.kaku.here.Model;

import java.util.Date;

public class Account {
    private String id;
    private String phone;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public Date getDate_created() {
        return date_created;
    }

    public void setDate_created(Date date_created) {
        this.date_created = date_created;
    }

    public String getAvartar() {
        return avartar;
    }

    public void setAvartar(String avartar) {
        this.avartar = avartar;
    }

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

    public int getAccount_type() {
        return account_type;
    }

    public void setAccount_type(int account_type) {
        this.account_type = account_type;
    }

    public int getGene() {
        return gene;
    }

    public void setGene(int gene) {
        this.gene = gene;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private String email;
    private String first_name;
    private String last_name;
    private Date date_created;
    private String avartar;
    private float rating;
    private int trip_count;
    private int trip_accept;
    private int trip_cancel;
    private int status;
    private int account_type;
    private int gene;
    private String address;

}
