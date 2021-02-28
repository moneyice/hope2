package com.qianyitian.hope2.spider.job;


import com.qianyitian.hope2.spider.external.DanjuanClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component("danjuanFundsRetreiver")
public class DanjuanFundsRetreiver extends WebStockRetreiver {

    @Autowired
    private DanjuanClient danjuanClient;

    @Override
    public String getFundsInfo(String code) throws IOException {
        String content = danjuanClient.getFundDetail(code);
        return content;
    }


}
