package com.qianyitian.hope2.analyzer.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

public class Utils {
    public static String timeformat = "MM/dd/yyyy";
    public static SimpleDateFormat SF = new SimpleDateFormat(timeformat);

    public static SimpleDateFormat SF1 = new SimpleDateFormat("yyyy-MM-dd");
    public static ReentrantLock lock = new ReentrantLock();
    private static ThreadLocal<DateFormat> threadLocal = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat(timeformat);
        }
    };

    public static String format(Date time) {
        return SF1.format(time);
    }


    public static LocalDate parseDate(String date) {
        //严格按照ISO yyyy-MM-dd验证
        return LocalDate.parse(date);
    }

    public static double handleDouble(String x) {
        Double y;
        if (Pattern.matches("N/A", x)) {
            y = 0.00;
        } else {
            y = Double.parseDouble(x);
        }
        return y;
    }

    public static int handleInt(String x) {
        int y;
        if (Pattern.matches("N/A", x)) {
            y = 0;
        } else {
            y = Integer.parseInt(x);
        }
        return y;
    }

    public static long handleLong(String x) {
        long y;
        if (Pattern.matches("N/A", x)) {
            y = 0;
        } else {
            y = Long.parseLong(x);
        }
        return y;
    }

    // 保留2位小数
    public static double get2Double(double a) {
        BigDecimal b = new BigDecimal(a);
        return b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    // 保留2位小数
    public static float get2Float(double a) {
        BigDecimal b = new BigDecimal(a);
        return b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    public static String double2Percentage(double input) {
        NumberFormat num = NumberFormat.getPercentInstance();
        num.setMaximumIntegerDigits(6);
        num.setMaximumFractionDigits(2);
        String result = num.format(input);
        if(result==null){
            return "";
        }
        return result;
    }

    public static String calcRangeLabel(double base, double value) {
        double result = value / base - 1;
        return double2Percentage(result);
    }

    public static double calcRange(double base, double value) {
        double result = value / base - 1;
        return result;
    }

    public static double calcRange(BigDecimal base, BigDecimal increase) {
        double result = increase.divide(base, 2, RoundingMode.HALF_UP).subtract(BigDecimal.ONE).doubleValue();
        return result;
    }

    public static double calcRange(int base, int increase) {
        double result = ((double) increase) / base - 1;
        return get2Double(result);
    }


    private static String STOCK_URL_TEMPLATE_163 = "http://quotes.money.163.com/{0}.html";
    private static String STOCK_URL_TEMPLATE_10JQKA = "http://stockpage.10jqka.com.cn/{0}/";

    public static String convert163StockURL(String code) {
//        //http://quotes.money.163.com/0600188.html
//        //http://quotes.money.163.com/1002024.html
//        if(code.startsWith("6")){
//            code="0".concat(code);
//        }else{
//            code="1".concat(code);
//        }
        return MessageFormat.format(STOCK_URL_TEMPLATE_10JQKA, code);
    }

    public static BigDecimal convertYiYuan(BigDecimal number) {
        return number.divide(BigDecimal.valueOf(100000000)).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public static double getCAGR(LocalDate startDate, LocalDate endDate, double startPrice, double endPrice) {
        double years = averageYears(startDate, endDate);
        double cagr = CAGR(startPrice, endPrice, years);
        return cagr;
    }

    protected static double CAGR(double initialValue, double currentValue, double averageYears) {
        double cagr = Math.pow((currentValue / initialValue), (1 / averageYears)) - 1;
        return cagr * 100;
    }

    protected static double averageYears(LocalDate from, LocalDate to) {
        long betweenDays = ChronoUnit.DAYS.between(from, to);
        double averageYears = betweenDays / 365.2;
        return averageYears;
    }


    public static int compare(double a, double b) {
        if (a == b) {
            return 0;
        }
        return a - b > 0.0D ? 1 : -1;
    }
}
