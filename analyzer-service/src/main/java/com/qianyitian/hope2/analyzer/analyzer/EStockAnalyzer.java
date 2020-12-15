package com.qianyitian.hope2.analyzer.analyzer;

/**
 * Created by bing.a.qian on 2/6/2017.
 */
public enum EStockAnalyzer {
    Demark, SuddentHighVolume, SuddentHighVolumeLite,
    SuddentIncrease, SuddentLowVolume, IPO,YesterdayOnceMore, MACD,MACDAdvance;

    public static String ANALYZER_PACKAGE_NAME = "com.qianyitian.hope2.analyzer.analyzer.";
    public static String ANALYZER_SUFFIX_NAME = "Analyzer";

    public String getFullQualifiedClassName() {
        return ANALYZER_PACKAGE_NAME + name() + ANALYZER_SUFFIX_NAME;

    }

    public EStockAnalyzer getEnumStockAnalyzer(String value) {
        return EStockAnalyzer.valueOf(value);
    }

}
