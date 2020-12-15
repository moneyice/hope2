package com.qianyitian.hope2.analyzer.service;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.IOUtils;
import com.aliyun.oss.model.OSSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * This sample demonstrates how to upload/download an object to/from
 * Aliyun OSS using the OSS SDK for Java.
 */
@Service("aliyunOSSStorageService")
public class AliyunOSSStorageService implements IReportStorageService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private static String endpoint = "oss-cn-zhangjiakou.aliyuncs.com";
    private static String accessKeyId = "y3y3mL89zT4N0YUR";
    private static String accessKeySecret = "Ub25yu6ymY0CJ0LQ7J0c09ZtuYoI1o";
    private static String bucketName = "hhope";
    private static String key = "samplekey";
    private  OSSClient ossClient;
    private InputStream inputStream;

    public AliyunOSSStorageService() {

    }

    @Override
    public void put(String fileName, String content){
        try {
            ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
            ossClient.putObject(bucketName, fileName, new ByteArrayInputStream(content.getBytes()));
        } catch (ClientException ce) {
            logger.error("store to aliyun OSS error " + fileName,ce);
        } finally {
            ossClient.shutdown();
        }
    }

    @Override
    public String get(String fileName){
        InputStream inputStream=null;
        try {
            ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
            OSSObject object = ossClient.getObject(bucketName, fileName);
            inputStream = object.getObjectContent();

            String result = toString(inputStream);
            return result;
        } catch (Exception ce) {
            logger.error("store to OSS error",ce);
        } finally {
            IOUtils.safeClose(inputStream);
            ossClient.shutdown();
        }
        return null;
    }



    private String toString(InputStream inputStream)  {
        ByteArrayOutputStream result=null;
        try {
            result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            String s = result.toString("UTF-8");
            return s;
        }catch (Exception e){
            logger.error("toString error",e);
        }finally {
            IOUtils.safeClose(inputStream);
            IOUtils.safeClose(result);
        }
        return null;
    }
}
