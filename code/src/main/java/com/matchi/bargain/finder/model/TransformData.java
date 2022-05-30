package com.matchi.bargain.finder.model;

public class TransformData {
    private final String html;
    private final String hour;
    private final String minute;

    public TransformData(String html, String hour, String minute) {
        this.html = html;
        this.hour = hour;
        this.minute = minute;
    }

    public String getHtml() {
        return html;
    }

    public String getHour() {
        return hour;
    }

    public String getMinute() {
        return minute;
    }
}
