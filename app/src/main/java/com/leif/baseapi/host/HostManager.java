package com.leif.baseapi.host;

/**
 * Created by zhangyang on 16/6/23.
 */
public class HostManager {
    private  static String BaseUrl = null;

    public static void setBaseUrl(String BASEURL) {
        HostManager.BaseUrl = BASEURL;
    }

    public static String getBaseUrl() {
        return BaseUrl;
    }
}
