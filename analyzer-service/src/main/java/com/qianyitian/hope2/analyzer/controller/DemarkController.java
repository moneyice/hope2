package com.qianyitian.hope2.analyzer.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.base.MoreObjects;
import com.google.common.cache.*;
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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
public class DemarkController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MyFavoriteStockService favoriteStockService;

    @Autowired
    StockAnalyzerFacotry stockAnalyzerFacotry;

    private Cache<String, String> cache = CacheBuilder.newBuilder()
            .maximumSize(1024).recordStats().expireAfterAccess(30, TimeUnit.MINUTES).expireAfterWrite(30, TimeUnit.MINUTES)
            .build();


    @GetMapping(value = "/cache/stock/status")
    public String cacheStatus() {
        return favoriteStockService.getCacheStatus();
    }

    public DemarkController() {
    }

    @RequestMapping("/demark")
    @CrossOrigin
    public String demark(@RequestParam(value = "days2Now", required = false) Integer days2Now) {
        String report = null;
        try {
            Callable<String> callable = () -> {
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
            };

            report = cache.get("demark-Hope150", callable);
        } catch (ExecutionException e) {
            report = e.toString();
            logger.error("demark-Hope150 error", e);
        }
        return report;
    }


    private Stock getStock(String code) {
        Stock stock = null;
        stock = favoriteStockService.getStockDaily(code);
        return stock;
    }

    @CrossOrigin
    @RequestMapping("/demark-backtrack/{code}")
    public String demarkBacktrack(@PathVariable String code, @RequestParam(value = "days2Now", required = false) Integer days2Now) {
        String seriesData = null;
        final int days2NowNumber = days2Now == null ? 300 : days2Now;
        try {
            Callable<String> callable = () -> {
                DemarkAnalyzer stockAnalyzer = (DemarkAnalyzer) stockAnalyzerFacotry.getStockAnalyzer(EStockAnalyzer.Demark);
                stockAnalyzer.setDaysToNow(days2NowNumber);

                Stock stock = getStock(code);
                if (stock == null) {
                    return "stock not exists " + code;
                }

                StockSelecter hs = new StockSelecter(null);
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
                    UnDemarkAnalyzer unStockAnalyzer = (UnDemarkAnalyzer) stockAnalyzerFacotry.getStockAnalyzer(EStockAnalyzer.UnDemark);
                    unStockAnalyzer.setDaysToNow(days2NowNumber);
                    ResultInfo unResultInfo = hs.analyze(unStockAnalyzer, stock);
                    map.put("flagSell", unResultInfo.getData().get("flag"));

                }
                String content = JSON.toJSONString(map);
                return content;
            };
            seriesData = cache.get("demark" + code + days2NowNumber, callable);
        } catch (ExecutionException e) {
            seriesData = e.toString();
            logger.error("demark error " + code, e);
        }
        return seriesData;
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
