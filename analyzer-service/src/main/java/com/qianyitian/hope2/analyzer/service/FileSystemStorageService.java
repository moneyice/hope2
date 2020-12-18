package com.qianyitian.hope2.analyzer.service;

import com.google.common.io.Files;
import com.qianyitian.hope2.analyzer.config.Constant;
import com.qianyitian.hope2.analyzer.config.PropertyConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.Charset;

/**
 * This sample demonstrates how to upload/download an object to/from
 * Aliyun OSS using the OSS SDK for Java.
 */
@Service("fileSystemStorageService")
public class FileSystemStorageService implements IReportStorageService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    PropertyConfig propertyConfig;

    public FileSystemStorageService() {
    }

    private File getAnalysisRootPath() {
        return new File(propertyConfig.getDataPath(), Constant.REPORT);
    }

    private File getStatisticsRootPath() {
        return new File(propertyConfig.getDataPath(), Constant.STATISTICS);
    }


    private void storeFile(File folder, String fileName, String content) {
        File to = new File(folder, fileName);
        try {
            Files.asCharSink(to, Charset.forName("UTF-8")).write(content);
        } catch (IOException e) {
            logger.error("store to aliyun OSS error " + fileName, e);
        }
    }

    private String getFile(File folder, String fileName) {
        File from = new File(folder, fileName);
        String rs = null;
        try {
            rs = Files.asCharSource(from, Charset.forName("UTF-8")).readFirstLine();
        } catch (IOException e) {
            logger.error("store to aliyun OSS error " + fileName, e);
        }
        return rs;
    }

    @Override
    public void storeAnalysis(String fileName, String content) {
        storeFile(getAnalysisRootPath(), fileName, content);
    }

    @Override
    public String getAnalysis(String fileName) {
        return getFile(getAnalysisRootPath(), fileName);
    }

    @Override
    public void storeStatistics(String fileName, String content) {
        storeFile(getStatisticsRootPath(), fileName, content);
    }

    @Override
    public String getStatistics(String fileName) {
        return getFile(getStatisticsRootPath(), fileName);
    }
}
