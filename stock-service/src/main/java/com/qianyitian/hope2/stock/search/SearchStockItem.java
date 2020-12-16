package com.qianyitian.hope2.stock.search;

import com.qianyitian.hope2.stock.model.Stock;

public class SearchStockItem implements SearchItem<Stock> {

    private Stock stock;

    SearchStockItem(Stock stock) {
        this.stock = stock;
    }

    private String alias;

    @Override
    public String[] getKeys() {
        return new String[]{stock.getCode(), alias};
    }


    @Override
    public Stock getItem() {
        return stock;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
