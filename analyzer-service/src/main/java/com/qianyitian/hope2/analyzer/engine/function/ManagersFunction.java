package com.qianyitian.hope2.analyzer.engine.function;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.qianyitian.hope2.analyzer.funds.model.FundProfileInfo;

import java.util.Map;

public class ManagersFunction extends AbstractFunction {
    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        FundProfileInfo fundDetail = (FundProfileInfo) env.get("fund");
        String managerName = FunctionUtils.getStringValue(arg1, env);

        boolean yes = fundDetail.getManagers() == null ? false : fundDetail.getManagers().contains(managerName);

        return AviatorBoolean.valueOf(yes);
    }

    @Override
    public String getName() {
        return "managers";
    }
}