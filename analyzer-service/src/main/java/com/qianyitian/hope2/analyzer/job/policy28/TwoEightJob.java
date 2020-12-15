package com.qianyitian.hope2.analyzer.job.policy28;

import com.qianyitian.hope2.analyzer.model.KLineInfo;
import com.qianyitian.hope2.analyzer.util.BusinessException;

/**
 * Created by bing.a.qian on 10/2/2017.
 */
public class TwoEightJob extends BaseTwoEightJob{

    protected void operate(KLineInfo current300, double week4Range300, KLineInfo current500, double week4Range500) throws BusinessException {
        if(week4Range300>week4Range500){
            //buy 300index
            if(is300Position()) {
                return;
            }
            else if(is500Position()) {
                account.clear(INDEX_500_CODE,current500.getClose(),current500.getDate());
            }
            account.allIn(INDEX_300_CODE,current300.getClose(),current300.getDate());
        }else if(week4Range300<week4Range500){
            if(is500Position()){
                return;
            } else if(is300Position()) {
                account.clear(INDEX_300_CODE,current300.getClose(),current300.getDate());
            }
            account.allIn(INDEX_500_CODE,current500.getClose(),current500.getDate());
        }else{
            return;
        }
    }
}
