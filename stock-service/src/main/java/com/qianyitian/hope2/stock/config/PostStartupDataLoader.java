package com.qianyitian.hope2.stock.config;

import com.google.common.io.Files;
import com.qianyitian.hope2.stock.dao.IStockDAO;
import com.qianyitian.hope2.stock.model.KLineInfo;
import com.qianyitian.hope2.stock.model.Stock;
import com.qianyitian.hope2.stock.search.EffectiveWordMatcher;
import com.qianyitian.hope2.stock.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class PostStartupDataLoader implements ApplicationRunner {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    EffectiveWordMatcher matcher;

    @Resource(name = "stockDAO4FileSystem")
    private IStockDAO stockDAO;

    @Autowired
    PropertyConfig propertyConfig;

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        init();
        loadAllStockSymbols();
        matcher.init();
        loadResources("oil.csv", "OIL 石油", "oil");
        loadResources("gold.csv", "GOLD 黄金", "gold");
        loadResources("silver.csv", "SILVER 白银", "silver");
        loadResources("bitcoin.csv", "Bitcoin 比特币", "bitcoin");
    }
    public void loadAllStockSymbols() throws IOException {
        File from = new File(getRootPath(), "allSymbols");
        if (from.exists()) {
            return;
        }
        List<Stock> list = new ArrayList<Stock>();
        loadSZStockSymbols(list);
        loadSHStockSymbols(list);
        stockDAO.storeAllSymbols(list);
    }

    private String getRootPath() {
        return propertyConfig.getDataPath();
    }

    private void loadSZStockSymbols(List<Stock> list) throws IOException {
        List<String> lines = Files.readLines(new File(getRootPath(), "SZ.stock.list.csv"), Charset.forName("UTF-8"));
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
        List<String> lines = Files.readLines(new File(getRootPath(), "SH.stock.list.csv"), Charset.forName("UTF-8"));
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] array = line.split(",");

            try{
                String market = "SH";
                String symbol = array[0].trim();
                String name = array[1].trim();
                logger.info(market + "  " + symbol + "  " + name);
                Stock stock = new Stock();
                stock.setName(name);
                stock.setCode(symbol);
                stock.setMarket(market);
                list.add(stock);
            }catch(Exception e){
                e.printStackTrace();
            }


        }
    }
    private void loadOil() throws IOException {

    }


    private void loadResources(String filename, String Name, String code) throws IOException {
//        String csvSplitBy = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
        String csvSplitBy = ",\\s*(?![^\"]*\"\\,)";
        DateTimeFormatter formatters = formatters = DateTimeFormatter.ofPattern("yyyy年M月d日").withLocale(Locale.SIMPLIFIED_CHINESE);//Oct 11, 2019
        List<String> lines = Files.readLines(new File(getRootPath(), filename), Charset.forName("UTF-8"));
        Stock stock = new Stock();
        stock.setName(Name);
        stock.setCode(code);
        stock.setkLineType(EStockKlineType.DAILY.getName());
        LinkedList<KLineInfo> kLineInfos = new LinkedList<>();
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] array = line.split(csvSplitBy);
            LocalDate date = LocalDate.parse(array[0].trim().replaceAll("\"", ""), formatters);
            String close = array[1].replaceAll("\"", "");
            String open = array[2].trim().replaceAll("\"", "");
            String high = array[3].trim().replaceAll("\"", "");
            String low = array[4].trim().replaceAll("\"", "");
            KLineInfo kline = new KLineInfo();
            kline.setDate(date);
            kline.setClose(Utils.handleDouble(close));
            kline.setOpen(Utils.handleDouble(open));
            kline.setHigh(Utils.handleDouble(high));
            kline.setLow(Utils.handleDouble(low));
            kLineInfos.add(0, kline);
        }
        stock.setkLineInfos(kLineInfos);
        stockDAO.storeStock(stock);
    }

    public void init() {
        File file = new File(getRootPath());
        if (!file.exists()) {
            file.mkdirs();
        }

        Arrays.stream(EStockKlineType.values()).forEach(eStockKlineType -> {
            File folder = new File(getRootPath(), eStockKlineType.folderName);
            if (!folder.exists()) {
                folder.mkdirs();
            }
        });

        File folder = new File(getRootPath(), Constant.STATISTICS);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        folder = new File(getRootPath(), Constant.REPORT);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        folder = new File(getRootPath(), Constant.FUNDS);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }
}