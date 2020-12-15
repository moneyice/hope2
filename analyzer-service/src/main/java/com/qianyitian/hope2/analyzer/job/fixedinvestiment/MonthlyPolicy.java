package com.qianyitian.hope2.analyzer.job.fixedinvestiment;

import com.qianyitian.hope2.analyzer.model.ETradeType;
import com.qianyitian.hope2.analyzer.model.KLineInfo;

import java.util.List;

public class MonthlyPolicy extends BaseFixedInvestmentPolicy {
    @Override
    public ETradeType check(int index, List<KLineInfo> kLineInfos) {
        Integer dayOfMonth=kLineInfos.get(index).getDate().getDayOfMonth();
        if(getDaysSet().contains(dayOfMonth))
        {
           return  ETradeType.Buy;
        }
        return ETradeType.NoAction;
    }


}
