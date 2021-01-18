package com.qianyitian.hope2.stock.controller;

import com.alibaba.fastjson.JSON;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.qianyitian.hope2.stock.dao.IStockDAO;
import com.qianyitian.hope2.stock.model.*;
import com.qianyitian.hope2.stock.statistics.RangePercentageStatistics;
import com.qianyitian.hope2.stock.util.KUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RestController
public class StockController {
    private Logger logger = LoggerFactory.getLogger(getClass());
    ExecutorService threadPool = Executors.newFixedThreadPool(4);
    @Resource(name = "stockDAO4FileSystem")
    private IStockDAO stockDAO;

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

    @GetMapping(value = "/data1/kline/daily/{code}")
    public Stock getStock1(@PathVariable String code) {
        Stock stock = stockDAO.getStock(code);
        return stock;
    }

    CacheLoader<String, Stock> loader = new CacheLoader<String, Stock>() {
        @Override
        public Stock load(String key) throws Exception {
            Stock stock = stockDAO.getStock(key);
            return stock;
        }
    };


    LoadingCache<String, Stock> cache = Caffeine.newBuilder()
            // 数量上限
            .maximumSize(1024).recordStats()
            // 过期机制
            .expireAfterWrite(1, TimeUnit.HOURS)
            .refreshAfterWrite(50, TimeUnit.MINUTES)
            .build(loader);


    @GetMapping(value = "/data/kline/daily/{code}")
    public Stock getStock(@PathVariable String code) {
        Stock stock = null;
        try {
            stock = cache.get(code);
        } catch (Exception e) {
            throw new RuntimeException("stock not exist " + code);
        }
        return stock;
    }

    @GetMapping(value = "/cache/status")
    public String cacheStatus() {
        CacheStats stats = cache.stats();
        Map map = new HashMap();
        map.put("hitCount", stats.hitCount());
        map.put("missCount", stats.missCount());
        map.put("loadSuccessCount", stats.loadSuccessCount());
        map.put("loadFailureCount", stats.loadFailureCount());
        map.put("totalLoadTime", stats.totalLoadTime());
        map.put("evictionCount", stats.evictionCount());
        map.put("evictionWeight", stats.evictionWeight());
        map.put("hitRate", stats.hitRate());
        map.put("missRate", stats.missRate());
        map.put("loadFailureRate", stats.loadFailureRate());
        return JSON.toJSONString(map);
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

    @GetMapping(value = "/stockList/{portfolio}")
    public SymbolList favoriteList(@PathVariable String portfolio) {
        List<Stock> list = stockDAO.getPortfolioSymbols(portfolio);
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


    @GetMapping(value = "/makeStatistics")
    @Async
    public String makeStatistics() {
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
        return JSON.toJSONString(result);

    }

    @GetMapping(value = "/increaseRangeStatistics")
    public String makeStatistics2() {
        SymbolList stockList = getStockList();

        RangePercentageStatistics rangePercentageStatistics = new RangePercentageStatistics();
        rangePercentageStatistics.init();


        stockList.getSymbols().parallelStream().forEach(item -> {
            Stock stock = getStock(item.getCode());
            if (stock == null) {
                return;
            }
            rangePercentageStatistics.makeStatistics(stock);
        });
        List<ChartItem> chartItemList = rangePercentageStatistics.getResult();


        Map map = new HashMap();
        map.put("stockChart", chartItemList);
        map.put("newStock", rangePercentageStatistics.getNewStockCount());


        List<ChartItem> indexChartList = new LinkedList<>();
        Arrays.stream(EIndex.values()).forEach(eIndex -> {
            Stock stock = stockDAO.getStock("i" + eIndex.getCode());
            if (stock == null) {
                return;
            }
            KLineInfo now = KUtils.findKLine(stock.getkLineInfos(), 0);
            KLineInfo base = KUtils.findKLine(stock.getkLineInfos(), RangePercentageStatistics.days2Now);

            double range = KUtils.calcIncreaseRange(base.getClose(), now.getClose()) * 100;
            BigDecimal bg = new BigDecimal(range);
            ChartItem<Double> ct = new ChartItem();
            ct.setName(eIndex.getName());
            ct.setY(bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
            indexChartList.add(ct);
        });
        map.put("indexChart", indexChartList);
        String s = JSON.toJSONString(map);
        stockDAO.storeStatistics("increaseRangeStatistics", s);
        return s;
    }

    private boolean isLimitDown(double changePercent) {
        return changePercent < -9.90;
    }

    private boolean isLimitUp(double changePercent) {
        return changePercent >= 9.90;
    }


}
