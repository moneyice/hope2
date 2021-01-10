package com.qianyitian.hope2.analyzer.controller;


import com.alibaba.fastjson.JSON;
import com.qianyitian.hope2.analyzer.config.PropertyConfig;
import com.qianyitian.hope2.analyzer.job.backtracking.BacktrackingJob;
import com.qianyitian.hope2.analyzer.job.backtracking.BuyAndHoldBacktrackingPolicy;
import com.qianyitian.hope2.analyzer.job.backtracking.MacdAndMean30BacktrackingPolicy;
import com.qianyitian.hope2.analyzer.model.KLineInfo;
import com.qianyitian.hope2.analyzer.model.Stock;
import com.qianyitian.hope2.analyzer.model.ValueLog;
import com.qianyitian.hope2.analyzer.service.DefaultStockService;
import com.qianyitian.hope2.analyzer.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class BacktrackingController {
    @Autowired
    private DefaultStockService stockService;
    @Autowired
    private PropertyConfig propertyConfig;

    @RequestMapping("/backtracking/{code}")
    public String compare(@PathVariable String code, @RequestParam String startDate) {
        LocalDate fromDate = null;
        try {
            fromDate = LocalDate.parse(startDate);
        } catch (Exception e) {
            //300 index starts from 2002 01
            //500 index starts from here
            fromDate = LocalDate.parse("2005-02-04");
        }

        Stock stock = stockService.getStockDaily(code);

        BacktrackingJob macdAndMean30PolicyJob = new BacktrackingJob(stock, new MacdAndMean30BacktrackingPolicy());
        macdAndMean30PolicyJob.setFromDate(fromDate);
        macdAndMean30PolicyJob.run();
        List<ValueLog> jobR1ValueLogList = macdAndMean30PolicyJob.getAccountSummary().getValueLogList();

        BacktrackingJob buyAndHoldPolicyJob = new BacktrackingJob(stock, new BuyAndHoldBacktrackingPolicy());
        buyAndHoldPolicyJob.setFromDate(fromDate);
        buyAndHoldPolicyJob.run();
        List<ValueLog> jobR2ValueLogList = buyAndHoldPolicyJob.getAccountSummary().getValueLogList();

        List<String> categoryList = new LinkedList<>();
        List<Double> r1DataList = new LinkedList<>();
        List<Double> r2DataList = new LinkedList<>();


        for (int i = 0; i < jobR1ValueLogList.size(); i++) {
            ValueLog wanted = jobR1ValueLogList.get(i);
            categoryList.add(getCategoryLabel(wanted));
            r1DataList.add(wanted.getTotalValue());

            r2DataList.add(jobR2ValueLogList.get(i).getTotalValue());
        }

        Map<String, List> map = new HashMap<String, List>();
        map.put("category", categoryList);
        map.put("r1", r1DataList);
        map.put("r2", r2DataList);
        return JSON.toJSONString(map);
    }

    private String getCategoryLabel(ValueLog wanted) {
        return wanted.getDate().getYear() + "-" + wanted.getDate().getMonthValue() + "-" + wanted.getDate().getDayOfMonth();
    }


    @RequestMapping("/backtrackingPeriodIncreaseByYearly/{code}")
    public String backtrackingPeriodIncreaseByYearly(@PathVariable String code, @RequestParam String startDate, @RequestParam String endDate) {
        //  /backtrackingPeriodIncreaseByYearly/000001?startDate=2021-09-01&endDate=2021-09-25
        List<String> list = new ArrayList<>();

        LocalDate fromDate = null;
        Stock stock = stockService.getStockDaily(code);
        List<KLineInfo> kLineInfoList = stock.getkLineInfos();
        int startYear = kLineInfoList.get(0).getDate().getYear();
        int endYear = kLineInfoList.get(kLineInfoList.size() - 1).getDate().getYear();
        if (startYear + 3 > endYear) {
            return "样本太少，没有意义";
        }
        Map<LocalDate, Double> priceMap = kLineInfoList.parallelStream().collect(Collectors.toMap(KLineInfo::getDate, KLineInfo::getClose));
        LocalDate from = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        try {
            for (int i = startYear; i <= endYear; i++) {
                from = from.withYear(i);
                end = end.withYear(i);
                Double fromPrice = getPrice(from, priceMap);
                if (fromPrice == null) {
                    continue;
                }

                Double endPrice = getPrice(end, priceMap);

                String range = Utils.calcRangeLabel(fromPrice, endPrice);

                String s = fromPrice + " ------> " + endPrice + " 涨幅 " + range + "  " + i + "年 ";
                list.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSON.toJSONString(list);
    }

    private Double getPrice(LocalDate from, Map<LocalDate, Double> priceMap) {
        LocalDate target = LocalDate.of(from.getYear(), from.getMonth(), from.getDayOfMonth());

        Double price = null;
        while (true) {
            if (target.getMonthValue() + 1 < from.getMonthValue()) {
                break;
            }

            Double find = priceMap.get(target);
            if (find != null) {
                price = find;
                break;
            } else {
                target = target.minusDays(1);
            }
        }
        return price;
    }
}
