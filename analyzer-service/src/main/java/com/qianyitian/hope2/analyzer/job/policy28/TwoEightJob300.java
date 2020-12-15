package com.qianyitian.hope2.analyzer.job.policy28;

import com.qianyitian.hope2.analyzer.model.KLineInfo;
import com.qianyitian.hope2.analyzer.util.BusinessException;

/**
 * Created by bing.a.qian on 17/3/2018.
 */
public class TwoEightJob300 extends BaseTwoEightJob{

    protected void operate(KLineInfo current300, double week4Range300, KLineInfo current500, double week4Range500) throws BusinessException {
        if(is300Position()) {
            return;
        }else{
            account.allIn(INDEX_300_CODE,current300.getClose(),current300.getDate());
        }
    }
}
