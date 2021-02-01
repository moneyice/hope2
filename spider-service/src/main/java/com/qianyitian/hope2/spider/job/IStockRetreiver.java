package com.qianyitian.hope2.spider.job;


import com.qianyitian.hope2.spider.model.Stock;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

public interface IStockRetreiver {
    List<Stock> getAllStockSymbols() throws IOException;

    List<Stock> getUSStockSymbols() throws IOException;

    List<Stock> getHKStockSymbols() throws IOException;

    List<Stock> getFundsSymbols() throws IOException;

    Stock getStockInfo(Stock stock) throws IOException;

    public String getFundsInfo(String symbol) throws IOException;
}