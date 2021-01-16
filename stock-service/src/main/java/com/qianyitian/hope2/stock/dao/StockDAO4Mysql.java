package com.qianyitian.hope2.stock.dao;

import com.alibaba.fastjson.JSON;
import com.google.common.io.Files;
import com.qianyitian.hope2.stock.config.EStockKlineType;
import com.qianyitian.hope2.stock.mapper.StockMapper;
import com.qianyitian.hope2.stock.model.KLineInfo;
import com.qianyitian.hope2.stock.model.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

import static com.qianyitian.hope2.stock.config.Constant.LITE_LEAST_DAY_NUMBER;


@Repository("stockDAO4Mysql")
public class StockDAO4Mysql extends AbstractStockDAO {

    @Autowired
    private StockMapper stockMapper;

    public StockDAO4Mysql() {
    }

    @Override
    public void storeAllSymbols(List<Stock> list) {
        throw new RuntimeException("not supported");
    }

    @Override
    public void storeStockInfo(Stock stock, EStockKlineType type) {
        String jsonStock = JSON.toJSONString(stock);

        switch (type) {
            case DAILY:
                stockMapper.insertDaily(stock.getCode(), jsonStock);
                break;
            case DAILY_LITE:
                stockMapper.insertDailyLite(stock.getCode(), jsonStock);
                break;
            case WEEKLY:
                stockMapper.insertWeekly(stock.getCode(), jsonStock);
                break;
            case MONTHLY:
                stockMapper.insertMonthly(stock.getCode(), jsonStock);
                break;
        }
    }


    @Override
    public Stock getStockInfo(String code, EStockKlineType type) {
        String rs = null;
        switch (type) {
            case DAILY:
                rs = stockMapper.getDaily(code);
                break;
            case DAILY_LITE:
                rs = stockMapper.getDailyLite(code);
                break;
            case WEEKLY:
                rs = stockMapper.getWeekly(code);
                break;
            case MONTHLY:
                rs = stockMapper.getMonthly(code);
                break;

        }
        if (rs == null) {
            return null;
        }
        Stock stock = JSON.parseObject(rs, Stock.class);
        return stock;
    }


    @Override
    public List<Stock> getAllSymbols() {
        throw new RuntimeException("not supported");
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
