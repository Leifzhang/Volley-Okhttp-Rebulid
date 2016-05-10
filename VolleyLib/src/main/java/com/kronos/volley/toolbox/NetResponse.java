package com.kronos.volley.toolbox;

/**
 * Created by zhangyang on 16/2/5.
 */
public class NetResponse {
    public String result;
    public boolean isCache = false;
    public Object data;

    public NetResponse(boolean isCache, String result, Object o) {
        this.isCache = isCache;
        this.result = result;
        this.data = o;
    }
}
