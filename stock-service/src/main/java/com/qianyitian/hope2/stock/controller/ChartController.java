package com.qianyitian.hope2.stock.controller;

import com.alibaba.fastjson.JSON;
import com.qianyitian.hope2.stock.config.Constant;
import com.qianyitian.hope2.stock.dao.IStockDAO;
import com.qianyitian.hope2.stock.model.*;
import com.qianyitian.hope2.stock.statistics.RangePercentageStatistics;
import com.qianyitian.hope2.stock.util.KUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@RestController
public class ChartController {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Resource(name = "stockDAO4FileSystem")
    private IStockDAO stockDAO;

    public ChartController() {
    }

    @GetMapping(value = "/chart/series/day/{code}")
    @CrossOrigin
    public Series getStock(@PathVariable String code) {
        Stock stock = stockDAO.getStock(code);
        //制作 chart K线数据
        List<Number[]> collect = convert2ChartFormat(stock);
        return Series.build(stock, collect);
    }

    protected List<Number[]> convert2ChartFormat(Stock stock) {
        List<Number[]> collect = stock.getkLineInfos().stream().map(bar -> {
            long dateMilliSeconds = Constant.ONE_DAY_MILLISECONDS * bar.getDate().toEpochDay();
            Number[] row = new Number[]{dateMilliSeconds, bar.getOpen(), bar.getHigh(), bar.getLow(), bar.getClose()};
            return row;
        }).collect(Collectors.toList());
        return collect;
    }

    @GetMapping(value = "/chart/series/daylite/{code}")
    public Series getStockDailyLite(@PathVariable String code) {
        Stock stock = stockDAO.getStockLite(code);
        List<Number[]> collect = convert2ChartFormat(stock);
        return Series.build(stock, collect);
    }

    @GetMapping(value = "/chart/series/week/{code}")
    public Series getStockWeeklyInfo(@PathVariable String code) {
        Stock stock = stockDAO.getStockWeekly(code);
        List<Number[]> collect = convert2ChartFormat(stock);
        return Series.build(stock, collect);
    }

    @GetMapping(value = "/chart/series/month/{code}")
    public Series getStockMonthlyInfo(@PathVariable String code) {
        Stock stock = stockDAO.getStockMonthly(code);
        List<Number[]> collect = convert2ChartFormat(stock);
        return Series.build(stock, collect);
    }
}
