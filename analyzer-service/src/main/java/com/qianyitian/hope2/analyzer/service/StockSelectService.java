package com.qianyitian.hope2.analyzer.service;

import com.alibaba.fastjson.JSON;
import com.qianyitian.hope2.analyzer.analyzer.IStockAnalyzer;
import com.qianyitian.hope2.analyzer.model.*;
import com.qianyitian.hope2.analyzer.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class StockSelectService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    List<IStockAnalyzer> analyzers = new ArrayList<IStockAnalyzer>();
    List<ResultInfo> selectResultList = null;
    ExecutorService es = Executors.newFixedThreadPool(15);

    StockService stockService;

    public StockSelectService(StockService stockService) {
        this.stockService = stockService;
    }

    public AnalyzeResult getAnalyzeResult() {
        AnalyzeResult ar = new AnalyzeResult();
        ar.setResultList(selectResultList);
        StringBuilder sb = new StringBuilder();
        for (IStockAnalyzer a : analyzers) {
            sb.append(a.getDescription()).append("\n");
        }
        ar.setDescription(sb.toString());
        ar.setGenerateTime(LocalDateTime.now().toString());
        return ar;
    }

    public void addAnalyzer(IStockAnalyzer analyzer) {
        analyzers.add(analyzer);
    }

    public void startAnalyze(String klineType) {
        logger.info("start analyzing " + klineType + " " + analyzers.get(0).getClass().getName());
        startAnalyzeInSequence(klineType);
//        startAnalyzeInParallel(klineType);

        long now = System.currentTimeMillis();
        logger.info("ending analyzing " + klineType + " " + analyzers.get(0).getClass().getName());
    }

    private void startAnalyzeInParallel(String klineType) {
        selectResultList = new CopyOnWriteArrayList<ResultInfo>();
        SymbolList allSymbols = getAllSymbols();
        for (Stock stock : allSymbols.getSymbols()) {
            Task task = new Task(stock.getCode(), klineType, selectResultList);
            es.execute(task);
        }
    }

    public void startAnalyzeInSequence(String klineType) {
        selectResultList = new ArrayList<ResultInfo>();
        SymbolList allSymbols = getAllSymbols();
        for (Stock stock : allSymbols.getSymbols()) {
            stock = stockService.getStock(stock.getCode(), klineType);
            if (stock != null && !outOfDate(stock)) {
                ResultInfo resultInfo = analyze(stock);
                if (resultInfo != null) {
                    selectResultList.add(resultInfo);
                }
            }
        }
    }

    private boolean outOfDate(Stock stock) {
        List<KLineInfo> list = stock.getkLineInfos();
        if (list.isEmpty()) {
            return true;
        }
        KLineInfo lastOne = stock.getkLineInfos().get(list.size() - 1);
        return lastOne.getDate().plusDays(5).isBefore(LocalDate.now());
    }

    private ResultInfo analyze(Stock stock) {
        ResultInfo resultInfo = new ResultInfo();
        resultInfo.appendMessage(stock.getCode() + " " + stock.getName());
        for (IStockAnalyzer analyzer : analyzers) {
            try {
                if (!analyzer.analyze(resultInfo, stock)) {
                    resultInfo = null;
                    return resultInfo;
                }
            } catch (Exception e) {
                logger.error("analyzer " + analyzer.getClass().getName() + " stock " + stock.getCode(), e);
            }

        }
        //get stock url
        //http://quotes.money.163.com/0601899.html
        resultInfo.setUrl(Utils.convert163StockURL(stock.getCode()));
        return resultInfo;
    }



    private SymbolList getAllSymbols() {
        SymbolList symbolList = stockService.getAllSymbols();
        return symbolList;
    }

    class Task implements Runnable {
        String code = null;
        List<ResultInfo> list = null;
        String klineType;

        public Task(String code, String klineType, List<ResultInfo> selectResultList) {
            this.code = code;
            this.list = selectResultList;
            this.klineType = klineType;
        }

        @Override
        public void run() {
            Stock stock = null;
            stock = stockService.getStock(code, klineType);
            if (stock != null) {
                ResultInfo resultInfo = analyze(stock);
                if (resultInfo != null) {
                    selectResultList.add(resultInfo);
                }
            }
        }
    }
}
