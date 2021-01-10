package com.qianyitian.hope2.analyzer.model;

/**
 * Created by bing.a.qian on 2021-1-9.
 */
public enum ETF {
    GFETF("光伏ETF", "515790", "SH"),
    HBETF("环保ETF", "512580", "SH"),
    YLETF("医疗ETF", "512170", "SH"),
    YSJSETF("有色金属ETF", "512400", "SH"),
    JDETF("家电ETF", "159996", "SZ"),
    GTETF("钢铁ETF", "515210", "SH"),
    MTETF("煤炭ETF", "515220", "SH"),
    XNYCETF("新能源车ETF", "515030", "SH"),
    YHETF("银行ETF", "512800", "SH"),
    ZYETF("证券ETF", "512880", "SH"),
    SWYYETF("生物医药ETF", "512290", "SH"),
    FDCETF("房地产ETF", "512200", "SH"),
    XPETF("芯片ETF", "512760", "SH"),
    G5ETF("5GETF", "515050", "SH"),
    JGETF("军工ETF", "512660", "SH"),
    JETF("酒ETF", "512690", "SH");


    String name;
    String code;
    String market;

    ETF(String name, String code, String market) {
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
