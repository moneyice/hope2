package com.qianyitian.hope2.analyzer.model;

public class DemarkFlag {
    long setup;
    long countdown;
    String setupDate;
    String countdownDate;

    int setupNumber;
    int countdownNumber;

    public int getSetupNumber() {
        return setupNumber;
    }

    public void setSetupNumber(int setupNumber) {
        this.setupNumber = setupNumber;
    }

    public int getCountdownNumber() {
        return countdownNumber;
    }

    public void setCountdownNumber(int countdownNumber) {
        this.countdownNumber = countdownNumber;
    }

    public long getSetup() {
        return setup;
    }

    public void setSetup(long setup) {
        this.setup = setup;
    }

    public long getCountdown() {
        return countdown;
    }

    public void setCountdown(long countdown) {
        this.countdown = countdown;
    }

    public String getSetupDate() {
        return setupDate;
    }

    public void setSetupDate(String setupDate) {
        this.setupDate = setupDate;
    }

    public String getCountdownDate() {
        return countdownDate;
    }

    public void setCountdownDate(String countdownDate) {
        this.countdownDate = countdownDate;
    }
}
