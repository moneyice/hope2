package com.qianyitian.hope2.stock.dao;

import com.alibaba.fastjson.JSON;
import com.google.common.io.Files;
import com.qianyitian.hope2.stock.model.KLineInfo;
import com.qianyitian.hope2.stock.model.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;


@Repository("stockDAO4FileSystem")
//@RefreshScope
public class StockDAO4FileSystem implements IStockDAO {
    public static final String DAILY_LITE_FILE_SUFFIX = "." + IStockDAO.TYPE_DAILY_LITE;
    public static final String WEEKLY_FILE_SUFFIX = "." + IStockDAO.TYPE_WEEKLY;
    public static final String MONTHLY_FILE_SUFFIX = "." + IStockDAO.TYPE_MONTHLY;
    public static final int LITE_LEAST_DAY_NUMBER = 12 * 22;// about one year

    public StockDAO4FileSystem() {
        File file = new File(getRootPath());
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(getRootPath() + IStockDAO.TYPE_WEEKLY);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(getRootPath() + IStockDAO.TYPE_MONTHLY);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(getRootPath() + IStockDAO.TYPE_DAILY_LITE);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(getRootPath() + IStockDAO.TYPE_DAILY);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private String getRootPath() {
        return "./data/";
    }

    @Override
    public void storeAllSymbols(List<Stock> list) {
        File rootFolder = new File(getRootPath());
        if (!rootFolder.exists()) {
            rootFolder.mkdirs();
        }
        String allSymbols = JSON.toJSONString(list);
        File to = new File(getRootPath(), "allSymbols");
        try {
            Files.asCharSink(to, Charset.forName("UTF-8")).write(allSymbols);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void storeStock(Stock stock) {
        String jsonStock = JSON.toJSONString(stock);
        File to = new File(getRootPath() + IStockDAO.TYPE_DAILY, stock.getCode());
        try {
            Files.asCharSink(to, Charset.forName("UTF-8")).write(jsonStock);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void storeStockLite(Stock stock) {
        List<KLineInfo> oldArray = stock.getkLineInfos();

        if (oldArray.size() > LITE_LEAST_DAY_NUMBER) {
            //只保留最近一年的
            List<KLineInfo> subList = oldArray.subList(oldArray.size() - LITE_LEAST_DAY_NUMBER, oldArray.size());
            stock.setkLineInfos(subList);
        }
        String jsonStock = JSON.toJSONString(stock);
        File to = new File(getRootPath() + IStockDAO.TYPE_DAILY_LITE, stock.getCode() + DAILY_LITE_FILE_SUFFIX);
        try {
            Files.asCharSink(to, Charset.forName("UTF-8")).write(jsonStock);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void storeStockWeeklyInfo(Stock stock) {
        String jsonStock = JSON.toJSONString(stock);
        File to = new File(getRootPath() + IStockDAO.TYPE_WEEKLY, stock.getCode() + WEEKLY_FILE_SUFFIX);
        try {
            Files.asCharSink(to, Charset.forName("UTF-8")).write(jsonStock);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void storeStockMonthlyInfo(Stock stock) {
        String jsonStock = JSON.toJSONString(stock);
        File to = new File(getRootPath() + IStockDAO.TYPE_MONTHLY, stock.getCode() + MONTHLY_FILE_SUFFIX);
        try {
            Files.asCharSink(to, Charset.forName("UTF-8")).write(jsonStock);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Stock getStock(String code) {
        File from = new File(getRootPath() + IStockDAO.TYPE_DAILY, code);
        String rs;
        try {
            rs = Files.asCharSource(from, Charset.forName("UTF-8")).readFirstLine();
            Stock stock = JSON.parseObject(rs.toString(), Stock.class);
            return stock;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Stock getStockLite(String code) {
        File from = new File(getRootPath() + IStockDAO.TYPE_DAILY_LITE, code + DAILY_LITE_FILE_SUFFIX);
        String rs;
        try {
            rs = Files.asCharSource(from, Charset.forName("UTF-8")).readFirstLine();
            Stock stock = JSON.parseObject(rs.toString(), Stock.class);
            return stock;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("code error" + code);

        }
        return null;
    }

    @Override
    public Stock getStockWeeklyInfo(String code) {
        File from = new File(getRootPath() + IStockDAO.TYPE_WEEKLY, code + WEEKLY_FILE_SUFFIX);
        String rs;
        try {
            rs = Files.asCharSource(from, Charset.forName("UTF-8")).readFirstLine();
            Stock stock = JSON.parseObject(rs.toString(), Stock.class);
            return stock;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("code error" + code);

        }
        return null;
    }

    @Override
    public Stock getStockMonthlyInfo(String code) {
        File from = new File(getRootPath() + IStockDAO.TYPE_MONTHLY, code + MONTHLY_FILE_SUFFIX);
        String rs;
        try {
            rs = Files.asCharSource(from, Charset.forName("UTF-8")).readFirstLine();
            Stock stock = JSON.parseObject(rs.toString(), Stock.class);
            return stock;
        } catch (IOException e) {
            System.out.println("code error" + code);
        }
        return null;
    }


    @Override
    public List<Stock> getAllSymbols() {
        File rootFolder = new File(getRootPath());
        if (!rootFolder.exists()) {
            rootFolder.mkdirs();
        }
        File from = new File(getRootPath(), "allSymbols");
        if (!from.exists()) {
            return null;
        }
        String rs;
        try {
            rs = Files.asCharSource(from, Charset.forName("UTF-8")).readFirstLine();
            List<Stock> stocks = JSON.parseArray(rs.toString(), Stock.class);
            return stocks;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Date getStockUpdateTime(String code) {
        File file = new File(getRootPath(), code);
        long time = file.lastModified();
        Date lastDate = new Date(time);
        return lastDate;
    }


    @Override
    public Date getAllSymbolsUpdateTime() {
        File file = new File(getRootPath(), "allSymbols");
        long time = file.lastModified();
        Date lastDate = new Date(time);
        return lastDate;
    }
}
