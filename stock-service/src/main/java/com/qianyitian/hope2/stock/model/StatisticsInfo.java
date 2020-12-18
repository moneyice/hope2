package com.qianyitian.hope2.stock.model;

import java.time.LocalDate;
import java.util.Objects;

public class StatisticsInfo {
    LocalDate date;
    int totalStockNumber = 0;
    int limitUpNumber = 0;
    int limitDownNumber = 0;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StatisticsInfo)) return false;
        StatisticsInfo that = (StatisticsInfo) o;
        return Objects.equals(getDate(), that.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDate());
    }

    public StatisticsInfo(LocalDate date) {
        this.date = date;
    }

    public StatisticsInfo() {
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getTotalStockNumber() {
        return totalStockNumber;
    }

    public void setTotalStockNumber(int totalStockNumber) {
        this.totalStockNumber = totalStockNumber;
    }

    public void addTotalStockNumber() {
        this.totalStockNumber++;
    }

    public int getLimitUpNumber() {
        return limitUpNumber;
    }

    public void setLimitUpNumber(int limitUpNumber) {
        this.limitUpNumber = limitUpNumber;
    }

    public void addLimitUpNumber() {
        this.limitUpNumber++;
    }

    public int getLimitDownNumber() {
        return limitDownNumber;
    }

    public void setLimitDownNumber(int limitDownNumber) {
        this.limitDownNumber = limitDownNumber;
    }

    public void addLimitDownNumber() {
        this.limitDownNumber++;
    }
}
