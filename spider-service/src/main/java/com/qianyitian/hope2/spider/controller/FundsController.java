package com.qianyitian.hope2.spider.controller;

import com.qianyitian.hope2.spider.job.FundsInfoSpider;
import com.qianyitian.hope2.spider.job.StockInfoSpider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

@RestController
public class FundsController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource(name = "fundsInfoSpider")
    private FundsInfoSpider fundsInfoSpider = null;

    @RequestMapping("/funds/force_refresh")
    @Async
    public void forceRefreshStockInfo() {
        fundsInfoSpider.run();
    }
}
