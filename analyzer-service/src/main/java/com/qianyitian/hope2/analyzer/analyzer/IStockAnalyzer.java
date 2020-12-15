package com.qianyitian.hope2.analyzer.analyzer;


import com.qianyitian.hope2.analyzer.model.ResultInfo;
import com.qianyitian.hope2.analyzer.model.Stock;

public interface IStockAnalyzer {


    String getDescription();


    void outPutResults();

    boolean analyze(ResultInfo resultInfo, Stock stock);
}
