package com.qianyitian.hope2.stock.model;

import java.time.LocalDate;

public class AdjFactorInfo {
    LocalDate date;
    double adjFactor;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getAdjFactor() {
        return adjFactor;
    }

    public void setAdjFactor(double adjFactor) {
        this.adjFactor = adjFactor;
    }
}
