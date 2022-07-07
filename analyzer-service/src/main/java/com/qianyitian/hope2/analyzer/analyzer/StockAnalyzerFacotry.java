package com.qianyitian.hope2.analyzer.analyzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by bing.a.qian on 2/6/2017.
 */

@Component
public class StockAnalyzerFacotry {
    private static Logger logger = LoggerFactory.getLogger(StockAnalyzerFacotry.class);
    private Map<EStockAnalyzer, IStockAnalyzer> map = new HashMap(EStockAnalyzer.values().length);

    public StockAnalyzerFacotry() {
        Arrays.stream(EStockAnalyzer.values()).forEach(numStockAnalyzer -> {
            try {
                IStockAnalyzer stockAnalyzer = (IStockAnalyzer) Class.forName(numStockAnalyzer.getFullQualifiedClassName()).newInstance();
                map.put(numStockAnalyzer, stockAnalyzer);
            } catch (Exception e) {
                logger.error("createStockAnalyzer error", e);
            }
        });

//        Map<EStockAnalyzer, IStockAnalyzer> allAnalyzers = Arrays.stream(EStockAnalyzer.values()).collect(Collectors.toMap(numStockAnalyzer -> numStockAnalyzer, numStockAnalyzer -> {
//            try {
//                IStockAnalyzer stockAnalyzer = (IStockAnalyzer) Class.forName(numStockAnalyzer.getFullQualifiedClassName()).newInstance();
//                return stockAnalyzer;
//            } catch (Exception e) {
//                logger.error("createStockAnalyzer error", e);
//                throw new RuntimeException(e);
//            }
//        }));
//        map.putAll(allAnalyzers);
    }


    public IStockAnalyzer getStockAnalyzer(EStockAnalyzer analyzerEnum) {
        return map.get(analyzerEnum);
    }
}
