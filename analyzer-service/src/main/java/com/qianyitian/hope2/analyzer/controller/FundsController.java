package com.qianyitian.hope2.analyzer.controller;

import com.alibaba.fastjson.JSON;
import com.qianyitian.hope2.analyzer.config.PropertyConfig;
import com.qianyitian.hope2.analyzer.funds.model.Fund;
import com.qianyitian.hope2.analyzer.funds.model.StockList;
import com.qianyitian.hope2.analyzer.model.*;
import com.qianyitian.hope2.analyzer.service.DefaultStockService;
import com.qianyitian.hope2.analyzer.service.IReportStorageService;
import com.qianyitian.hope2.analyzer.service.MyFavoriteStockService;
import com.qianyitian.hope2.analyzer.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class FundsController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource(name = "fileSystemStorageService")
    private IReportStorageService reportService;

    @Autowired
    private MyFavoriteStockService favoriteStockService;
    @Autowired
    PropertyConfig propertyConfig;

    public FundsController() {
    }

    @RequestMapping("/generateFundPositionStatusReport")
    @CrossOrigin
    public void generateFundPositionStatusReport() {
        Map<String, PositionStatus> map = new HashMap<>();
        List<Stock> funds = favoriteStockService.getSymbols("FUNDS").getSymbols();

        removeDulplicatedFunds(funds);

//        List<Stock> funds = getAllFundCode();
        for (Stock fund : funds) {
            try {
                String content = favoriteStockService.getFund(fund.getCode());
                if (content == null) {
                    continue;
                }
                Fund fundDetail = JSON.parseObject(content, Fund.class);

                if (fundDetail.getData() == null || fundDetail.getData().getFundPosition() == null || fundDetail.getData().getFundPosition().getStockList() == null) {
                    continue;
                }

                double assetValue = fundDetail.getData().getFundPosition().getAssetVal();

                List<StockList> stockList = fundDetail.getData().getFundPosition().getStockList();
                for (StockList stock : stockList) {
                    String key = stock.getCode();
                    if (map.containsKey(key)) {
                        PositionStatus status = map.get(key);
                        status.number = status.number + 1;
                        status.value = status.value + stock.getPercent() * assetValue;
                    } else {
                        PositionStatus status = new PositionStatus();
                        status.code = stock.getCode();
                        status.name = stock.getName();
                        status.number = 1;
                        status.value = stock.getPercent() * assetValue;
                        map.put(key, status);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        List<PositionStatus> numberslist = sortMapByNumbers(map);

//        List<PositionStatus> valueslist = sortMapByValues(map);

        Map result = new HashMap();
        result.put("numbers", numberslist);
//        result.put("values", valueslist);
        result.put("generateTime", LocalDate.now().toString());
        String conent = JSON.toJSONString(result);
        reportService.storeAnalysis("fundPosition-20201231", conent);
    }

    @RequestMapping("/funds")
    @CrossOrigin
    public String funds() {
        String report = reportService.getAnalysis("fundPosition-20201231");
        return report;
    }

    private void removeDulplicatedFunds(List<Stock> funds) {
        Set<String> set = new HashSet<>();
        for (Stock fund : funds) {
            if (fund.getName().endsWith("A")) {
                String name = fund.getName().substring(0, fund.getName().length() - 1);
                set.add(name);
            }
        }

        Iterator<Stock> iterator = funds.iterator();
        while (iterator.hasNext()) {
            Stock stock = iterator.next();
            if (stock.getName().endsWith("C")) {
                String name = stock.getName().substring(0, stock.getName().length() - 1);
                if (set.contains(name)) {
                    iterator.remove();
                }
            }
        }
    }

    public List<Stock> getAllFundCode() {
        File parent = new File(getRootPath(), "funds");
        List<Stock> list = new LinkedList<>();
        for (File file : parent.listFiles()) {
            Stock s = new Stock();
            s.setCode(file.getName().substring(0, 6));
            list.add(s);
        }


        return list;
    }

    private String getRootPath() {
        return propertyConfig.getDataPath();
    }

    public List<PositionStatus> sortMapByValues(Map<String, PositionStatus> aMap) {
        List<PositionStatus> collect = aMap.entrySet()
                .stream()
                .sorted((p1, p2) -> (int) (p2.getValue().value - p1.getValue().value)).map(entry -> {
                    double value = entry.getValue().getValue();
                    entry.getValue().setValue((long) value);
                    return entry.getValue();
                })
                .collect(Collectors.toList());
        return collect;
    }

    public List<PositionStatus> sortMapByNumbers(Map<String, PositionStatus> aMap) {
        List<PositionStatus> collect = aMap.entrySet()
                .stream()
                .sorted((p1, p2) -> p2.getValue().number - p1.getValue().number).map(entry -> {
                    double value = entry.getValue().getValue();
                    entry.getValue().setValue((long) value);
                    return entry.getValue();
                })
                .collect(Collectors.toList());
        return collect;
    }
}

class PositionStatus {
    String code;
    String name;
    int number = 0;
    double value = 0;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
