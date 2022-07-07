package com.qianyitian.hope2.analyzer.service;

import com.qianyitian.hope2.analyzer.funds.model.Fund;
import com.qianyitian.hope2.analyzer.model.Stock;
import com.qianyitian.hope2.analyzer.model.SymbolList;

public interface IStockService {
    public SymbolList getSymbols(String portfolio);

    public Stock getStock(String symbol, String klineType);

    public Stock getStockDaily(String symbol);

    Stock getStockDailySA(String symbol);

    public Stock getStockWeekly(String symbol);
    public Stock getStockMonthly(String symbol);

    public String getFundProfile(String code);

    public String getFundDetail(String code);

    public String getFundHistory(String code);

}
