package com.qianyitian.hope2.stock.dao;


import com.qianyitian.hope2.stock.model.Stock;
import com.qianyitian.hope2.stock.model.StockAdjFactor;

import java.util.Date;
import java.util.List;

public interface IStockDAO {
    void storeAllSymbols(List<Stock> list);

    void storeStock(Stock stock);

    void storeFunds(String name, String fundsInfo);

    String getFunds(String name);

    void storeStockSA(Stock stock);

    void storeStockLite(Stock stock);

    void storeStockLiteSA(Stock stock);

    void storeStockWeekly(Stock stock);

    void storeStockMonthly(Stock stock);

    void storeStockAdj(StockAdjFactor stockAdjFactor);

    StockAdjFactor getStockAdj(String code);

    Stock getStock(String code);

    Stock getStockLite(String code);

    Stock getStockWeekly(String code);

    Stock getStockMonthly(String code);

    List<Stock> getAllSymbols();

    List<Stock> getPortfolioSymbols(String portfolio);

    void storeStatistics(String filename, String content);

    String getStatistics(String filename);


    Stock getStockSA(String code);
    Stock getStockLiteSA(String code);

    Stock getStockWeeklySA(String code);

    Stock getStockMonthlySA(String code);
}
