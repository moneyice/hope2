package com.qianyitian.hope2.analyzer.controller;


import com.alibaba.fastjson.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import com.qianyitian.hope2.analyzer.config.Constant;
import com.qianyitian.hope2.analyzer.model.KLineInfo;
import com.qianyitian.hope2.analyzer.model.Stock;
import com.qianyitian.hope2.analyzer.model.SymbolList;
import com.qianyitian.hope2.analyzer.service.DefaultStockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.qianyitian.hope2.analyzer.util.Utils.get2Double;

@RestController
public class HongYunGaoZhaoController {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    DefaultStockService stockService;

    @Autowired
    private Cache<String, String> cache;

    public HongYunGaoZhaoController() {
    }

    @GetMapping("/report/hongyun")
    @CrossOrigin
    public String hongyun() {
       return cache.get("hongyun", key -> {
           SymbolList symbols = stockService.getSymbols(null);
           List<HoneYunResult> result = new LinkedList<>();
           for (Stock stock : symbols.getSymbols()) {
               Stock stockDetail = stockService.getStock(stock.getCode(), Constant.TYPE_DAILY_LITE);
               analyze(stockDetail, result);
           }
           Map map = new HashMap();
           map.put("items", result);
           map.put("generateTime", LocalDateTime.now().toString());
           return JSON.toJSONString(map);
        });
    }

    int recentDays = 10;

    private void analyze(Stock stock, List collectList) {
        if (stock.getName().contains("ST") || stock.getName().contains("st")||stock.getName().contains("退")) {
            return;
        }
        List<KLineInfo> kLineInfos = stock.getkLineInfos();
        for (int i = kLineInfos.size() - recentDays; i < kLineInfos.size(); i++) {
            KLineInfo current = kLineInfos.get(i);
            KLineInfo previous = kLineInfos.get(i - 1);

            if (current.getTurnoverRate() >= previous.getTurnoverRate() * 2 && current.getClose() / previous.getClose() - 1 > 0.05) {
                HoneYunResult result = new HoneYunResult();
                result.setCode(stock.getCode());
                result.setName(stock.getName());
                result.setDate(current.getDate());
                result.setVolumeRise(get2Double(current.getTurnoverRate() * 100 / previous.getTurnoverRate()));
                result.setPriceRise(get2Double((current.getClose() / previous.getClose() - 1) * 100));
                collectList.add(result);
                break;
            }
        }
    }
}

class HoneYunResult {
    String code;
    String name;
    LocalDate date;
    double priceRise;
    double volumeRise;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getPriceRise() {
        return priceRise;
    }

    public void setPriceRise(double priceRise) {
        this.priceRise = priceRise;
    }

    public double getVolumeRise() {
        return volumeRise;
    }

    public void setVolumeRise(double volumeRise) {
        this.volumeRise = volumeRise;
    }
}
