package com.qianyitian.hope2.analyzer.model;

import java.math.BigDecimal;
import java.util.List;

public class DemarkInfo {
    private String code;
    private List<DemarkFlag> flag;
    private String name;
    private List<BigDecimal[]> bars;
    private List<DemarkFlag> flagSell;

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<DemarkFlag> getFlag() {
        return flag;
    }

    public void setFlag(List<DemarkFlag> flag) {
        this.flag = flag;
    }

    public List<BigDecimal[]> getBars() {
        return bars;
    }

    public void setBars(List<BigDecimal[]> bars) {
        this.bars = bars;
    }

    public List<DemarkFlag> getFlagSell() {
        return flagSell;
    }

    public void setFlagSell(List<DemarkFlag> flagSell) {
        this.flagSell = flagSell;
    }
}
