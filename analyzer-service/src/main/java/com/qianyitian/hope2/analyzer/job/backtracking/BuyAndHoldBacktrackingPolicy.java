package com.qianyitian.hope2.analyzer.job.backtracking;

import com.qianyitian.hope2.analyzer.model.ETradeType;
import com.qianyitian.hope2.analyzer.model.KLineInfo;

import java.util.List;

/**
 * 买入并一直持有
 */
public class BuyAndHoldBacktrackingPolicy implements IBacktrackingPolicy{

    boolean ifBought=false;
    public ETradeType check(int index, List<KLineInfo> kLineInfos) {
        if(ifBought){
            return ETradeType.NoAction;
        }else{
            ifBought=true;
            return ETradeType.Buy;
        }
    }
}
