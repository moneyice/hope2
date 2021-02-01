package com.qianyitian.hope2.stock.dao;


import com.qianyitian.hope2.stock.model.Stock;

import java.util.Date;
import java.util.List;

public interface IStockDAO {
    void storeAllSymbols(List<Stock> list);

    Date getStockUpdateTime(String code);

    void storeStock(Stock stock);

    void storeFunds(String name, String fundsInfo);
    String getFunds(String name);
    void storeStockLite(Stock stock);

    void storeStockWeekly(Stock stock);

    void storeStockMonthly(Stock stock);

    Stock getStock(String code);

    Stock getStockLite(String code);

    Stock getStockWeekly(String code);

    Stock getStockMonthly(String code);

    List<Stock> getAllSymbols();

    List<Stock> getPortfolioSymbols(String portfolio);

    Date getAllSymbolsUpdateTime();

    void storeStatistics(String filename, String content);

    String getStatistics(String filename);


}
