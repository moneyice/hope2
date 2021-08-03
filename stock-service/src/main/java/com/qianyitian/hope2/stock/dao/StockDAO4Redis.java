package com.qianyitian.hope2.stock.dao;

import com.alibaba.fastjson.JSON;
import com.qianyitian.hope2.stock.config.Constant;
import com.qianyitian.hope2.stock.config.EStockKlineType;
import com.qianyitian.hope2.stock.model.Stock;
import com.qianyitian.hope2.stock.model.StockAdjFactor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Repository("stockDAO4Redis")
//@RefreshScope
public class StockDAO4Redis extends AbstractStockDAO {

    @Resource(name = "stockDAO4FileSystem")
    StockDAO4FileSystem fileSystemDao;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public StockDAO4Redis() {
    }


    @Override
    public void storeFunds(String name, String fundsInfo) {
        String key = Constant.FUNDS + name;
        redisTemplate.opsForValue().set(key, fundsInfo);
    }

    @Override
    public String getFunds(String name) {
        String key = Constant.FUNDS + name;
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    protected void storeStockInfo(Stock stock, EStockKlineType type) {
        String jsonStock = JSON.toJSONString(stock);
        String key = stock.getCode() + type.getFileSuffix();
        redisTemplate.opsForValue().set(key, jsonStock);
    }

    @Override
    public Stock getStockInfo(String code, EStockKlineType type) {
        String key = code + type.getFileSuffix();
        String result = redisTemplate.opsForValue().get(key);
        Stock stock = JSON.parseObject(result, Stock.class);
        return stock;
    }

    @Override
    public void storeStockAdj(StockAdjFactor stockAdjFactor) {
        String key = stockAdjFactor.getCode() + "." + Constant.ADJFactor;
        String jsonStock = JSON.toJSONString(stockAdjFactor);
        redisTemplate.opsForValue().set(key, jsonStock);
    }

    @Override
    public StockAdjFactor getStockAdj(String code) {
        String key = code + "." + Constant.ADJFactor;
        String result = redisTemplate.opsForValue().get(key);
        StockAdjFactor stock = JSON.parseObject(result, StockAdjFactor.class);
        return stock;
    }

    @Override
    public void storeAllSymbols(List<Stock> list) {
        String allSymbols = JSON.toJSONString(list);
        redisTemplate.opsForValue().set("allSymbols", allSymbols);
    }

    @Override
    public List<Stock> getAllSymbols() {
        String allSymbols = redisTemplate.opsForValue().get("allSymbols");
        if (allSymbols == null) {
            List<Stock> portfolioSymbols = fileSystemDao.getAllSymbols();
            String json = JSON.toJSONString(portfolioSymbols);
            redisTemplate.opsForValue().set("allSymbols", json);
        }
        allSymbols = redisTemplate.opsForValue().get("allSymbols");
        List<Stock> stocks = JSON.parseArray(allSymbols, Stock.class);
        return stocks;
    }

    @Override
    public List<Stock> getPortfolioSymbols(String portfolio) {
        String result = redisTemplate.opsForValue().get(portfolio);
        if (result == null) {
            List<Stock> portfolioSymbols = fileSystemDao.getPortfolioSymbols(portfolio);
            String json = JSON.toJSONString(portfolioSymbols);
            redisTemplate.opsForValue().set(portfolio, json, 1, TimeUnit.MINUTES);
        }
        String allSymbols = redisTemplate.opsForValue().get(portfolio);
        List<Stock> stocks = JSON.parseArray(allSymbols, Stock.class);
        return stocks;
    }

    @Override
    public void storeStatistics(String filename, String content) {
        String key = Constant.STATISTICS + filename;
        redisTemplate.opsForValue().set(key, content);
    }

    @Override
    public String getStatistics(String filename) {
        String key = Constant.STATISTICS + filename;
        return redisTemplate.opsForValue().get(key);
    }
}
