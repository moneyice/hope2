package com.qianyitian.hope2.analyzer.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public interface IReportStorageService {
    public void put(String fileName, String content);
    public String get(String fileName);
}
