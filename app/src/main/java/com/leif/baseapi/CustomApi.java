package com.leif.baseapi;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import com.kronos.volley.Request;
import com.kronos.volley.RequestResponse;
import com.kronos.volley.VolleyError;
import com.kronos.volley.toolbox.BaseApiParser;
import com.kronos.volley.toolbox.NetResponse;
import com.kronos.volley.toolbox.StringRequest;
import com.leif.baseapi.host.UrlPacker;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * Created by zhangyang on 16/1/20.
 */
public abstract class CustomApi implements BaseApi {
    protected ResponseListener responseListener;//
    protected Bundle bundle;
    private String Tag = getClass().getName();
    private String realUrl;

    public CustomApi(ResponseListener responseListener) {
        this(responseListener, null);
    }

    public CustomApi(ResponseListener responseListener, Bundle bundle) {
        this.responseListener = null != responseListener ? responseListener : new ResponseListener() {
            @Override
            public void onSuccess(Object model, boolean isCache) {
                Log.i("getStatiscicsApi", "geturl:  " + getUrl() + "model:  " + model);
            }

            @Override
            public void onErrorResponse(int code, String error) {
                Log.i("getStatiscicsApi", "error:  " + error);
            }
        };
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
            responseListener.onErrorResponse(ErrorCode.EMPTYURL, "url为空");
            return null;
        }
        if (checkAddTimeTemp(url)) {
            url += String.format("&_eva_t=%d", System.currentTimeMillis() / 1000);
        } else {
            if (Method() != Request.Method.GET) {
                url += String.format("?_eva_t=%d", System.currentTimeMillis() / 1000);
            }
        }
        StringRequest request = new StringRequest(url);
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
        request.setRequestBody(getRequestBody());
        return request;
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

    @Override
    public void start() {
        Observable.create(new Observable.OnSubscribe<Request>() {
            @Override
            public void call(Subscriber<? super Request> subscriber) {
                subscriber.onNext(getRequest());
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()).subscribe(new Action1<Request>() {
            @Override
            public void call(Request request) {
                VolleyQueue.getInstance().addRequest(getRequest());
            }
        });
    }

    @Override
    public void cancel() {

    }

    public HashMap<String, String> getHashMapFromBundle() {
        HashMap<String, String> map = new HashMap<>();
        for (String key : bundle.keySet()) {
            map.put(key, bundle.getString(key));
        }
        return map;
    }

    long cacheTime = 0;

    boolean isNeedRefresh = false;

    public final void setCacheTime(long cacheTime) {
        this.cacheTime = cacheTime;
    }

    public final void setIsNeedRefersh(boolean isNeedRefersh) {
        this.isNeedRefresh = isNeedRefersh;
    }

    private boolean checkAddTimeTemp(String url) {
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
