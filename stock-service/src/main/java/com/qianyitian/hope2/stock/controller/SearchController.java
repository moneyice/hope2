package com.qianyitian.hope2.stock.controller;

import com.alibaba.fastjson.JSON;
import com.qianyitian.hope2.stock.dao.IStockDAO;
import com.qianyitian.hope2.stock.filter.StockFilter;
import com.qianyitian.hope2.stock.model.KLineInfo;
import com.qianyitian.hope2.stock.model.StatisticsInfo;
import com.qianyitian.hope2.stock.model.Stock;
import com.qianyitian.hope2.stock.model.SymbolList;
import com.qianyitian.hope2.stock.search.EffectiveWordMatcher;
import com.qianyitian.hope2.stock.search.SearchItem;
import com.qianyitian.hope2.stock.search.SearchStockItem;
import com.qianyitian.hope2.stock.util.KUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@RestController
public class SearchController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    EffectiveWordMatcher wordMatcher;

    public SearchController() {
    }

    @GetMapping(value = "/search")
    @CrossOrigin
    public SymbolList getStock(@RequestParam(value = "word", required = true) String word) {
        word = word.toLowerCase();
        Set<SearchItem> searchItems = wordMatcher.obtainMatchedWords(word);
        List collect = searchItems.parallelStream().map(item -> item.getItem()).collect(Collectors.toList());
        SymbolList symbolList = new SymbolList();
        symbolList.setSymbols(collect);
        return symbolList;
    }

}
