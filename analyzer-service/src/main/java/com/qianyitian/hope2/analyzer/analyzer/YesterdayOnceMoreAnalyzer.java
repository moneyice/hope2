package com.qianyitian.hope2.analyzer.analyzer;

import com.qianyitian.hope2.analyzer.model.KLineInfo;
import com.qianyitian.hope2.analyzer.model.ResultInfo;
import com.qianyitian.hope2.analyzer.model.Stock;
import com.qianyitian.hope2.analyzer.util.Utils;

import java.util.List;

public class YesterdayOnceMoreAnalyzer extends AbstractStockAnalyzer {

    int daysToNow = 110;
    double range = 0.5;
    int rangeDays = 20;
    double highest = 0d;
    double lowest = 0.0d;

    KLineInfo current;
    double closeRangeTop = 0.3;
    double closeRangeDown = -0.5;

    public YesterdayOnceMoreAnalyzer() {
    }

    @Override
    public boolean analyze(ResultInfo resultInfo, Stock stock) {
        boolean ok = false;
        if (daysToNow <= rangeDays) {
            throw new RuntimeException("考察天数要大于涨幅天数");
        }

        List<KLineInfo> infos = stock.getkLineInfos();
        if (infos.size() <= daysToNow) {
            // 元数据天数要求大于考察天数
            return ok;
        }
        current = infos.get(infos.size() - 1);

        for (int i = infos.size() - daysToNow - 1; i + rangeDays < infos.size(); i++) {
            if (infos.size() <= daysToNow) {
                continue;
            }

            KLineInfo from = infos.get(i);
            KLineInfo end = infos.get(i + rangeDays);

            boolean condition1 = (end.getClose() - from.getClose())
                    / from.getClose() >= range;

            double r = (current.getClose() - from.getClose()) / from.getClose();
            boolean condition2 = r >= closeRangeDown && r <= closeRangeTop;

            if (condition1 && condition2) {
                String msg = format(stock, from, end);
                ok = true;
                resultInfo.appendMessage(msg);
                break;
            }
        }
        return ok;
    }

    public String format(Stock stock, KLineInfo from, KLineInfo end) {
        StringBuilder sb = new StringBuilder();
        sb.append(from.getDate()).append("  ")
                .append(from.getClose()).append("  --->   ");
        sb.append(end.getDate()).append("  ")
                .append(end.getClose());
        sb.append("\n");

        double risingRange = (end.getClose() - from.getClose())
                / from.getClose();
        sb.append("涨幅：").append(Utils.double2Percentage(risingRange))
                .append("\n");
        sb.append("现价: ").append(getCurrentPrice(stock)).append("\n\r");
        return (sb.toString());
    }

    @Override
    public String getDescription() {
        return "只考虑" + daysToNow + "天内的股票，曾经在" + rangeDays + "天内涨幅超过"
                + (range * 100) + "%，现值比前期低点涨幅大于 " + (closeRangeDown * 100)
                + "%， 小于 " + (closeRangeTop * 100) + "%\n";
    }

    public int getDaysToNow() {
        return daysToNow;
    }

    public void setDaysToNow(int daysToNow) {
        this.daysToNow = daysToNow;
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public int getRangeDays() {
        return rangeDays;
    }

    public void setRangeDays(int rangeDays) {
        this.rangeDays = rangeDays;
    }

    public double getCloseRangeTop() {
        return closeRangeTop;
    }

    public void setCloseRangeTop(double closeRangeTop) {
        this.closeRangeTop = closeRangeTop;
    }

    public double getCloseRangeDown() {
        return closeRangeDown;
    }

    public void setCloseRangeDown(double closeRangeDown) {
        this.closeRangeDown = closeRangeDown;
    }

    public int getWeight() {
        return 50;
    }
}
