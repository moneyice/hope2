package com.qianyitian.hope2.analyzer.analyzer;

import com.qianyitian.hope2.analyzer.model.KLineInfo;
import com.qianyitian.hope2.analyzer.model.ResultInfo;
import com.qianyitian.hope2.analyzer.model.Stock;

import java.util.List;

//n天之内某一天的跌幅超过x，成交量小于前一天百分之50；
public class SuddentLowVolumeAnalyzer extends AbstractStockAnalyzer {

    // consider how many days before now.
    int daysToNow = 5;

    // decrease x%
    double decrease = -4;

    public SuddentLowVolumeAnalyzer() {
    }

    @Override
    public boolean analyze(ResultInfo resultInfo, Stock stock) {
        boolean ok = false;
        // setStock(stock);
        List<KLineInfo> infos = stock.getkLineInfos();
        if (infos.size() <= daysToNow + 1) {
            // 元数据天数要求大于考察天数
            return ok;
        }
        for (int i = infos.size() - daysToNow - 1; i < infos.size(); i++) {
            KLineInfo toCheck = infos.get(i);
            KLineInfo compare = infos.get(i - 1);

            double decreaseReage = (toCheck.getClose() - toCheck.getOpen())
                    / toCheck.getOpen();
            double percent = ((double) (toCheck.getTurnoverRate() - compare
                    .getTurnoverRate())) / compare.getTurnoverRate();

            boolean condition1 = decreaseReage * 100 <= decrease;
            boolean condition2 = percent * 100 <= -50;

            if (condition1 && condition2) {
                String msg = format(stock, toCheck);
                resultInfo.appendMessage(msg);
                ok = true;
                break;
            }
        }
        return ok;
    }

    public String format(Stock stock, KLineInfo check) {
        StringBuilder sb = new StringBuilder();
        sb.append(stock.getCode()).append("  ").append(stock.getName())
                .append("\n");
        sb.append("时间：").append(check.getDate()).append("\n");
        sb.append(
                "跌幅：" + (check.getClose() - check.getOpen()) / check.getOpen()
                        * 100).append("%\n");
        sb.append("现价: ").append(getCurrentPrice(stock)).append("\n\r");
        return (sb.toString());
    }

    @Override
    public String getDescription() {
        return daysToNow + "天之内某一天的跌幅超过" + decrease + "，成交量小于前一天百分之50";
    }

    public int getDaysToNow() {
        return daysToNow;
    }

    public void setDaysToNow(int daysToNow) {
        this.daysToNow = daysToNow;
    }
}
