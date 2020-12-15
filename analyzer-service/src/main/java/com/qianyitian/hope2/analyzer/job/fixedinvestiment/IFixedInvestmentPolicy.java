package com.qianyitian.hope2.analyzer.job.fixedinvestiment;

import com.qianyitian.hope2.analyzer.model.ETradeType;
import com.qianyitian.hope2.analyzer.model.KLineInfo;

import java.util.List;

public interface IFixedInvestmentPolicy {
    ETradeType check(int index, List<KLineInfo> kLineInfos);

    void setDays(String days);

    //定投本金
    void setCapital(double capital);
    //定投金额
    void setFixedInvestimentValue(double fixedInvestimentValue);
}
