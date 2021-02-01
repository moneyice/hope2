/**
  * Copyright 2021 jb51.net 
  */
package com.qianyitian.hope2.analyzer.funds.model;
import com.alibaba.fastjson.annotation.JSONField;
/**
 * Auto-generated: 2021-01-27 10:33:18
 *
 * @author jb51.net (i@jb51.net)
 * @website http://tools.jb51.net/code/json2javabean
 */
public class Fund {

    private Data data;
    @JSONField(name = "result_code")
    private int resultCode;
    public void setData(Data data) {
         this.data = data;
     }
     public Data getData() {
         return data;
     }

    public void setResultCode(int resultCode) {
         this.resultCode = resultCode;
     }
     public int getResultCode() {
         return resultCode;
     }

}