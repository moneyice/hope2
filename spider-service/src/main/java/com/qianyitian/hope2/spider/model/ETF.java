package com.qianyitian.hope2.spider.model;

import java.util.Arrays;

/**
 * Created by bing.a.qian on 2021-1-9.
 */
public enum ETF {
    MTETF("煤炭ETF", "515220", "SH"),
    ZYETF("证券ETF", "512880", "SH"),
    YZETF("养殖ETF", "159865", "SZ"),
    JGETF("军工ETF", "512660", "SH"),
    SWYYETF("生物医药ETF", "512290", "SH"),
    JSJETF("计算机ETF", "512720", "SH"),
    TXETF("通信ETF", "515880", "SH"),
    JRETF("金融ETF", "510230", "SH"),
    JDETF("家电ETF", "159996", "SZ"),
    GTETF("钢铁ETF", "515210", "SH"),
    SPYLETF("食品饮料ETF", "515170", "SH"),
    CCZETF("创成长ETF", "159967", "SZ"),
    XPETF("芯片ETF", "512760", "SH"),
    YLETF("医疗ETF", "512170", "SH"),
    HGETF("化工ETF", "516020", "SH"),
    YHETF("银行ETF", "512800", "SH"),
    KJETF("科技ETF", "515000", "SH"),
    DZETF("电子ETF", "515260", "SH"),
    ZNZZETF("智能制造ETF", "516800", "SH"),
    GFETF("光伏ETF", "515790", "SH"),
    HBETF("环保ETF", "512580", "SH"),
    YSJSETF("有色金属ETF", "512400", "SH"),
    XNYCETF("新能源车ETF", "515030", "SH"),
    FDCETF("房地产ETF", "512200", "SH"),
    G5ETF("5GETF", "515050", "SH"),
    JETF("酒ETF", "512690", "SH"),
    RJETF("软件ETF", "159852", "SZ"),
    ZGHL("中概互联","513050","SH"),
    CHINAHL("中国互联","164906","SH"),
    HETF("H股ETF","510900","SH"),
    HSETF("恒生ETF","159920","SH"),
    NZETF("纳指ETF","513100","SH"),
    PP500("标普500","513500","SH"),
    DG30("德国30","513030","SH"),
    HSHLWETF("恒生互联网","513330","SH");


//    public static void main(String[] args) {
//        Arrays.stream(ETF.values()).forEach(eft->{
//
//            System.out.println(eft.code+","+eft.name);
//
//        });
//    }

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
