package com.qianyitian.hope2.analyzer.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public interface IReportStorageService {
    public void storeAnalysis(String fileName, String content);

    public String getAnalysis(String fileName);

    public void storeStatistics(String fileName, String content);

    public String getStatistics(String fileName);

}
