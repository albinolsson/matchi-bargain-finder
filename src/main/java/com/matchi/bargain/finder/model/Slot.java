package com.matchi.bargain.finder.model;

public class Slot {
    private String slotId;
    private String currency;
    private int price;

    public Slot() {
    }

    public Slot(String slotId, String currency, int price) {
        this.slotId = slotId;
        this.currency = currency;
        this.price = price;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
