package com.qianyitian.hope2.analyzer.controller;

import com.alibaba.fastjson.JSON;
import com.qianyitian.hope2.analyzer.analyzer.DemarkAnalyzer;
import com.qianyitian.hope2.analyzer.analyzer.EStockAnalyzer;
import com.qianyitian.hope2.analyzer.analyzer.IStockAnalyzer;
import com.qianyitian.hope2.analyzer.analyzer.StockAnalyzerFacotry;
import com.qianyitian.hope2.analyzer.config.Constant;
import com.qianyitian.hope2.analyzer.model.AnalyzeResult;
import com.qianyitian.hope2.analyzer.model.KLineInfo;
import com.qianyitian.hope2.analyzer.model.Stock;
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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@RestController
public class AnalysisController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DefaultStockService stockService;

    @Autowired
    private MyFavoriteStockService favoriteStockService;

//    @Autowired
//    private ReportDAO4Redis reportDAO4Redis;

    @Resource(name = "fileSystemStorageService")
    private IReportStorageService reportService;

    @Autowired
    StockAnalyzerFacotry stockAnalyzerFacotry;

    public AnalysisController() {
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public void status() {

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
        }
        {
            analyze(EStockAnalyzer.MACDAdvance, Constant.TYPE_WEEKLY);
            analyze(EStockAnalyzer.MACDAdvance, Constant.TYPE_MONTHLY);
        }
    }

    private void analyze(EStockAnalyzer macd, String kLineType) {
        analyze(macd, kLineType, kLineType);
    }

    private void analyze(EStockAnalyzer macd, String retreivalKLineType, String storageKlineType) {
        IStockAnalyzer analyzer = stockAnalyzerFacotry.getStockAnalyzer(macd);
        StockSelecter hs = new StockSelecter(stockService);
        hs.addAnalyzer(analyzer);
        hs.startAnalyze(retreivalKLineType);
        AnalyzeResult result = hs.getAnalyzeResult();
        storeAnalysisResult(result, macd, storageKlineType);
    }

    private void storeAnalysisResult(AnalyzeResult result, EStockAnalyzer enumAnalyzer, String type) {
        String filename = enumAnalyzer.name() + "-" + type;
        String content = JSON.toJSONString(result);
        reportService.storeAnalysis(filename, content);
        logger.info("aliyunOSS storage successful " + filename);

        String fullFilename = LocalDate.now().toString() + "-" + filename;
        reportService.storeAnalysis(fullFilename, content);
        logger.info("aliyunOSS storage successful " + fullFilename);

        //clear cache
//        reportDAO4Redis.clearReport(filename);
    }

    @RequestMapping(value = "/analysis/{analyzerName}/daily", method = RequestMethod.GET)
    public String analyze(@PathVariable String analyzerName) {
        String filename = analyzerName + "-daily";
        return lazyLoadReport(filename);
    }

    private String lazyLoadReport(String filename) {
//        String report = reportDAO4Redis.getReport(filename);
//        if (report == null) {
//            report = reportService.get(filename);
//            reportDAO4Redis.storeReport(filename, report);
//        }
        return reportService.getAnalysis(filename);
    }

    @RequestMapping(value = "/analysis/{analyzerName}/weekly", method = RequestMethod.GET)
    public String analyzeByWeekly(@PathVariable String analyzerName) {
        String filename = analyzerName + "-weekly";
        return lazyLoadReport(filename);
    }

    @RequestMapping(value = "/analysis/{analyzerName}/monthly", method = RequestMethod.GET)
    public String analyzeByMonthly(@PathVariable String analyzerName) {
        String filename = analyzerName + "-monthly";
        return lazyLoadReport(filename);
    }


    @RequestMapping("/demark")
    public String demark(@RequestParam(value = "days2Now", required = false) Integer days2Now) {
        DemarkAnalyzer stockAnalyzer = (DemarkAnalyzer) stockAnalyzerFacotry.getStockAnalyzer(EStockAnalyzer.Demark);
        if (days2Now != null) {
            stockAnalyzer.setDaysToNow(days2Now);
        }
        StockSelecter hs = new StockSelecter(favoriteStockService);
        hs.addAnalyzer(stockAnalyzer);
        hs.startAnalyze(Constant.TYPE_DAILY);
        AnalyzeResult result = hs.getAnalyzeResult();
        String content = JSON.toJSONString(result);
        return content;
    }

    @RequestMapping(value = "/statistics/increaseRangeStatistics", method = RequestMethod.GET)
    public String increaseRangeStatistics() {
        String filename = "increaseRangeStatistics";
        return reportService.getStatistics(filename);
    }


    @RequestMapping(value = "/data/kline/date/{code}", method = RequestMethod.GET)
    public String increaseRangeStatistics(@PathVariable String code) {
        Stock stockDaily = stockService.getStockDaily(code);
        List<KLineInfo> kLineInfos = stockDaily.getkLineInfos();

        List<Number[]> data = new LinkedList<>();
        for (KLineInfo kLineInfo : kLineInfos) {
            long dateMilliSeconds = Constant.ONE_DAY_MILLISECONDS * kLineInfo.getDate().toEpochDay();
            double close = kLineInfo.getClose();
            Number[] row = new Number[]{dateMilliSeconds, close};
            data.add(row);
        }
        String s = JSON.toJSONString(data);
        return s;
    }

}
