package com.qianyitian.hope2.analyzer.controller;

import com.alibaba.fastjson.JSON;
import com.qianyitian.hope2.analyzer.analyzer.EStockAnalyzer;
import com.qianyitian.hope2.analyzer.analyzer.IStockAnalyzer;
import com.qianyitian.hope2.analyzer.analyzer.StockAnalyzerFacotry;
import com.qianyitian.hope2.analyzer.config.Constant;
import com.qianyitian.hope2.analyzer.model.*;
import com.qianyitian.hope2.analyzer.service.IReportStorageService;
import com.qianyitian.hope2.analyzer.service.MyFavoriteStockService;
import com.qianyitian.hope2.analyzer.service.StockSelecter;
import com.qianyitian.hope2.analyzer.service.DefaultStockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
public class AnalysisController {
    private Logger logger = LoggerFactory.getLogger(getClass());


    @Autowired
    private DefaultStockService stockService;

    @Resource(name = "redisStorageService")
    private IReportStorageService reportService;

    @Autowired
    StockAnalyzerFacotry stockAnalyzerFacotry;

    public AnalysisController() {
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public String status() {
        return LocalDateTime.now().toString();
    }

    @Async
    @RequestMapping(value = "/startAnalyze", method = RequestMethod.GET)
    public void startAnalyzeInBackgroud() {
        for (EStockAnalyzer enumAnalyzer : EStockAnalyzer.values()) {
            analyze(enumAnalyzer, Constant.TYPE_DAILY_LITE, Constant.TYPE_DAILY);
        }
        {
            analyze(EStockAnalyzer.MACD, Constant.TYPE_WEEKLY);
            analyze(EStockAnalyzer.MACD, Constant.TYPE_MONTHLY);
            analyze(EStockAnalyzer.Demark, Constant.TYPE_WEEKLY);
            analyze(EStockAnalyzer.Demark, Constant.TYPE_MONTHLY);
        }
        {
            analyze(EStockAnalyzer.MACDAdvance, Constant.TYPE_WEEKLY);
            analyze(EStockAnalyzer.MACDAdvance, Constant.TYPE_MONTHLY);
        }
    }

    @Async
    @RequestMapping(value = "/startDemarkAnalyze", method = RequestMethod.GET)
    public void startDemarkAnalyze() {
        analyze(EStockAnalyzer.Demark, Constant.TYPE_DAILY_LITE, Constant.TYPE_DAILY);
        analyze(EStockAnalyzer.Demark, Constant.TYPE_WEEKLY, Constant.TYPE_WEEKLY);
        analyze(EStockAnalyzer.Demark, Constant.TYPE_MONTHLY, Constant.TYPE_MONTHLY);
    }

    private void analyze(EStockAnalyzer macd, String kLineType) {
        analyze(macd, kLineType, kLineType);
    }

    private void analyze(EStockAnalyzer macd, String retreivalKLineType, String storageKlineType) {
        IStockAnalyzer analyzer = stockAnalyzerFacotry.getStockAnalyzer(macd);
        SymbolList symbols = stockService.getSymbols(null);
        StockSelecter hs = new StockSelecter(symbols.getSymbols(), stockService);
        hs.addAnalyzer(analyzer);
        hs.startAnalyze(retreivalKLineType);
        AnalyzeResult result = hs.getAnalyzeResult();
        storeAnalysisResult(result, macd, storageKlineType);
    }

    private void storeAnalysisResult(AnalyzeResult result, EStockAnalyzer enumAnalyzer, String type) {
        String filename = enumAnalyzer.name() + "-" + type;
        String content = JSON.toJSONString(result);
        reportService.storeAnalysis(filename, content);
        logger.info("Store report successful " + filename + " result size is " + result.getResultList().size());
    }

    @RequestMapping(value = "/analysis/{analyzerName}/daily", method = RequestMethod.GET)
    @CrossOrigin
    public String analyze(@PathVariable String analyzerName) {
        String filename = analyzerName + "-daily";
        return lazyLoadReport(filename);
    }

    private String lazyLoadReport(String filename) {
        return reportService.getAnalysis(filename);
    }

    @RequestMapping(value = "/analysis/{analyzerName}/weekly", method = RequestMethod.GET)
    @CrossOrigin
    public String analyzeByWeekly(@PathVariable String analyzerName) {
        String filename = analyzerName + "-weekly";
        return lazyLoadReport(filename);
    }

    @RequestMapping(value = "/analysis/{analyzerName}/monthly", method = RequestMethod.GET)
    @CrossOrigin
    public String analyzeByMonthly(@PathVariable String analyzerName) {
        String filename = analyzerName + "-monthly";
        return lazyLoadReport(filename);
    }

    @RequestMapping(value = "/statistics/increaseRangeStatistics", method = RequestMethod.GET)
    public String increaseRangeStatistics() {
        String filename = "increaseRangeStatistics";
        return reportService.getStatistics(filename);
    }
}
