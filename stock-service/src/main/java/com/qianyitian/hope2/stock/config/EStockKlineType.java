package com.qianyitian.hope2.stock.config;

public enum EStockKlineType {
    DAILY("daily", "daily", ".daily"),
    DAILY_LITE("dailylite", "dailylite", ".dailylite"),
    WEEKLY("weekly", "weekly", ".weekly"),
    MONTHLY("monthly", "monthly", ".monthly");

    private String name;
    String folderName;
    String fileSuffix;

    EStockKlineType(String name, String folderName, String fileSuffix) {
        this.name = name;
        this.folderName = folderName;
        this.fileSuffix = fileSuffix;
    }

    public String getFolderName() {
        return folderName;
    }

    public String getFileSuffix() {
        return fileSuffix;
    }

    public String getName() {
        return name;
    }
}
