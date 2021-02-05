package com.qianyitian.hope2.analyzer.engine.function;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorDouble;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.qianyitian.hope2.analyzer.model.FundProfileInfo;

import java.util.Map;

public class TotalShareFunction extends AbstractFunction {
    @Override
    public AviatorObject call(Map<String, Object> env) {
        FundProfileInfo fundDetail = (FundProfileInfo) env.get("fund");
        return new AviatorDouble(fundDetail.getTotalShareNumber());
    }

    @Override
    public String getName() {
        return "totalShare";
    }
}