package com.qianyitian.hope2.spider.controller;

import com.qianyitian.hope2.spider.job.StockInfoSpider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

@RestController
public class ScheduledController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource(name = "stockInfoSpider")
    private StockInfoSpider stockInfoSpider = null;

//    @Scheduled(initialDelay = 1000 * 60*10, fixedDelay = 1000 * 60 * 60 * 4)
////    每6个小时
    @Scheduled(cron = "0 30 18 * * MON,TUE,WED,THU,FRI")
    //每周1-5 18:30:00 执行
    public void retrieveStockDailyData() {
        logger.info("scheduled to retrieve stock daily data");
        stockInfoSpider.run();
    }

    @GetMapping("/lastUpdateTime")
    public String getLastUpdateTime() {
        Date date = stockInfoSpider.getLastUpdateTime();
        return date.toString();
    }

    @RequestMapping("/force_refresh_stocks")
    @Async
    public void forceRefreshStockInfo() {
        stockInfoSpider.setCheckOutOfDate(false);
        stockInfoSpider.run();
        stockInfoSpider.setCheckOutOfDate(true);
    }

    @RequestMapping("/force_refresh_us")
    @Async
    public void forceRefreshUS() {
        stockInfoSpider.runUS();
    }
}
