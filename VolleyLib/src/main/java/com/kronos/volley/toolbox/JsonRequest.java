package com.kronos.volley.toolbox;


import com.kronos.volley.AuthFailureError;
import com.kronos.volley.RequestResponse;

import org.json.JSONObject;

/**
 * Created by zhangyang on 16/1/27.
 */
public class JsonRequest extends StringRequest {
    public JsonRequest(int method, String url, RequestResponse.Listener<NetResponse> listener, RequestResponse.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public JsonRequest(String url, RequestResponse.Listener<NetResponse> listener, RequestResponse.ErrorListener errorListener) {
        super(url, listener, errorListener);
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        if (jsonObject != null)
            return jsonObject.toString().getBytes();
        return super.getBody();
    }

    @Override
    public String getCacheKey() {
        return MD5.MD5(String.format("%s_%s", getUrl(), jsonObject == null ? "" : jsonObject.toString()));
    }

    private JSONObject jsonObject;

    public void setRequestBody(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    /**
     * Charset for request.
     */
    private static final String PROTOCOL_CHARSET = "utf-8";
    /**
     * Content type for request.
     */
    private static final String PROTOCOL_CONTENT_TYPE =
            String.format("application/json; charset=%s", PROTOCOL_CHARSET);

    @Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }
}
