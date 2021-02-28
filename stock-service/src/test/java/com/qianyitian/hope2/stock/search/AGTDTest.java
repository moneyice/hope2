package com.qianyitian.hope2.stock.search;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.qianyitian.hope2.stock.model.KLineInfo;
import com.qianyitian.hope2.stock.model.Stock;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class AGTDTest {

    void AgTDAverage21DaysTest() throws IOException {
        File file = new File("C:\\new_tdx\\T0002\\export\\agtd.csv");

        List<String> lines = Files.readLines(file, Charset.forName("UTF-8"));

        List<KLineInfo> infoList = new ArrayList<>();
        for (String line : lines) {
            KLineInfo info = new KLineInfo();
            String[] array = line.split(",");
//            LocalDate date = LocalDate.parse(array[0]);
            info.setOpen(Double.valueOf(array[1]));
            info.setHigh(Double.valueOf(array[2]));
            info.setLow(Double.valueOf(array[3]));
            info.setClose(Double.valueOf(array[4]));
            infoList.add(info);
        }
        calcAvg21Price(infoList);
        //计算正向
        calHigh(infoList);
        calHigh2(infoList);

        //计算负向
        calLow(infoList);
        calLow2(infoList);
    }

    private void calLow(List<KLineInfo> infoList) {
        double totalRise = 0;
        int number = 0;
        for (int i = 0; i < infoList.size(); i++) {
            KLineInfo info = infoList.get(i);
            if (info.getOpen() < info.getMacd()) {
                //如果< 21天均值
                double rise = info.getLow() - info.getOpen();
                totalRise = totalRise + rise;
                number++;
            }
        }

        System.out.println("做空 开盘价到最低阶  " + totalRise* -1 / number );
        System.out.println("胜率为   " + (number*1.0  / (infoList.size()-avg21)));
    }

    private void calLow2(List<KLineInfo> infoList) {
        double totalRise = 0;
        int number = 0;
        for (int i = 0; i < infoList.size(); i++) {
            KLineInfo info = infoList.get(i);
            if (info.getOpen() < info.getMacd()) {
                //如果< 21天均值
                double rise = info.getOpen()-info.getHigh();
                totalRise = totalRise + rise;
                number++;
            }
        }

        System.out.println("做空 开盘价到最高价  " + totalRise* -1 / number );

    }


    private void calHigh(List<KLineInfo> infoList) {
        double totalRise = 0;
        int number = 0;
        for (int i = 0; i < infoList.size(); i++) {
            KLineInfo info = infoList.get(i);
            if (info.getOpen() >= info.getMacd()) {
                //如果>= 21天均值
                double rise = info.getHigh() - info.getOpen();
                totalRise = totalRise + rise;
                number++;
            }
        }

        System.out.println("做多  开盘到最高价  " + totalRise  / number);
        System.out.println("胜率为   " + (number*1.0  / (infoList.size()-avg21)));
    }
    private void calHigh2(List<KLineInfo> infoList) {
        double totalRise = 0;
        int number = 0;
        for (int i = 0; i < infoList.size(); i++) {
            KLineInfo info = infoList.get(i);
            if (info.getOpen() >= info.getMacd()) {
                //如果>= 21天均值
                double rise = info.getOpen()-info.getLow();
                totalRise = totalRise + rise;
                number++;
            }
        }

        System.out.println("做多  开盘到最低价  " + totalRise  / number);
    }

    int avg21 = 21;

    private void calcAvg21Price(List<KLineInfo> infoList) {
        for (int i = avg21; i < infoList.size(); i++) {
            KLineInfo info = infoList.get(i);
            double avg = pre21DaysAvgPrice(infoList, i);
            //使用 macd 保存下 21天均值
            info.setMacd(avg);
        }
    }

    private double pre21DaysAvgPrice(List<KLineInfo> infoList, int i) {
        double total = 0;
        for (int j = i - avg21; j < i; j++) {
            total = total + infoList.get(j).getClose();
        }
        return total / avg21;
    }
}