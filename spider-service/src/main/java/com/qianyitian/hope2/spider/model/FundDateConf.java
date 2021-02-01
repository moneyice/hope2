/**
 * Copyright 2021 jb51.net
 */
package com.qianyitian.hope2.spider.model;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Auto-generated: 2021-01-27 10:33:18
 *
 * @author jb51.net (i@jb51.net)
 * @website http://tools.jb51.net/code/json2javabean
 */
public class FundDateConf {

    @JSONField(name = "fd_code")
    private String fdCode;
    @JSONField(name = "buy_confirm_date")
    private int buyConfirmDate;
    @JSONField(name = "buy_query_date")
    private int buyQueryDate;
    @JSONField(name = "sale_confirm_date")
    private int saleConfirmDate;
    @JSONField(name = "sale_query_date")
    private int saleQueryDate;
    @JSONField(name = "all_buy_days")
    private int allBuyDays;
    @JSONField(name = "all_sale_days")
    private int allSaleDays;

    public void setFdCode(String fdCode) {
        this.fdCode = fdCode;
    }

    public String getFdCode() {
        return fdCode;
    }

    public void setBuyConfirmDate(int buyConfirmDate) {
        this.buyConfirmDate = buyConfirmDate;
    }

    public int getBuyConfirmDate() {
        return buyConfirmDate;
    }

    public void setBuyQueryDate(int buyQueryDate) {
        this.buyQueryDate = buyQueryDate;
    }

    public int getBuyQueryDate() {
        return buyQueryDate;
    }

    public void setSaleConfirmDate(int saleConfirmDate) {
        this.saleConfirmDate = saleConfirmDate;
    }

    public int getSaleConfirmDate() {
        return saleConfirmDate;
    }

    public void setSaleQueryDate(int saleQueryDate) {
        this.saleQueryDate = saleQueryDate;
    }

    public int getSaleQueryDate() {
        return saleQueryDate;
    }

    public void setAllBuyDays(int allBuyDays) {
        this.allBuyDays = allBuyDays;
    }

    public int getAllBuyDays() {
        return allBuyDays;
    }

    public void setAllSaleDays(int allSaleDays) {
        this.allSaleDays = allSaleDays;
    }

    public int getAllSaleDays() {
        return allSaleDays;
    }

}