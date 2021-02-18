package com.qianyitian.hope2.spider.external;

import com.dtflys.forest.annotation.DataVariable;
import com.dtflys.forest.annotation.LogEnabled;
import com.dtflys.forest.annotation.Request;

public interface DanjuanClient {


    /**
     * 这里面包含财报信息，所以每个季度更新一次即可
     */
    @Request(
            url = "https://danjuanfunds.com/djapi/fund/detail/${code}",
            type = "GET",
            headers = {
                    "Accept: application/json, text/plain, */*",
                    "Accept-Language: zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7",
                    "Connection: keep-alive",
                    "Referer: https://danjuanfunds.com/funding/005827?channel=1300100141",
                    "Sec-Fetch-Dest: empty",
                    "Sec-Fetch-Mode: cors",
                    "Sec-Fetch-Site: same-origin",
                    "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 11_1_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Safari/537.36",
                    "elastic-apm-traceparent: 00-8936c0d09ef99045716fac06ff3777a0-3df470af2db9906a-01"
            }
    )
    @LogEnabled(false)
    String getFundDetail(@DataVariable("code") String code);

}
