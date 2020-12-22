package com.qianyitian.hope2.stock.model;

public class ChartItem<T> {
    String name;
    T y;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public T getY() {
        return y;
    }

    public void setY(T y) {
        this.y = y;
    }
}
