package com.qianyitian.hope2.analyzer.job.policy28;

import com.qianyitian.hope2.analyzer.model.KLineInfo;
import com.qianyitian.hope2.analyzer.util.BusinessException;

/**
 * Created by bing.a.qian on 10/2/2017.
 */
public class TwoEightJobBase300 extends BaseTwoEightJob{

    @Override
    protected void operate(KLineInfo current300, double week4Range300, KLineInfo current500, double week4Range500) throws BusinessException {
        if(is300Position()) {
            if(week4Range300>=0){
                return;
            }else{
                account.clear(INDEX_300_CODE,current300.getClose(),current300.getDate());
            }
        }else{
            if(week4Range300>=0){
                account.allIn(INDEX_300_CODE,current300.getClose(),current300.getDate());
            }
        }
    }
}
