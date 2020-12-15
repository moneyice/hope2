package com.qianyitian.hope2.analyzer.controller;

import com.alibaba.fastjson.JSON;
import com.qianyitian.hope2.analyzer.analyzer.EStockAnalyzer;
import com.qianyitian.hope2.analyzer.analyzer.IStockAnalyzer;
import com.qianyitian.hope2.analyzer.analyzer.StockAnalyzerFacotry;
import com.qianyitian.hope2.analyzer.dao.ReportDAO4Redis;
import com.qianyitian.hope2.analyzer.model.AnalyzeResult;
import com.qianyitian.hope2.analyzer.service.IReportService;
import com.qianyitian.hope2.analyzer.service.StockSelectService;
import com.qianyitian.hope2.analyzer.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.time.LocalDate;

@RestController
public class AnalysisController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private StockService stockService;

    @Autowired
    private ReportDAO4Redis reportDAO4Redis;

    @Resource(name = "fileSystemStorageService")
    private IReportService reportService;


    public AnalysisController() {
    }

    @Async
    @RequestMapping(value = "/startAnalyze", method = RequestMethod.GET)
    public void startAnalyzeInBackgroud() {
        for (EStockAnalyzer enumAnalyzer : EStockAnalyzer.values()) {
            analyze(enumAnalyzer, "dailylite", "daily");
        }
        {
            analyze(EStockAnalyzer.MACD, "weekly");
            analyze(EStockAnalyzer.MACD, "monthly");
        }
        {
            analyze(EStockAnalyzer.MACDAdvance, "weekly");
            analyze(EStockAnalyzer.MACDAdvance, "monthly");
        }
    }

    private void analyze(EStockAnalyzer macd, String kLineType) {
        analyze(macd, kLineType, kLineType);
    }

    private void analyze(EStockAnalyzer macd, String retreivalKLineType, String storageKlineType) {
        IStockAnalyzer analyzer = StockAnalyzerFacotry.createStockAnalyzer(macd);
        StockSelectService hs = new StockSelectService(stockService);
        hs.addAnalyzer(analyzer);
        hs.startAnalyze(retreivalKLineType);
        AnalyzeResult result = hs.getAnalyzeResult();
        storeAnalysisResult(result, macd, storageKlineType);
    }

    private void storeAnalysisResult(AnalyzeResult result, EStockAnalyzer enumAnalyzer, String type) {
        String filename = enumAnalyzer.name() + "-" + type;
        String content = JSON.toJSONString(result);
        reportService.put(filename, content);
        logger.info("aliyunOSS storage successful " + filename);

        String fullFilename = LocalDate.now().toString() + "-" + filename;
        reportService.put(fullFilename, content);
        logger.info("aliyunOSS storage successful " + fullFilename);

        //clear cache
        reportDAO4Redis.clearReport(filename);
    }

    @RequestMapping(value = "/analysis/{analyzerName}/daily", method = RequestMethod.GET)
    public String analyze(@PathVariable String analyzerName) {
        String filename = analyzerName + "-daily";
        return lazyLoadReport(filename);
    }

    private String lazyLoadReport(String filename) {
        String report = reportDAO4Redis.getReport(filename);
        if (report == null) {
            report = reportService.get(filename);
            reportDAO4Redis.storeReport(filename, report);
        }
        return report;
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

}
