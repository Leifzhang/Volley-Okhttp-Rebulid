package com.leif.baseapi;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import com.kronos.volley.Request;
import com.kronos.volley.Response;
import com.kronos.volley.VolleyError;
import com.kronos.volley.toolbox.BaseApiParser;
import com.kronos.volley.toolbox.FileUploadRequest;
import com.kronos.volley.toolbox.NetResponse;
import com.leif.baseapi.host.UrlPacker;

import org.json.JSONObject;

import java.util.Map;


/**
 * Created by Leif Zhang on 16/8/4.
 * Email leifzhanggithub@gmail.com
 */
public abstract class CustomFileApi implements BaseApi {
    private ResponseListener responseListener;
    protected Bundle bundle;
    private String Tag = getClass().getName();
    private String realUrl;

    public CustomFileApi(ResponseListener responseListener) {
        this(responseListener, null);
    }

    public CustomFileApi(ResponseListener responseListener, Bundle bundle) {
        this.responseListener = responseListener;
        if (bundle == null)
            bundle = new Bundle();
        this.bundle = bundle;
    }

    protected String getRealUrl() {
        if (TextUtils.isEmpty(realUrl)) {
            realUrl = getUrl();
            if (!Patterns.WEB_URL.matcher(realUrl).matches()) {
                realUrl = UrlPacker.pack(getUrl(), Method() == Request.Method.GET ? getRequestBody() : null);
                if (!Patterns.WEB_URL.matcher(realUrl).matches())
                    throw new NullPointerException();
            }
        }
        return realUrl;
    }

    @Override
    public Request getRequest() {
        String url = getRealUrl();
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        FileUploadRequest request = new FileUploadRequest(url);
        request.setRequestListener(new Response.Listener<NetResponse>() {
            @Override
            public void onResponse(NetResponse response) {
                try {
                    responseListener.onSuccess(response.data, response.isCache);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).setErrorListener(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    onError(error.networkResponse.statusCode, error.networkResponse.errorResponseString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).setMethod(Method()).setHeader(getHeader()).setApiParser(getParser())
                .setCacheTime(cacheTime).setIsRefreshNeed(isNeedRefresh);
        request.setFilePath(getFileName());
        return request;
    }

    public abstract String getFileName();

    @Override
    public final Map<String, String> getRequestBody() {
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
        return Request.Method.POST;
    }

    @Override
    public BaseApiParser getParser() {
        return new StringApiParser();
    }


    @Override
    public void cancel() {

    }

    @Override
    public void start() {
        VolleyQueue.getInstance().addRequest(getRequest());
    }

    long cacheTime = 0;

    boolean isNeedRefresh = false;

    public void setCacheTime(long cacheTime) {
        this.cacheTime = cacheTime;
    }

    public void setIsNeedRefersh(boolean isNeedRefresh) {
        this.isNeedRefresh = isNeedRefresh;
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
