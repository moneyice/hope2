package com.qianyitian.hope2.spider.util;

import java.text.*;
import java.time.LocalDate;
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

    public static Date format(String strDate) throws ParseException {

        // lock.lock();
        // try {
        // return SF.parse(strDate);
        // } finally {
        // lock.unlock();
        // }

        // synchronized (SF) {
        // return SF.parse(strDate);
        // }

        return threadLocal.get().parse(strDate);
    }

    public static String format(Date time) {
        return SF1.format(time);
    }

    public static LocalDate parseDate(String date) {
        //严格按照ISO yyyy-MM-dd验证
        return LocalDate.parse(date);
    }

    public static double handleDouble(String x) {
        try {
            return Double.parseDouble(x);
        } catch (Exception e) {
            return 0.00;
        }
    }

    public static float handleFloat(String x) {
        try {
            return Float.parseFloat(x);
        } catch (Exception e) {
            return 0.00F;
        }
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
        DecimalFormat df = new DecimalFormat("0.00");
        return new Double(df.format(a).toString());
    }

    public static String double2Percentage(double input) {
        NumberFormat num = NumberFormat.getPercentInstance();
        num.setMaximumIntegerDigits(3);
        num.setMaximumFractionDigits(2);
        String result = num.format(input);
        return result;
    }

    public static String double2Currency(double input) {
        NumberFormat num = NumberFormat.getCurrencyInstance();
        num.setMaximumIntegerDigits(5);
        num.setMaximumFractionDigits(2);
        String result = num.format(input);
        return result;
    }

    public static void sleep(long n) {
        try {
            Thread.sleep(n);
        } catch (Exception e) {

        }
    }

    public static boolean stringEquals(String s1, String s2) {
        if (s1 == null && s2 == null) {
            return true;
        }
        if (s1 != null) {
            return s1.equals(s2);
        } else {
            return s2.equals(s1);
        }
    }
    public static double calcRange(double base, double value) {
        double result = value / base - 1;
        return result;
    }
}
