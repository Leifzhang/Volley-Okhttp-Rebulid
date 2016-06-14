package com.leif.baseapi;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.kronos.volley.Request;
import com.kronos.volley.RequestResponse;
import com.kronos.volley.VolleyError;
import com.kronos.volley.toolbox.JsonRequest;
import com.kronos.volley.toolbox.NetResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by zhangyang on 16/1/27.
 */
public class CustomJsonApi implements BaseApi {
    private ResponseListener responseListener;
    protected Bundle bundle;
    private String Tag = getClass().getName();

    public CustomJsonApi(ResponseListener responseListener) {
        this(responseListener, null);
    }

    public CustomJsonApi(ResponseListener responseListener, Bundle bundle) {
        this.responseListener = responseListener;
        if (bundle == null)
            bundle = new Bundle();
        this.bundle = bundle;
    }

    @Override
    public Request getRequest() {
        String url = getUrl();
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        JsonRequest request = new JsonRequest(url);
        request.setRequestListener(new RequestResponse.Listener<NetResponse>() {
            @Override
            public void onResponse(NetResponse response) {
                try {
                    responseListener.onSuccess(response.data, response.isCache);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).setErrorListener(new RequestResponse.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    onError(error.networkResponse.statusCode, error.networkResponse.errorResponseString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).setMethod(Method()).setHeader(getHeader()).setApiParser(getParser()).setCacheTime(cacheTime).setIsRefreshNeed(isNeedRefresh);
        request.setRequestBody(getRequestJSONBody());
        return request;
    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public final Map<String, String> getRequestBody() {
        return null;
    }

    @Override
    public Map<String, String> getHeader() {
        return null;
    }

    @Override
    public JSONObject getRequestJSONBody() {
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

    public JSONObject getJsonFromBundle() {
        JSONObject jsonObject = new JSONObject();
        for (String key : bundle.keySet()) {
            try {
                jsonObject.put(key, bundle.get(key) + "");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    long cacheTime = 0;

    boolean isNeedRefresh = false;

    public void setCacheTime(long cacheTime) {
        this.cacheTime = cacheTime;
    }

    public void setIsNeedRefersh(boolean isNeedRefersh) {
        this.isNeedRefresh = isNeedRefersh;
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
