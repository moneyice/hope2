package com.qianyitian.hope2.spider.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.io.Resources;
import com.qianyitian.hope2.spider.config.PropertyConfig;
import com.qianyitian.hope2.spider.model.KLineInfo;
import com.qianyitian.hope2.spider.model.Stock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

@Component("danjuanFundsRetreiver")
public class DanjuanFundsRetreiver extends WebStockRetreiver {
    @Autowired
    PropertyConfig propertyConfig;

    private Logger logger = LoggerFactory.getLogger(getClass());

    //这里面包含财报信息，所以每个季度更新一次即可
    String FUND_URL = "https://danjuanfunds.com/djapi/fund/detail/{0}";

    @Override
    public String getFundsInfo(String symbol) throws IOException {
        //这家的api很破，一次只能取一年的
        String url = MessageFormat.format(FUND_URL, symbol);

        return readData(url);
    }

    protected String readData(String url) throws IOException {
        try {
            URL danjuanURL = new URL(url);
            String content = Resources.asCharSource(danjuanURL, Charset.forName("UTF-8")).readFirstLine();
            return content;
        } catch (IOException e) {
            logger.error("retrieve danjuan funds data error ", e);
            throw e;
        }
    }
}
