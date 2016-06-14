/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kronos.volley.toolbox;

import com.kronos.volley.AuthFailureError;
import com.kronos.volley.NetworkResponse;
import com.kronos.volley.Request;
import com.kronos.volley.RequestResponse;
import com.kronos.volley.VolleyError;

import java.util.Map;

/**
 * A canned request for retrieving the response body at a given URL as a String.
 */
public class StringRequest extends Request<NetResponse> {
    private RequestResponse.Listener<NetResponse> mListener;
    private Map<String, String> requestBody;

    public StringRequest(String url) {
        super(url);
    }

    /**
     * Creates a new request with the given method.
     *
     * @param method        the request {@link Method} to use
     * @param url           URL to fetch the string at
     * @param listener      Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public StringRequest(int method, String url, RequestResponse.Listener<NetResponse> listener,
                         RequestResponse.ErrorListener errorListener) {
        super(url);
        setErrorListener(errorListener).setMethod(method);
        setRequestListener(listener);
    }

    public Request setRequestListener(RequestResponse.Listener<NetResponse> mListener) {
        this.mListener = mListener;
        return this;
    }

    @Override
    protected void deliverResponse(NetResponse response, boolean intermediate) {
        mListener.onResponse(response);
    }

    @Override
    protected RequestResponse<NetResponse> parseNetworkResponse(NetworkResponse response) throws Exception {
        NetResponse netResponse = null;
        try {
            String parsed = new String(response.data, "UTF-8");
            Object o = getApiParser().parse(parsed);
            netResponse = new NetResponse(response.isCache, o);
        } catch (Exception e) {
            throw new VolleyError(response);
        }
        return RequestResponse.success(netResponse, HttpHeaderParser.parseCacheHeaders(response));
    }


    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        return super.parseNetworkError(volleyError);
    }

    @Override
    public String getCacheKey() {
        return MD5.MD5(getUrl());
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return requestBody;
    }

    public StringRequest setRequestBody(Map<String, String> requestBody) {
        this.requestBody = requestBody;
        return this;
    }


}
