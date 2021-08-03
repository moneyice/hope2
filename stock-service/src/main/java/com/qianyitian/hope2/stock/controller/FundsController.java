package com.qianyitian.hope2.stock.controller;

import com.alibaba.fastjson.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.google.common.cache.CacheBuilder;
import com.qianyitian.hope2.stock.dao.IStockDAO;
import com.qianyitian.hope2.stock.model.*;
import com.qianyitian.hope2.stock.statistics.RangePercentageStatistics;
import com.qianyitian.hope2.stock.util.KUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@RestController
public class FundsController {
    private Logger logger = LoggerFactory.getLogger(getClass());
    ExecutorService threadPool = Executors.newFixedThreadPool(4);
    @Resource(name = "stockDAO4Redis")
    private IStockDAO stockDAO;

    @Autowired
    Map<String, String> fundProfileMapDB;

    public FundsController() {
    }

    @PostMapping(value = "/fund/detail/{code}")
    public void storeFundDetail(@PathVariable String code, @RequestBody String funds) {
        logger.info("storing fund detail ====== " + code);
        FundsStoringTask task = new FundsStoringTask(code, funds);
        threadPool.execute(task);
    }

    @GetMapping(value = "/data/fund/detail/{code}")
    public String getFunds(@PathVariable String code) {
        String info = stockDAO.getFunds(code);
        return info;
    }

    @PostMapping(value = "/fund/profile/{code}")
    public void storeFundProfile(@PathVariable String code, @RequestBody String funds) {
        logger.info("storing fund profile ====== " + code);
        fundProfileMapDB.put(code, funds);
    }

    @GetMapping(value = "/data/fund/profile/{code}")
    public String getFundProfile(@PathVariable String code) {
        return fundProfileMapDB.get(code);
    }

    @GetMapping(value = "/data/fund/profile/size")
    public int getFundProfileSize() {
        return fundProfileMapDB.size();
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
