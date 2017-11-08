package com.kronos.rx2adapter;

import com.kronos.volley.Response;
import com.kronos.volley.VolleyError;
import com.kronos.volley.toolbox.NetResponse;
import com.kronos.volley.toolbox.StringRequest;

/**
 * Created by Leif Zhang on 2017/4/1.
 * Email leifzhanggithub@gmail.com
 */

public class RequestAdapter implements Response.Listener<NetResponse>, Response.ErrorListener {
    private NetResponse netResponse;
    private boolean isReady = false;
    private boolean isFinish = false;
    private VolleyError volleyError;
    private StringRequest request;

    public boolean isReady() {
        boolean ready = isReady;
        if (isReady) {
            isReady = false;
        }
        return ready;
    }

    public NetResponse getNetResponse() throws VolleyError {
        if (volleyError != null) {
            throw volleyError;
        }
        return netResponse;
    }

    public boolean isFinish() {
        return isFinish;

    }

    public RequestAdapter(final StringRequest request) {
        this.request = request;
        request.setRequestListener(this).setErrorListener(this);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        volleyError = error;
        isFinish = true;
        isReady = true;
    }

    @Override
    public void onResponse(NetResponse response) {
        try {
            netResponse = response;
            isReady = true;
            isFinish = !request.isRefreshNeed() || !response.isCache;
        } catch (Exception e) {
            e.printStackTrace();
            onErrorResponse(new VolleyError());
        }
    }
}
