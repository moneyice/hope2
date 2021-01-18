package com.qianyitian.hope2.spider.job;


import com.google.common.io.Files;
import com.qianyitian.hope2.spider.config.PropertyConfig;
import com.qianyitian.hope2.spider.model.Stock;
import com.qianyitian.hope2.spider.model.SymbolList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public abstract class WebStockRetreiver implements IStockRetreiver {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    PropertyConfig propertyConfig;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<Stock> getAllStockSymbols() throws IOException {
        SymbolList symbolList = restTemplate.getForObject(propertyConfig.getStockService() + "/stockList", SymbolList.class);
        return symbolList.getSymbols();
    }

    @Override
    public List<Stock> getUSStockSymbols() throws IOException {
        SymbolList symbolList = restTemplate.getForObject(propertyConfig.getStockService() + "/stockList/us", SymbolList.class);
        return symbolList.getSymbols();
    }
    @Override
    public List<Stock> getHKStockSymbols() throws IOException {
        SymbolList symbolList = restTemplate.getForObject(propertyConfig.getStockService() + "/stockList/hk", SymbolList.class);
        return symbolList.getSymbols();
    }
}
