package com.qianyitian.hope2.analyzer.controller;

import com.alibaba.fastjson.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.qianyitian.hope2.analyzer.analyzer.DemarkAnalyzer;
import com.qianyitian.hope2.analyzer.analyzer.StockAnalyzerFacotry;
import com.qianyitian.hope2.analyzer.analyzer.UnDemarkAnalyzer;
import com.qianyitian.hope2.analyzer.config.Constant;
import com.qianyitian.hope2.analyzer.model.AnalyzeResult;
import com.qianyitian.hope2.analyzer.model.ResultInfo;
import com.qianyitian.hope2.analyzer.model.Stock;
import com.qianyitian.hope2.analyzer.model.SymbolList;
import com.qianyitian.hope2.analyzer.service.MyFavoriteStockService;
import com.qianyitian.hope2.analyzer.service.StockSelecter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class DemarkService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private MyFavoriteStockService favoriteStockService;

    @Autowired
    StockAnalyzerFacotry stockAnalyzerFacotry;

    @Autowired
    private Cache<String, String> cache;


    public String getStockCacheStatus() {
        return favoriteStockService.getCacheStatus();
    }

    public String demark(String portfolio, Integer days2Now) {
        String report = null;
        final int days2NowNumber = days2Now == null ? DemarkAnalyzer.DEFAULT_DAYS2NOW : days2Now;
        try {
            report = cache.get("demark" + portfolio + days2NowNumber, key -> {
                return portfolioReportJson(portfolio, days2NowNumber);
            });
        } catch (NullPointerException e) {
            report = e.toString();
            logger.error("demark-" + portfolio, e);
        }
        return report;
    }

    //生成组合的报告
    protected String portfolioReportJson(String portfolio, Integer days2Now) {
        AnalyzeResult result = portfolioReport(portfolio, days2Now);
        String content = JSON.toJSONString(result);
        return content;
    }

    //生成组合的报告
    public AnalyzeResult portfolioReport(String portfolio, Integer days2Now) {
//        DemarkAnalyzer stockAnalyzer = (DemarkAnalyzer) stockAnalyzerFacotry.getStockAnalyzer(EStockAnalyzer.Demark);
        DemarkAnalyzer stockAnalyzer = new DemarkAnalyzer();
        if (days2Now != null) {
            stockAnalyzer.setDaysToNow(days2Now);
        }
        SymbolList symbols = favoriteStockService.getSymbols(portfolio);
        StockSelecter hs = new StockSelecter(symbols.getSymbols(), favoriteStockService);
        hs.setFilterEmptyResult(false);
        hs.addAnalyzer(stockAnalyzer);
        hs.startAnalyze(Constant.TYPE_DAILY);
        AnalyzeResult result = hs.getAnalyzeResult();
        logger.info("get portfolioReport " + portfolio + " from system");
        return result;
    }

    private Stock getStock(String code) {
        Stock stock = null;
        stock = favoriteStockService.getStockDaily(code);
        return stock;
    }

    public String demarkBacktrack(String code, Integer days2Now) {
        String seriesData = null;
        final int days2NowNumber = days2Now == null ? DemarkAnalyzer.DEFAULT_DAYS2NOW + 100 : days2Now;
        try {
            seriesData = cache.get("demark" + code + days2NowNumber, key -> {
                return stockReport(code, days2NowNumber);
            });
        } catch (Exception e) {
            seriesData = e.toString();
            logger.error("demark error " + code, e);
        }
        return seriesData;
    }

    //生成单个证券的报告
    protected String stockReport(String code, int days2NowNumber) {
//        DemarkAnalyzer stockAnalyzer = (DemarkAnalyzer) stockAnalyzerFacotry.getStockAnalyzer(EStockAnalyzer.Demark);
        DemarkAnalyzer stockAnalyzer = new DemarkAnalyzer();
        stockAnalyzer.setDaysToNow(days2NowNumber);

        Stock stock = getStock(code);
        if (stock == null) {
            return "stock not exists " + code;
        }
        StockSelecter hs = new StockSelecter(null, null);
        ResultInfo resultInfo = hs.analyze(stockAnalyzer, stock);

        Map<String, Object> map = new HashMap<>();
        map.put("code", stock.getCode());
        map.put("name", stock.getName());

        //buy flags
        {
            if (resultInfo != null) {
                map.put("flag", resultInfo.getData().get("flag"));
            }
        }

        List<Number[]> bars = convert2ChartFormat(stock);
        map.put("bars", bars);

        //sell flags
        {
//            UnDemarkAnalyzer unStockAnalyzer = (UnDemarkAnalyzer) stockAnalyzerFacotry.getStockAnalyzer(EStockAnalyzer.UnDemark);
            UnDemarkAnalyzer unStockAnalyzer = new UnDemarkAnalyzer();
            unStockAnalyzer.setDaysToNow(days2NowNumber);
            ResultInfo unResultInfo = hs.analyze(unStockAnalyzer, stock);
            if (unResultInfo != null) {
                map.put("flagSell", unResultInfo.getData().get("flag"));
            }
        }
        String content = JSON.toJSONString(map);
        logger.error("get stockReport " + code + " from system");
        return content;
    }

    protected List<Number[]> convert2ChartFormat(Stock stock) {
        List<Number[]> collect = stock.getkLineInfos().stream().map(bar -> {
            long dateMilliSeconds = Constant.ONE_DAY_MILLISECONDS * bar.getDate().toEpochDay();
            Number[] row = new Number[]{dateMilliSeconds, bar.getOpen(), bar.getHigh(), bar.getLow(), bar.getClose()};
            return row;
        }).collect(Collectors.toList());
        return collect;
    }
}
