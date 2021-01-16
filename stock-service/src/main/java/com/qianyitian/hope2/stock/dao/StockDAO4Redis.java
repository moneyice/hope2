package com.qianyitian.hope2.stock.dao;

import com.alibaba.fastjson.JSON;
import com.google.common.io.Files;
import com.qianyitian.hope2.stock.config.Constant;
import com.qianyitian.hope2.stock.config.EStockKlineType;
import com.qianyitian.hope2.stock.model.KLineInfo;
import com.qianyitian.hope2.stock.model.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

import static com.qianyitian.hope2.stock.config.Constant.LITE_LEAST_DAY_NUMBER;


@Repository("stockDAO4Redis")
//@RefreshScope
public class StockDAO4Redis extends AbstractStockDAO {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public StockDAO4Redis() {
    }

    @Override
    public void storeAllSymbols(List<Stock> list) {
        String allSymbols = JSON.toJSONString(list);
        stringRedisTemplate.opsForValue().set("allSymbols", allSymbols);
    }

    @Override
    protected void storeStockInfo(Stock stock, EStockKlineType type) {
        String key = stock.getCode() + type.getFolderName();
        String jsonStock = JSON.toJSONString(stock);
        stringRedisTemplate.opsForValue().set(key, jsonStock);
    }


    @Override
    protected Stock getStockInfo(String code, EStockKlineType type) {
        String key = code + type.getFolderName();
        String rs = stringRedisTemplate.opsForValue().get(key);
        Stock stock = JSON.parseObject(rs, Stock.class);
        return stock;
    }

    @Override
    public List<Stock> getAllSymbols() {
        String rs = stringRedisTemplate.opsForValue().get("allSymbols");
        List<Stock> stocks = JSON.parseArray(rs, Stock.class);
        return stocks;
    }

    @Override
    public Date getStockUpdateTime(String code) {
        throw new RuntimeException("not supported");
    }

    @Override
    public Date getAllSymbolsUpdateTime() {
        throw new RuntimeException("not supported");
    }

    @Override
    public void storeStatistics(String filename, String content) {
        throw new RuntimeException("not supported");
    }

    @Override
    public String getStatistics(String filename) {
        throw new RuntimeException("not supported");
    }
}
