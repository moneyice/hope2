/**
 * Copyright 2021 jb51.net
 */
package com.qianyitian.hope2.spider.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

/**
 * Auto-generated: 2021-01-27 10:33:18
 *
 * @author jb51.net (i@jb51.net)
 * @website http://tools.jb51.net/code/json2javabean
 */
public class AchievementList {

    @JSONField(name = "fund_code")
    private String fundCode;
    private String fundsname;
    @JSONField(name = "post_date")
    private Date postDate;
    @JSONField(name = "cp_rate")
    private double cpRate;

    public void setFundCode(String fundCode) {
        this.fundCode = fundCode;
    }

    public String getFundCode() {
        return fundCode;
    }

    public void setFundsname(String fundsname) {
        this.fundsname = fundsname;
    }

    public String getFundsname() {
        return fundsname;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setCpRate(double cpRate) {
        this.cpRate = cpRate;
    }

    public double getCpRate() {
        return cpRate;
    }

}