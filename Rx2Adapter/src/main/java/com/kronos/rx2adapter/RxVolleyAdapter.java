package com.kronos.rx2adapter;

import com.kronos.volley.VolleyError;
import com.kronos.volley.toolbox.NetResponse;
import com.kronos.volley.toolbox.StringRequest;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;


/**
 * Created by Leif Zhang on 2017/4/1.
 * Email leifzhanggithub@gmail.com
 */

public class RxVolleyAdapter {
    public static Observable<NetResponse> getObservable(StringRequest request) {
        final RequestAdapter adapter = new RequestAdapter(request);
        return Observable.interval(100, TimeUnit.MILLISECONDS).filter(new Predicate<Long>() {
            @Override
            public boolean test(Long aLong) throws Exception {
                return adapter.isReady();
            }
        }).takeUntil(new Predicate<Long>() {
            @Override
            public boolean test(Long aLong) throws Exception {
                return adapter.isFinish();
            }
        }).map(new Function<Long, NetResponse>() {
            @Override
            public NetResponse apply(Long aLong) throws Exception {
                try {
                    return adapter.getNetResponse();
                } catch (VolleyError volleyError) {
                    throw Exceptions.propagate(volleyError);
                }
            }
        });

    }
}