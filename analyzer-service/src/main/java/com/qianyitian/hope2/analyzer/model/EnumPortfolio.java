package com.qianyitian.hope2.analyzer.model;

/**
 * Created by bing.a.qian on 2021-2-8.
 */
public enum EnumPortfolio {
    HK("港股", "hk"),
    US("美股", "us"),
    MAO("茅指数", "mao"),
    NING("宁组合", "ning"),
    F200("金仓200", "f200"),
    ETF("ETF基金", "etf");

    String code;
    String name;

    EnumPortfolio(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

}
