package com.qianyitian.hope2.spider.controller;

import com.qianyitian.hope2.spider.job.FundsInfoSpider;
import com.qianyitian.hope2.spider.model.Stock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class FundsController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource(name = "fundsInfoSpider")
    private FundsInfoSpider fundsInfoSpider = null;

    @GetMapping("/fund/refresh-detail")
    @Async
    @Scheduled(cron = "0 22 2 * * TUE,WED,THU,FRI,SAT")
    //每周2-6 02:22:00 执行
    public void forceRefreshFundInfo() {
        fundsInfoSpider.run();
    }

    @GetMapping("/fund/sync/{code}")
    public void forceRefreshFundInfo(@PathVariable String code) {
        fundsInfoSpider.syncFundData(code);
    }



    @GetMapping("/fund/financial-report/sync/{code}")
    public void genFinancialReport(@PathVariable String code) {
        fundsInfoSpider.syncFundFinancialReport(code);
    }
    @GetMapping("/fund/check-financial-report")
    //使用蛋卷的数据，3个月执行一次，会封IP,小心使用
    public void checkReport() {
        fundsInfoSpider.checkReport();
    }
    @GetMapping("/fund/refresh-financial-report")
    //使用蛋卷的数据，3个月执行一次，会封IP,小心使用
    public void genFinancialReport() {
        fundsInfoSpider.syncFundFinancialReport();
    }

}
