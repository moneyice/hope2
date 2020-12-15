package com.qianyitian.hope2.analyzer.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by bing.a.qian on 10/2/2017.
 */
public class AccountSummary {
    //可用金额
    double availableValue=0;
    //证券市值
    double marketValue=0;

    //总市值
    double totalValue=availableValue;

    List<TradeItem> tradeItemList=new LinkedList<>();

    List<PositionItem> positionItemMap=new LinkedList<>();
    List<ValueLog> valueLogList;

    public List<ValueLog> getValueLogList() {
        return valueLogList;
    }

    public void setValueLogList(List<ValueLog> valueLogList) {
        this.valueLogList = valueLogList;
    }

    public double getAvailableValue() {
        return availableValue;
    }

    public void setAvailableValue(double availableValue) {
        this.availableValue = availableValue;
    }

    public double getMarketValue() {
        return marketValue;
    }

    public void setMarketValue(double marketValue) {
        this.marketValue = marketValue;
    }

    public double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(double totalValue) {
        this.totalValue = totalValue;
    }

    public List<TradeItem> getTradeItemList() {
        return tradeItemList;
    }

    public void setTradeItemList(List<TradeItem> tradeItemList) {
        this.tradeItemList = tradeItemList;
    }

    public List<PositionItem> getPositionItemMap() {
        return positionItemMap;
    }

    public void setPositionItemMap(List<PositionItem> positionItemMap) {
        this.positionItemMap = positionItemMap;
    }
}
