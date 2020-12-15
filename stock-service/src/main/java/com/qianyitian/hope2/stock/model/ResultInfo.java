package com.qianyitian.hope2.stock.model;

public class ResultInfo {
    Stock stock;

    String msg = "";

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public String getMsg() {
        return msg;
    }

    public void appendMessage(String msg) {
        this.msg = this.msg + "|" + msg;
    }
}
