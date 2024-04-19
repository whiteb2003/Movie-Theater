package com.group5.cpl.model.enums;

public enum SeatType {
    VIP(100000.0),
    NORMAL(70000.0);

    private final Double price;

    SeatType(Double price) {
        this.price = price;
    }

    public Double getPrice() {
        return this.price;
    }
}
