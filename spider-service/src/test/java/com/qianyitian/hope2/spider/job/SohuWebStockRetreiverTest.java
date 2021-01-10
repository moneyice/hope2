package com.qianyitian.hope2.spider.job;

import com.qianyitian.hope2.spider.model.ETF;
import com.qianyitian.hope2.spider.model.Stock;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SohuWebStockRetreiverTest {

    @Test
    void getStockInfo() {
        SohuWebStockRetreiver retreiver = new SohuWebStockRetreiver();

        Arrays.stream(ETF.values()).forEach(etf -> {
            Stock stock = new Stock();
            stock.setCode(etf.getCode());
            try {
                stock = retreiver.getStockInfo(stock);
                System.out.println(stock);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


    }
}