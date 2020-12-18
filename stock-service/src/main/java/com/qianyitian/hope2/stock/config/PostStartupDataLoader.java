package com.qianyitian.hope2.stock.config;

import com.google.common.io.Files;
import com.qianyitian.hope2.stock.dao.IStockDAO;
import com.qianyitian.hope2.stock.model.Stock;
import com.qianyitian.hope2.stock.search.EffectiveWordMatcher;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    }
}