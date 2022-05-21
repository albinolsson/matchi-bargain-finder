package com.matchi.bargain.finder.model;

public class Court {
    private String name;
    private int price;
    private String duration;
    private String facilityName;
    private double hourlyRate;
    private String slotPriceId;

    public Court(String name, int price, String duration, String facilityName, double hourlyRate, String slotPriceId) {
        this.name = name;
        this.price = price;
        this.duration = duration;
        this.facilityName = facilityName;
        this.hourlyRate = hourlyRate;
        this.slotPriceId = slotPriceId;
    }

    public Court(String name, String duration, String facilityName, String slotPriceId) {
        this.name = name;
        this.duration = duration;
        this.facilityName = facilityName;
        this.slotPriceId = slotPriceId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public String getSlotPriceId() {
        return slotPriceId;
    }

    public void setSlotPriceId(String slotPriceId) {
        this.slotPriceId = slotPriceId;
    }
}
