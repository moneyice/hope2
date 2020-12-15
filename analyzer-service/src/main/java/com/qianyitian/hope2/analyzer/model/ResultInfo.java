package com.qianyitian.hope2.analyzer.model;

import org.springframework.util.StringUtils;

public class ResultInfo {
    //Stock stock;

    String msg = "";
    String url;

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    //    public Stock getStock() {
//        return stock;
//    }
//
//    public void setStock(Stock stock) {
//        this.stock = stock;
//    }

    public String getMsg() {
        return msg;
    }

    public void appendMessage(String msg) {
        if (StringUtils.isEmpty(this.msg)) {
            this.msg = msg;
        } else {
            this.msg = this.msg + "|" + msg;
        }
    }
}
