package com.qianyitian.hope2.analyzer.service;

import com.qianyitian.hope2.analyzer.config.PropertyConfig;
import com.qianyitian.hope2.analyzer.model.SymbolList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DefaultStockService extends AbstractStockService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    PropertyConfig propertyConfig;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public SymbolList getSymbols(String portfolio) {
        SymbolList symbolList = restTemplate.getForObject(propertyConfig.getStockService() + "/stockList", SymbolList.class);
        return symbolList;
    }
}
