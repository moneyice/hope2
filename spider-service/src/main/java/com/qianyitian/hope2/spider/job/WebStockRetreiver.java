package com.qianyitian.hope2.spider.job;


import com.google.common.io.Files;
import com.qianyitian.hope2.spider.model.Stock;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public abstract class WebStockRetreiver implements IStockRetreiver {
    private Logger logger = LoggerFactory.getLogger(getClass());
    String codeListHTML = "http://quote.eastmoney.com/stock_list.html";


    @Override
    public List<Stock> getAllStockSymbols() throws IOException {
        List<Stock> list = new ArrayList<Stock>();
        loadSZStockSymbols(list);
        loadSHStockSymbols(list);
        return list;
    }

    private void loadSZStockSymbols(List<Stock> list) throws IOException {
        List<String> lines = Files.readLines(new File("spider-service/data/SZ.stock.list.csv"), Charset.forName("UTF-8"));
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] array = line.split(",");

            String market = "SZ";
            String symbol = array[2].trim();
            String name = array[3].trim();
            logger.info(market + "  " + symbol + "  " + name);
            Stock stock = new Stock();
            stock.setName(name);
            stock.setCode(symbol);
            stock.setMarket(market);
            list.add(stock);
        }
    }
    private void loadSHStockSymbols(List<Stock> list) throws IOException {
        List<String> lines = Files.readLines(new File("spider-service/data/SH.stock.list.csv"), Charset.forName("UTF-8"));
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] array = line.split(",");

            String market = "SH";
            String symbol = array[0].trim();
            String name = array[1].trim();
            logger.info(market + "  " + symbol + "  " + name);
            Stock stock = new Stock();
            stock.setName(name);
            stock.setCode(symbol);
            stock.setMarket(market);
            list.add(stock);
        }
    }
}
