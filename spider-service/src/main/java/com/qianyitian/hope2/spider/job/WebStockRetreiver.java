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

        File f=new File(".");
        System.out.println(f.getAbsoluteFile());

        List<String> lines = Files.readLines(new File("spider-service/data/SZ.stock.list.csv"), Charset.forName("UTF-8"));
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] array = line.split(",");

            String market = "SZ";
            String symbol = array[2];
            String name = array[3];
            logger.info(market + "  " + symbol + "  " + name);
            Stock stock = new Stock();
            stock.setName(name);
            stock.setCode(symbol);
            stock.setMarket(market);
            list.add(stock);
        }
        return list;


//        Document doc = Jsoup.connect(codeListHTML).get();
//        Elements codeList = doc.select("#quotesearch ul li a");
//        StockFilter sf = new StockFilter();
//        for (Element element : codeList) {
//            String text = element.text();
//            String linkHref = element.attr("href"); // http://quote.eastmoney.com/sz300409.html
//            String market = linkHref.substring(27, 29).toUpperCase();
//            String symbol = linkHref.substring(29, 35);
//            String name = text.substring(0, text.length() - 8);
//            logger.info(market + "  " + symbol + "  " + name);
//            if (sf.select(symbol)) {
//                Stock stock = new Stock();
//                stock.setName(name);
//                stock.setCode(symbol);
//                stock.setMarket(market);
//                list.add(stock);
//            }
//        }
//        return list;
    }
}
