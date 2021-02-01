/**
  * Copyright 2021 jb51.net 
  */
package com.qianyitian.hope2.spider.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * Auto-generated: 2021-01-27 10:33:18
 *
 * @author jb51.net (i@jb51.net)
 * @website http://tools.jb51.net/code/json2javabean
 */
public class Data {

    @JSONField(name = "fund_company")
    private String fundCompany;
    @JSONField(name = "fund_position")
    private FundPosition fundPosition;
    @JSONField(name = "fund_rates")
    private FundRates fundRates;
    @JSONField(name = "manager_list")
    private List<ManagerList> managerList;
    @JSONField(name = "fund_date_conf")
    private FundDateConf fundDateConf;
    public void setFundCompany(String fundCompany) {
         this.fundCompany = fundCompany;
     }
     public String getFundCompany() {
         return fundCompany;
     }

    public void setFundPosition(FundPosition fundPosition) {
         this.fundPosition = fundPosition;
     }
     public FundPosition getFundPosition() {
         return fundPosition;
     }

    public void setFundRates(FundRates fundRates) {
         this.fundRates = fundRates;
     }
     public FundRates getFundRates() {
         return fundRates;
     }

    public void setManagerList(List<ManagerList> managerList) {
         this.managerList = managerList;
     }
     public List<ManagerList> getManagerList() {
         return managerList;
     }

    public void setFundDateConf(FundDateConf fundDateConf) {
         this.fundDateConf = fundDateConf;
     }
     public FundDateConf getFundDateConf() {
         return fundDateConf;
     }

}