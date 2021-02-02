package com.qianyitian.hope2.spider.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.io.Resources;
import com.qianyitian.hope2.spider.config.PropertyConfig;
import com.qianyitian.hope2.spider.job.FundsInfoSpider;
import com.qianyitian.hope2.spider.job.IStockRetreiver;
import com.qianyitian.hope2.spider.job.StockInfoSpider;
import com.qianyitian.hope2.spider.model.Fund;
import com.qianyitian.hope2.spider.model.FundProfileInfo;
import com.qianyitian.hope2.spider.model.ManagerList;
import com.qianyitian.hope2.spider.model.Stock;
import com.qianyitian.hope2.spider.service.DataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class FundsController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource(name = "fundsInfoSpider")
    private FundsInfoSpider fundsInfoSpider = null;

    @Autowired
    DataService dataService;

    @Resource(name = "danjuanFundsRetreiver")
    private IStockRetreiver danjuanFundsRetreiver;

    @RequestMapping("/fund/refresh-detail")
    @Async
    public void forceRefreshStockInfo() {
        fundsInfoSpider.run();
    }

    @Async
    @RequestMapping("/fund/refresh-profile")
    public void summary() throws IOException {
        List<Stock> fundsSymbols = danjuanFundsRetreiver.getFundsSymbols();
        for (Stock stock : fundsSymbols) {
            String content = getFundDetail(stock.getCode());
            if (content == null) {
                continue;
            }
            Fund fundDetail = JSON.parseObject(content, Fund.class);

            if (fundDetail.getData() == null) {
                continue;
            }

            FundProfileInfo fundProfileInfo = new FundProfileInfo();
            fundProfileInfo.setCode(stock.getCode());
            fundProfileInfo.setName(stock.getName());


            //获取基金规模
            if (fundDetail.getData().getFundPosition() != null) {
                fundProfileInfo.setTotalShareNumber((float) fundDetail.getData().getFundPosition().getAssetVal());
            }
//            //获取基金经理
//            if (fundDetail.getData().getManagerList() != null) {
//                String value = fundDetail.getData().getManagerList().stream().map(manager -> manager.getName()).collect(Collectors.joining(" "));
//                fundProfileInfo.setManagers(value);
//            }

            //获取其他
            fillFundDetail(fundProfileInfo);
            storeFundProfile(fundProfileInfo.getCode(), JSON.toJSONString(fundProfileInfo));
        }
    }

    private void fillFundDetail(FundProfileInfo fundProfileInfo) throws IOException {
        String urlString = "https://danjuanapp.com/djapi/fund/{0}";
        String url = MessageFormat.format(urlString, fundProfileInfo.getCode());
        String content = Resources.asCharSource(new URL(url), Charset.forName("UTF-8")).readFirstLine();
        JSONObject fundDetail = JSON.parseObject(content);
        if (fundDetail.getIntValue("result_code") != 0) {
            return;
        }
        JSONObject data = fundDetail.getJSONObject("data");
        String found_date = data.getString("found_date");
        String totshare = data.getString("totshare");
        String manager_name = data.getString("manager_name");

        JSONObject fund_derived = data.getJSONObject("fund_derived");
        String end_date = fund_derived.getString("end_date");
        String unit_nav = fund_derived.getString("unit_nav");
        String nav_grtd = fund_derived.getString("nav_grtd");
        String nav_grl1m = fund_derived.getString("nav_grl1m");
        String nav_grl3m = fund_derived.getString("nav_grl3m");
        String nav_grl6m = fund_derived.getString("nav_grl6m");
        String nav_grlty = fund_derived.getString("nav_grlty");
        String nav_grl1y = fund_derived.getString("nav_grl1y");
        String nav_grl3y = fund_derived.getString("nav_grl3y");
        String nav_grl5y = fund_derived.getString("nav_grl5y");
        String nav_grbase = fund_derived.getString("nav_grbase");

        //指数型
        String type_desc = data.getString("type_desc");
        //"三星基金"
        String rating_desc = data.getString("rating_desc");

        fundProfileInfo.setFoundDate(LocalDate.parse(found_date));
        fundProfileInfo.setTotalShare(totshare);
        fundProfileInfo.setManagers(manager_name);
        fundProfileInfo.setCurrentDate(LocalDate.parse(end_date));
        fundProfileInfo.setNetValue(string2Float(unit_nav));
        fundProfileInfo.setGrToday(string2Float(nav_grtd));
        fundProfileInfo.setGrl1Month(string2Float(nav_grl1m));
        fundProfileInfo.setGrl3Month(string2Float(nav_grl3m));
        fundProfileInfo.setGrl6Month(string2Float(nav_grl6m));
        fundProfileInfo.setGrl1Year(string2Float(nav_grl1y));
        fundProfileInfo.setGrl3Year(string2Float(nav_grl3y));
        fundProfileInfo.setGrl5Year(string2Float(nav_grl5y));
        fundProfileInfo.setGrThisYear(string2Float(nav_grlty));
        fundProfileInfo.setGrBase(string2Float(nav_grbase));
        fundProfileInfo.setType(type_desc);
        fundProfileInfo.setMorningRate(rating_desc);
    }

    float string2Float(String number) {
        if (number == null) {
            return -1;
        } else {
            return Float.parseFloat(number);
        }
    }

    public String getFundDetail(String code) {
        return dataService.getFundDetail(code);
    }

    public void storeFundProfile(String code, String content) {
        dataService.storeFundProfile(code, content);
    }

}
