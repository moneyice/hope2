package com.qianyitian.hope2.spider.service;

import com.qianyitian.hope2.spider.config.PropertyConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DataService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    PropertyConfig propertyConfig;

    @Autowired
    private RestTemplate restTemplate;

    public String getFundProfile(String code) {
        try {
            String fund = restTemplate.getForObject(propertyConfig.getStockService() + "/data/fund/profile/" + code, String.class);
            return fund;
        } catch (Exception e) {
            logger.error(code);
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public String getFundDetail(String code) {
        try {
            String fund = restTemplate.getForObject(propertyConfig.getStockService() + "/data/fund/detail/" + code, String.class);
            return fund;
        } catch (Exception e) {
            logger.error(code);
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public void storeFundDetail(String code, String content) {
        try {
            restTemplate.postForObject(propertyConfig.getStockService() + "/fund/detail/" + code, content, String.class);
        } catch (Exception e) {
            logger.error(code);
            logger.error(e.getMessage(), e);
        }

    }

    public void storeFundProfile(String code, String content) {
        try {
            restTemplate.postForObject(propertyConfig.getStockService() + "/fund/profile/" + code, content, String.class);
        } catch (Exception e) {
            logger.error(code);
            logger.error(e.getMessage(), e);
        }
    }
}
