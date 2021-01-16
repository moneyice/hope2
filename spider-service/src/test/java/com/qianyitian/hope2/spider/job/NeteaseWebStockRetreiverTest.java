package com.qianyitian.hope2.spider.job;

import com.qianyitian.hope2.spider.model.Stock;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class NeteaseWebStockRetreiverTest {

    @Test
    void getStockInfo() throws IOException {
        NeteaseWebStockRetreiver ns=new NeteaseWebStockRetreiver();
        Stock stock =new Stock();
        stock.setName("中芯国际");
        stock.setCode("688981");
        stock.setMarket("SH");
        Stock stockInfo = ns.getStockInfo(stock);
        System.out.println(stockInfo);


    }
}