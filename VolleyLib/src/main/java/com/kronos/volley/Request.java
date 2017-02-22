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

package com.kronos.volley;

import android.net.TrafficStats;
import android.net.Uri;
import android.os.SystemClock;
import android.text.TextUtils;

import com.kronos.volley.toolbox.BaseApiParser;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


public abstract class Request<T> implements Comparable<Request<T>> {

    /**
     * Default encoding for POST or PUT parameters. See {@link #getParamsEncoding()}.
     */
    private static final String DEFAULT_PARAMS_ENCODING = "UTF-8";

    /**
     * Supported request methods.
     */
    public interface Method {
        int DEPRECATED_GET_OR_POST = -1;
        int GET = 0;
        int POST = 1;
        int PUT = 2;
        int DELETE = 3;
        int HEAD = 4;
        int OPTIONS = 5;
        int TRACE = 6;
        int PATCH = 7;
    }


    /**
     * Request method of this request.  Currently supports GET, POST, PUT, DELETE, HEAD, OPTIONS,
     * TRACE, and PATCH.
     */
    private int mMethod = Method.GET;

    /**
     * URL of this request.
     */
    private final String mUrl;

    /**
     * Default tag for {@link TrafficStats}.
     */
    private final int mDefaultTrafficStatsTag;

    /**
     * Listener interface for errors.
     */
    private RequestResponse.ErrorListener mErrorListener;

    /**
     * Sequence number of this request, used to enforce FIFO ordering.
     */
    private Integer mSequence;

    /**
     * The request queue this request is associated with.
     */
    private RequestQueue mRequestQueue;

    /**
     * Whether or not responses to this request should be cached.
     */
    private boolean mShouldCache = false;

    /**
     * Whether or not this request has been canceled.
     */
    private boolean mCanceled = false;

    /**
     * Whether or not a response has been delivered for this request yet.
     */
    private boolean mResponseDelivered = false;

    // A cheap variant of request tracing used to dump slow requests.
    private long mRequestBirthTime = 0;

    private static final long SLOW_REQUEST_THRESHOLD_MS = 3000;

    private RetryPolicy mRetryPolicy;


    private Cache.Entry mCacheEntry = null;


    private Object mTag;


    public Request(String url) {
        mUrl = url;
        setRetryPolicy(new DefaultRetryPolicy());

        mDefaultTrafficStatsTag = findDefaultTrafficStatsTag(url);
    }

    public Request<?> setErrorListener(RequestResponse.ErrorListener mErrorListener) {
        this.mErrorListener = mErrorListener;
        return this;
    }

    public Request<?> setMethod(int mMethod) {
        this.mMethod = mMethod;
        return this;
    }

    public int getMethod() {
        return mMethod;
    }


    public Request<?> setTag(Object tag) {
        mTag = tag;
        return this;
    }


    public Object getTag() {
        return mTag;
    }


    public int getTrafficStatsTag() {
        return mDefaultTrafficStatsTag;
    }


    private static int findDefaultTrafficStatsTag(String url) {
        if (!TextUtils.isEmpty(url)) {
            Uri uri = Uri.parse(url);
            if (uri != null) {
                String host = uri.getHost();
                if (host != null) {
                    return host.hashCode();
                }
            }
        }
        return 0;
    }


    public Request<?> setRetryPolicy(RetryPolicy retryPolicy) {
        mRetryPolicy = retryPolicy;
        return this;
    }


    public void addMarker(String tag) {
        if (mRequestBirthTime == 0) {
            mRequestBirthTime = SystemClock.elapsedRealtime();
        }
    }


    void finish(final String tag) {
        if (mRequestQueue != null) {
            mRequestQueue.finish(this);
        }

        long requestTime = SystemClock.elapsedRealtime() - mRequestBirthTime;
        if (requestTime >= SLOW_REQUEST_THRESHOLD_MS) {
            VolleyLog.d("%d ms: %s", requestTime, this.toString());
        }
    }

    public Request<?> setRequestQueue(RequestQueue requestQueue) {
        mRequestQueue = requestQueue;
        return this;
    }


    public final Request<?> setSequence(int sequence) {
        mSequence = sequence;
        return this;
    }


    public final int getSequence() {
        if (mSequence == null) {
            throw new IllegalStateException("getSequence called before setSequence");
        }
        return mSequence;
    }


    public String getUrl() {
        return mUrl;
    }


    public String getCacheKey() {
        return isPermaCache() ? Cache.PERMACACHE_KEY + getUrl() : getUrl();
    }


    public boolean isPermaCache() {
        return false;
    }


    public Request<?> setCacheEntry(Cache.Entry entry) {
        mCacheEntry = entry;
        return this;
    }

    public Cache.Entry getCacheEntry() {
        return mCacheEntry;
    }

    public void cancel() {
        mCanceled = true;
    }


    public boolean isCanceled() {
        return mCanceled;
    }


    public Map<String, String> getHeaders() throws AuthFailureError {
        return header;
    }


    protected Map<String, String> getParams() throws AuthFailureError {
        return null;
    }

    protected String getParamsEncoding() {
        return DEFAULT_PARAMS_ENCODING;
    }

    public String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
    }


    public byte[] getBody() throws AuthFailureError {
        Map<String, String> params = getParams();
        if (params != null && params.size() > 0) {
            return encodeParameters(params, getParamsEncoding());
        }
        return null;
    }

    private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                encodedParams.append('&');
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }

    public final Request<?> setShouldCache(boolean shouldCache) {
        mShouldCache = shouldCache;
        return this;
    }

    public final boolean shouldCache() {
        return mShouldCache;
    }

    public enum Priority {
        LOW,
        NORMAL,
        HIGH,
        IMMEDIATE
    }

    public Priority getPriority() {
        return Priority.NORMAL;
    }


    public final int getTimeoutMs() {
        return mRetryPolicy.getCurrentTimeout();
    }


    public RetryPolicy getRetryPolicy() {
        return mRetryPolicy;
    }


    public void markDelivered() {
        mResponseDelivered = true;
    }


    public boolean hasHadResponseDelivered() {
        return mResponseDelivered;
    }


    abstract protected RequestResponse<T> parseNetworkResponse(NetworkResponse response) throws ParseError;


    protected VolleyError parseNetworkError(VolleyError volleyError) {
        return volleyError;
    }

    abstract protected void deliverResponse(T response, boolean intermediate);


    public void deliverError(VolleyError error) {
        if (mErrorListener != null) {
            mErrorListener.onErrorResponse(error);
        }
    }


    @Override
    public int compareTo(Request<T> other) {
        Priority left = this.getPriority();
        Priority right = other.getPriority();
        return left == right ?
                this.mSequence - other.mSequence :
                right.ordinal() - left.ordinal();
    }

    @Override
    public String toString() {
        String trafficStatsTag = "0x" + Integer.toHexString(getTrafficStatsTag());
        return (mCanceled ? "[X] " : "[ ] ") + getUrl() + " " + trafficStatsTag + " "
                + getPriority() + " " + mSequence;
    }

    private boolean isRefreshNeed = false;

    public boolean isRefreshNeed() {
        return isRefreshNeed;
    }

    public Request<T> setIsRefreshNeed(boolean isRefreshNeed) {
        this.isRefreshNeed = isRefreshNeed;
        return this;
    }

    private Map<String, String> header = new HashMap<>();

    public Request<T> setHeader(Map<String, String> map) {
        if (map != null)
            this.header = map;
        return this;
    }

    private long cacheTime = 0;

    public Request<T> setCacheTime(long cacheTime) {
        if (cacheTime > 0) {
            this.cacheTime = cacheTime;
            setShouldCache(true);
        }
        return this;
    }

    public long getCacheTime() {
        return cacheTime;
    }

    private BaseApiParser apiParser;

    public Request<T> setApiParser(BaseApiParser apiParser) {
        this.apiParser = apiParser;
        return this;
    }

    public BaseApiParser getApiParser() {
        return apiParser;
    }

    private boolean ignoreExpired = false;

    public boolean isIgnoreExpired() {
        return ignoreExpired;
    }

    public void setIgnoreExpired(boolean ignoreExpired) {
        this.ignoreExpired = ignoreExpired;
    }
}
