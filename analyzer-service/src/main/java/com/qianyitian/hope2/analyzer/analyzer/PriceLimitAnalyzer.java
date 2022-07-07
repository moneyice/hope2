package com.qianyitian.hope2.analyzer.analyzer;

import com.qianyitian.hope2.analyzer.model.KLineInfo;
import com.qianyitian.hope2.analyzer.model.ResultInfo;
import com.qianyitian.hope2.analyzer.model.Stock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PriceLimitAnalyzer extends AbstractStockAnalyzer {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private double maxPrice = 10;
    private double minPrice = 0;

    public PriceLimitAnalyzer() {
    }

    @Override
    public boolean analyze(ResultInfo resultInfo, Stock stock) {
        boolean ok = false;

        if(stock.getkLineInfos()==null||stock.getkLineInfos().isEmpty()){
            logger.warn(stock.getCode()+ " has no kline data");
            return ok;
        }

        KLineInfo toCheck = stock.getkLineInfos().get(
                stock.getkLineInfos().size() - 1);

        if (toCheck.getClose() >= minPrice && toCheck.getClose() <= maxPrice) {
            ok = true;
            resultInfo.appendMessage("");
        }
        return ok;
    }

    private String format(Stock stock, KLineInfo check, double times) {
        return "";
    }
    public int getWeight() {
        return 50;
    }

    @Override
    public String getDescription() {
        return null;
    }
}
