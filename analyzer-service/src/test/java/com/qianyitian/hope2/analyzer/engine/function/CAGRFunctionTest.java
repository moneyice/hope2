package com.qianyitian.hope2.analyzer.engine.function;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CAGRFunctionTest {

    @Test
    void averageYearsTest() {
        CAGRFunction f = new CAGRFunction();
        LocalDate from = LocalDate.of(2017, 1, 1);
        LocalDate to = LocalDate.of(2018, 1, 2);
        double r = f.averageYears(from, to);
        System.out.println(r);
    }

    @Test
    void CAGRTest() {
        LocalDate from = LocalDate.of(2011, 12, 20);
        LocalDate to = LocalDate.of(2021, 2, 2);
        CAGRFunction f = new CAGRFunction();
        double years = f.averageYears(from, to);
        System.out.println(years);
        double cagr = f.CAGR(100, 100 + 570.1, years);
        System.out.println(cagr * 100);


        cagr = f.CAGR(1, 7, 10);
        System.out.println(cagr * 100);
        cagr = f.CAGR(1, 11, 10);
        System.out.println(cagr * 100);
    }

    @Test
    void CAGRTest2() {
        CAGRFunction f = new CAGRFunction();
        double r = f.CAGR(1, 2, 2);

        double growth = 100;
        double cagr = f.CAGR(100, 100 + growth, 2);
        System.out.println(cagr);
    }
}