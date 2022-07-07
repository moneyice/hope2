package com.qianyitian.hope2.analyzer.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.io.Resources;
import com.qianyitian.hope2.analyzer.analyzer.DemarkAnalyzer;
import com.qianyitian.hope2.analyzer.analyzer.EStockAnalyzer;
import com.qianyitian.hope2.analyzer.analyzer.StockAnalyzerFacotry;
import com.qianyitian.hope2.analyzer.config.Constant;
import com.qianyitian.hope2.analyzer.external.NeteaseClient;
import com.qianyitian.hope2.analyzer.model.*;
import com.qianyitian.hope2.analyzer.service.DefaultStockService;
import com.qianyitian.hope2.analyzer.service.IReportStorageService;
import com.qianyitian.hope2.analyzer.service.MyFavoriteStockService;
import com.qianyitian.hope2.analyzer.service.StockSelecter;
import com.qianyitian.hope2.analyzer.util.BarUtil;
import com.qianyitian.hope2.analyzer.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class ETFController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DefaultStockService stockService;

    public ETFController() {
    }

    private Map getMap() {
        Map map = new HashMap();
        map.put("description", "每个交易周末，在16个ETF中，选出本周涨幅前6的ETF，等权重买入或者持有。");
        map.put("generateTime", LocalDateTime.now().toString());
        return map;
    }

    @RequestMapping("/etf-compare")
    @CrossOrigin
    public String etfCompare(@RequestParam(value = "days", required = false, defaultValue = "20") Integer days) {
        List<Map> list = new ArrayList<>();

        Arrays.stream(ETF.values()).forEach(etf -> {
            Stock stock = stockService.getStockDaily(etf.getCode());

            String code = stock.getCode();
            String name = stock.getName();

            List<KLineInfo> kLineInfos = BarUtil.getRecentKLine(stock, days);
            stock.setkLineInfos(kLineInfos);

            List<Number[]> numbers = BarUtil.convert2ChartFormatOnlyClosePrice(stock);
            Map<String, Object> map = new HashMap<>(3);
            map.put("name", name);
            map.put("data", numbers);
            map.put("code", code);

            list.add(map);
        });
        return JSON.toJSONString(list);
    }

    @RequestMapping("/etf")
    @CrossOrigin
    public Map etf() {
        Map map = getMap();
        List<ETFStatus> list = new LinkedList();
        map.put("resultList", list);

        Arrays.stream(ETF.values()).forEach(etf -> {
            Stock stock = stockService.getStockWeekly(etf.getCode());
            if(stock==null){
                logger.error("etf not found "+ etf.getCode());
                return;
            }
            String code = stock.getCode();
            String name = stock.getName();
            List<KLineInfo> kLineInfos = stock.getkLineInfos();


            KLineInfo thisWeekK = kLineInfos.get(kLineInfos.size() - 1);
            KLineInfo previousWeekK = kLineInfos.get(kLineInfos.size() - 2);
            KLineInfo beforePreviousWeekK = kLineInfos.get(kLineInfos.size() - 3);

            //本周涨幅
            double thisRange = Utils.calcRange(previousWeekK.getClose(), thisWeekK.getClose());
            //上周涨幅
            double previousRange = Utils.calcRange(beforePreviousWeekK.getClose(), previousWeekK.getClose());

            ETFStatus status = new ETFStatus();
            status.setCode(code);
            status.setName(name);
            status.setThisWeekRange(thisRange);
            status.setThisWeekRangeLabel(Utils.double2Percentage(thisRange));

            status.setPreviousWeekRange(previousRange);
            status.setPreviousWeekRangeLabel(Utils.double2Percentage(previousRange));

            list.add(status);
        });

        {
            List<ETFStatus> resultList = list.stream().sorted(Comparator.comparing(ETFStatus::getThisWeekRange).reversed()).collect(Collectors.toList());
            for (int i = 0; i < resultList.size(); i++) {
                if (i < 6) {
                    resultList.get(i).setThisHold("Y");
                }
                resultList.get(i).setThisRank(i + 1);
            }
        }
        {
            List<ETFStatus> resultList = list.stream().sorted(Comparator.comparing(ETFStatus::getPreviousWeekRange).reversed()).collect(Collectors.toList());
            for (int i = 0; i < resultList.size(); i++) {
                if (i < 6) {
                    resultList.get(i).setPreviousHold("Y");
                }
                resultList.get(i).setPreviousRank(i + 1);
            }
        }

        return map;
    }

    @RequestMapping("/etf2")
    @CrossOrigin
    public Map etf2() {
        Map map = getMap();
        List<ETFStatus> list = new LinkedList();
        map.put("resultList", list);

        LocalDate now = LocalDate.now();
        String end = now.format(DateTimeFormatter.BASIC_ISO_DATE);
        String start = now.minusWeeks(1).format(DateTimeFormatter.BASIC_ISO_DATE);

        Arrays.stream(ETF.values()).forEach(etf -> {
            Stock stock = getETFWeekly(etf.getCode(), start, end);
            if(stock==null){
                logger.error("etf not found "+ etf.getCode());
                return;
            }
            stock.setName(etf.getName());
            stock.setCode(etf.getCode());

            String code = stock.getCode();
            String name = stock.getName();
            List<KLineInfo> kLineInfos = stock.getkLineInfos();


            KLineInfo thisWeekK = kLineInfos.get(kLineInfos.size() - 1);
            KLineInfo previousWeekK = kLineInfos.get(kLineInfos.size() - 2);

            //本周涨幅
            double thisRange = Utils.calcRange(previousWeekK.getClose(), thisWeekK.getClose());

            //上周涨幅
            double previousRange = previousWeekK.getChangePercent();

            ETFStatus status = new ETFStatus();
            status.setCode(code);
            status.setName(name);
            status.setThisWeekRange(thisRange);
            status.setThisWeekRangeLabel(Utils.double2Percentage(thisRange));

            status.setPreviousWeekRange(previousRange);
            status.setPreviousWeekRangeLabel(Utils.double2Percentage(previousRange));

            list.add(status);
        });

        {
            List<ETFStatus> resultList = list.stream().sorted(Comparator.comparing(ETFStatus::getThisWeekRange).reversed()).collect(Collectors.toList());
            for (int i = 0; i < resultList.size(); i++) {
                if (i < 6) {
                    resultList.get(i).setThisHold("Y");
                }
                resultList.get(i).setThisRank(i + 1);
            }
        }
        {
            List<ETFStatus> resultList = list.stream().sorted(Comparator.comparing(ETFStatus::getPreviousWeekRange).reversed()).collect(Collectors.toList());
            for (int i = 0; i < resultList.size(); i++) {
                if (i < 6) {
                    resultList.get(i).setPreviousHold("Y");
                }
                resultList.get(i).setPreviousRank(i + 1);
            }
        }

        return map;
    }

    String URL = "https://q.stock.sohu.com/hisHq?code=cn_{0}&start={1}&end={2}&stat=1&order=A&period=w";
    String URL2 = "https://img1.money.126.net/data/hs/time/today/{0}{1}.json";


    @Autowired
    NeteaseClient neteaseClient;

    private Stock getETFWeekly(String code, String start, String end) {
        String url = MessageFormat.format(URL, code, start, end);
        Stock stock = new Stock();

        List<KLineInfo> kLineInfos = new LinkedList<KLineInfo>();
        stock.setkLineType(Stock.TYPE_WEEKLY);
        try {
            java.net.URL URL = new URL(url);
            String content = Resources.asCharSource(URL, Charset.forName("UTF-8")).readFirstLine();
            JSONArray jsonArray = JSON.parseArray(content);
            jsonArray = jsonArray.getJSONObject(0).getJSONArray("hq");

            for (int i = jsonArray.size() - 2; i < jsonArray.size(); i++) {
                JSONArray kInfo = (JSONArray) jsonArray.get(i);
                String dateString = kInfo.getString(0);
                double open = kInfo.getDouble(1);
                double close = kInfo.getDouble(2);
                double low = kInfo.getDouble(5);
                double high = kInfo.getDouble(6);
                double change = Utils.handleDouble(kInfo.getString(4).replace("%", "")) / 100;

                KLineInfo daily = new KLineInfo();
                LocalDate date = Utils.parseDate(dateString);
                daily.setOpen(open);
                daily.setHigh(high);
                daily.setLow(low);
                daily.setClose(close);
                daily.setDate(date);
                daily.setChangePercent(change);
                kLineInfos.add(daily);
            }
            stock.setkLineInfos(kLineInfos);
        } catch (IOException e) {
            logger.error("retrieve sohu stock data error " + url, e);
        }

        {
            String market = code.startsWith("5") ? "0" : "1";
            try {
                String content = neteaseClient.getStockToday(market, code);
                JSONObject jsonObject = JSON.parseObject(content);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                JSONArray kInfo = (JSONArray) jsonArray.get(jsonArray.size() - 1);
                double close = kInfo.getDouble(1);
                stock.getkLineInfos().get(1).setClose(close);
            } catch (Exception e) {
                logger.error("retrieve sohu stock data error " + URL2, e);
            }
        }

        return stock;
    }
}
