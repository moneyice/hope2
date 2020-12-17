package com.qianyitian.hope2.analyzer.job;

import com.qianyitian.hope2.analyzer.job.policy28.*;

/**
 * Created by bing.a.qian on 10/5/2017.
 */
public class JobFacotry {

    public static BaseTwoEightJob getTwoEightJob(String version) {
        if (version.equals("r1")) {
            return new TwoEightJob();
        }
        if (version.equals("r2")) {
            return new TwoEightJob2();
        }
        if (version.equals("base300")) {
            return new TwoEightJobBase300();
        }
        if (version.equals("base500")) {
            return new TwoEightJobBase500();
        }
        if (version.equals("300")) {
            return new TwoEightJob300();
        }
        if (version.equals("500")) {
            return new TwoEightJob500();
        }
        return null;
    }

}
