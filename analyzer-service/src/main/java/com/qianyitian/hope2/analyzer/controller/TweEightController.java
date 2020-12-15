package com.qianyitian.hope2.analyzer.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.io.Resources;
import com.qianyitian.hope2.analyzer.job.*;
import com.qianyitian.hope2.analyzer.job.policy28.BaseTwoEightJob;
import com.qianyitian.hope2.analyzer.model.AccountSummary;
import com.qianyitian.hope2.analyzer.model.Stock;
import com.qianyitian.hope2.analyzer.model.ValueLog;
import com.qianyitian.hope2.analyzer.service.DefaultStockService;
import com.qianyitian.hope2.analyzer.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.lang.System.out;

@RestController
public class TweEightController {
    static String historyURL = "http://quotes.money.163.com/service/chddata.html?code=#{symbol}&start=20140101&&fields=TOPEN;HIGH;LOW;TCLOSE;VOTURNOVER";
    static String currentURL = "http://api.money.126.net/data/feed/#{symbol},money.api";

    @Autowired
    private DefaultStockService stockService;

    @RequestMapping("/28")
    public String analyze28(
            @RequestParam(value = "name", defaultValue = "World") String name) {
        String result = null;
        DecimalFormat df = new DecimalFormat("######0.00");
        try {
            // 沪深300
            double current300 = getCurrentIndex("0000300");
            double history300 = getIndexOf4WeeksAgo("0000300");

            // 中证500
            double current500 = getCurrentIndex("0000905");
            double history500 = getIndexOf4WeeksAgo("0000905");

            result = "300" + "     "
                    + df.format((current300 / history300 - 1) * 100) + "%";
            result = result + "-------------------------";
            result = result + "500" + "      "
                    + df.format((current500 / history500 - 1) * 100) + "%";

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("163 process error ");
        }
        return result;
    }

    @RequestMapping("/28/{version}")
    public AccountSummary trace28(
            @PathVariable String version, @RequestParam String startDate) {
        LocalDate fromDate = null;
        try {
            fromDate = LocalDate.parse(startDate);
        } catch (Exception e) {
            //300 index starts from 2002 01
            //500 index starts from here
            fromDate = LocalDate.parse("2005-02-04");
        }

        BaseTwoEightJob job = JobFacotry.getTwoEightJob(version);
        job.setFromDate(fromDate);

        Stock stock300 = stockService.getStockWeekly("i000300");
        job.setIndex300List(stock300.getkLineInfos());

        Stock stock500 = stockService.getStockWeekly("i000905");
        job.setIndex500List(stock500.getkLineInfos());

        job.run();

        return job.accountSummary();
    }


    @RequestMapping("/28/compare")
    public String compare(@RequestParam String startDate) {
        LocalDate fromDate = null;
        try {
            fromDate = LocalDate.parse(startDate);
        } catch (Exception e) {
            //300 index starts from 2002 01
            //500 index starts from here
            fromDate = LocalDate.parse("2005-02-04");
        }

        Stock index300 = stockService.getStockWeekly("i000300");
        Stock index500 = stockService.getStockWeekly("i000905");
        List<ValueLog> jobR1ValueLogList = getValueLogs("r1", fromDate, index300, index500);
        List<ValueLog> jobR2ValueLogList = getValueLogs("r2", fromDate, index300, index500);
        List<ValueLog> jobBase300ValueLogList = getValueLogs("base300", fromDate, index300, index500);
        List<ValueLog> jobBase500ValueLogList = getValueLogs("base500", fromDate, index300, index500);
        List<ValueLog> job300ValueLogList = getValueLogs("300", fromDate, index300, index500);
        List<ValueLog> job500ValueLogList = getValueLogs("500", fromDate, index300, index500);


        List<String> categoryList = new LinkedList<>();
        List<Double> r1DataList = new LinkedList<>();
        List<Double> r2DataList = new LinkedList<>();
        List<Double> base300DataList = new LinkedList<>();
        List<Double> base500DataList = new LinkedList<>();
        List<Double> index300DataList = new LinkedList<>();
        List<Double> index500DataList = new LinkedList<>();

        //按月分组显示
        int monthIndex = jobR1ValueLogList.get(0).getDate().getMonthValue();
        for (int i = 1; i < jobR1ValueLogList.size(); i++) {
            LocalDate date = jobR1ValueLogList.get(i).getDate();
            int monthValue = date.getMonthValue();
            if (monthIndex != monthValue) {
                ValueLog wanted = jobR1ValueLogList.get(i - 1);
                categoryList.add(getCategoryLabel(wanted));
                r1DataList.add(wanted.getTotalValue());
                r2DataList.add(jobR2ValueLogList.get(i - 1).getTotalValue());
                base300DataList.add(jobBase300ValueLogList.get(i - 1).getTotalValue());
                base500DataList.add(jobBase500ValueLogList.get(i - 1).getTotalValue());
                index300DataList.add(job300ValueLogList.get(i - 1).getTotalValue());
                index500DataList.add(job500ValueLogList.get(i - 1).getTotalValue());
                monthIndex = monthValue;
            }
        }

        Map<String, List> map = new HashMap<String, List>();
        map.put("category", categoryList);
        map.put("r1", r1DataList);
        map.put("r2", r2DataList);
        map.put("base300", base300DataList);
        map.put("base500", base500DataList);
        map.put("index300", index300DataList);
        map.put("index500", index500DataList);

        return JSON.toJSONString(map);
    }

    private List<ValueLog> getValueLogs(String jobVersion, LocalDate fromDate, Stock index300, Stock index500) {
        BaseTwoEightJob jobR1 = JobFacotry.getTwoEightJob(jobVersion);
        jobR1.setFromDate(fromDate);
        jobR1.setIndex300List(index300.getkLineInfos());
        jobR1.setIndex500List(index500.getkLineInfos());
        jobR1.run();
        return jobR1.accountSummary().getValueLogList();
    }

    private String getCategoryLabel(ValueLog wanted) {
        return wanted.getDate().getYear() + "-" + wanted.getDate().getMonthValue();
    }


    private double getCurrentIndex(String symbol) throws IOException {
        String url = currentURL.replace("#{symbol}", symbol);
        String json = Resources
                .toString(new URL(url), Charset.forName("UTF-8"));
        json = json.replaceAll("_ntes_quote_callback\\(", "").replaceAll(
                "\\);", "");

        out.println(json);
        JSONObject jsonObj = com.alibaba.fastjson.JSON.parseObject(json);
        jsonObj = jsonObj.getJSONObject(symbol);

        out.println(jsonObj);
        double index = jsonObj.getDouble("price");
        return index;
    }

    private double getIndexOf4WeeksAgo(String symbol) throws IOException {
        String url = historyURL.replace("#{symbol}", symbol);
        List<String> list = Resources.readLines(new URL(url),
                Charset.forName("UTF-8"));

        String info = list.get(20);
        String[] result = info.split(",");
        double close = Utils.handleDouble(result[3]);
        return close;
    }
}
