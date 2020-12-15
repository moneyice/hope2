package com.qianyitian.hope2.analyzer.job.backtracking;

import com.qianyitian.hope2.analyzer.model.ETradeType;
import com.qianyitian.hope2.analyzer.model.KLineInfo;

import java.util.List;

public class MacdAndMean30BacktrackingPolicy implements IBacktrackingPolicy{
    public static int MEAN_30_DAYS = 30;
    @Override
    public ETradeType check(int index, List<KLineInfo> kLineInfos) {
        KLineInfo currentInfo=kLineInfos.get(index);
        double mean30Price=calcMean30Price(index,kLineInfos);
        if(currentInfo.getMacd()>=0 && currentInfo.getClose()>mean30Price){
            return ETradeType.Buy;
        }else{
            return ETradeType.Sell;
        }
    }

    private double calcMean30Price(int index, List<KLineInfo> kLineInfos) {
        if(index<MEAN_30_DAYS){
            return 0D;
        }
        double sum30Days=0;

        for (int i = index - MEAN_30_DAYS; i < index; i++) {
            KLineInfo toCheck = kLineInfos.get(i);
            sum30Days+=toCheck.getClose();
        }

        double mean30DayPrice= sum30Days/MEAN_30_DAYS;
        return mean30DayPrice;
    }
}
