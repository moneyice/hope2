/**
  * Copyright 2021 jb51.net 
  */
package com.qianyitian.hope2.analyzer.funds.model;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;
/**
 * Auto-generated: 2021-01-27 10:33:18
 *
 * @author jb51.net (i@jb51.net)
 * @website http://tools.jb51.net/code/json2javabean
 */
public class FundRates {

    @JSONField(name = "fd_code")
    private String fdCode;
    @JSONField(name = "subscribe_rate")
    private String subscribeRate;
    @JSONField(name = "declare_rate")
    private String declareRate;
    @JSONField(name = "withdraw_rate")
    private String withdrawRate;
    private String discount;
    @JSONField(name = "subscribe_discount")
    private String subscribeDiscount;
    @JSONField(name = "declare_discount")
    private String declareDiscount;
    @JSONField(name = "declare_rate_table")
    private List<DeclareRateTable> declareRateTable;
    @JSONField(name = "withdraw_rate_table")
    private List<WithdrawRateTable> withdrawRateTable;
    @JSONField(name = "other_rate_table")
    private List<OtherRateTable> otherRateTable;
    public void setFdCode(String fdCode) {
         this.fdCode = fdCode;
     }
     public String getFdCode() {
         return fdCode;
     }

    public void setSubscribeRate(String subscribeRate) {
         this.subscribeRate = subscribeRate;
     }
     public String getSubscribeRate() {
         return subscribeRate;
     }

    public void setDeclareRate(String declareRate) {
         this.declareRate = declareRate;
     }
     public String getDeclareRate() {
         return declareRate;
     }

    public void setWithdrawRate(String withdrawRate) {
         this.withdrawRate = withdrawRate;
     }
     public String getWithdrawRate() {
         return withdrawRate;
     }

    public void setDiscount(String discount) {
         this.discount = discount;
     }
     public String getDiscount() {
         return discount;
     }

    public void setSubscribeDiscount(String subscribeDiscount) {
         this.subscribeDiscount = subscribeDiscount;
     }
     public String getSubscribeDiscount() {
         return subscribeDiscount;
     }

    public void setDeclareDiscount(String declareDiscount) {
         this.declareDiscount = declareDiscount;
     }
     public String getDeclareDiscount() {
         return declareDiscount;
     }

    public void setDeclareRateTable(List<DeclareRateTable> declareRateTable) {
         this.declareRateTable = declareRateTable;
     }
     public List<DeclareRateTable> getDeclareRateTable() {
         return declareRateTable;
     }

    public void setWithdrawRateTable(List<WithdrawRateTable> withdrawRateTable) {
         this.withdrawRateTable = withdrawRateTable;
     }
     public List<WithdrawRateTable> getWithdrawRateTable() {
         return withdrawRateTable;
     }

    public void setOtherRateTable(List<OtherRateTable> otherRateTable) {
         this.otherRateTable = otherRateTable;
     }
     public List<OtherRateTable> getOtherRateTable() {
         return otherRateTable;
     }

}