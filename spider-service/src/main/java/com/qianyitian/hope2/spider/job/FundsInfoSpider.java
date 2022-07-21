package com.qianyitian.hope2.spider.job;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qianyitian.hope2.spider.config.PropertyConfig;
import com.qianyitian.hope2.spider.fetcher.DanjuanFundsRetreiver;
import com.qianyitian.hope2.spider.fetcher.IStockRetreiver;
import com.qianyitian.hope2.spider.model.FundBar;
import com.qianyitian.hope2.spider.model.FundFinancialReport;
import com.qianyitian.hope2.spider.model.FundInfo;
import com.qianyitian.hope2.spider.model.Stock;
import com.qianyitian.hope2.spider.service.DataService;
import com.qianyitian.hope2.spider.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Component("fundsInfoSpider")
public class FundsInfoSpider {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource(name = "xiaoxiongFundRetreiver")
    private IStockRetreiver fundsRetreiver;



    @Resource(name = "danjuanFundsRetreiver")
    private DanjuanFundsRetreiver danjuanFundsRetreiver;

    @Autowired
    DataService dataService;

    @Autowired
    PropertyConfig propertyConfig;

    private ExecutorService es = Executors.newFixedThreadPool(4);

    public FundsInfoSpider() {

    }

    public void run() {
        try {
            syncFundsData();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


    private Runnable createStoreStockRunnable(final String code, final String info) {
        Runnable runnable = () -> {
            try {
                FundInfo fundInfo = formatFundInfo(info);
                if (fundInfo == null) {
                    logger.error("不保存少于1年的基金 " + code);
                    return;
                }
                calcValue(fundInfo);
                String content = JSON.toJSONString(fundInfo);
                dataService.storeFundDetail(code, content);
            } catch (Exception e) {
                logger.error("fund error " + code, e);
            }
        };
        return runnable;
    }

    private void calcValue(FundInfo fundInfo) {
        List<FundBar> fundBarList = fundInfo.getFundBarList();
        int startIndex = 0;
        for (int i = 0; i < fundBarList.size(); i++) {
            FundBar bar = fundBarList.get(i);
            bar.setValue(bar.getNetValue());
            if (bar.getChange() < 0.0F || bar.getChange() > 0.0F) {
                startIndex = i;
                break;
            }
        }
        for (int i = startIndex + 1; i < fundBarList.size(); i++) {
            FundBar currentBar = fundBarList.get(i);
            FundBar preBar = fundBarList.get(i - 1);
            currentBar.setValue(preBar.getValue() * (1 + currentBar.getChange() / 100));
        }
    }

    //convert xiaoxiong fund format to hope format
    private FundInfo formatFundInfo(String content) {
        JSONObject jsonObject = JSON.parseObject(content);
        //original format is like https://api.doctorxiong.club/v1/fund/detail?code=270002
        JSONObject fundObject = jsonObject.getJSONObject("data");

        FundInfo fundInfo = new FundInfo();
        {
            String lastYearGrowth = fundObject.getString("lastYearGrowth");
            if (StringUtils.isEmpty(lastYearGrowth)) {
                //少于一年的基金先不管了
                return null;
            }

            fundInfo.setCode(fundObject.getString("code"));
            fundInfo.setName(fundObject.getString("name"));
            fundInfo.setType(fundObject.getString("type"));
            fundInfo.setNetValue(fundObject.getFloatValue("netWorth"));
            fundInfo.setTotalValue(fundObject.getFloatValue("totalWorth"));
            fundInfo.setGrToday(Utils.handleFloat(fundObject.getString("dayGrowth")));
            fundInfo.setGrl1Week(Utils.handleFloat(fundObject.getString("lastWeekGrowth")));
            fundInfo.setGrl1Month(Utils.handleFloat(fundObject.getString("lastMonthGrowth")));
            fundInfo.setGrl3Month(Utils.handleFloat(fundObject.getString("lastThreeMonthsGrowth")));
            fundInfo.setGrl6Month(Utils.handleFloat(fundObject.getString("lastSixMonthsGrowth")));
            fundInfo.setGrl1Year(Utils.handleFloat(fundObject.getString("lastYearGrowth")));
            fundInfo.setManagers(fundObject.getString("manager"));
            fundInfo.setScale(Utils.handleFloat(fundObject.getString("fundScale").replaceFirst("亿", "")));
            fundInfo.setUpdateDate(LocalDate.parse(fundObject.getString("netWorthDate")));
            {
                List<FundBar> list = new ArrayList<>();
                JSONArray netWorthDataList = fundObject.getJSONArray("netWorthData");
                JSONArray totalWorthDataList = fundObject.getJSONArray("totalNetWorthData");
                LocalDate foundDate = LocalDate.parse(netWorthDataList.getJSONArray(0).getString(0));
                fundInfo.setFoundDate(foundDate);
                for (int i = 0; i < netWorthDataList.size(); i++) {
                    JSONArray itemList = netWorthDataList.getJSONArray(i);
                    LocalDate date = null;
                    date = LocalDate.parse(itemList.getString(0));

                    float value = Utils.handleFloat(itemList.getString(1));
                    float change = Utils.handleFloat(itemList.getString(2));
                    float totalValue = Utils.handleFloat(totalWorthDataList.getJSONArray(i).getString(1));
                    FundBar fundBar = new FundBar();
                    fundBar.setDate(date);
                    fundBar.setNetValue(value);
                    fundBar.setChange(change);
                    fundBar.setTotalValue(totalValue);
                    list.add(fundBar);
                }
                fundInfo.setFundBarList(list);
            }
        }
        return fundInfo;
    }


    private void runInPool(Runnable runnable) {
        es.execute(runnable);
    }

    private void syncFundsData() throws IOException {
        List<Stock> fundsSymbols = fundsRetreiver.getFundsSymbols();
        for (Stock stock : fundsSymbols) {
            syncFundData(stock.getCode());
        }
    }

    public void syncFundData(String code) {
        try {
            String info = fundsRetreiver.fetchFundsInfo(code);
            runInPool(createStoreStockRunnable(code, info));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


    public void syncFundFinancialReport() {
        int syncNumber = 0;
        int total = 0;
        try {
            List<Stock> fundsSymbols = fundsRetreiver.getFundsSymbols();
            total = fundsSymbols.size();
            for (Stock stock : fundsSymbols) {
                boolean ok = syncFundFinancialReport(stock.getCode());
                if (ok) {
                    syncNumber++;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        logger.info("所有基金数量 " + total + "; 同步基金数量 " + syncNumber);
    }

    public void checkReport() {
        try {
            List<Stock> fundsSymbols = fundsRetreiver.getFundsSymbols();
            for (Stock stock : fundsSymbols) {
                String fundProfile = dataService.getFundProfile(stock.getCode());
                if (fundProfile == null) {
                    continue;
                }

                if (!fundProfile.contains(DATE)) {
                    System.out.println(stock.getCode());
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    String DATE = "2021-12-31";
    String LAST_DATE = "2021-09-30";

    public boolean syncFundFinancialReport(String code) {

        try {
            String fundProfile = dataService.getFundProfile(code);
            if (fundProfile == null || fundProfile.contains(DATE)) {
                return false;
            }
            FundFinancialReport financialReport = danjuanFundsRetreiver.getFinancialReport(code);
            dataService.storeFundProfile(financialReport.getCode(), JSON.toJSONString(financialReport));
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }


}
