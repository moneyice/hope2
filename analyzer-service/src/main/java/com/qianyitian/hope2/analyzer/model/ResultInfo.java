package com.qianyitian.hope2.analyzer.model;

import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResultInfo {
    String code;
    String name;
    String msg = "";
    String url;
    List<String> buyPositions = null;

    public List<String> getBuyPositions() {
        return buyPositions;
    }

    public void setBuyPositions(List<String> buyPositions) {
        this.buyPositions = buyPositions;
    }



    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMsg() {
        return msg;
    }



    public void appendMessage(String msg) {
        if (StringUtils.isEmpty(this.msg)) {
            this.msg = msg;
        } else {
            this.msg = this.msg + msg;
        }
    }
}
