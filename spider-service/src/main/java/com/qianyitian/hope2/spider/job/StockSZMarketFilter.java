package com.qianyitian.hope2.spider.job;

/**
 * Created by bing.a.qian on 12/9/2016.
 */
public class StockSZMarketFilter extends StockFilter {
    @Override
    public boolean select(String code) {
        boolean result = false;
        if (code.startsWith("00") || code.startsWith("30")) {
            result = true;
        }
        return result;
    }
}
