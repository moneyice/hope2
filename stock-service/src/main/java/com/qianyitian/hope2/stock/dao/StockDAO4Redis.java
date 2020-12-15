package com.qianyitian.hope2.stock.dao;

import com.alibaba.fastjson.JSON;
import com.google.common.io.Files;
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


@Repository("stockDAO4Redis")
//@RefreshScope
public class StockDAO4Redis implements IStockDAO {

    public static final int LITE_LEAST_DAY_NUMBER = 12 * 22;// about one year

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    public StockDAO4Redis() {
    }

    private String getRootPath() {
        return "/home/working/temp/stocks/";
    }

    @Override
    public void storeAllSymbols(List<Stock> list) {
        File rootFolder = new File(getRootPath());
        if (!rootFolder.exists()) {
            rootFolder.mkdirs();
        }
        String allSymbols = JSON.toJSONString(list);
        File to = new File(getRootPath(), "allSymbols");
        try {
            Files.write(allSymbols, to, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void storeStock(Stock stock) {
        String key = stock.getCode() + stock.getkLineType();
        String jsonStock = JSON.toJSONString(stock);
        stringRedisTemplate.opsForValue().set(key, jsonStock);
    }

    @Override
    public void storeStockLite(Stock stock) {
        stock.setkLineType(IStockDAO.TYPE_DAILY_LITE);
        List<KLineInfo> oldArray = stock.getkLineInfos();

        if (oldArray.size() > LITE_LEAST_DAY_NUMBER) {
            //只保留最近一年的
            List<KLineInfo> subList = oldArray.subList(oldArray.size() - LITE_LEAST_DAY_NUMBER, oldArray.size());
            stock.setkLineInfos(subList);
        }
        String key = stock.getCode() + IStockDAO.TYPE_DAILY_LITE;
        String jsonStock = JSON.toJSONString(stock);
        stringRedisTemplate.opsForValue().set(key, jsonStock);
    }

    @Override
    public void storeStockWeeklyInfo(Stock stock) {
        String jsonStock = JSON.toJSONString(stock);
        String key = stock.getCode() + IStockDAO.TYPE_WEEKLY;
        stringRedisTemplate.opsForValue().set(key, jsonStock);
    }

    @Override
    public void storeStockMonthlyInfo(Stock stock) {
        String jsonStock = JSON.toJSONString(stock);
        String key = stock.getCode() + IStockDAO.TYPE_MONTHLY;
        stringRedisTemplate.opsForValue().set(key, jsonStock);
    }

    @Override
    public Stock getStock(String code) {
        String key = code + IStockDAO.TYPE_DAILY;
        String rs = stringRedisTemplate.opsForValue().get(key);
        Stock stock = JSON.parseObject(rs, Stock.class);
        return stock;
    }

    @Override
    public Stock getStockLite(String code) {
        String key = code + IStockDAO.TYPE_DAILY_LITE;
        String rs = stringRedisTemplate.opsForValue().get(key);
        Stock stock = JSON.parseObject(rs, Stock.class);
        return stock;
    }

    @Override
    public Stock getStockWeeklyInfo(String code) {
        String key = code + IStockDAO.TYPE_WEEKLY;
        String rs = stringRedisTemplate.opsForValue().get(key);
        Stock stock = JSON.parseObject(rs, Stock.class);
        return stock;
    }

    @Override
    public Stock getStockMonthlyInfo(String code) {
        String key = code + IStockDAO.TYPE_MONTHLY;
        String rs = stringRedisTemplate.opsForValue().get(key);
        Stock stock = JSON.parseObject(rs, Stock.class);
        return stock;
    }

    @Override
    public List<Stock> getAllSymbols() {
        File rootFolder = new File(getRootPath());
        if (!rootFolder.exists()) {
            rootFolder.mkdirs();
        }
        File from = new File(getRootPath(), "allSymbols");
        if (!from.exists()) {
            return null;
        }
        String rs;
        try {
            rs = Files.readFirstLine(from, Charset.forName("UTF-8"));
            List<Stock> stocks = JSON.parseArray(rs.toString(), Stock.class);
            return stocks;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Date getStockUpdateTime(String code) {
        File file = new File(getRootPath(), code);
        long time = file.lastModified();
        Date lastDate = new Date(time);
        return lastDate;
    }

    @Override
    public Date getAllSymbolsUpdateTime() {
        File file = new File(getRootPath(), "allSymbols");
        long time = file.lastModified();
        Date lastDate = new Date(time);
        return lastDate;
    }
}
