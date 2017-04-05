package com.kronos.rxadapter;

import com.kronos.volley.RequestResponse;
import com.kronos.volley.VolleyError;
import com.kronos.volley.toolbox.NetResponse;
import com.kronos.volley.toolbox.StringRequest;

/**
 * Created by Leif Zhang on 2017/4/1.
 * Email leifzhanggithub@gmail.com
 */

public class RequestAdapter {
    private NetResponse netResponse;
    private boolean isReady = false;
    private boolean isFinish = false;
    private VolleyError volleyError;

    public boolean isReady() {
        boolean ready = isReady;
        if (isReady) {
            isReady = false;
        }
        return ready;
    }

    public NetResponse getNetResponse() throws VolleyError {
        if (netResponse == null) {
            throw volleyError;
        }
        return netResponse;
    }

    public boolean isFinish() {
        return isFinish;

    }

    public RequestAdapter(final StringRequest request) {
        request.setRequestListener(new RequestResponse.Listener<NetResponse>() {

            @Override
            public void onResponse(NetResponse response) {
                netResponse = response;
                isReady = true;
                if (!request.isRefreshNeed()) {
                    isFinish = true;
                } else {
                    isFinish = !response.isCache;
                }
            }
        }).setErrorListener(new RequestResponse.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyError = error;
                isFinish = true;
                isReady = true;
            }
        });
    }
}
