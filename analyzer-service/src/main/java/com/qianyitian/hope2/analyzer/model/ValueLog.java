package com.qianyitian.hope2.analyzer.model;

import java.time.LocalDate;

/**
 * Created by bing.a.qian on 10/5/2017.
 */
public class ValueLog {

    LocalDate date;
    double totalValue;

    public double getCapital() {
        return capital;
    }

    public void setCapital(double capital) {
        this.capital = capital;
    }

    //本金
    double capital;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(double totalValue) {
        this.totalValue = totalValue;
    }
}
