package com.qianyitian.hope2.analyzer.analyzer;

import com.qianyitian.hope2.analyzer.model.KLineInfo;
import com.qianyitian.hope2.analyzer.model.ResultInfo;
import com.qianyitian.hope2.analyzer.model.Stock;
import com.qianyitian.hope2.analyzer.util.Utils;

import java.util.List;

//n天之内某一天成交量是前一天的  x 倍
public class SuddentHighVolumeLiteAnalyzer extends SuddentHighVolumeAnalyzer {

    String descTemplate = "在2个工作日内，发生过1次成交量是前一天的2.4倍,换手大于1.2%,涨幅大于0";

    public SuddentHighVolumeLiteAnalyzer() {
        setDaysToNow("3");
        setOccurTimes("1");
        setVolumeIncrease("2.4");
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
            if (actualVolumeIncrease >= volumeIncrease&&toCheck.getTurnoverRate()>1.2&&toCheck.getClose()>toCheck.getOpen()) {
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
        sb.append(stock.getCode()).append("  ").append(stock.getName())
                .append("\n");
//		sb.append("时间：").append(check.getTime()).append("\n");
//		sb.append("成交量放大倍数：" + times).append("\n");
//		sb.append("现价: ").append(getCurrentPrice(stock)).append("\n\r");
        return (sb.toString());
    }

    @Override
    public String getDescription() {
        return descTemplate;
    }
}
