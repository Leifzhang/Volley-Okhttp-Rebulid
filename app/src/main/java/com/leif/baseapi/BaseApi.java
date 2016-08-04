package com.leif.baseapi;

import com.kronos.volley.Request;
import com.kronos.volley.toolbox.BaseApiParser;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Leif Zhang on 16/8/4.
 * Email leifzhanggithub@gmail.com
 */
public interface BaseApi {
    Request getRequest();

    String getUrl();

    Map<String, String> getRequestBody();

    Map<String, String> getHeader();

    JSONObject getRequestJSONBody();

    int Method();

    BaseApiParser getParser();

    void start();

    void cancel();
}
