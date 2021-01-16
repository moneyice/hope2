package com.qianyitian.hope2.analyzer.service;

import com.alibaba.fastjson.JSON;
import com.google.common.base.MoreObjects;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import com.qianyitian.hope2.analyzer.config.PropertyConfig;
import com.qianyitian.hope2.analyzer.model.Stock;
import com.qianyitian.hope2.analyzer.model.SymbolList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class MyFavoriteStockService extends AbstractStockService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    PropertyConfig propertyConfig;

    @Autowired
    private RestTemplate restTemplate;

    String portfolio;

    CacheLoader<String, Stock> loader = new CacheLoader<String, Stock>() {
        @Override
        public Stock load(String symbol) throws Exception {
            Stock stock = MyFavoriteStockService.super.getStockDaily(symbol);
            return stock;
        }
    };

    LoadingCache<String, Stock> cache = CacheBuilder.newBuilder()
            .maximumSize(1024).recordStats().expireAfterAccess(30, TimeUnit.MINUTES).expireAfterWrite(30, TimeUnit.MINUTES).refreshAfterWrite(20, TimeUnit.MINUTES)
            .build(loader);

    @Override
    public Stock getStockDaily(String symbol) {
        Stock stock = null;
        try {
            stock = cache.get(symbol);
        } catch (ExecutionException e) {
            logger.error("stock not exist " + symbol, e);
        }
        return stock;
    }


    public String getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(String portfolio) {
        this.portfolio = portfolio;
    }

    @Override
    public SymbolList getAllSymbols() {
        SymbolList symbolList = restTemplate.getForObject(propertyConfig.getStockService() + "/stockList/" + portfolio, SymbolList.class);
        return symbolList;
    }

    public String getCacheStatus() {
        CacheStats stats = cache.stats();
        Map map = new HashMap();
        map.put("hitCount", stats.hitCount());
        map.put("missCount", stats.missCount());
        map.put("loadSuccessCount", stats.loadSuccessCount());
        map.put("loadExceptionCount", stats.loadExceptionCount());
        map.put("hitCtotalLoadTimeount", stats.totalLoadTime());
        map.put("evictionCount", stats.evictionCount());
        map.put("hitRate", stats.hitRate());
        map.put("missRate", stats.missRate());
        map.put("loadExceptionRate", stats.loadExceptionRate());
        return JSON.toJSONString(map);
    }


}
