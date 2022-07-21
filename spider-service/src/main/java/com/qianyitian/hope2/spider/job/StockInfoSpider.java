package com.qianyitian.hope2.spider.job;


import com.qianyitian.hope2.spider.config.PropertyConfig;
import com.qianyitian.hope2.spider.fetcher.IStockRetreiver;
import com.qianyitian.hope2.spider.fetcher.TuShareWebStockRetreiver;
import com.qianyitian.hope2.spider.model.EIndex;
import com.qianyitian.hope2.spider.model.ETF;
import com.qianyitian.hope2.spider.model.Stock;
import com.qianyitian.hope2.spider.model.StockAdjFactor;
import com.qianyitian.hope2.spider.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


@Component("stockInfoSpider")
public class StockInfoSpider {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    PropertyConfig propertyConfig;

    @Resource(name = "neteaseWebStockRetreiver")
    private IStockRetreiver stockRetreiver;

    @Resource(name = "guguDataWebStockRetreiver")
    private IStockRetreiver guguDataWebStockRetreiver;

    @Resource(name = "sohuWebStockRetreiver")
    private IStockRetreiver etfStockRetreiver;

    @Resource(name = "tuShareWebStockRetreiver")
    private TuShareWebStockRetreiver tuShareWebStockRetreiver;

    @Resource(name = "xiaoxiongFundRetreiver")
    private IStockRetreiver xiaoxiongStockRetreiver;

    private Date lastUpdateTime = null;

    private ExecutorService es = Executors.newFixedThreadPool(5);
    // if it's needed to check the stock info is out of date
    // if true, check
    // if false, no need to check, will refresh all the data.
    // it's useless for getAllStockSymbols
    private boolean checkOutOfDate = true;

    public StockInfoSpider() {

    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public boolean isCheckOutOfDate() {
        return checkOutOfDate;
    }

    public void setCheckOutOfDate(boolean checkOutOfDate) {
        this.checkOutOfDate = checkOutOfDate;
    }

    public void run() {
        try {
            if (!isStockOutOfDate(lastUpdateTime)) {
                logger.info("不需要更新 " + new Date());
                return;
            }

            logger.info("需要更新 " + new Date());
            syncUSData();
            syncHKData();
            syncIndexData();
            syncETFData();
            syncStockData();
            lastUpdateTime = new Date();
            startAnalyze();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void runStockAdjFactor() {
        try {
            List<Stock> stockSymbols = stockRetreiver.getAllStockSymbols();
            for (Stock stock : stockSymbols) {
                try {
                    StockAdjFactor simpleAdjFactor = tuShareWebStockRetreiver.getSimpleAdjFactor(stock);
                    Utils.sleep(100);
                    runInPool(createStoreAdjRunnable(simpleAdjFactor));
                } catch (IOException e) {
                    logger.error("get stock adj factor error: " + stock.getCode(), e);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private Runnable createStoreAdjRunnable(final StockAdjFactor simpleAdjFactor) {
        Runnable runnable = () -> {
            try {
                storeAdj(simpleAdjFactor);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        };
        return runnable;
    }

    public void runUS() {
        syncHKData();
        syncUSData();
    }
    public void runIndex() {
        syncIndexData();
    }

    private void syncUSData() {
        try {
            List<Stock> stockSymbols = stockRetreiver.getUSStockSymbols();
            loadAndSaveStock(stockSymbols, guguDataWebStockRetreiver);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void syncHKData() {
        try {
            List<Stock> stockSymbols = stockRetreiver.getHKStockSymbols();
            loadAndSaveStock(stockSymbols, guguDataWebStockRetreiver);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void loadAndSaveStock(List<Stock> stockSymbols, IStockRetreiver retreiver) {
        for (Stock stock : stockSymbols) {
            Runnable runnable = () -> {
                try {
                    final Stock info = retreiver.fetchStockInfo(stock);
                    storeStock(info);
                } catch (Exception e) {
                    logger.error(e.getMessage() + " " + stock.getCode(), e);
                }
            };
            runInPool(runnable);
        }
    }

    private void loadAndSaveIndex(List<Stock> stockSymbols, IStockRetreiver retreiver) {
        for (Stock stock : stockSymbols) {
            Runnable runnable = () -> {
                try {
                    final Stock info = retreiver.fetchIndexInfo(stock);
                    storeStock(info);
                } catch (Exception e) {
                    logger.error(e.getMessage() + " " + stock.getCode(), e);
                }
            };
            runInPool(runnable);
        }
    }

    public Stock getUSStock(String code) throws IOException {
        Stock info = new Stock();
        info.setCode(code);
        info.setMarket("US");
        return guguDataWebStockRetreiver.fetchStockInfo(info);
    }

    public Stock getHKStock(String code) throws IOException {
        Stock info = new Stock();
        info.setCode(code);
        info.setMarket("HK");
        return guguDataWebStockRetreiver.fetchStockInfo(info);
    }

    private void syncIndexData() {
        try {
            List<Stock> indexSymbols = stockRetreiver.getIndexSymbols();
            loadAndSaveIndex(indexSymbols, xiaoxiongStockRetreiver);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


    private void syncETFData() {
        List<Stock> etfSymbols = Arrays.asList(ETF.values()).stream().map(etf -> {
            Stock stock = new Stock();
            stock.setCode(etf.getCode());
            stock.setName(etf.getName());
            stock.setMarket(etf.getMarket());
            return stock;
        }).collect(Collectors.toList());

        loadAndSaveStock(etfSymbols, etfStockRetreiver);
    }


    private void syncStockData() {
        try {
            List<Stock> stockSymbols = stockRetreiver.getAllStockSymbols();
            loadAndSaveStock(stockSymbols, stockRetreiver);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void runInPool(Runnable runnable) {
//        runnable.run();
        es.execute(runnable);
    }

    private void startAnalyze() {
        restTemplate.getForObject(propertyConfig.getAnalyzerService() + "/startAnalyze", Stock.class);
        logger.info("trigger start analyzing ");
    }

    private void storeStock(Stock info) {
        restTemplate.postForObject(propertyConfig.getStockService() + "/stock", info, Stock.class);
//        logger.info("saved stock " + info.getCode());
    }

    private void storeAdj(StockAdjFactor simpleAdjFactor) {
        restTemplate.postForObject(propertyConfig.getStockService() + "/adj", simpleAdjFactor, StockAdjFactor.class);
//        logger.info("saved stock " + info.getCode());
    }


    protected boolean isStockOutOfDate(Date lastUpdateTime) {
        return isStockOutOfDate(new Date(), lastUpdateTime);
    }

    protected boolean isStockOutOfDate(Date compareTime, Date lastUpdateTime) {

        if (!isCheckOutOfDate()) {
            return true;
        }
        // 每天16:00 以前，最新数据是昨天的， 每天16:00 以后，最新数据是今天的，就什么都不做。
        // 否则更新数据
        if (lastUpdateTime == null) {
            return true;
        }
        Calendar lastUpdateCalendar = Calendar.getInstance();
        lastUpdateCalendar.setTime(lastUpdateTime);

        Calendar nowTime = Calendar.getInstance();
        nowTime.setTime(compareTime);

        //基准时间为当天的16:00
        Calendar alignmentTime = Calendar.getInstance();
        alignmentTime.setTime(compareTime);
        alignmentTime.set(Calendar.HOUR_OF_DAY, 16);
        alignmentTime.set(Calendar.MINUTE, 0);
        alignmentTime.set(Calendar.SECOND, 0);
        alignmentTime.set(Calendar.MILLISECOND, 0);

        logger.info("上次修改时间" + lastUpdateCalendar.getTime());
        logger.info("当前时间" + nowTime.getTime());
        logger.info("基准时间" + alignmentTime.getTime());

        //如果当前时间过了基准时间，最后更新时间没有过基准时间，就说明过期了
        return isLater(nowTime, alignmentTime) && !isLater(lastUpdateCalendar, alignmentTime);
    }

    private boolean isLater(Calendar c1, Calendar c2) {
        return c1.compareTo(c2) > 0;
    }
}
