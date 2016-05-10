package com.leif.baseapi;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.kronos.volley.Request;
import com.kronos.volley.RequestResponse;
import com.kronos.volley.VolleyError;
import com.kronos.volley.toolbox.NetResponse;
import com.kronos.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Created by zhangyang on 16/1/20.
 */
public class CustomApi implements BaseApi {
    protected ResponseListener responseListener;//
    protected Bundle bundle;
    private String Tag = getClass().getName();

    public CustomApi(ResponseListener responseListener) {
        this(responseListener, null);
    }

    public CustomApi(ResponseListener responseListener, Bundle bundle) {
        this.responseListener = responseListener;
        if (bundle == null)
            bundle = new Bundle();
        this.bundle = bundle;
    }

    @Override
    public Request getRequest() {
        String url = getUrl();
        if (TextUtils.isEmpty(url)) {
            responseListener.onErrorResponse(ErrorCode.EMPTYURL, "url为空");
            return null;
        }
        if (checkAddTimeStemp()) {
            url += String.format("&_eva_t=%d", System.currentTimeMillis() / 1000);
        } else {
            if (Method() != Request.Method.GET) {
                url += String.format("?_eva_t=%d", System.currentTimeMillis() / 1000);
            }
        }
        StringRequest request = new StringRequest(Method(), url, new RequestResponse.Listener<NetResponse>() {
            @Override
            public void onResponse(NetResponse response) {
                if (TextUtils.isEmpty(response.result)) {
                    onError(ErrorCode.PASERERROR, "返回值为空");
                    return;
                }
                try {
                    responseListener.onSuccess(response.data, response.isCache);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new RequestResponse.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    onError(error.networkResponse.statusCode, error.networkResponse.errorResponseString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Map<String, String> map = getRequestBody();
        if (map != null) {
            map.put("_eva_t", System.currentTimeMillis() / 1000 + "");
            request.setRequestBody(map);
        }
        request.setHeader(getHeader());
        request.setApiParser(getParser());
        if (cacheTime > 0) {
            request.setCacheTime(cacheTime);
            request.setIsRefershNeed(isNeedRefersh);
        }
        return request;
    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public Map<String, String> getRequestBody() {
        return null;
    }

    @Override
    public Map<String, String> getHeader() {
        return null;
    }

    @Override
    public final JSONObject getRequestJSONBody() {
        return null;
    }

    @Override
    public int Method() {
        return Request.Method.GET;
    }

    @Override
    public BaseApiParser getParser() {
        return new StringApiParser();
    }

    public HashMap<String, String> getHashMapFromBundle() {
        HashMap<String, String> map = new HashMap<>();
        for (String key : bundle.keySet()) {
            map.put(key, bundle.getString(key));
        }
        return map;
    }

    long cacheTime = 0;

    boolean isNeedRefersh = false;

    public final void setCacheTime(long cacheTime) {
        this.cacheTime = cacheTime;
    }

    public final void setIsNeedRefersh(boolean isNeedRefersh) {
        this.isNeedRefersh = isNeedRefersh;
    }

    private boolean checkAddTimeStemp() {
        if (Method() == Request.Method.GET || Method() == Request.Method.DELETE) {
            Uri uri = Uri.parse(getUrl());
            Set<String> para = uri.getQueryParameterNames();
            return para.size() > 0;
        } else {
            return false;
        }
    }

    public void onError(int statusCode, String errorMessage) {
        try {
            Log.i("onVolleyError", Tag + getUrl());
            if (responseListener != null) {
                responseListener.onErrorResponse(statusCode, errorMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
