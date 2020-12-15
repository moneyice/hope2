package com.qianyitian.hope2.analyzer.analyzer;

import com.qianyitian.hope2.analyzer.model.KLineInfo;
import com.qianyitian.hope2.analyzer.model.ResultInfo;
import com.qianyitian.hope2.analyzer.model.Stock;
import com.qianyitian.hope2.analyzer.util.Utils;

import java.util.List;

public class MACDAdvanceAnalyzer extends AbstractStockAnalyzer {

    // consider how many days before now.
    int daysToNow = 30;

    public MACDAdvanceAnalyzer() {
    }

    @Override
    public boolean analyze(ResultInfo resultInfo, Stock stock) {
        boolean ok = false;
        List<KLineInfo> infos = stock.getkLineInfos();
        if (infos.size() <= daysToNow) {
            // 元数据天数要求大于考察天数
            return ok;
        }

        double sum30Days=0;
        boolean ifMacdLess0=false;
        for (int i = infos.size() - daysToNow -1; i < infos.size()-1; i++) {
            KLineInfo toCheck = infos.get(i);
            sum30Days+=toCheck.getClose();
            if(toCheck.getMacd()<=0){
                ifMacdLess0=true;
            }
        }

        double average30Days= sum30Days/daysToNow;
        KLineInfo today = infos.get(infos.size()-1);
        KLineInfo yesterday=infos.get(infos.size()-2);
        boolean IsMacdNowLarger0=today.getMacd()>=0&&today.getMacd()>=yesterday.getMacd();
        boolean IsPriceLargerAverage30Days=today.getClose()>=average30Days;
        ok=ifMacdLess0&&IsMacdNowLarger0&&IsPriceLargerAverage30Days;
        if (ok) {
            String msg=format(average30Days,today.getClose());
            resultInfo.appendMessage(msg);
        }
        return ok;
    }

    public String format(double base, double close) {
        return Utils.calcRange(base,close);
    }

    @Override
    public String getDescription() {
        return daysToNow+" 天之内 MACD小于0过，目前MACD大于0，股价高于30天均线 ";
    }

    public int getDaysToNow() {
        return daysToNow;
    }

    public void setDaysToNow(int daysToNow) {
        this.daysToNow = daysToNow;
    }


}
