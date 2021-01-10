package com.qianyitian.hope2.spider.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.io.CharSource;
import com.google.common.io.Resources;
import com.qianyitian.hope2.spider.model.KLineInfo;
import com.qianyitian.hope2.spider.model.Stock;
import com.qianyitian.hope2.spider.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

@Component("sohuWebStockRetreiver")
public class SohuWebStockRetreiver extends WebStockRetreiver {
    private Logger logger = LoggerFactory.getLogger(getClass());
    String URL = "https://q.stock.sohu.com/hisHq?code=cn_{0}&start={1}&end={2}&stat=1&order=A&period=d";
    String start = "20190101";

    @Override
    public Stock getStockInfo(Stock stock) throws IOException {
        String end = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String url = MessageFormat.format(URL, stock.getCode(), start, end);

//        String stockJsonString = JSON.toJSONString(stock);
//        stock = JSON.parseObject(stockJsonString, Stock.class);
        List<KLineInfo> kLineInfos = new LinkedList<KLineInfo>();
        stock.setkLineType(Stock.TYPE_DAILY);
        try {
            URL neteaseUrl = new URL(url);
            String content = Resources.asCharSource(neteaseUrl, Charset.forName("UTF-8")).readFirstLine();
            JSONArray jsonArray = JSON.parseArray(content);
            jsonArray = jsonArray.getJSONObject(0).getJSONArray("hq");
            jsonArray.stream().forEach(jsonObject -> {
                JSONArray kInfo=(JSONArray)jsonObject;
                String dateString=kInfo.getString(0);
                double open=kInfo.getDouble(1);
                double close=kInfo.getDouble(2);
                double low=kInfo.getDouble(5);
                double high=kInfo.getDouble(6);

                KLineInfo daily = new KLineInfo();
                LocalDate date = Utils.parseDate(dateString);
                daily.setOpen(open);
                daily.setHigh(high);
                daily.setLow(low);
                daily.setClose(close);
                daily.setDate(date);
                kLineInfos.add(daily);
//                daily.setTurnoverRate(turnoverRate);
//                daily.setChangePercent(changePercent);
            });
            stock.setkLineInfos(kLineInfos);
        } catch (IOException e) {
            logger.error("retrieve sohu stock data error ", e);
            throw e;
        }
        return stock;
    }
}
