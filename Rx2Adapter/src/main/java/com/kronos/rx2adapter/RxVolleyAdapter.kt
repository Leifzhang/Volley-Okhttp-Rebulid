package com.kronos.rx2adapter

import com.kronos.volley.toolbox.NetResponse
import com.kronos.volley.toolbox.StringRequest
import io.reactivex.Observable
import io.reactivex.Single


/**
 * Created by Leif Zhang on 2017/4/1.
 * Email leifzhanggithub@gmail.com
 */


fun StringRequest.getObservable(): Observable<NetResponse> {
    val adapter = RequestAdapter(this)
    return Observable.create(adapter)
}

fun StringRequest.single(): Single<NetResponse> {
    val adapter = SingleRequestAdapter(this)
    return Single.create(adapter)
}