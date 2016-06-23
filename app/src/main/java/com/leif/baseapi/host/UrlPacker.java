package com.leif.baseapi.host;

import android.support.annotation.Nullable;

import java.util.Map;

/**
 * Created by zhangyang on 16/6/23.
 */
public class UrlPacker {
    public static String pack(@Nullable String host) {
        return pack(host, null);
    }

    public static String pack(@Nullable String host, Map<String, String> params) {
        String url = String.format("%s/%s?", HostManager.getBaseUrl(), host);
        if (params != null)
            for (Map.Entry<String, String> entry : params.entrySet()) {
                url += String.format("%s=%s&", entry.getKey(), entry.getValue());
            }
        return url.substring(0, url.length() - 1);
    }

}
