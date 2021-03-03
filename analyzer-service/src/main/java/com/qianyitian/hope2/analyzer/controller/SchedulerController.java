package com.qianyitian.hope2.analyzer.controller;

import com.qianyitian.hope2.analyzer.analyzer.DemarkAnalyzer;
import com.qianyitian.hope2.analyzer.external.*;
import com.qianyitian.hope2.analyzer.model.AnalyzeResult;
import com.qianyitian.hope2.analyzer.model.DemarkFlag;
import com.qianyitian.hope2.analyzer.model.EnumPortfolio;
import com.qianyitian.hope2.analyzer.model.ResultInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class SchedulerController {
    private Logger logger = LoggerFactory.getLogger(getClass());


    @Autowired
    NotifyClient notifyClient;

    @Autowired
    DemarkService demarkService;

//    @GetMapping(value = "/test")
//    @Scheduled(cron = "0 25 20 * * SUN,MON,TUE,WED,THU,FRI")
//    //每周1-5 20:00:00 执行
//    public void test() {
//        logger.info("test");
//    }

    @GetMapping(value = "/notify/h20")
    @Scheduled(cron = "0 0 20 * * MON,TUE,WED,THU,FRI")
    //每周1-5 20:00:00 执行
    public String portfolioH20Notify() {
        return portfolioNotify(EnumPortfolio.H20);
    }

    @GetMapping(value = "/notify/h150")
    @Scheduled(cron = "0 1 20 * * MON,TUE,WED,THU,FRI")
    //每周1-5 20:01:00 执行
    public String portfolioH150Notify() {
        return portfolioNotify(EnumPortfolio.H150);
    }

    @GetMapping(value = "/notify/f200")
    @Scheduled(cron = "0 2 20 * * MON,TUE,WED,THU,FRI")
    //每周1-5 20:02:00 执行
    public String portfolioF200Notify() {
        return portfolioNotify(EnumPortfolio.F200);
    }

    @GetMapping(value = "/notify/hk")
    @Scheduled(cron = "0 3 20 * * MON,TUE,WED,THU,FRI")
    //每周1-5 20:03:00 执行
    public String portfolioHKNotify() {
        return portfolioNotify(EnumPortfolio.HK);
    }

    @GetMapping(value = "/notify/us")
    @Scheduled(cron = "0 4 20 * * MON,TUE,WED,THU,FRI")
    //每周1-5 20:04:00 执行
    public String portfolioUSNotify() {
        return portfolioNotify(EnumPortfolio.US);
    }


    private String portfolioNotify(EnumPortfolio portfolio) {
        logger.info("scheduled notify portfolio " + portfolio.getName());
        LocalDate today = LocalDate.now();
        AnalyzeResult result = demarkService.portfolioReport(portfolio.getCode(), DemarkAnalyzer.DEFAULT_DAYS2NOW);
        Map<String, String> map = filterFirstPoint(today, result);
        String info = toString(map);
        notifyAll(info, portfolio);
        return info;
    }

    private void notifyAll(String info, EnumPortfolio portfolio) {
        if (!StringUtils.isEmpty(info)) {
            info = portfolio.getName() + "\n" + info;
            notifyClient.notifyAll(info);
        }
    }

    private void notifyAdmin(String info, EnumPortfolio portfolio) {
        if (!StringUtils.isEmpty(info)) {
            info = portfolio.getName() + "\n" + info;
            notifyClient.notifyAdmin(info);
        }
    }

    private Map<String, String> filterFirstPoint(LocalDate today, AnalyzeResult result) {
        Map<String, String> map = new HashMap<>();
        for (ResultInfo resultInfo : result.getResultList()) {
            List<DemarkFlag> list = (List<DemarkFlag>) resultInfo.getData().get("flag");
            if (list == null || list.isEmpty()) {
                continue;
            }
            //取最新的一条
            DemarkFlag selected = list.get(list.size() - 1);
            if (selected.getCountdownDate() != null) {
                //有BC点
                if (notExpired(selected.getCountdownDate(), today)) {
                    map.put(resultInfo.getName(), "BC " + selected.getCountdownDate());
                }
            } else if (selected.getSetupDate() != null) {
                //有BS点
                if (notExpired(selected.getSetupDate(), today)) {
                    map.put(resultInfo.getName(), "BS "+selected.getSetupDate());
                }
            }
        }
        return map;
    }

    private String toString(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(entry.getKey()).append(" ").append(entry.getValue()).append("\n");
        }
        return sb.toString();

    }

    private boolean notExpired(String countdownDate, LocalDate today) {
        LocalDate markDate = LocalDate.parse(countdownDate);
        long days = ChronoUnit.DAYS.between(markDate, today);
        return days <= 3;
    }


}
