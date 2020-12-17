package com.qianyitian.hope2.spider.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PropertyConfig {

    @Value(value = "${hope2.service.spider}")
    private String spiderService;
    @Value(value = "${hope2.service.stock}")
    private String stockService;
    @Value(value = "${hope2.service.analyzer}")
    private String analyzerService;
    @Value(value = "${hope2.data.path}")
    private String dataPath;

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    public String getSpiderService() {
        return spiderService;
    }

    public void setSpiderService(String spiderService) {
        this.spiderService = spiderService;
    }

    public String getStockService() {
        return stockService;
    }

    public void setStockService(String stockService) {
        this.stockService = stockService;
    }

    public String getAnalyzerService() {
        return analyzerService;
    }

    public void setAnalyzerService(String analyzerService) {
        this.analyzerService = analyzerService;
    }
}
