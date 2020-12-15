package com.qianyitian.hope2.analyzer.model;

/**
 * Created by bing.a.qian on 5/8/2017.
 */
public class Macd {
    double diff;
    double dea;
    double bar;
    double ema12;
    double ema26;
    KLineInfo dailyInfo;

    public KLineInfo getDailyInfo() {
        return dailyInfo;
    }

    public void setDailyInfo(KLineInfo dailyInfo) {
        this.dailyInfo = dailyInfo;
    }

    public double getEma12() {
        return ema12;
    }

    public void setEma12(double ema12) {
        this.ema12 = ema12;
    }

    public double getEma26() {
        return ema26;
    }

    public void setEma26(double ema26) {
        this.ema26 = ema26;
    }

    public double getDiff() {
        return diff;
    }

    public void setDiff(double diff) {
        this.diff = diff;
    }

    public double getDea() {
        return dea;
    }

    public void setDea(double dea) {
        this.dea = dea;
    }

    public double getBar() {
        return bar;
    }

    public void setBar(double bar) {
        this.bar = bar;
    }
}
