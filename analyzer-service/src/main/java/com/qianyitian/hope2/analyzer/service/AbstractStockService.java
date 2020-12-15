package com.qianyitian.hope2.analyzer.service;

import com.alibaba.fastjson.JSON;
import com.qianyitian.hope2.analyzer.config.PropertyConfig;
import com.qianyitian.hope2.analyzer.model.Stock;
import com.qianyitian.hope2.analyzer.model.SymbolList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public abstract class AbstractStockService implements IStockService{
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    PropertyConfig propertyConfig;



    @Autowired
    private RestTemplate restTemplate;

    @Override
    public abstract SymbolList getAllSymbols();

    @Override
    public Stock getStock(String symbol, String klineType) {
        //"timerCache is above the warning threshold of 1000 with size XXX"
        //https://my.oschina.net/u/2408085/blog/733900
//        这个告警主要是说创建的timer已经超过默认阈值1000了，可以通过增大配置netflix.metrics.servo.cacheWarningThreshold来解决
        // /data/kline/daily/{code}
        //-Djava.net.preferIPv4Stack=true
        Stock input = new Stock();
        input.setCode(symbol);
        input.setkLineType(klineType);

        try {
            Stock stock = restTemplate.postForObject(propertyConfig.getStockService() + "/data/kline/", input, Stock.class);
            if (stock == null || stock.getkLineInfos() == null) {
                logger.warn(symbol + " has no kline data");
                return null;
            }
            return stock;
        } catch (Exception e) {
            logger.error(JSON.toJSONString(input));
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Stock getStockDaily(String symbol) {
        try {
            Stock stock = restTemplate.getForObject(propertyConfig.getStockService() + "/data/kline/daily/" + symbol, Stock.class);
            return stock;
        } catch (Exception e) {
            logger.error(symbol);
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Stock getStockWeekly(String symbol) {
        try {
            Stock stock = restTemplate.getForObject(propertyConfig.getStockService() + "/data/kline/weekly/" + symbol, Stock.class);
            return stock;
        } catch (Exception e) {
            logger.error(symbol);
            logger.error(e.getMessage(), e);
        }
        return null;
    }
    public PropertyConfig getPropertyConfig() {
        return propertyConfig;
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

}
