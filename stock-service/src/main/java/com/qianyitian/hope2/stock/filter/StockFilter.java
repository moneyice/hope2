package com.qianyitian.hope2.stock.filter;

/**
 * Created by bing.a.qian on 12/9/2016.
 */
public class StockFilter {
    public boolean select(String code) {
        boolean result = false;
        if (code.startsWith("0") || code.startsWith("6") || code.startsWith("3")) {
            result = true;
        }
        return result;
    }
}
