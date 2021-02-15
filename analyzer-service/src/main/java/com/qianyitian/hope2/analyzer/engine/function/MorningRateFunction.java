package com.qianyitian.hope2.analyzer.engine.function;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;
import com.qianyitian.hope2.analyzer.funds.model.FundProfileInfo;

import java.util.Map;

public class MorningRateFunction extends AbstractFunction {
    @Override
    public AviatorObject call(Map<String, Object> env) {
        FundProfileInfo fundDetail = (FundProfileInfo) env.get("fund");
        return new AviatorString(fundDetail.getMorningRate());
    }

    @Override
    public String getName() {
        return "morningRate";
    }
}