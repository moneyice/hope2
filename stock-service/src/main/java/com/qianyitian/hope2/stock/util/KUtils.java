package com.qianyitian.hope2.stock.util;


import com.qianyitian.hope2.stock.config.Constant;
import com.qianyitian.hope2.stock.config.EStockKlineType;
import com.qianyitian.hope2.stock.model.KLineInfo;
import com.qianyitian.hope2.stock.model.Macd;
import com.qianyitian.hope2.stock.model.Stock;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bing.a.qian on 5/18/2017.
 */
public class KUtils {

    private static int MACD_SHORT = 12;
    private static int MACD_LONG = 26;
    private static int MACD_M = 9;

    public static Stock daily2Weekly(Stock stock) {
        List<KLineInfo> weeklyInfos = new LinkedList<KLineInfo>();
        WeekFields wf = WeekFields.ISO;

        List<KLineInfo> dailyInfoList = stock.getkLineInfos();
        int weekInYearIndex = -1;
        KLineInfo weeklyInfo = null;
        for (KLineInfo dailyInfo : dailyInfoList) {
            LocalDate localDate = dailyInfo.getDate();

            int currentWeekInYear = localDate.get(wf.weekOfWeekBasedYear());
            if (weekInYearIndex != currentWeekInYear) {
                weeklyInfo = new KLineInfo();
                weeklyInfo.setHigh(0);
                weeklyInfo.setLow(999999);
                weeklyInfos.add(weeklyInfo);
                weekInYearIndex = currentWeekInYear;
            }
            addDailyInfoToWeeklyOrMonthly(weeklyInfo, dailyInfo);

        }
        Stock newStock = new Stock();
        newStock.setCode(stock.getCode());
        newStock.setMarket(stock.getMarket());
        newStock.setName(stock.getName());
        newStock.setkLineInfos(weeklyInfos);
        newStock.setkLineType(EStockKlineType.WEEKLY.getName());
        return newStock;
    }

    private static void addDailyInfoToWeeklyOrMonthly(KLineInfo weeklyOrMonthInfo, KLineInfo dailyInfo) {
        if (weeklyOrMonthInfo.getOpen() <= 0.0d) {
            weeklyOrMonthInfo.setOpen(dailyInfo.getOpen());
        }

        if (dailyInfo.getHigh() > weeklyOrMonthInfo.getHigh()) {
            weeklyOrMonthInfo.setHigh(dailyInfo.getHigh());
        }

        if (dailyInfo.getLow() < weeklyOrMonthInfo.getLow()) {
            weeklyOrMonthInfo.setLow(dailyInfo.getLow());
        }

        weeklyOrMonthInfo.setClose(dailyInfo.getClose());

        //weeklyInfo.setVolume(weeklyInfo.getVolume() + dailyInfo.getVolume());
        weeklyOrMonthInfo.setTurnoverRate(weeklyOrMonthInfo.getTurnoverRate() + dailyInfo.getTurnoverRate());
        weeklyOrMonthInfo.setDate(dailyInfo.getDate());
    }

    public static Stock daily2Monthly(Stock stock) {
        List<KLineInfo> monthlyInfos = new LinkedList<KLineInfo>();
        List<KLineInfo> dailyInfoList = stock.getkLineInfos();
        int monthIndex = -1;
        KLineInfo monthlyInfo = null;

        for (int i = 0; i < dailyInfoList.size(); i++) {
            KLineInfo dailyInfo = dailyInfoList.get(i);

            LocalDate localDate = dailyInfo.getDate();
            int currentMonth = localDate.getMonthValue();

            if (monthIndex != currentMonth) {
                if (i > 0) {
                    //TODO to many useless setting here
                    setKLineInfoLastDay(monthlyInfo, dailyInfoList.get(i - 1));
                    monthlyInfos.add(monthlyInfo);
                }
                monthlyInfo = new KLineInfo();
                monthlyInfo.setHigh(0);
                monthlyInfo.setLow(999999);
                monthlyInfo.setOpen(dailyInfo.getOpen());
                monthIndex = currentMonth;
            }
            monthlyInfo.setTurnoverRate(monthlyInfo.getTurnoverRate() + dailyInfo.getTurnoverRate());
            if (dailyInfo.getHigh() > monthlyInfo.getHigh()) {
                monthlyInfo.setHigh(dailyInfo.getHigh());
            }

            if (dailyInfo.getLow() < monthlyInfo.getLow()) {
                monthlyInfo.setLow(dailyInfo.getLow());
            }
        }
        if (!dailyInfoList.isEmpty()) {
            //for the last day
            setKLineInfoLastDay(monthlyInfo, dailyInfoList.get(dailyInfoList.size() - 1));
            monthlyInfos.add(monthlyInfo);
        }


        Stock newStock = new Stock();
        newStock.setCode(stock.getCode());
        newStock.setMarket(stock.getMarket());
        newStock.setName(stock.getName());
        newStock.setkLineInfos(monthlyInfos);
        newStock.setkLineType(EStockKlineType.MONTHLY.getName());
        return newStock;
    }

    private static void setKLineInfoLastDay(KLineInfo monthlyInfo, KLineInfo lastDay) {
        monthlyInfo.setClose(lastDay.getClose());
        monthlyInfo.setDate(lastDay.getDate());
    }

    public static void appendMacdInfo(List<KLineInfo> kLineInfos) {
        if (kLineInfos.isEmpty()) {
            return;
        }
        Macd previous = null;

        previous = new Macd();
        KLineInfo firstDayInfo = kLineInfos.get(0);
        previous.setkLineInfo(firstDayInfo);

        double ema12 = firstDayInfo.getClose();
        double ema26 = firstDayInfo.getClose();
        double diff = ema12 - ema26;
        double dea = diff;
        double macdValue = 2.0 * (diff - dea);
        previous.setEma12(ema12);
        previous.setEma26(ema26);
        previous.setDiff(diff);
        previous.setDea(dea);
        previous.setBar(macdValue);
        {
            firstDayInfo.setDea(previous.getDea());
            firstDayInfo.setDiff(previous.getDiff());
            firstDayInfo.setMacd(previous.getBar());
        }

        for (int i = 1; i < kLineInfos.size(); i++) {
            Macd macd = calculateMacd(kLineInfos.get(i), previous);
            {
                kLineInfos.get(i).setDea(macd.getDea());
                kLineInfos.get(i).setDiff(macd.getDiff());
                kLineInfos.get(i).setMacd(macd.getBar());
            }
            previous = macd;
        }
    }

    private static Macd calculateMacd(KLineInfo kLineInfo, Macd previous) {
//		EMA（12）= 前一日EMA（12）×11/13＋今日收盘价×2/13
//		EMA（26）= 前一日EMA（26）×25/27＋今日收盘价×2/27
//		DIFF=今日EMA（12）- 今日EMA（26）
//		DEA（MACD）= 前一日DEA×8/10＋今日DIF×2/10
//		BAR=2×(DIFF－DEA)
        double ema12 = (2 * kLineInfo.getClose() + (MACD_SHORT - 1) * previous.getEma12()) / (MACD_SHORT + 1);
        double ema26 = (2 * kLineInfo.getClose() + (MACD_LONG - 1) * previous.getEma26()) / (MACD_LONG + 1);
        double diff = ema12 - ema26;
        double dea = (2 * diff + (MACD_M - 1) * previous.getDea()) / (MACD_M + 1);
        double macdValue = 2.0 * (diff - dea);

        Macd macd = new Macd();
        macd.setEma12(ema12);
        macd.setEma26(ema26);
        macd.setDiff(diff);
        macd.setDea(dea);
        macd.setBar(macdValue);
        return macd;
    }

    public static KLineInfo findKLine(List<KLineInfo> kLineList, int days2Now) {
        if (days2Now >= kLineList.size()) {
            days2Now = kLineList.size() - 1;
        }
        KLineInfo find = kLineList.get(kLineList.size() - 1 - days2Now);
        return find;
    }

    public static double calcIncreaseRange(double base, double now) {
        double r = now / base - 1;
        return r;
    }
}
