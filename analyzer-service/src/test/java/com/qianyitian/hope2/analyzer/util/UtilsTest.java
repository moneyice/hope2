package com.qianyitian.hope2.analyzer.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    @Test
    void double2Percentage() {
        String s = Utils.double2Percentage(0.1234);
        System.out.println(s);
    }
}