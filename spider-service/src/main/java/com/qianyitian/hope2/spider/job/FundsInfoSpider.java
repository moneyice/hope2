package com.qianyitian.hope2.spider.job;


import com.alibaba.fastjson.JSON;
import com.qianyitian.hope2.spider.config.PropertyConfig;
import com.qianyitian.hope2.spider.model.*;
import com.qianyitian.hope2.spider.service.DataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;


@Component("fundsInfoSpider")
public class FundsInfoSpider {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RestTemplate restTemplate;

    @Resource(name = "danjuanFundsRetreiver")
    private IStockRetreiver danjuanFundsRetreiver;

    @Autowired
    DataService dataService;

    @Autowired
    PropertyConfig propertyConfig;

    private ExecutorService es = Executors.newFixedThreadPool(1);

    public FundsInfoSpider() {

    }

    public void run() {
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue();
        try {
            syncFundsData(queue);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


    private Runnable createStoreStockRunnable(final String code, final String info) {
        Runnable runnable = () -> {
            try {
                dataService.storeFundDetail(code, info);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        };
        return runnable;
    }


    private void runInPool(Runnable runnable) {
        es.execute(runnable);
    }

    private void syncFundsData(final ConcurrentLinkedQueue<String> queue) throws IOException {
        try {
            List<Stock> fundsSymbols = danjuanFundsRetreiver.getFundsSymbols();
            for (Stock stock : fundsSymbols) {
                queue.add(stock.getCode());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        new Thread() {
            @Override
            public void run() {
                while (true) {
                    String code = queue.poll();
                    if (code == null) {
                        break;
                    }
                    try {
                        waitFor(50);
                        String info = danjuanFundsRetreiver.getFundsInfo(code);
                        {
                            Fund fund = JSON.parseObject(info, Fund.class);
                            if (fund.getData() == null) {
                                continue;
                            }
                        }
                        runInPool(createStoreStockRunnable(code, info));
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        }.start();
    }

//    private List<String> getAllRelatedFund(List<ManagerList> managerList) {
//        List<String> codes = new LinkedList<>();
//        for (ManagerList manager : managerList) {
//            for (AchievementList achievement : manager.getAchievementList()) {
//                codes.add(achievement.getFundCode());
//            }
//        }
//        return codes;
//    }

    private void waitFor(long i) {
        try {
            Thread.sleep(i);
        } catch (Exception e) {

        }
    }


}
