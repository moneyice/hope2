package com.qianyitian.hope2.analyzer.job.policy28;

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
public abstract class BaseTwoEightJob {
    private static Logger logger = LoggerFactory.getLogger(BaseTwoEightJob.class);
    String INDEX_300_CODE="000300";
    String INDEX_500_CODE="000905";

    List<KLineInfo> index300List=null;
    List<KLineInfo> index500List=null;
    LocalDate fromDate=null;

    List<ValueLog> valueLogList =new LinkedList<>();
    Account account=new Account();

    public void setFromDate(LocalDate fromDate){
        this.fromDate=fromDate;
    }
    public void run(){
        int position300=findPosition(fromDate,index300List);
        int position500=findPosition(fromDate,index500List);

        try{
            for (int i=0;i<index300List.size()-position300;i++){
                KLineInfo current300=index300List.get(i+position300);
                KLineInfo week4Ago300=index300List.get(i+position300-4);
                double week4Range300 = current300.getClose() / week4Ago300.getClose() - 1;


                KLineInfo current500=index500List.get(i+position500);
                KLineInfo week4Ago500=index500List.get(i+position500-4);
                double week4Range500 = current500.getClose() / week4Ago500.getClose() - 1;

                operate(current300, week4Range300, current500, week4Range500);

                //记录市值变化
                logValues(current300,current500);

            }
        }catch (BusinessException e){
            logger.error(e.toString(),e);
        }

        List<TradeItem> tradeItemList = account.getTradeItemList();
    }

    private void logValues(KLineInfo current300, KLineInfo current500) throws BusinessException {
        if(!current300.getDate().equals(current500.getDate())){
            throw new BusinessException("Date index is wrong: 300's date is " +current300.getDate()+"; 500's date is "+current500.getDate()  );
        }

        double latestPrice300= current300.getClose();
        PositionItem position300 = account.getPosition(INDEX_300_CODE);
        double marketValue300=0;
        if(position300!=null){
            marketValue300=position300.getQuantity()*latestPrice300;
        }

        double latestPrice500= current500.getClose();
        PositionItem position500 = account.getPosition(INDEX_500_CODE);
        double marketValue500=0;
        if(position500!=null){
            marketValue500=position500.getQuantity()*latestPrice500;

        }
        double totalMarketValue=marketValue300+marketValue500;

        double totalValue=account.getAvailableValue()+totalMarketValue;

        ValueLog log=new ValueLog();
        log.setDate(current300.getDate());
        log.setTotalValue(totalValue);
        valueLogList.add(log);
    }

    protected abstract void operate(KLineInfo current300, double week4Range300, KLineInfo current500, double week4Range500) throws BusinessException;

    public AccountSummary accountSummary() {
        double latestPrice300= getIndex300List().get(getIndex300List().size()-1).getClose();
        PositionItem position300 = account.getPosition(INDEX_300_CODE);
        double marketValue300=0;
        if(position300!=null){
            marketValue300=position300.getQuantity()*latestPrice300;
        }

        double latestPrice500= getIndex500List().get(getIndex500List().size()-1).getClose();
        PositionItem position500 = account.getPosition(INDEX_500_CODE);
        double marketValue500=0;
        if(position500!=null){
            marketValue500=position500.getQuantity()*latestPrice500;

        }
        double totalMarketValue=marketValue300+marketValue500;

        double totalValue=account.getAvailableValue()+totalMarketValue;

        AccountSummary accountSummary=new AccountSummary();
        accountSummary.setMarketValue(totalMarketValue);
        accountSummary.setAvailableValue(account.getAvailableValue());
        accountSummary.setTotalValue(totalValue);
        accountSummary.setTradeItemList(account.getTradeItemList());
        accountSummary.setValueLogList(valueLogList);
        return accountSummary;
    }

    public boolean is300Position() {
        boolean ok=false;
        PositionItem positionItem=account.getPosition(INDEX_300_CODE);
        if(positionItem!=null&&positionItem.getQuantity()>0){
            ok=true;
        }
        return ok;
    }

    public boolean is500Position() {
        boolean ok=false;
        PositionItem positionItem=account.getPosition(INDEX_500_CODE);
        if(positionItem!=null&&positionItem.getQuantity()>0){
            ok=true;
        }
        return ok;
    }


    private int findPosition(LocalDate fromDate, List<KLineInfo> kLineInfos) {
        for (int i = 1; i < kLineInfos.size(); i++) {
            if(fromDate.isAfter(kLineInfos.get(i-1).getDate())&&(fromDate.isBefore(kLineInfos.get(i).getDate())  || fromDate.isEqual(kLineInfos.get(i).getDate())  )){
                return i;
            }
        }
        return -1;
    }




    public List<KLineInfo> getIndex300List() {
        return index300List;
    }

    public void setIndex300List(List<KLineInfo> index300List) {
        this.index300List = index300List;
    }

    public List<KLineInfo> getIndex500List() {
        return index500List;
    }

    public void setIndex500List(List<KLineInfo> index500List) {
        this.index500List = index500List;
    }

}
