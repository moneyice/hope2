package com.qianyitian.hope2.analyzer.engine.function;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorBigInt;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.qianyitian.hope2.analyzer.funds.model.FundProfileInfo;

import java.time.LocalDate;
import java.util.Map;

public class FoundYearFunction extends AbstractFunction {
    @Override
    public AviatorObject call(Map<String, Object> env) {
        FundProfileInfo fundDetail = (FundProfileInfo) env.get("fund");
        try {
            return AviatorBigInt.valueOf(LocalDate.now().getYear() - fundDetail.getFoundDate().getYear());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return AviatorBigInt.valueOf(-1);
    }

    @Override
    public String getName() {
        return "foundYear";
    }
}