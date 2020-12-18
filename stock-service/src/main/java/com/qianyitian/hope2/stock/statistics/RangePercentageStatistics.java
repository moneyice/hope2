package com.qianyitian.hope2.stock.statistics;

import com.alibaba.fastjson.JSON;
import com.qianyitian.hope2.stock.model.KLineInfo;
import com.qianyitian.hope2.stock.model.StatisticsInfo;
import com.qianyitian.hope2.stock.model.Stock;
import com.qianyitian.hope2.stock.model.SymbolList;
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
    private Logger logger = LoggerFactory.getLogger(getClass());

    private int days2Now = 250;
    Map<ERangePercentage, AtomicInteger> map = new ConcurrentHashMap<>(ERangePercentage.values().length);

    private final ReentrantLock lock = new ReentrantLock();

    public void init() {
        map = new ConcurrentHashMap<>(ERangePercentage.values().length);
    }

    public void makeStatistics(Stock stock) {
        if (stock.getkLineInfos().isEmpty()) {
            logger.info("{} {} kline is empty", stock.getName(), stock.getCode());
            return;
        }

        days2Now = Math.min(stock.getkLineInfos().size(), days2Now);

        KLineInfo base = stock.getkLineInfos().get(days2Now - 1);
        KLineInfo now = stock.getkLineInfos().get(0);

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
                    map.put(first.get(), new AtomicInteger(0));
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

    public List<Lable> getResult() {
        List<Lable> list = Arrays.stream(ERangePercentage.values()).map(eRangePercentage -> {
            Lable label = new Lable();
            label.label = eRangePercentage.label;
            AtomicInteger count = map.get(eRangePercentage);
            if (count == null) {
                label.count = 0;
            } else {
                label.count = count.get();
            }
            return label;
        }).collect(Collectors.toList());
        return list;
    }

    class Lable {
        String label;
        Integer count;
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
