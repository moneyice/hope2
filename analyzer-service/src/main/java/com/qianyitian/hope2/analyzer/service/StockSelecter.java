package com.qianyitian.hope2.analyzer.service;

import com.qianyitian.hope2.analyzer.analyzer.IStockAnalyzer;
import com.qianyitian.hope2.analyzer.model.*;
import com.qianyitian.hope2.analyzer.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StockSelecter {
    private Logger logger = LoggerFactory.getLogger(getClass());
    List<IStockAnalyzer> analyzers = new ArrayList<IStockAnalyzer>();
    List<ResultInfo> selectResultList = null;
    IStockService stockService;
    List<Stock> symbols;

    private boolean filterEmptyResult = true;


    public StockSelecter(List<Stock> symbols, IStockService stockService) {
        this.stockService = stockService;
        this.symbols = symbols;
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
        logger.info("ending analyzing " + klineType + " " + analyzers.get(0).getClass().getName());
    }


    public void startAnalyzeInSequence(String klineType) {
        selectResultList = new ArrayList<ResultInfo>();
        for (Stock stock : getSymbols()) {
            stock = getStock(stock.getCode(), klineType);
            if (stock != null && !outOfDate(stock)) {
                ResultInfo resultInfo = analyze(stock);
                if (resultInfo != null) {
                    selectResultList.add(resultInfo);
                } else if (!filterEmptyResult) {
                    selectResultList.add(createEmptyResultInfo(stock));
                }
            }
        }
    }

    public Stock getStock(String symbol, String klineType) {
        return stockService.getStock(symbol, klineType);
    }

    private ResultInfo createEmptyResultInfo(Stock stock) {
        ResultInfo resultInfo = new ResultInfo();
        resultInfo.setCode(stock.getCode());
        resultInfo.setName(stock.getName());
        resultInfo.setUrl(Utils.convert163StockURL(stock.getCode()));
        Map map = new HashMap<>();
        List<DemarkFlag> list = new LinkedList();
        map.put("flag", list);
        resultInfo.setData(map);
        return resultInfo;
    }

    private boolean outOfDate(Stock stock) {
        List<KLineInfo> list = stock.getkLineInfos();
        if (list.isEmpty()) {
            return true;
        }

        KLineInfo lastOne = list.get(list.size() - 1);
        //过期1年的就不要了
        return lastOne.getDate().plusDays(250).isBefore(LocalDate.now());
    }

    private ResultInfo analyze(Stock stock) {
        ResultInfo resultInfo = new ResultInfo();
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
        resultInfo.setCode(stock.getCode());
        resultInfo.setName(stock.getName());
        //get stock url
        //http://quotes.money.163.com/0601899.html
        resultInfo.setUrl(Utils.convert163StockURL(stock.getCode()));
        return resultInfo;
    }

    public ResultInfo analyze(IStockAnalyzer analyzer, Stock stock) {
        ResultInfo resultInfo = new ResultInfo();
        resultInfo.setCode(stock.getCode());
        resultInfo.setName(stock.getName());

        try {
            if (!analyzer.analyze(resultInfo, stock)) {
                resultInfo = null;
                return resultInfo;
            }
        } catch (Exception e) {
            logger.error("analyzer " + analyzer.getClass().getName() + " stock " + stock.getCode(), e);
        }

        //get stock url
        //http://quotes.money.163.com/0601899.html
        resultInfo.setUrl(Utils.convert163StockURL(stock.getCode()));
        return resultInfo;
    }

    public void setFilterEmptyResult(boolean filterEmptyResult) {
        this.filterEmptyResult = filterEmptyResult;
    }

    public List<Stock> getSymbols() {
        return symbols;
    }

    public void setSymbols(List<Stock> symbols) {
        this.symbols = symbols;
    }
}
