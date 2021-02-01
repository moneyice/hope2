/**
  * Copyright 2021 jb51.net 
  */
package com.qianyitian.hope2.spider.model;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;
import java.util.List;

/**
 * Auto-generated: 2021-01-27 10:33:18
 *
 * @author jb51.net (i@jb51.net)
 * @website http://tools.jb51.net/code/json2javabean
 */
public class FundPosition {

    @JSONField(name = "stock_percent")
    private double stockPercent;
    @JSONField(name = "cash_percent")
    private double cashPercent;
    @JSONField(name = "other_percent")
    private double otherPercent;
    @JSONField(name = "asset_tot")
    private double assetTot;
    @JSONField(name = "asset_val")
    private double assetVal;
    @JSONField(name = "source_mark")
    private String sourceMark;
    private String source;
    private Date enddate;
    @JSONField(name = "stock_list")
    private List<StockList> stockList;
    @JSONField(name = "bond_list")
    private List<String> bondList;
    public void setStockPercent(double stockPercent) {
         this.stockPercent = stockPercent;
     }
     public double getStockPercent() {
         return stockPercent;
     }

    public void setCashPercent(double cashPercent) {
         this.cashPercent = cashPercent;
     }
     public double getCashPercent() {
         return cashPercent;
     }

    public void setOtherPercent(double otherPercent) {
         this.otherPercent = otherPercent;
     }
     public double getOtherPercent() {
         return otherPercent;
     }

    public void setAssetTot(double assetTot) {
         this.assetTot = assetTot;
     }
     public double getAssetTot() {
         return assetTot;
     }

    public void setAssetVal(double assetVal) {
         this.assetVal = assetVal;
     }
     public double getAssetVal() {
         return assetVal;
     }

    public void setSourceMark(String sourceMark) {
         this.sourceMark = sourceMark;
     }
     public String getSourceMark() {
         return sourceMark;
     }

    public void setSource(String source) {
         this.source = source;
     }
     public String getSource() {
         return source;
     }

    public void setEnddate(Date enddate) {
         this.enddate = enddate;
     }
     public Date getEnddate() {
         return enddate;
     }

    public void setStockList(List<StockList> stockList) {
         this.stockList = stockList;
     }
     public List<StockList> getStockList() {
         return stockList;
     }

    public void setBondList(List<String> bondList) {
         this.bondList = bondList;
     }
     public List<String> getBondList() {
         return bondList;
     }

}