package com.qianyitian.hope2.analyzer.model;

/**
 * Created by bing.a.qian on 2021-2-8.
 */
public enum EnumPortfolio {
    HK("港股", "hk"),
    US("美股", "us"),
    H20("希望20", "h20"),
    H150("希望150", "h150"),
    F200("基金重仓", "f200");

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
