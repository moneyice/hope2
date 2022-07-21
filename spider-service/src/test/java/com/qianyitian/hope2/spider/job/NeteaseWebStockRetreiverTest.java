package com.qianyitian.hope2.spider.job;

import com.google.common.io.Resources;
import com.qianyitian.hope2.spider.fetcher.NeteaseWebStockRetreiver;
import com.qianyitian.hope2.spider.model.Stock;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.MessageFormat;

class NeteaseWebStockRetreiverTest {

    @Test
    void getStockInfo() throws IOException {
        NeteaseWebStockRetreiver ns = new NeteaseWebStockRetreiver();
        Stock stock = new Stock();
        stock.setName("中芯国际");
        stock.setCode("688981");
        stock.setMarket("SH");
        Stock stockInfo = ns.fetchStockInfo(stock);
        System.out.println(stockInfo);


    }

    @Test
    void getDayInfo() throws IOException {
        String URL2 = "https://img1.money.126.net/data/hs/time/today/{0}{1}.json";

        String code = "515220";
        String market = code.startsWith("5") ? "0" : "1";
        String url2 = MessageFormat.format(URL2, market, code);
        System.out.println(url2);
        String content = Resources.toString(new URL(url2), Charset.defaultCharset());
        System.out.println(content);

    }


}