package com.example.hoang.myapplication.Model;

public class Vehicle {
    private String id;
    private String ownerName;
    private String brand;
    private String color;
    private String plate;

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    private String ownerId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getImgVehicle() {
        return imgVehicle;
    }

    public void setImgVehicle(String imgVehicle) {
        this.imgVehicle = imgVehicle;
    }

    public String getImgVehicleCer() {
        return imgVehicleCer;
    }

    public void setImgVehicleCer(String imgVehicleCer) {
        this.imgVehicleCer = imgVehicleCer;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    private String imgVehicle;
    private String imgVehicleCer;
    private String vehicleType;
}
