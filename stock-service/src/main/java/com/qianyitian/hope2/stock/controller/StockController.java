package com.qianyitian.hope2.stock.controller;

import com.alibaba.fastjson.JSON;

import com.qianyitian.hope2.stock.dao.IStockDAO;
import com.qianyitian.hope2.stock.filter.StockFilter;
import com.qianyitian.hope2.stock.model.KLineInfo;
import com.qianyitian.hope2.stock.model.StatisticsInfo;
import com.qianyitian.hope2.stock.model.Stock;
import com.qianyitian.hope2.stock.model.SymbolList;
import com.qianyitian.hope2.stock.statistics.RangePercentageStatistics;
import com.qianyitian.hope2.stock.util.KUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@RestController
public class StockController {
    private Logger logger = LoggerFactory.getLogger(getClass());
    ExecutorService threadPool = Executors.newFixedThreadPool(4);
    @Resource(name = "stockDAO4FileSystem")
    private IStockDAO stockDAO;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public StockController() {
    }

    @GetMapping(value = "/pool")
    public String pool() {
        ThreadPoolExecutor pool = (ThreadPoolExecutor) threadPool;
        StringBuilder sb = new StringBuilder();
        sb.append("task count " + pool.getTaskCount()).append(" queue size " + pool.getQueue().size()).append(" completed tasks " + pool.getCompletedTaskCount());
        sb.append(" active count " + pool.getActiveCount());
        return sb.toString();

    }

    @PostMapping(value = "/stock")
    public void storeStock(@RequestBody Stock stock) {
        logger.info("storing   ======================================= " + stock.getCode());
        StockStoringTask task = new StockStoringTask(stock);
        threadPool.execute(task);
    }

    @PostMapping(value = "/data/kline/")
    public Stock getStock(@RequestBody Stock input) {
        if (input.getkLineType().equals("dailylite")) {
            return stockDAO.getStockLite(input.getCode());
        } else if (input.getkLineType().equals("daily")) {
            return stockDAO.getStock(input.getCode());
        } else if (input.getkLineType().equals("weekly")) {
            return stockDAO.getStockWeekly(input.getCode());
        } else if (input.getkLineType().equals("monthly")) {
            return stockDAO.getStockMonthly(input.getCode());
        }
        return input;
    }

    @GetMapping(value = "/data/kline/daily/{code}")
    public Stock getStock(@PathVariable String code) {
        Stock stock = stockDAO.getStock(code);
        return stock;
    }

    @GetMapping(value = "/data/kline/dailylite/{code}")
    public Stock getStockDailyLite(@PathVariable String code) {
        Stock stock = stockDAO.getStockLite(code);
        return stock;
    }

    @GetMapping(value = "/data/kline/weekly/{code}")
    public Stock getStockWeeklyInfo(@PathVariable String code) {
        Stock stock = stockDAO.getStockWeekly(code);
        return stock;
    }

    @GetMapping(value = "/data/kline/monthly/{code}")
    public Stock getStockYearlyInfo(@PathVariable String code) {
        Stock stock = stockDAO.getStockMonthly(code);
        return stock;
    }

    @GetMapping(value = "/stockList")
    public SymbolList getStockList() {
        List<Stock> list = stockDAO.getAllSymbols();
        SymbolList symbolList = new SymbolList();
        symbolList.setSymbols(list);
        return symbolList;
    }

    @GetMapping(value = "/favoriteList")
    public SymbolList favoriteList() {
        List<Stock> list = stockDAO.getFavoriteSymbols();
        SymbolList symbolList = new SymbolList();
        symbolList.setSymbols(list);
        return symbolList;
    }

    @PostMapping(value = "/stockList")
    public void storeStockList(@RequestBody List<Stock> stockList) {
        stockDAO.storeAllSymbols(stockList);
    }

    class StockStoringTask implements Runnable {
        Stock stock;

        public StockStoringTask(Stock stock) {
            this.stock = stock;
        }

        @Override
        public void run() {
            stockDAO.storeStock(stock);
            {
                Stock stockWeekly = KUtils.daily2Weekly(stock);
                KUtils.appendMacdInfo(stockWeekly.getkLineInfos());
                stockDAO.storeStockWeekly(stockWeekly);
                stockWeekly = null;
            }
            {
                Stock stockMonthly = KUtils.daily2Monthly(stock);
                KUtils.appendMacdInfo(stockMonthly.getkLineInfos());
                stockDAO.storeStockMonthly(stockMonthly);
                stockMonthly = null;
            }

            {
                KUtils.appendMacdInfo(stock.getkLineInfos());
                if (stock.getCode().startsWith("i")) {
                    //this is index
                    stockDAO.storeStock(stock);
                } else {
                    stockDAO.storeStock(stock);
                    stockDAO.storeStockLite(stock);
                }
                stock = null;
            }
        }
    }


    @GetMapping(value = "/getStatistics")
    public List<StatisticsInfo> getStatistics() {
        LocalDate from = LocalDate.parse("2004-01-05");

        String statistics = stringRedisTemplate.opsForValue().get("statistics");
        List<StatisticsInfo> list = JSON.parseArray(statistics, StatisticsInfo.class);

        StatisticsInfo toFind = new StatisticsInfo(from);
        int index = list.indexOf(toFind);

        List<StatisticsInfo> result = new ArrayList<>(list.subList(index, list.size() - 1));
        return result;
    }


    @GetMapping(value = "/makeStatistics")
    @Async
    public void makeStatistics() {

        Map<LocalDate, StatisticsInfo> totalStockInfo = new HashMap<>();

        SymbolList stockList = getStockList();
        logger.info("total number is {}", stockList.getSymbols().size());
        stockList.getSymbols().parallelStream().forEach(item -> {
            Stock stock = getStock(item.getCode());

            if (stock.getkLineInfos().isEmpty()) {
                logger.info("{} {} kline is empty", stock.getName(), stock.getCode());
                return;
            }
            {
                LocalDate firstDay = stock.getkLineInfos().get(0).getDate();

                Optional<StatisticsInfo> op = Optional.ofNullable(totalStockInfo.get(firstDay));
                if (!op.isPresent()) {
                    op = Optional.of(new StatisticsInfo(firstDay));
                    totalStockInfo.put(firstDay, op.get());
                }
                op.get().addTotalStockNumber();
            }

            for (KLineInfo kLineInfo : stock.getkLineInfos()) {
                if (isLimitUp(kLineInfo.getChangePercent())) {
                    Optional<StatisticsInfo> opUp = Optional.ofNullable(totalStockInfo.get(kLineInfo.getDate()));
                    if (!opUp.isPresent()) {
                        opUp = Optional.of(new StatisticsInfo(kLineInfo.getDate()));
                        totalStockInfo.put(kLineInfo.getDate(), opUp.get());
                    }
                    opUp.get().addLimitUpNumber();


                } else if (isLimitDown(kLineInfo.getChangePercent())) {
                    Optional<StatisticsInfo> opDown = Optional.ofNullable(totalStockInfo.get(kLineInfo.getDate()));
                    if (!opDown.isPresent()) {
                        opDown = Optional.of(new StatisticsInfo(kLineInfo.getDate()));
                        totalStockInfo.put(kLineInfo.getDate(), opDown.get());
                    }
                    opDown.get().addLimitDownNumber();
                }
            }
        });

        List<StatisticsInfo> result = totalStockInfo.values().parallelStream().sorted(Comparator.comparing(StatisticsInfo::getDate)).collect(Collectors.toList());

        for (int i = 1; i < result.size(); i++) {
            StatisticsInfo info = result.get(i);
            info.setTotalStockNumber(info.getTotalStockNumber() + result.get(i - 1).getTotalStockNumber());
        }
        stringRedisTemplate.opsForValue().set("statistics", JSON.toJSONString(result));

    }

    @GetMapping(value = "/makeStatistics2")
    @Async
    public List makeStatistics2() {
        SymbolList stockList = getStockList();

        RangePercentageStatistics rangePercentageStatistics = new RangePercentageStatistics();
        rangePercentageStatistics.init();


        stockList.getSymbols().parallelStream().forEach(item -> {
            Stock stock = getStock(item.getCode());
            rangePercentageStatistics.makeStatistics(stock);
        });

        return rangePercentageStatistics.getResult();
    }

    private boolean isLimitDown(double changePercent) {
        return changePercent < -9.90;
    }

    private boolean isLimitUp(double changePercent) {
        return changePercent >= 9.90;
    }


}
