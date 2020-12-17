package com.qianyitian.hope2.stock.dao;



import com.qianyitian.hope2.stock.model.Stock;

import java.util.Date;
import java.util.List;

public interface IStockDAO {
    void storeAllSymbols(List<Stock> list);

    Date getStockUpdateTime(String code);

    void storeStock(Stock stock);
    void storeStockLite(Stock stock);
    void storeStockWeeklyInfo(Stock stock);

    void storeStockMonthlyInfo(Stock stock);

    Stock getStock(String code);
    Stock getStockLite(String code);

    Stock getStockWeeklyInfo(String code);

    Stock getStockMonthlyInfo(String code);

    List<Stock> getAllSymbols();

    List<Stock> getFavoriteSymbols();

    Date getAllSymbolsUpdateTime();



}
