package com.qianyitian.hope2.stock.controller;

import com.alibaba.fastjson.JSON;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.qianyitian.hope2.stock.dao.IStockDAO;
import com.qianyitian.hope2.stock.model.*;
import com.qianyitian.hope2.stock.statistics.RangePercentageStatistics;
import com.qianyitian.hope2.stock.util.KUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
public class FundsController {
    private Logger logger = LoggerFactory.getLogger(getClass());
    ExecutorService threadPool = Executors.newFixedThreadPool(4);
    @Resource(name = "stockDAO4FileSystem")
    private IStockDAO stockDAO;

    public FundsController() {
    }

    @PostMapping(value = "/funds/{code}")
    public void storeFunds(@PathVariable String code, @RequestBody String funds) {
        FundsStoringTask task = new FundsStoringTask(code, funds);
        threadPool.execute(task);
    }

    @GetMapping(value = "/data/funds/{code}")
    public String getFunds(@PathVariable String code) {
        String info = stockDAO.getFunds(code);
        return info;
    }

    class FundsStoringTask implements Runnable {
        String funds;
        String code;

        public FundsStoringTask(String code, String funds) {
            this.code = code;
            this.funds = funds;
        }

        @Override
        public void run() {
            stockDAO.storeFunds(code, funds);
        }
    }
}
