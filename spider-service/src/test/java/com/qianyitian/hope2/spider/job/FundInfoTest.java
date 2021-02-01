package com.qianyitian.hope2.spider.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.MessageFormat;

class FundInfoTest {
    String stockFund = "https://www.todergroup.com/financial-products/api/products/?category=13&format=json&type=165&page={0}&sort=ticker_asc";

    String hybridFund = "https://www.todergroup.com/financial-products/api/products/?category=13&format=json&type=167&page={0}&sort=ticker_asc";

    String etfFund = "https://www.todergroup.com/financial-products/api/products/?category=13&format=json&type=169&page={0}&sort=ticker_asc";

    String fofFund = "https://www.todergroup.com/financial-products/api/products/?category=13&format=json&type=170&page={0}&sort=ticker_asc";


    @Test
    void getStockInfoTest() throws IOException {
        getFundList(stockFund, "S");
        getFundList(hybridFund, "H");
        getFundList(etfFund, "E");
//        getFundList(fofFund);

    }

    private void getFundList(String urlString, String type) throws IOException {
        int page = 0;
        int index = 0;
        while (true) {
            page++;
            String url = MessageFormat.format(urlString, page);
            String content = Resources.asCharSource(new URL(url), Charset.forName("UTF-8")).readFirstLine();
            JSONObject jsonObject = JSON.parseObject(content);
            JSONArray itemArray = jsonObject.getJSONArray("items");
            if (itemArray.size() == 0) {
                break;
            }


            for (int i = 0; i < itemArray.size(); i++) {
                JSONObject item = itemArray.getJSONObject(i);
                JSONArray dataArray = item.getJSONArray("data");

                String code = dataArray.getJSONObject(0).getString("value");
                String name = dataArray.getJSONObject(1).getString("value");

                System.out.println(code + "," + name+","+type);
            }
        }
    }
}