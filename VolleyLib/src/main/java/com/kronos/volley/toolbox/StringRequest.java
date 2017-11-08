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
import com.kronos.volley.ParseError;
import com.kronos.volley.Request;
import com.kronos.volley.Response;
import com.kronos.volley.VolleyError;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * A canned request for retrieving the response body at a given URL as a String.
 */
public class StringRequest extends Request<NetResponse> {
    private Response.Listener<NetResponse> mListener;
    private Map<String, String> requestBody;

    public StringRequest(String url) {
        super(url);
    }


    public StringRequest(int method, String url, Response.Listener<NetResponse> listener,
                         Response.ErrorListener errorListener) {
        super(url);
        setErrorListener(errorListener).setMethod(method);
        setRequestListener(listener);
    }

    public Request setRequestListener(Response.Listener<NetResponse> mListener) {
        this.mListener = mListener;
        return this;
    }


    @Override
    protected Response<NetResponse> parseNetworkResponse(NetworkResponse response) throws ParseError {
        NetResponse netResponse = null;
        String parsed = null;
        try {
            parsed = new String(response.data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Object o = getApiParser().parse(parsed);
        netResponse = new NetResponse(response.isCache, o);
        return Response.success(netResponse, HttpHeaderParser.parseCacheHeaders(response));
    }


    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        return super.parseNetworkError(volleyError);
    }

    @Override
    protected void deliverResponse(NetResponse response) {

        mListener.onResponse(response);
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
