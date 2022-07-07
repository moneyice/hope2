package com.qianyitian.hope2.analyzer.analyzer;

/**
 * Created by bing.a.qian on 2/6/2017.
 */
public enum EStockAnalyzer {
    TomorrowEdge,Demark, SuddentHighVolume, SuddentHighVolumeLite,
    SuddentIncrease, SuddentLowVolume, IPO, YesterdayOnceMore, MACD, MACDAdvance, UnDemark;

    public static String ANALYZER_PACKAGE_NAME = "com.qianyitian.hope2.analyzer.analyzer.";
    public static String ANALYZER_SUFFIX_NAME = "Analyzer";

    public String getFullQualifiedClassName() {
        return ANALYZER_PACKAGE_NAME + name() + ANALYZER_SUFFIX_NAME;

    }
}
