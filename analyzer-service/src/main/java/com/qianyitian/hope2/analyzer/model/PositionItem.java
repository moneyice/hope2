package com.qianyitian.hope2.analyzer.model;

/**
 * Created by bing.a.qian on 10/3/2017.
 */
public class PositionItem {
    String code;
    //数量
    double quantity =0;
    double value=0;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getMeanPrice() {
        if(getQuantity()==0){
            return 0D;
        }else{
            return getValue()/getQuantity();
        }
    }
}
