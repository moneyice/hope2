package com.qianyitian.hope2.analyzer.model;

import java.time.LocalDate;

/**
 * Created by bing.a.qian on 10/2/2017.
 */
public class TradeItem {
    LocalDate date;
    //成交额
    double tradeValue;

    String code;
    //买入还是卖出
    ETradeType tradeType;

    //总资产=市值+可用金额
    double totalValue=0;
    //市值
    double marketValue=0;
    //可用金额
    double availableValue=0;

    //成交数量
    double quantity;

    //交易价格
    double tradePrice;
    //手续费
    double fee;

    public double getAvailableValue() {
        return availableValue;
    }

    public void setAvailableValue(double availableValue) {
        this.availableValue = availableValue;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getTradePrice() {
        return tradePrice;
    }

    public void setTradePrice(double tradePrice) {
        this.tradePrice = tradePrice;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getTradeValue() {
        return tradeValue;
    }

    public void setTradeValue(double tradeValue) {
        this.tradeValue = tradeValue;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ETradeType getTradeType() {
        return tradeType;
    }

    public void setTradeType(ETradeType tradeType) {
        this.tradeType = tradeType;
    }

    public double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(double totalValue) {
        this.totalValue = totalValue;
    }

    public double getMarketValue() {
        return marketValue;
    }

    public void setMarketValue(double marketValue) {
        this.marketValue = marketValue;
    }


    @Override
    public String toString() {
        return "TradeItem{" +
                "date=" + date +
                ", tradeValue=" + tradeValue +
                ", code='" + code + '\'' +
                ", tradeType=" + tradeType +
                ", totalValue=" + totalValue +
                ", marketValue=" + marketValue +
                ", availableValue=" + availableValue +
                ", quantity=" + quantity +
                ", tradePrice=" + tradePrice +
                ", fee=" + fee +
                '}';
    }
}
