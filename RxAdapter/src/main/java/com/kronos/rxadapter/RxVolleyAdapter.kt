package com.kronos.rxadapter

import android.util.Log

import com.kronos.volley.VolleyError
import com.kronos.volley.toolbox.NetResponse
import com.kronos.volley.toolbox.StringRequest

import java.util.concurrent.TimeUnit

import rx.Observable
import rx.exceptions.Exceptions
import rx.functions.Func1

/**
 * Created by Leif Zhang on 2017/4/1.
 * Email leifzhanggithub@gmail.com
 */

object RxVolleyAdapter {
    fun getObservable(request: StringRequest): Observable<NetResponse> {
        val adapter = RequestAdapter(request)
        return Observable.create(adapter)
    }
}