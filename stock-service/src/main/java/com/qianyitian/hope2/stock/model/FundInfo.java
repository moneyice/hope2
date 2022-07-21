
package com.qianyitian.hope2.stock.model;

import java.time.LocalDate;
import java.util.List;


public class FundInfo {
    String code;
    String name;
    String type;
    float netValue;
    float totalValue;
    float grToday;
    float grl1Week;
    float grl1Month;
    float grl3Month;
    float grl6Month;
    float grl1Year;
    String managers;
    float scale;
    LocalDate updateDate;
    float grl2Year;
    float grl3Year;
    float grl5Year;
    float grThisYear;
    float grBase;
    float cagr;
    LocalDate foundDate;

    public LocalDate getFoundDate() {
        return foundDate;
    }

    public void setFoundDate(LocalDate foundDate) {
        this.foundDate = foundDate;
    }

    List<FundBar> fundBarList = null;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getNetValue() {
        return netValue;
    }

    public void setNetValue(float netValue) {
        this.netValue = netValue;
    }

    public float getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(float totalValue) {
        this.totalValue = totalValue;
    }

    public float getGrToday() {
        return grToday;
    }

    public void setGrToday(float grToday) {
        this.grToday = grToday;
    }

    public float getGrl1Week() {
        return grl1Week;
    }

    public void setGrl1Week(float grl1Week) {
        this.grl1Week = grl1Week;
    }

    public float getGrl1Month() {
        return grl1Month;
    }

    public void setGrl1Month(float grl1Month) {
        this.grl1Month = grl1Month;
    }

    public float getGrl3Month() {
        return grl3Month;
    }

    public void setGrl3Month(float grl3Month) {
        this.grl3Month = grl3Month;
    }

    public float getGrl6Month() {
        return grl6Month;
    }

    public void setGrl6Month(float grl6Month) {
        this.grl6Month = grl6Month;
    }

    public float getGrl1Year() {
        return grl1Year;
    }

    public void setGrl1Year(float grl1Year) {
        this.grl1Year = grl1Year;
    }

    public String getManagers() {
        return managers;
    }

    public void setManagers(String managers) {
        this.managers = managers;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public LocalDate getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDate updateDate) {
        this.updateDate = updateDate;
    }

    public float getGrl2Year() {
        return grl2Year;
    }

    public void setGrl2Year(float grl2Year) {
        this.grl2Year = grl2Year;
    }

    public float getGrl3Year() {
        return grl3Year;
    }

    public void setGrl3Year(float grl3Year) {
        this.grl3Year = grl3Year;
    }

    public float getGrl5Year() {
        return grl5Year;
    }

    public void setGrl5Year(float grl5Year) {
        this.grl5Year = grl5Year;
    }

    public float getGrThisYear() {
        return grThisYear;
    }

    public void setGrThisYear(float grThisYear) {
        this.grThisYear = grThisYear;
    }

    public float getGrBase() {
        return grBase;
    }

    public void setGrBase(float grBase) {
        this.grBase = grBase;
    }

    public float getCagr() {
        return cagr;
    }

    public void setCagr(float cagr) {
        this.cagr = cagr;
    }

    public List<FundBar> getFundBarList() {
        return fundBarList;
    }

    public void setFundBarList(List<FundBar> fundBarList) {
        this.fundBarList = fundBarList;
    }
}