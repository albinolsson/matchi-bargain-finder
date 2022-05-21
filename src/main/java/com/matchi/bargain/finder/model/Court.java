package com.matchi.bargain.finder.model;

public class Court {
    private final String name;
    private final int price;
    private final String duration;
    private final String facilityName;
    private final double hourlyRate;

    public Court(String name, int price, String duration, String facilityName, double hourlyRate) {
        this.name = name;
        this.price = price;
        this.duration = duration;
        this.facilityName = facilityName;
        this.hourlyRate = hourlyRate;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getDuration() {
        return duration;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }
}
