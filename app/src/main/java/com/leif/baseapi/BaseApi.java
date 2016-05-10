package com.leif.baseapi;

import com.kronos.volley.Request;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by zhangyang on 16/1/20.
 */
public interface BaseApi {
    Request getRequest();

    String getUrl();

    Map<String, String> getRequestBody();

    Map<String, String> getHeader();

    JSONObject getRequestJSONBody();

    int Method();

    BaseApiParser getParser();

}
