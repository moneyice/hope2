package com.qianyitian.hope2.stock.search;

import com.qianyitian.hope2.stock.model.Stock;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class EffectiveWordMatcherTest {

    @Test
    void processOneWord() {
        EffectiveWordMatcher wordMatcher = new EffectiveWordMatcher();
        Stock stock = new Stock();
        stock.setName("美的集团");
        stock.setCode("000333");
        SearchStockItem searchItem = new SearchStockItem(stock);
        searchItem.setAlias("mdjt");
        String keyword = "000333";

        List<EffectiveWordMatcher.Pair> pairs = new ArrayList<>();

        wordMatcher.processOneWord(keyword, pairs, searchItem);

        keyword = "mdjt";
        wordMatcher.processOneWord(keyword, pairs, searchItem);
        System.out.println(pairs);

    }
}