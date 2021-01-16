package com.qianyitian.hope2.analyzer.service;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.qianyitian.hope2.analyzer.config.PropertyConfig;
import com.qianyitian.hope2.analyzer.model.Stock;
import com.qianyitian.hope2.analyzer.model.SymbolList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class DefaultStockService extends AbstractStockService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    PropertyConfig propertyConfig;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public SymbolList getAllSymbols() {
        SymbolList symbolList = restTemplate.getForObject(propertyConfig.getStockService() + "/stockList", SymbolList.class);
        return symbolList;
    }
}
