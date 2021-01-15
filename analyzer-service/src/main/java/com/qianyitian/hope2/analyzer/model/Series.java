package com.qianyitian.hope2.analyzer.model;

import java.util.List;

public class Series {
    String code;
    String name;

    public List<Number[]> getBars() {
        return bars;
    }

    public void setBars(List<Number[]> bars) {
        this.bars = bars;
    }

    List<Number[]> bars;

    public static Series build(Stock stock, List<Number[]> collect) {
        Series s = new Series();
        s.setCode(stock.getCode());
        s.setName(stock.getName());
        s.setBars(collect);
        return s;
    }

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


}
