package com.qianyitian.hope2.spider.job;

import com.qianyitian.hope2.spider.model.Stock;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WebStockRetreiverTest {

    @Test
    void getAllStockSymbols() throws IOException {


        WebStockRetreiver wr=new WebStockRetreiver(){

            @Override
            public Stock getStockInfo(Stock stock) throws IOException {
                return null;
            }
        };


        List<Stock> allStockSymbols = wr.getAllStockSymbols();
        System.out.println(allStockSymbols);

    }
}