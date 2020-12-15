package com.qianyitian.hope2.analyzer.analyzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by bing.a.qian on 2/6/2017.
 */
public class StockAnalyzerFacotry {
    private static Logger logger = LoggerFactory.getLogger(StockAnalyzerFacotry.class);
    public static IStockAnalyzer createStockAnalyzer(EStockAnalyzer analyzerEnum) {
        String fullQualifiedName = analyzerEnum.getFullQualifiedClassName();
        try {
            return (IStockAnalyzer) Class.forName(fullQualifiedName).newInstance();
        } catch (Exception e) {
            logger.error("createStockAnalyzer error",e);
        }
        return null;
    }

    public static IStockAnalyzer createStockAnalyzer(String analyzerName) {
        EStockAnalyzer analyzerEnum = EStockAnalyzer.valueOf(analyzerName);
        return createStockAnalyzer(analyzerEnum);
    }


}
