package com.qianyitian.hope2.analyzer.util;

import com.qianyitian.hope2.analyzer.funds.model.FundBar;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


class UtilsTest {

    @Test
    void double2Percentage() {
        String s = Utils.double2Percentage(0.1234);
        System.out.println(s);
    }

    @Test
    void intDivide() {
        double i1 = (double) 5;
        int i2 = 6;
        System.out.println(i1 / i2);
    }

    @Test
    void testList() {
        List<Integer> list = Stream.of(1, 2, 3, 4, 5, 6).collect(Collectors.toList());
        System.out.println(list);
        List<Integer> integers = list.subList(list.size() - 1, 6);
        System.out.println(integers);

    }

    @Test
    void maxWithdrawal() {
        List<Integer> Datelist = Stream.of(4, 2, 7, 6, 5, 4, 7, 4, 8, 2, 15).collect(Collectors.toList());

        //最大值
        double maxValue = Integer.MIN_VALUE;
        //最小值
        double minValue = Integer.MAX_VALUE;
        //回撤值
        double withdrawal = Integer.MIN_VALUE;

        //符合回撤条件的最大值
        double tempMax = 0;
        //符合回撤条件的最小值
        double tempMin = 0;

        for (Integer value : Datelist) {
            if (maxValue < value) {
                maxValue = value;
                minValue = value;
            }
            if (minValue > value) {
                minValue = value;
            }
            //判断此次回撤是否超过了历史最大回撤
            if ((maxValue - minValue) / maxValue > withdrawal && maxValue != minValue) {
                tempMax = maxValue;
                tempMin = minValue;
                withdrawal = 1 - minValue / maxValue;
            }
        }
        System.out.println("最大回撤 " + withdrawal * 100 + "%");
        System.out.println("最大值 " + tempMax);
        System.out.println("最小值 " + tempMin);
    }


    @Test
    void maxdrawback3() {
        //初始化一个价格列表
        List<Integer> intList = Arrays.asList(3, 1, 6, 5, 4, 3, 6, 4, 8, 2, 9);

        //传换成K线
        List<FundBar> list = intList.stream().map(item -> {
            FundBar bar = new FundBar();
            bar.setValue(item);
            return bar;
        }).collect(Collectors.toList());

        //开始执行
        FundBar[] fundBars = AlgorithmUtil.maxDrawback(list, 100);
        //找到值为8的位置
        System.out.println(fundBars[0].getValue());
        //找到值为2的位置
        System.out.println(fundBars[1].getValue());
    }

    @Test
    void calcRange() {
        BigDecimal base = BigDecimal.valueOf(0.65);
        BigDecimal increase = BigDecimal.valueOf(25.34);
        double per = Utils.calcRange(base, increase);
        System.out.println(per);

        String valueChange = Utils.double2Percentage(Utils.calcRange(base, increase));
        System.out.println(valueChange);
    }
}