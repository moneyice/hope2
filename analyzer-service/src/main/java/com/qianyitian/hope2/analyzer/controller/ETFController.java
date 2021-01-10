package com.qianyitian.hope2.analyzer.controller;

import com.alibaba.fastjson.JSON;
import com.qianyitian.hope2.analyzer.analyzer.DemarkAnalyzer;
import com.qianyitian.hope2.analyzer.analyzer.EStockAnalyzer;
import com.qianyitian.hope2.analyzer.analyzer.StockAnalyzerFacotry;
import com.qianyitian.hope2.analyzer.config.Constant;
import com.qianyitian.hope2.analyzer.model.*;
import com.qianyitian.hope2.analyzer.service.DefaultStockService;
import com.qianyitian.hope2.analyzer.service.IReportStorageService;
import com.qianyitian.hope2.analyzer.service.MyFavoriteStockService;
import com.qianyitian.hope2.analyzer.service.StockSelecter;
import com.qianyitian.hope2.analyzer.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class ETFController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DefaultStockService stockService;

    @Autowired
    private MyFavoriteStockService favoriteStockService;

    public ETFController() {
    }

    @RequestMapping("/etf")
    @CrossOrigin
    public List<ETFStatus> etf() {
        List<ETFStatus> list = new LinkedList();

        Arrays.stream(ETF.values()).parallel().forEach(etf -> {
            Stock stock = stockService.getStockWeekly(etf.getCode());

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
            List<ETFStatus> resultList = list.parallelStream().sorted(Comparator.comparing(ETFStatus::getThisWeekRange).reversed()).collect(Collectors.toList());
            for (int i = 0; i < resultList.size(); i++) {
                if (i < 6) {
                    resultList.get(i).setThisHold("Y");
                }
                resultList.get(i).setThisRank(i + 1);
            }
        }
        {
            List<ETFStatus> resultList = list.parallelStream().sorted(Comparator.comparing(ETFStatus::getPreviousWeekRange).reversed()).collect(Collectors.toList());
            for (int i = 0; i < resultList.size(); i++) {
                if (i < 6) {
                    resultList.get(i).setPreviousHold("Y");
                }
                resultList.get(i).setPreviousRank(i + 1);
            }
        }

        return list;
    }
}
