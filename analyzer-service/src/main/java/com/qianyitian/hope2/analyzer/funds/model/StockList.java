/**
 * Copyright 2021 jb51.net
 */
package com.qianyitian.hope2.analyzer.funds.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Objects;

/**
 * Auto-generated: 2021-01-27 10:33:18
 *
 * @author jb51.net (i@jb51.net)
 * @website http://tools.jb51.net/code/json2javabean
 */
public class StockList {
    private String name;
    private String code;
    private double percent;
    @JSONField(name = "current_price")
    private double currentPrice;
    @JSONField(name = "change_percentage")
    private double changePercentage;
    @JSONField(name = "xq_symbol")
    private String xqSymbol;
    @JSONField(name = "xq_url")
    private String xqUrl;
    private boolean amarket;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    public double getPercent() {
        return percent;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setChangePercentage(double changePercentage) {
        this.changePercentage = changePercentage;
    }

    public double getChangePercentage() {
        return changePercentage;
    }

    public void setXqSymbol(String xqSymbol) {
        this.xqSymbol = xqSymbol;
    }

    public String getXqSymbol() {
        return xqSymbol;
    }

    public void setXqUrl(String xqUrl) {
        this.xqUrl = xqUrl;
    }

    public String getXqUrl() {
        return xqUrl;
    }

    public void setAmarket(boolean amarket) {
        this.amarket = amarket;
    }

    public boolean getAmarket() {
        return amarket;
    }

    @Override
    public String toString() {
        return code + "," + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StockList)) return false;
        StockList stockList = (StockList) o;
        return getCode().equals(stockList.getCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCode());
    }
}