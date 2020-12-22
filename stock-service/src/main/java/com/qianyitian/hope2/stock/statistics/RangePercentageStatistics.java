package com.qianyitian.hope2.stock.statistics;

import com.alibaba.fastjson.JSON;
import com.qianyitian.hope2.stock.model.*;
import com.qianyitian.hope2.stock.util.KUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class RangePercentageStatistics {
    public static int days2Now = 250;

    public int getNewStockCount() {
        return newStockCount.get();
    }

    AtomicInteger newStockCount;
    private Logger logger = LoggerFactory.getLogger(getClass());
    Map<ERangePercentage, AtomicInteger> map = new ConcurrentHashMap<>(ERangePercentage.values().length);

    private final ReentrantLock lock = new ReentrantLock();

    public void init() {
        map = new ConcurrentHashMap<>(ERangePercentage.values().length);
        newStockCount = new AtomicInteger(0);
    }


    public void makeStatistics(Stock stock) {
        if (stock == null || stock.getkLineInfos() == null) {
            return;
        }
        if (stock.getkLineInfos().isEmpty()) {
            logger.info("{} {} kline is empty", stock.getName(), stock.getCode());
            return;
        }


        if (days2Now >= stock.getkLineInfos().size()) {
            newStockCount.getAndIncrement();
        }
        KLineInfo now = KUtils.findKLine(stock.getkLineInfos(), 0);
        KLineInfo base = KUtils.findKLine(stock.getkLineInfos(), RangePercentageStatistics.days2Now);

        double range = KUtils.calcIncreaseRange(base.getClose(), now.getClose());

        Optional<ERangePercentage> first = findRange(range);
        record(first);
    }

    private void record(Optional<ERangePercentage> first) {
        if (first.isPresent()) {
            lock.lock();
            try {
                AtomicInteger atomicInteger = map.get(first.get());
                if (atomicInteger == null) {
                    atomicInteger = new AtomicInteger(0);
                    map.put(first.get(), atomicInteger);
                }
                atomicInteger.getAndIncrement();
            } finally {
                lock.unlock();
            }
        }
    }

    private Optional<ERangePercentage> findRange(double range) {
        return Arrays.stream(ERangePercentage.values()).
                filter(eRangePercentage -> range >= eRangePercentage.bottom && range < eRangePercentage.top).
                findFirst();
    }

    public List<ChartItem> getResult() {
        List<ChartItem> list = Arrays.stream(ERangePercentage.values()).map(eRangePercentage -> {
            ChartItem<Integer> chartItem = new ChartItem();
            chartItem.setName(eRangePercentage.label);
            AtomicInteger count = map.get(eRangePercentage);
            if (count == null) {
                chartItem.setY(0);
            } else {
                chartItem.setY(count.get());
            }
            return chartItem;
        }).collect(Collectors.toList());
        return list;
    }

    enum ERangePercentage {
        R1("涨幅 < 0%", -1, 0), R2("0<= 涨幅 <20%", 0, 0.2f), R3("20% <= 涨幅 <50%", 0.2f, 0.5f), R4("50% <= 涨幅 <100%", 0.5f, 1), R5("100% <= 涨幅", 1, Float.MAX_VALUE);
        String label;
        float bottom;
        float top;

        ERangePercentage(String label, float bottom, float top) {
            this.label = label;
            this.top = top;
            this.bottom = bottom;
        }
    }


}
