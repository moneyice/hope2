package com.qianyitian.hope2.analyzer.service;

import com.alibaba.fastjson.JSON;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class MyFavoriteStockService extends AbstractStockService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    PropertyConfig propertyConfig;

    @Autowired
    private RestTemplate restTemplate;

    CacheLoader<String, Stock> loader = symbol -> {
        Stock stock = MyFavoriteStockService.super.getStockDaily(symbol);
        logger.error("get stock  from system "+ symbol);
        return stock;
    };

    AsyncLoadingCache<String, Stock> cache = Caffeine.newBuilder()
            .maximumSize(256).recordStats()
            .expireAfterAccess(60, TimeUnit.MINUTES)
            .expireAfterWrite(60, TimeUnit.MINUTES)
            .refreshAfterWrite(30, TimeUnit.MINUTES)
            .buildAsync(loader);

    @Override
    public Stock getStockDaily(String symbol) {
        Stock stock = null;
        try {
            CompletableFuture<Stock> stockCompletableFuture = cache.get(symbol);
            stock = stockCompletableFuture.get();
        } catch (Exception e) {
            logger.error("stock not exist " + symbol, e);
        }
        return stock;
    }




    @Override
    public SymbolList getSymbols(String portfolio) {
        SymbolList symbolList = restTemplate.getForObject(propertyConfig.getStockService() + "/stockList/" + portfolio, SymbolList.class);
        return symbolList;
    }

    public String getCacheStatus() {
        CacheStats stats = cache.synchronous().stats();
        Map map = new HashMap();
        map.put("hitCount", stats.hitCount());
        map.put("missCount", stats.missCount());
        map.put("loadSuccessCount", stats.loadSuccessCount());
        map.put("loadFailureCount", stats.loadFailureCount());
        map.put("totalLoadTime", stats.totalLoadTime());
        map.put("evictionCount", stats.evictionCount());
        map.put("evictionWeight", stats.evictionWeight());
        map.put("hitRate", stats.hitRate());
        map.put("missRate", stats.missRate());
        map.put("loadFailureRate", stats.loadFailureRate());
        return JSON.toJSONString(map);
    }


}
