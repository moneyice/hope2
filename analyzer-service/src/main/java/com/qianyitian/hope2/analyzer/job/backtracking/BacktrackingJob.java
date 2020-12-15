package com.qianyitian.hope2.analyzer.job.backtracking;

import com.qianyitian.hope2.analyzer.model.*;
import com.qianyitian.hope2.analyzer.util.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bing.a.qian on 10/2/2017.
 */
public class BacktrackingJob {
    private static Logger logger = LoggerFactory.getLogger(BacktrackingJob.class);
    Stock stock;
    IBacktrackingPolicy policy;

    LocalDate fromDate=null;
    List<ValueLog> valueLogList =new LinkedList<>();
    Account account=new Account();

    public BacktrackingJob(Stock stock,  IBacktrackingPolicy policy) {
        this.stock = stock;
        this.policy = policy;
    }

    public void setFromDate(LocalDate fromDate){
        this.fromDate=fromDate;
    }


    private boolean isHoldingPosition(String code) {
        boolean ok=false;
        PositionItem positionItem=account.getPosition(code);
        if(positionItem!=null&&positionItem.getQuantity()>0){
            ok=true;
        }
        return ok;
    }

    public void run()  {
        List<KLineInfo> kLineInfos = stock.getkLineInfos();
        String code=stock.getCode();
        int index=findPosition(fromDate,kLineInfos);
        try{
            for (int i=index;i<kLineInfos.size();i++){
                KLineInfo currentKLine=kLineInfos.get(i);
                ETradeType eTradeType=policy.check(i,kLineInfos);
                if(ETradeType.NoAction==eTradeType){
                  //do nothing, only records
                }
                if(ETradeType.Buy==eTradeType)
                {
                    if(!isHoldingPosition(code)) {
                        account.allIn(code,currentKLine.getClose(),currentKLine.getDate());
                    }
                }
                if(ETradeType.Sell==eTradeType)
                {
                    if(isHoldingPosition(code)) {
                        account.clear(code,currentKLine.getClose(),currentKLine.getDate());
                    }
                }
                //记录市值变化
                logValues(code,currentKLine);
            }
        }catch (Exception e){
            logger.error(e.toString(),e);
        }
    }

    private void logValues(String code, KLineInfo kLineInfo) throws BusinessException {
        double marketValue= account.calcMarketValue(code,kLineInfo.getClose());
        double totalValue=account.getAvailableValue()+marketValue;
        ValueLog log=new ValueLog();
        log.setDate(kLineInfo.getDate());
        log.setTotalValue(totalValue);
        valueLogList.add(log);
    }

    public AccountSummary getAccountSummary() {
        double latestTotalValue=valueLogList.get(valueLogList.size()-1).getTotalValue();
        double marketValue=latestTotalValue-account.getAvailableValue();
        AccountSummary accountSummary=new AccountSummary();
        accountSummary.setMarketValue(marketValue);
        accountSummary.setAvailableValue(account.getAvailableValue());
        accountSummary.setTotalValue(latestTotalValue);
        accountSummary.setTradeItemList(account.getTradeItemList());
        accountSummary.setValueLogList(valueLogList);
        return accountSummary;
    }


    //find the fromDate's index in the kLineInfos
    private int findPosition(LocalDate fromDate, List<KLineInfo> kLineInfos) {
        for (int i = 0; i < kLineInfos.size(); i++) {
            if(fromDate.isEqual(kLineInfos.get(i).getDate())){
                return i;
            }

            if(fromDate.isAfter(kLineInfos.get(i).getDate())&&(fromDate.isBefore(kLineInfos.get(i+1).getDate()))){
                return i+1;
            }
        }
        return 0;
    }






}
