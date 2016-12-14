package com.kronos.volley.toolbox;

import android.text.TextUtils;

import com.kronos.volley.AuthFailureError;

/**
 * Created by zhangyang on 16/4/21.
 */
public class FileUploadRequest extends StringRequest {

    public FileUploadRequest(String url) {
        super(url);
    }

    private String filePath;

    public FileUploadRequest setFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        if (!TextUtils.isEmpty(filePath)) {
            return filePath.getBytes();
        }
        return super.getBody();
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data";
    }

    private String mediaType = "image/png";

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }
}
