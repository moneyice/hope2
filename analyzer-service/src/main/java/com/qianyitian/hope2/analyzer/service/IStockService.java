package com.qianyitian.hope2.analyzer.service;

import com.alibaba.fastjson.JSON;
import com.qianyitian.hope2.analyzer.model.Stock;
import com.qianyitian.hope2.analyzer.model.SymbolList;

public interface IStockService {
    public SymbolList getAllSymbols();

    public Stock getStock(String symbol, String klineType);

    public Stock getStockDaily(String symbol);

    public Stock getStockWeekly(String symbol);
}
