package com.qianyitian.hope2.analyzer.external;

import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.LogEnabled;
import com.dtflys.forest.annotation.Request;

public interface NotifyClient {


    /**
     * 这里面包含财报信息，所以每个季度更新一次即可
     */
    @Request(
            url = "http://tool.qianyitian.com/alert/wecom/admin",
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
            url = "http://tool.qianyitian.com/alert/wecom/all",
            type = "POST",
            headers = {
                    "Content-Type: text/plain"
            }
    )
    @LogEnabled(true)
    String notifyAll(@Body String info);

    /**
     *
     */
    @Request(
            url = "http://tool.qianyitian.com/alert/wecom/admin/lego",
            type = "POST",
            headers = {
                    "Content-Type: text/plain"
            }
    )
    @LogEnabled(true)
    String notifyAdminLego(@Body String info);

}
