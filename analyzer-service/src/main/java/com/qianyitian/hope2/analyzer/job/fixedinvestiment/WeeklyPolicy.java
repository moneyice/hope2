package com.qianyitian.hope2.analyzer.job.fixedinvestiment;

import com.qianyitian.hope2.analyzer.model.ETradeType;
import com.qianyitian.hope2.analyzer.model.KLineInfo;

import java.util.List;

public class WeeklyPolicy extends BaseFixedInvestmentPolicy {


    @Override
    public ETradeType check(int index, List<KLineInfo> kLineInfos) {
        Integer dayOfWeek=kLineInfos.get(index).getDate().getDayOfWeek().getValue();
        if(getDaysSet().contains(dayOfWeek)){
            return  ETradeType.Buy;
        }
        return ETradeType.NoAction;
    }
}
