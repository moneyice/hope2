package com.qianyitian.hope2.stock.model;

import java.util.List;

public class StockAdjFactor {
    String code;
    String name;
    List<AdjFactorInfo> adjList;

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

    public List<AdjFactorInfo> getAdjList() {
        return adjList;
    }

    public void setAdjList(List<AdjFactorInfo> adjList) {
        this.adjList = adjList;
    }
}
