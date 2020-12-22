package com.qianyitian.hope2.stock.model;

/**
 * Created by bing.a.qian on 2/6/2017.
 */
public enum EIndex {
    ShanghaiIndex("上证指数", "000001", "SH"),
    ShenzhenIndex("深证成指", "399001", "SZ"),
    SZ50("上证50", "000016", "SH"),
    HS300("沪深300", "000300", "SH"),
    ZZ500("中证500", "000905", "SH"),
    CYB("创业板", "399006", "SZ");

    String name;
    String code;
    String market;

    EIndex(String name, String code, String market) {
        this.name = name;
        this.code = code;
        this.market = market;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getMarket() {
        return market;
    }
}
