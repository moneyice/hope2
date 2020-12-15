package com.qianyitian.hope2.analyzer.job.fixedinvestiment;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class BaseFixedInvestmentPolicy implements IFixedInvestmentPolicy {
    Set<Integer> daysSet = new HashSet<Integer>();
    double capital;
    double fixedInvestimentValue;

    public Set<Integer> getDaysSet() {
        return daysSet;
    }


    //定投本金
    @Override
    public void setCapital(double capital) {
        this.capital = capital;
    }

    //定投金额
    @Override
    public void setFixedInvestimentValue(double fixedInvestimentValue) {
        this.fixedInvestimentValue = fixedInvestimentValue;
    }
    @Override
    public void setDays(String days) {
        String[] dayArray = days.split(",");
        if (dayArray != null) {
            Set<Integer> sets = Arrays.stream(dayArray).mapToInt(Integer::valueOf).boxed().collect(Collectors.toSet());
            daysSet.addAll(sets);
        }
    }
}
