package com.qianyitian.hope2.spider.job;

import com.alibaba.fastjson.JSON;
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
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@Component("neteaseWebStockRetreiver")
public class NeteaseWebStockRetreiver extends WebStockRetreiver {
    private Logger logger = LoggerFactory.getLogger(getClass());
    //http://img1.money.126.net/data/hs/kline/month/times/1000001.json
//         http://quotes.money.163.com/service/chddata.html?code=0600000&start=20140101&end=20151231&fields=TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP
//    CHG  涨跌额度
//    PCHG 涨跌幅度
//    TURNOVER 换手率
//    VOTURNOVER 成交量
//    VATURNOVER 成交金额
//    TCAP 总市值
//    MCAP 流通市值


    String URL = "http://quotes.money.163.com/service/chddata.html?code=#{symbol}&fields=TOPEN;HIGH;LOW;TCLOSE;VOTURNOVER;TURNOVER;PCHG";

    @Override
    public Stock getStockInfo(Stock stock) throws IOException {
        String symbol = null;
        String stockJsonString = JSON.toJSONString(stock);
        stock = JSON.parseObject(stockJsonString, Stock.class);

        if (stock.getMarket().equals("SH")) {
            symbol = "0" + stock.getCode();
        } else {
            symbol = "1" + stock.getCode();
        }

        String url = URL.replace("#{symbol}", symbol);
        List<KLineInfo> kLineInfos = new LinkedList<KLineInfo>();
        try {
            // Retrieve CSV File
            URL neteaseUrl = new URL(url);
            List<String> list = Resources.readLines(neteaseUrl,
                    Charset.forName("UTF-8"));
            // pass the first head line
            // 日期 股票代码 名称 开盘价 最高价 最低价 收盘价 成交量 换手率 涨跌幅
            // 2015-1-8 '600000 浦发银行 15.87 15.88 15.2 15.25 330627172 0.11 2.2
            // the date of netease money is desc
            // First line is header, pass it.
            for (int i = list.size() - 1; i > 0; i--) {
                KLineInfo daily = new KLineInfo();
                String line = null;
                try {
                    line = list.get(i).replaceAll("\"", "");
                    String[] result = line.split(",");
                    double open = Utils.handleDouble(result[3]);
                    if (open < 0.01) {
                        continue;
                    }
                    double high = Utils.handleDouble(result[4]);
                    double low = Utils.handleDouble(result[5]);
                    double close = Utils.handleDouble(result[6]);
                    //long volume = Utils.handleLong(result[7]);
                    double turnoverRate;
                    try {
                        turnoverRate = Utils.handleDouble(result[8]);
                    } catch (Exception e) {
                        turnoverRate = 0.0;
                    }
                    double changePercent = 0;
                    try {
                        changePercent = Utils.handleDouble(result[9]);
                    } catch (Exception e) {
                        changePercent = 0.0;
                    }

                    LocalDate date = Utils.parseDate(result[0]);
                    daily.setOpen(open);
                    daily.setHigh(high);
                    daily.setLow(low);
                    daily.setClose(close);
                    daily.setDate(date);
                    daily.setTurnoverRate(turnoverRate);
                    daily.setChangePercent(changePercent);
                } catch (Exception e) {
                    logger.error("retrieve 163 stock data error  " + symbol + line, e);
                }
                // by asc order
                kLineInfos.add(daily);
            }
            stock.setkLineInfos(kLineInfos);
            stock.setkLineType(Stock.TYPE_DAILY);
        } catch (IOException e) {
            logger.error("retrieve 163 stock data error ", e);
            throw e;
        }
        return stock;
    }


}
