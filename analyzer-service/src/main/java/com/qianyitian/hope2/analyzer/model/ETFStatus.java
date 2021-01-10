package com.qianyitian.hope2.analyzer.model;

import org.springframework.util.StringUtils;

import java.util.Map;

public class ETFStatus {
    String code;
    String name;
    double thisWeekRange;
    double previousWeekRange;
    String thisWeekRangeLabel;
    String previousWeekRangeLabel;
    int thisRank;
    int previousRank;
    String thisHold = "";
    String previousHold = "";

    String msg = "";
    String url;
    String comments = null;



    public String getThisHold() {
        return thisHold;
    }

    public void setThisHold(String thisHold) {
        this.thisHold = thisHold;
    }

    public String getPreviousHold() {
        return previousHold;
    }

    public void setPreviousHold(String previousHold) {
        this.previousHold = previousHold;
    }

    public double getThisWeekRange() {
        return thisWeekRange;
    }

    public void setThisWeekRange(double thisWeekRange) {
        this.thisWeekRange = thisWeekRange;
    }

    public double getPreviousWeekRange() {
        return previousWeekRange;
    }

    public void setPreviousWeekRange(double previousWeekRange) {
        this.previousWeekRange = previousWeekRange;
    }

    public String getThisWeekRangeLabel() {
        return thisWeekRangeLabel;
    }

    public void setThisWeekRangeLabel(String thisWeekRangeLabel) {
        this.thisWeekRangeLabel = thisWeekRangeLabel;
    }

    public String getPreviousWeekRangeLabel() {
        return previousWeekRangeLabel;
    }

    public void setPreviousWeekRangeLabel(String previousWeekRangeLabel) {
        this.previousWeekRangeLabel = previousWeekRangeLabel;
    }

    public int getThisRank() {
        return thisRank;
    }

    public void setThisRank(int thisRank) {
        this.thisRank = thisRank;
    }

    public int getPreviousRank() {
        return previousRank;
    }

    public void setPreviousRank(int previousRank) {
        this.previousRank = previousRank;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMsg() {
        return msg;
    }


    public void appendMessage(String msg) {
        if (StringUtils.isEmpty(this.msg)) {
            this.msg = msg;
        } else {
            this.msg = this.msg + msg;
        }
    }
}
