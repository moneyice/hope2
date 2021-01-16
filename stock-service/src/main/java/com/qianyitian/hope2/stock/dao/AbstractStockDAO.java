package com.qianyitian.hope2.stock.dao;

import com.alibaba.fastjson.JSON;
import com.google.common.io.Files;
import com.qianyitian.hope2.stock.config.EStockKlineType;
import com.qianyitian.hope2.stock.config.PropertyConfig;
import com.qianyitian.hope2.stock.model.KLineInfo;
import com.qianyitian.hope2.stock.model.Stock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

import static com.qianyitian.hope2.stock.config.Constant.LITE_LEAST_DAY_NUMBER;


public abstract class AbstractStockDAO implements IStockDAO {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public List<Stock> getPortfolioSymbols(String portfolio) {
        return null;
    }

    protected abstract void storeStockInfo(Stock stock, EStockKlineType type);

    @Override
    public void storeStock(Stock stock) {
        storeStockInfo(stock, EStockKlineType.DAILY);
    }

    @Override
    public void storeStockLite(Stock stock) {
        List<KLineInfo> oldArray = stock.getkLineInfos();
        if (oldArray.size() > LITE_LEAST_DAY_NUMBER) {
            //只保留最近一年的
            List<KLineInfo> subList = oldArray.subList(oldArray.size() - LITE_LEAST_DAY_NUMBER, oldArray.size());
            stock.setkLineInfos(subList);
        }
        String jsonStock = JSON.toJSONString(stock);
        storeStockInfo(stock, EStockKlineType.DAILY_LITE);
    }

    @Override
    public void storeStockWeekly(Stock stock) {
        storeStockInfo(stock, EStockKlineType.WEEKLY);
    }

    @Override
    public void storeStockMonthly(Stock stock) {
        storeStockInfo(stock, EStockKlineType.MONTHLY);
    }


    protected abstract Stock getStockInfo(String code, EStockKlineType type);

    @Override
    public Stock getStock(String code) {
        return getStockInfo(code, EStockKlineType.DAILY);
    }

    @Override
    public Stock getStockLite(String code) {
        return getStockInfo(code, EStockKlineType.DAILY_LITE);
    }

    @Override
    public Stock getStockWeekly(String code) {
        return getStockInfo(code, EStockKlineType.WEEKLY);
    }

    @Override
    public Stock getStockMonthly(String code) {
        return getStockInfo(code, EStockKlineType.MONTHLY);
    }

}
