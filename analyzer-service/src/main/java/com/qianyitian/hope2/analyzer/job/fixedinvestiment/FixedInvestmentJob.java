package com.qianyitian.hope2.analyzer.job.fixedinvestiment;

import com.qianyitian.hope2.analyzer.model.*;
import com.qianyitian.hope2.analyzer.util.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bing.a.qian on 13/2/2018.
 */
public class FixedInvestmentJob {
    static double fixedInvestimentValue = 10000;
    private static Logger logger = LoggerFactory.getLogger(FixedInvestmentJob.class);
    Stock stock;
    LocalDate fromDate = null;
    LocalDate toDate = null;
    IFixedInvestmentPolicy policy;
    //定投金额

    List<ValueLog> valueLogList = new LinkedList<>();
    Account account ;

    //本金一次性投入
    Account benchmarkAccount;
    List<ValueLog> benchmarkValueLogList = new LinkedList<>();


    public FixedInvestmentJob(Stock stock, IFixedInvestmentPolicy policy) {
        this.stock = stock;
        this.policy = policy;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public void run() {
        List<KLineInfo> kLineInfos = stock.getkLineInfos();
        String code = stock.getCode();
        int beginIndex = findPosition(fromDate, kLineInfos);
        int endIndex = findPosition(toDate, kLineInfos);


        account = new Account(0);
        try {
            int depositTimes = 0;
            List<KLineInfo> fixedInvestingDateList=new ArrayList<>();
            for (int i = beginIndex; i <= endIndex; i++) {
                KLineInfo currentKLine = kLineInfos.get(i);

                ETradeType eTradeType = policy.check(i, kLineInfos);
                if (ETradeType.Buy == eTradeType) {
                    account.deposit(fixedInvestimentValue);
                    account.allIn(code, currentKLine.getClose(), currentKLine.getDate());

                    //记录市值变化
                    logValues(account,valueLogList,code, currentKLine, ++depositTimes * fixedInvestimentValue);
                    //记录定投的日期，便于benchmarkAccount 使用
                    fixedInvestingDateList.add(currentKLine);
                }
            }

            //计算benchmarkAccount
            double capital=(fixedInvestingDateList.size())*fixedInvestimentValue;
            benchmarkAccount=new Account(capital);
            if(!fixedInvestingDateList.isEmpty()){
                KLineInfo currentKLine=fixedInvestingDateList.get(0);
                benchmarkAccount.allIn(code, currentKLine.getClose(), currentKLine.getDate());
                logValues(benchmarkAccount,benchmarkValueLogList,code, currentKLine, capital);
                for (int i = 1; i < fixedInvestingDateList.size(); i++) {
                     currentKLine = fixedInvestingDateList.get(i);
                    logValues(benchmarkAccount,benchmarkValueLogList,code, currentKLine, capital);
                }
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
    }

    private void logValues(Account account,List<ValueLog> valueLogList,String code, KLineInfo kLineInfo, double capitalValue) throws BusinessException {
        double marketValue = account.calcMarketValue(code, kLineInfo.getClose());
        double totalValue = account.getAvailableValue() + marketValue;
        ValueLog log = new ValueLog();
        log.setDate(kLineInfo.getDate());
        log.setTotalValue(totalValue);
        log.setCapital(capitalValue);
        valueLogList.add(log);
    }

    public AccountSummary getAccountSummary() {
        double latestTotalValue = valueLogList.get(valueLogList.size() - 1).getTotalValue();
        double marketValue = latestTotalValue - account.getAvailableValue();
        AccountSummary accountSummary = new AccountSummary();
        accountSummary.setMarketValue(marketValue);
        accountSummary.setAvailableValue(account.getAvailableValue());
        accountSummary.setTotalValue(latestTotalValue);
        accountSummary.setTradeItemList(account.getTradeItemList());
        accountSummary.setValueLogList(valueLogList);
        return accountSummary;
    }

    public AccountSummary getBenchmarkAccountSummary() {
        double latestTotalValue = benchmarkValueLogList.get(benchmarkValueLogList.size() - 1).getTotalValue();
        double marketValue = latestTotalValue - benchmarkAccount.getAvailableValue();
        AccountSummary accountSummary = new AccountSummary();
        accountSummary.setMarketValue(marketValue);
        accountSummary.setAvailableValue(benchmarkAccount.getAvailableValue());
        accountSummary.setTotalValue(latestTotalValue);
        accountSummary.setTradeItemList(benchmarkAccount.getTradeItemList());
        accountSummary.setValueLogList(benchmarkValueLogList);
        return accountSummary;
    }


    //find the Date's index in the kLineInfos
    private int findPosition(LocalDate fromDate, List<KLineInfo> kLineInfos) {
        if (fromDate == null) {
            return 0;
        }

        if(kLineInfos.get(0).getDate().isAfter(fromDate)){
            return 0;
        }
        if(kLineInfos.get(kLineInfos.size()-1).getDate().isBefore(fromDate)){
            return kLineInfos.size()-1;
        }
        int i = 0;
        for (; i < kLineInfos.size(); i++) {
            if (kLineInfos.get(i).getDate().isEqual(fromDate)) {
                return i;
            }
            if (kLineInfos.get(i).getDate().isAfter(fromDate)) {
                return i;
            }
        }
        return i-1;
    }


}
