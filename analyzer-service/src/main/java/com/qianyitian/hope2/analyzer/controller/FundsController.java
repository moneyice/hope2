package com.qianyitian.hope2.analyzer.controller;

import com.qianyitian.hope2.analyzer.service.FundReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class FundsController {


    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    FundReportService fundReportService;

    public FundsController() {

    }


    @GetMapping("/fund/maxBackdraw/{code}")
    @CrossOrigin
    public String maxBackdraw(@PathVariable String code, @RequestParam(value = "days", required = false, defaultValue = "200") Integer days) {
        return fundReportService.maxBackdraw(code, days);
    }

    @PostMapping("/fund/report/position/collection")
    @CrossOrigin
    public String generateFundReport(@RequestBody String codes) {
        return fundReportService.generateFundReport(codes);
    }

    @GetMapping("/fund/report/position/all")
    @CrossOrigin
    public String generateFundPositionStatusReport() {
        return fundReportService.generateFundPositionStatusReport();
    }

    @GetMapping("/top200FundPosition")
    public String top200FundPosition() {
        String report = fundReportService.top200FundPosition();
        return report;
    }


    @GetMapping("/reportMapDBKeys")
    public void reportMapDBKeys() {
        fundReportService.reportMapDBKeys();
    }

    @GetMapping("/fundPositionReport/{date}")
    public String funds(@PathVariable String date) {
        return fundReportService.funds(date);
    }

    @GetMapping("/fundPositionReport")
    @CrossOrigin
    public String funds() {
        return fundReportService.funds();
    }


    @GetMapping("/fundPositionTrend")
    public String fundPositionTrend() {
        return fundReportService.fundPositionTrend();
    }

    @RequestMapping("/fund/all/profile")
    public String allFunds() throws IOException {
        String content = fundReportService.allFunds();
        return content;
    }

    @RequestMapping("/calculateFundsProfile")
    @Scheduled(cron = "0 14 4 * * TUE,WED,THU,FRI,SAT")
    //每周2-6 04:14:00 执行
    public String calculateFundsProfile() throws IOException {
        String content = fundReportService.calculateFundsProfile();
        return content;
    }
}



