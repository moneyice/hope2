package com.qianyitian.hope2.spider.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.io.Resources;
import com.qianyitian.hope2.spider.config.PropertyConfig;
import com.qianyitian.hope2.spider.external.DanjuanClient;
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
    private DanjuanClient danjuanClient;

    @Override
    public String getFundsInfo(String code) throws IOException {
        String content = danjuanClient.getFundDetail(code);
        return content;
    }


}
