package com.leif.baseapi;

/**
 * Created by zhangyang on 16/1/20.
 */
public interface ResponseListener<T> {
    void onSuccess(T model, boolean isCache);

    void onErrorResponse(int code, String error);
}
