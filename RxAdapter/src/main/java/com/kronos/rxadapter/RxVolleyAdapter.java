package com.kronos.rxadapter;

import android.util.Log;

import com.kronos.volley.VolleyError;
import com.kronos.volley.toolbox.NetResponse;
import com.kronos.volley.toolbox.StringRequest;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.exceptions.Exceptions;
import rx.functions.Func1;

/**
 * Created by Leif Zhang on 2017/4/1.
 * Email leifzhanggithub@gmail.com
 */

public class RxVolleyAdapter {
    public static Observable<NetResponse> getObservable(StringRequest request) {
        final RequestAdapter adapter = new RequestAdapter(request);
        return Observable.interval(100, TimeUnit.MILLISECONDS).filter(new Func1<Long, Boolean>() {
            @Override
            public Boolean call(Long aLog) {
                Log.i("Observable", "filter");
                return adapter.isReady();
            }
        }).takeUntil(new Func1<Long, Boolean>() {
            @Override
            public Boolean call(Long aLong) {
                Log.i("Observable", "takeUntil");
                return adapter.isFinish();
            }
        }).map(new Func1<Long, NetResponse>() {
            @Override
            public NetResponse call(Long o) {
                Log.i("Observable", "NetResponse");
                try {
                    return adapter.getNetResponse();
                } catch (VolleyError volleyError) {
                    throw Exceptions.propagate(volleyError);
                }
            }
        });

    }
}