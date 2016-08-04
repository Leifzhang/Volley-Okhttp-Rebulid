package com.leif.baseapi;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import com.kronos.volley.Request;
import com.kronos.volley.RequestResponse;
import com.kronos.volley.VolleyError;
import com.kronos.volley.toolbox.BaseApiParser;
import com.kronos.volley.toolbox.JsonRequest;
import com.kronos.volley.toolbox.NetResponse;
import com.leif.baseapi.host.UrlPacker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by zhangyang on 16/1/27.
 */
public abstract class CustomJsonApi<T> implements BaseApi {
    private ResponseListener responseListener;
    protected Bundle bundle;
    private String Tag = getClass().getName();
    private String realUrl;

    public CustomJsonApi(ResponseListener<T> responseListener) {
        this(responseListener, null);
    }

    public CustomJsonApi(ResponseListener<T> responseListener, Bundle bundle) {
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

    @Override
    public void cancel() {

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
