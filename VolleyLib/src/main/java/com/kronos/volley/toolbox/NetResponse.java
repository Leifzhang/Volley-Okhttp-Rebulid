package com.kronos.volley.toolbox;

/**
 * Created by zhangyang on 16/2/5.
 */
public class NetResponse {
    public boolean isCache = false;
    public Object data;

    public NetResponse(boolean isCache, Object o) {
        this.isCache = isCache;
        this.data = o;
    }

    public <T> T getData() {
        return (T) data;
    }
}
