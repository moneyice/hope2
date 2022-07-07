package com.qianyitian.hope2.analyzer.model;

import java.io.Serializable;
import java.time.LocalDate;

public class KLineInfo implements Serializable {
    double close;
    double open;
    double high;
    double low;
    LocalDate date;
    double macd;
    double turnoverRate;
    double changePercent;

    public double getChangePercent() {
        return changePercent;
    }

    public void setChangePercent(double changePercent) {
        this.changePercent = changePercent;
    }

    public double getMacd() {
        return macd;
    }

    public void setMacd(double macd) {
        this.macd = macd;
    }

    public double getTurnoverRate() {
        return turnoverRate;
    }

    public void setTurnoverRate(double turnoverRate) {
        this.turnoverRate = turnoverRate;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "DailyInfo{" +
                "close=" + close +
                ", open=" + open +
                ", high=" + high +
                ", low=" + low +
                ", date=" + date +
                ", turnoverRate=" + turnoverRate +
                '}';
    }
}
