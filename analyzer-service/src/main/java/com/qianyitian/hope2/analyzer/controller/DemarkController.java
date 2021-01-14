package com.qianyitian.hope2.analyzer.controller;

import com.alibaba.fastjson.JSON;
import com.qianyitian.hope2.analyzer.analyzer.*;
import com.qianyitian.hope2.analyzer.config.Constant;
import com.qianyitian.hope2.analyzer.model.AnalyzeResult;
import com.qianyitian.hope2.analyzer.model.KLineInfo;
import com.qianyitian.hope2.analyzer.model.ResultInfo;
import com.qianyitian.hope2.analyzer.model.Stock;
import com.qianyitian.hope2.analyzer.service.DefaultStockService;
import com.qianyitian.hope2.analyzer.service.IReportStorageService;
import com.qianyitian.hope2.analyzer.service.MyFavoriteStockService;
import com.qianyitian.hope2.analyzer.service.StockSelecter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
public class DemarkController {
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

    public DemarkController() {
    }

    @RequestMapping("/demark")
    @CrossOrigin
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

    @CrossOrigin
    @RequestMapping("/demark-backtrack/{code}")
    public String demarkBacktrack(@PathVariable String code, @RequestParam(value = "days2Now", required = false) Integer days2Now) {
        DemarkAnalyzer stockAnalyzer = (DemarkAnalyzer) stockAnalyzerFacotry.getStockAnalyzer(EStockAnalyzer.Demark);
        if (days2Now != null) {
            stockAnalyzer.setDaysToNow(days2Now);
        } else {
            stockAnalyzer.setDaysToNow(300);
        }
        Stock stock = stockService.getStockDaily(code);
        if (stock == null) {
            return "stock not exists " + code;
        }
        StockSelecter hs = new StockSelecter(null);
        ResultInfo resultInfo = hs.analyze(stockAnalyzer, stock);

        Map<String, Object> map = new HashMap<>();
        map.put("code", stock.getCode());
        map.put("name", stock.getName());

        {
            if(resultInfo!=null){
                map.put("flag", resultInfo.getData().get("flag"));
            }
        }

        {
            UnDemarkAnalyzer unStockAnalyzer = (UnDemarkAnalyzer) stockAnalyzerFacotry.getStockAnalyzer(EStockAnalyzer.UnDemark);
            ResultInfo unResultInfo = hs.analyze(unStockAnalyzer, stock);
            map.put("flagSell", unResultInfo.getData().get("flag"));

        }


        String content = JSON.toJSONString(map);
        return content;
    }
}
