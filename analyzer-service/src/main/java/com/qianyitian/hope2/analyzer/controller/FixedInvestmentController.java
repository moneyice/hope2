package com.qianyitian.hope2.analyzer.controller;

import com.alibaba.fastjson.JSON;
import com.qianyitian.hope2.analyzer.config.PropertyConfig;
import com.qianyitian.hope2.analyzer.job.fixedinvestiment.FixedInvestmentJob;
import com.qianyitian.hope2.analyzer.job.fixedinvestiment.IFixedInvestmentPolicy;
import com.qianyitian.hope2.analyzer.job.fixedinvestiment.MonthlyPolicy;
import com.qianyitian.hope2.analyzer.job.fixedinvestiment.WeeklyPolicy;
import com.qianyitian.hope2.analyzer.model.Stock;
import com.qianyitian.hope2.analyzer.model.ValueLog;
import com.qianyitian.hope2.analyzer.service.DefaultStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by bing.a.qian on 13/2/2018.
 */

@RestController
public class FixedInvestmentController {
    @Autowired
    private DefaultStockService stockService;

    @Autowired
    PropertyConfig propertyConfig;

    @RequestMapping("/fixInvestment/{code}")
    public String calcFixInvestment(@PathVariable String code, @RequestParam String startDate, @RequestParam String endDate, @RequestParam String type, @RequestParam String days) {
        LocalDate fromDate = null;
        try {
            fromDate = LocalDate.parse(startDate);
        } catch (Exception e) {
            //300 index starts from 2002 01
            //500 index starts from here
            fromDate = LocalDate.parse("2005-02-04");
        }
        LocalDate toDate = null;
        try {
            toDate = LocalDate.parse(endDate);
        } catch (Exception e) {
            toDate = LocalDate.now();
        }

        Stock stock = stockService.getStockDaily(code);

        IFixedInvestmentPolicy policy = buildPolicy(type, days);


        FixedInvestmentJob job = new FixedInvestmentJob(stock, policy);
        job.setFromDate(fromDate);
        job.setToDate(toDate);
        job.run();
        List<ValueLog> jobR1ValueLogList = job.getAccountSummary().getValueLogList();
        List<ValueLog> benchmarkValueLogList = job.getBenchmarkAccountSummary().getValueLogList();
        List<String> categoryList = new LinkedList<>();
        List<Double> r1DataList = new LinkedList<>();
        List<Double> r2DataList = new LinkedList<>();


        for (int i = 0; i < jobR1ValueLogList.size(); i++) {
            ValueLog wanted = jobR1ValueLogList.get(i);
            categoryList.add(getCategoryLabel(wanted));
            r1DataList.add(wanted.getTotalValue());
            r2DataList.add(benchmarkValueLogList.get(i).getTotalValue());
        }

        Map<String, List> map = new HashMap<String, List>();
        map.put("category", categoryList);
        map.put("r1", r1DataList);
        map.put("r2", r2DataList);


        return JSON.toJSONString(map);
    }

    private IFixedInvestmentPolicy buildPolicy(String type, String days) {
        IFixedInvestmentPolicy policy;
        if ("week".equals(type)) {
            policy = new WeeklyPolicy();
        } else {
            policy = new MonthlyPolicy();
        }
        policy.setDays(days);

        return policy;
    }

    private String getCategoryLabel(ValueLog wanted) {
        return wanted.getDate().getYear() + "-" + wanted.getDate().getMonthValue() + "-" + wanted.getDate().getDayOfMonth();
    }
}
