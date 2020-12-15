package com.qianyitian.hope2.analyzer.model;

import java.util.List;

public class Stock {
    public static final String TYPE_DAILY="daily";
    public static final String TYPE_WEEKLY="weekly";
    public static final String TYPE_MONTHLY="monthly";

    List<KLineInfo> kLineInfos = null;

    String code;
    String name;
    String market;
    String kLineType;

    public List<KLineInfo> getkLineInfos() {
        return kLineInfos;
    }

    public void setkLineInfos(List<KLineInfo> kLineInfos) {
        this.kLineInfos = kLineInfos;
    }

    public String getkLineType() {
        return kLineType;
    }

    public void setkLineType(String kLineType) {
        this.kLineType = kLineType;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Stock [code=" + code + ", name=" + name + ", market=" + market
                + "]";
    }

}
