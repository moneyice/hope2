package com.qianyitian.hope2.analyzer.engine.function;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorBigInt;
import com.googlecode.aviator.runtime.type.AviatorDouble;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.qianyitian.hope2.analyzer.funds.model.FundProfileInfo;
import org.apache.tomcat.jni.Local;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public class CAGRFunction extends AbstractFunction {
    @Override
    public AviatorObject call(Map<String, Object> env) {
        FundProfileInfo fundDetail = (FundProfileInfo) env.get("fund");
        if (fundDetail.getFoundDate() == null) {
            return AviatorBigInt.valueOf(-1);
        }
        try {
            return AviatorDouble.valueOf(fundDetail.getCagr());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return AviatorBigInt.valueOf(-1);
    }

    protected double getCAGR(LocalDate startDate,LocalDate endDate, double startPrice, double endPrice){
        double years = averageYears(startDate, endDate);
        double cagr = CAGR(startPrice, endPrice, years);
        return cagr;
    }

    protected double CAGR(double initialValue, double currentValue, double averageYears) {
        double cagr = Math.pow((currentValue / initialValue), (1 / averageYears)) - 1;
        return cagr;
    }

    protected double averageYears(LocalDate from, LocalDate to) {
        long betweenDays = ChronoUnit.DAYS.between(from, to);
        double averageYears = betweenDays / 365.2;
        return averageYears;
    }


    @Override
    public String getName() {
        return "cagr";
    }
}