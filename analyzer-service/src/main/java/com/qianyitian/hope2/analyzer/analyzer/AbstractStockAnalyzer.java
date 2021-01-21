package com.qianyitian.hope2.analyzer.analyzer;

import com.qianyitian.hope2.analyzer.model.Stock;

public abstract class AbstractStockAnalyzer implements IStockAnalyzer {

    private Stock stock;


    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }


    public double getCurrentPrice(Stock stock) {
        return stock.getkLineInfos().get(stock.getkLineInfos().size() - 1)
                .getClose();
    }
}
