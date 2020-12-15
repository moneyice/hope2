package com.qianyitian.hope2.analyzer.model;

import java.util.List;

/**
 * Created by bing.a.qian on 9/20/2017.
 */
public class AnalyzeResult {
    String generateTime;
    List<ResultInfo> resultList = null;
    String description;


    public String getGenerateTime() {
        return generateTime;
    }

    public void setGenerateTime(String generateTime) {
        this.generateTime = generateTime;
    }

    public List<ResultInfo> getResultList() {
        return resultList;
    }

    public void setResultList(List<ResultInfo> resultList) {
        this.resultList = resultList;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
