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
    @Resource(name = "stockDAO4FileSystem")
    private IStockDAO stockDAO;

    @Autowired
    Map<String, String> fundProfileMapDB;

    public FundsController() {
    }

    @PostMapping(value = "/fund/detail/{code}")
    public void storeFundDetail(@PathVariable String code, @RequestBody String funds) {
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
        fundProfileMapDB.put(code, funds);
    }

    @GetMapping(value = "/data/fund/profile/{code}")
    public String getFundProfile(@PathVariable String code) {
        return fundProfileMapDB.get(code);
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

    @Autowired
    ConcurrentMap<String, String> testMapdb;

    @GetMapping(value = "/testVolume")
    public Map testVolume() {
        List<Stock> funds = stockDAO.getPortfolioSymbols("funds");
        int i = 0;
        for (Stock fund : funds) {
            String content = stockDAO.getFunds(fund.getCode());
            i++;
            if (content != null) {
                testMapdb.put(fund.getCode(), content);
            }
        }
        Map result = new HashMap();
        result.put("funds size", funds.size());
        result.put("testMapdb size", testMapdb.size());
        result.put("i", i);
        return result;
    }

    @GetMapping(value = "/testPerformance")
    public Map testPerformance() {
        Map map = new HashMap();
        List<Stock> funds = stockDAO.getPortfolioSymbols("funds");
        map.put("funds number is " + funds.size(), "");


        {
            long start = System.currentTimeMillis();
            int i = 0;
            for (Stock fund : funds) {
                String content = stockDAO.getFunds(fund.getCode());
                if (content != null) {
                    i++;
                }

            }
            mark(start, map, i, "file read");
        }
        {
            long start = System.currentTimeMillis();
            int i = 0;
            for (Stock fund : funds) {
                String content = testMapdb.get(fund.getCode());
                if (content != null) {
                    i++;
                }
            }
            mark(start, map, i, "mapdb read");
        }

        {
            com.google.common.cache.Cache<Object, Object> guavaCache = CacheBuilder.newBuilder()
                    // 数量上限
                    .maximumSize(7000)
                    // 过期机制
                    .expireAfterWrite(1, TimeUnit.HOURS)
                    .build();

            {
                for (Stock fund : funds) {
                    String content = testMapdb.get(fund.getCode());
                    if (content != null) {
                        guavaCache.put(fund.getCode(), content);
                    }

                }
            }
            {
                long start = System.currentTimeMillis();
                int i = 0;
                for (Stock fund : funds) {
                    String content = null;
                    try {
                        content = (String) guavaCache.get(fund.getCode(), () -> {
                            return "";
                        });
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    if (content != null && !content.equals("")) {
                        i++;
                    }
                }
                mark(start, map, i, "guava cache read");
                guavaCache.cleanUp();
            }
        }





        {
            Cache cache = Caffeine.newBuilder()
                    // 数量上限
                    .maximumSize(7000).recordStats()
                    // 过期机制
                    .expireAfterWrite(1, TimeUnit.HOURS)
                    .build();
            {
                for (Stock fund : funds) {
                    String content = testMapdb.get(fund.getCode());
                    if (content != null) {
                        cache.put(fund.getCode(), content);
                    }

                }
            }
            {
                long start = System.currentTimeMillis();
                int i = 0;
                for (Stock fund : funds) {
                    String content = (String) cache.get(fund.getCode(), item -> {
                        return null;
                    });
                    if (content != null) {
                        i++;
                    }
                }
                mark(start, map, i, "Caffeine cache read");
                cache.cleanUp();
            }
        }

        return map;
    }


    private void mark(long start, Map map, int i, String label) {
        long last = (System.currentTimeMillis() - start) ;
        map.put(label + " number " + i + " , cost " + last + " 毫秒", "");
    }


}
