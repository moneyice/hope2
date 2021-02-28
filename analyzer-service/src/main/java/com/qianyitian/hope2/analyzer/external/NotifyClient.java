package com.qianyitian.hope2.analyzer.external;

import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.LogEnabled;
import com.dtflys.forest.annotation.Request;

public interface NotifyClient {


    /**
     * 这里面包含财报信息，所以每个季度更新一次即可
     */
    @Request(
            url = "http://127.0.0.1:9001/wecom/admin",
            type = "POST",
            headers = {
                    "Content-Type: text/plain"
            }
    )
    @LogEnabled(true)
    String notifyAdmin(@Body String info);

    /**
     * 这里面包含财报信息，所以每个季度更新一次即可
     */
    @Request(
            url = "http://127.0.0.1:9001/wecom/all",
            type = "POST",
            headers = {
                    "Content-Type: text/plain"
            }
    )
    @LogEnabled(true)
    String notifyAll(@Body String info);

}
