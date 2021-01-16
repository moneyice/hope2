package com.qianyitian.hope2.spider.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.io.Resources;
import com.qianyitian.hope2.spider.config.PropertyConfig;
import com.qianyitian.hope2.spider.model.KLineInfo;
import com.qianyitian.hope2.spider.model.Stock;
import com.qianyitian.hope2.spider.util.Utils;
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

@Component("guguDataWebStockRetreiver")
public class GuguDataWebStockRetreiver extends WebStockRetreiver {
    @Autowired
    PropertyConfig propertyConfig;

    private Logger logger = LoggerFactory.getLogger(getClass());
    //    DataStatus: {
//        RequestParameter: "symbol=BABA&beginDate=20200101&endDate=20210101",
//                StatusCode: 100,
//                StatusDescription: "请求成功",
//                ResponseDateTime: "2021-01-16 20:50:44",
//                DataTotalCount: 252
//    },
//    Data: [
//    {
//        Symbol: "BABA",
//                TimeKey: 20200102,
//            Open: 216.6,
//            Close: 219.77,
//            High: 219.98,
//            Low: 216.54,
//            Volume: 15873500
//    }
    String URL = "https://api.gugudata.com/stock/us?appkey={0}&symbol={1}&beginDate={2}&endDate={3}";

    @Override
    public Stock getStockInfo(Stock stock) throws IOException {
        stock.setkLineType(Stock.TYPE_DAILY);

        String symbol = stock.getCode();
        String appkey = propertyConfig.getGugudataAPIKey();

        //这家的api很破，一次只能取一年的
        String url = MessageFormat.format(URL, appkey, symbol, "20190101", "20191231");

        List<KLineInfo> all = readData(stock, url);
        url = MessageFormat.format(URL, appkey, symbol, "20200101", "20201231");
        List<KLineInfo> appendList = readData(stock, url);
        all.addAll(appendList);
        url = MessageFormat.format(URL, appkey, symbol, "20210101", "20211231");
        appendList = readData(stock, url);
        all.addAll(appendList);
        stock.setkLineInfos(all);
        return stock;
    }

    protected List<KLineInfo> readData(Stock stock, String url) throws IOException {
        List<KLineInfo> kLineInfos = new LinkedList<KLineInfo>();
        try {
            URL neteaseUrl = new URL(url);
            String content = Resources.asCharSource(neteaseUrl, Charset.forName("UTF-8")).readFirstLine();
            JSONObject jsonObject = JSON.parseObject(content);

            jsonObject.getJSONArray("Data").stream().forEach(obj -> {
                JSONObject kInfo = (JSONObject) obj;

                String dateString = kInfo.getString("TimeKey");
                double open = kInfo.getDouble("Open");
                double close = kInfo.getDouble("Close");
                double low = kInfo.getDouble("Low");
                double high = kInfo.getDouble("High");
                int volume = kInfo.getIntValue("Volume");
                KLineInfo daily = new KLineInfo();
                LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.BASIC_ISO_DATE);
                daily.setOpen(open);
                daily.setHigh(high);
                daily.setLow(low);
                daily.setClose(close);
                daily.setDate(date);
                daily.setVolume(volume);
                kLineInfos.add(daily);
            });
        } catch (IOException e) {
            logger.error("retrieve gugudata stock data error ", e);
            throw e;
        }
        return kLineInfos;
    }


}
