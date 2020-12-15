package com.qianyitian.hope2.analyzer.service;

import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public FileSystemStorageService() {
        File to = new File(getRootPath());
        if(!to.exists()){
            to.mkdirs();
        }
    }

    private String getRootPath() {
        return "./data/report/";
    }

    @Override
    public void put(String fileName, String content) {
        File to = new File(getRootPath() + "/", fileName);
        try {
            Files.asCharSink(to, Charset.forName("UTF-8")).write(content);
        } catch (IOException e) {
            logger.error("store to aliyun OSS error " + fileName, e);
        }
    }

    @Override
    public String get(String fileName) {
        File from = new File(getRootPath() + "/", fileName);
        String rs;
        try {
            rs = Files.asCharSource(from, Charset.forName("UTF-8")).readFirstLine();
            return rs;
        } catch (IOException e) {
            logger.error("store to aliyun OSS error " + fileName, e);
        }
        return null;
    }
}
