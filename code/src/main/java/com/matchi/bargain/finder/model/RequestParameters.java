package com.matchi.bargain.finder.model;

import com.matchi.bargain.finder.enumeration.EnvironmentType;

import java.time.LocalDateTime;

public class RequestParameters {
    private final LocalDateTime dateTime;
    private final EnvironmentType environmentType;
    private final String city;

    public RequestParameters(LocalDateTime dateTime, EnvironmentType environmentType, String city) {
        this.dateTime = dateTime;
        this.environmentType = environmentType;
        this.city = city;
    }

    public LocalDateTime getLocalDateTime() {
        return dateTime;
    }

    public EnvironmentType getEnvironmentType() {
        return environmentType;
    }

    public String getCity() {
        return city;
    }
}
