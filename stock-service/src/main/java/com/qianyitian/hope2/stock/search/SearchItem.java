package com.qianyitian.hope2.stock.search;

public interface SearchItem<T> {

    public String[] getKeys();

    T getItem();
}
