package com.qianyitian.hope2.spider.model;

/**
 * Created by bing.a.qian on 2/6/2017.
 */
public enum EIndex {
    ShanghaiIndex("上证指数","000001","SH"), ShenzhenIndex("深证成指","399001","SZ"), HS300("沪深300","000300","SH"),ZZ500("中证500","000905","SH");

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
