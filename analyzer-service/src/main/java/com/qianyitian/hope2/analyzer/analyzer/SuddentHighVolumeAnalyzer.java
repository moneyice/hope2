package com.qianyitian.hope2.analyzer.analyzer;

import com.qianyitian.hope2.analyzer.model.KLineInfo;
import com.qianyitian.hope2.analyzer.model.ResultInfo;
import com.qianyitian.hope2.analyzer.model.Stock;
import com.qianyitian.hope2.analyzer.util.Utils;

import java.util.List;

//n天之内某一天成交量是前一天的  x 倍
public class SuddentHighVolumeAnalyzer extends AbstractStockAnalyzer {

    String descTemplate = "在%d个工作日内，发生过%d次成交量是前一天的%f倍";
    // consider how many days before now.
    int daysToNow = 20;
    //how many times this occurs
    int occurTimes = 3;

    // volume increase times
    double volumeIncrease = 2.5;

    double[] riseRange = {0.001, 0.06};

    public SuddentHighVolumeAnalyzer() {
    }

    @Override
    public boolean analyze(ResultInfo resultInfo, Stock stock) {
        boolean ok = false;
        List<KLineInfo> infos = stock.getkLineInfos();
        if (infos.size() < daysToNow + 1) {
            // 元数据天数要求大于等于考察天数+1
            return ok;
        }
        int actualOccurTimes = 0;
        for (int i = infos.size() - daysToNow; i < infos.size(); i++) {
            KLineInfo toCheck = infos.get(i);

            KLineInfo compare = infos.get(i - 1);
            double actualVolumeIncrease = ((double) toCheck.getTurnoverRate() / compare.getTurnoverRate());
            actualVolumeIncrease = Utils.get2Double(actualVolumeIncrease);
            if (actualVolumeIncrease >= volumeIncrease) {
                actualOccurTimes++;
            }
            if (actualOccurTimes >= occurTimes) {
                String msg = format(stock, toCheck, actualVolumeIncrease);
                resultInfo.appendMessage(msg);
                ok = true;
                break;
            }
//			boolean condition = actualVolumeIncrease >= volumeIncrease;
//			double risePercentage = (toCheck.getClose() - compare.getClose())
//					/ toCheck.getClose();

//			boolean condition2 = risePercentage >= riseRange[0]
//					&& risePercentage <= riseRange[1];
        }
        return ok;
    }

    private String format(Stock stock, KLineInfo check, double times) {
        StringBuilder sb = new StringBuilder();
//		sb.append("时间：").append(check.getTime()).append("\n");
//		sb.append("成交量放大倍数：" + times).append("\n");
//		sb.append("现价: ").append(getCurrentPrice(stock)).append("\n\r");
        return (sb.toString());
    }

    @Override
    public String getDescription() {
        return String.format(descTemplate, daysToNow, occurTimes, volumeIncrease);
    }

    public int getDaysToNow() {
        return daysToNow;
    }

    public void setDaysToNow(String daysToNow) {
        this.daysToNow = Integer.valueOf(daysToNow);
    }

    public void setOccurTimes(String occurTimes) {
        this.occurTimes = Integer.valueOf(occurTimes);
    }

    public void setVolumeIncrease(String volumeIncrease) {
        this.volumeIncrease = Double.valueOf(volumeIncrease);
    }
}
