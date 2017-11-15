package com.kronos.volley.toolbox;

import android.util.Log;

import com.kronos.volley.Request;
import com.kronos.volley.VolleyError;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;


public class OkHttpStack implements HttpStack {
    private OkHttpClient client;

    public OkHttpStack() {
        this(new OkHttpClient());
    }

    public OkHttpStack(OkHttpClient client) {
        if (client == null) {
            throw new NullPointerException("Client must not be null.");
        }
        this.client = client;
        mHeaders = new HashMap<>();
    }

    public OkHttpClient getClient() {
        return client;
    }

    @Override
    public Response performRequest(Request<?> request, Map<String, String> additionalHeaders)
            throws IOException, VolleyError {
        OkHttpClient client = getClient();
        okhttp3.Request.Builder okHttpRequestBuilder =
                new okhttp3.Request.Builder();
        okHttpRequestBuilder.url(request.getUrl());
        Map<String, String> headers = request.getHeaders();
        mHeaders.putAll(headers);
        for (final String name : mHeaders.keySet()) {
            okHttpRequestBuilder.addHeader(name, mHeaders.get(name));
        }
        for (final String name : additionalHeaders.keySet()) {
            okHttpRequestBuilder.addHeader(name, additionalHeaders.get(name));
        }
        setConnectionParametersForRequest(okHttpRequestBuilder, request);

        okhttp3.Request okHttpRequest = okHttpRequestBuilder.build();
        Call okHttpCall = client.newCall(okHttpRequest);
        return okHttpCall.execute();

    }

    protected void setConnectionParametersForRequest(okhttp3.Request.Builder builder, Request<?> request)
            throws IOException, VolleyError {
        switch (request.getMethod()) {
            case Request.Method.DEPRECATED_GET_OR_POST:
                byte[] postBody = request.getBody();
                if (postBody != null) {
                    builder.post(createRequestBody(request));
                }
                break;

            case Request.Method.GET:
                builder.get();
                break;

            case Request.Method.DELETE:
                builder.delete();
                break;

            case Request.Method.POST:
                Log.i("volleyError", "" + request.getUrl());
                builder.post(createRequestBody(request));
                break;

            case Request.Method.PUT:
                builder.put(createRequestBody(request));
                break;

            case Request.Method.HEAD:
                builder.head();
                break;

            case Request.Method.OPTIONS:
                builder.method("OPTIONS", null);
                break;

            case Request.Method.TRACE:
                builder.method("TRACE", null);
                break;

            case Request.Method.PATCH:
                builder.patch(createRequestBody(request));
                break;

            default:
                throw new IllegalStateException("Unknown method type.");
        }
    }

    protected RequestBody createRequestBody(Request r) throws VolleyError {
        byte[] body = r.getBody();
        if (body == null) body = new byte[0];
        if (r instanceof FileUploadRequest) {
            return createFileRequestBody((FileUploadRequest) r);
        }
        return RequestBody.create(MediaType.parse(r.getBodyContentType()), body);
    }

    protected RequestBody createFileRequestBody(FileUploadRequest r) throws VolleyError {
        String fileName = new String(r.getBody());
        File file = new File(fileName);
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(MediaType.parse(r.getMediaType()), new File(fileName)))
                .build();
    }

    private Map<String, String> mHeaders;

    public void addHeader(Map<String, String> header) {
        if (header != null) {
            mHeaders.putAll(header);
        }
    }
}