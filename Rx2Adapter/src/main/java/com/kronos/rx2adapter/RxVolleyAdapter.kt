package com.kronos.rx2adapter

import com.kronos.volley.toolbox.NetResponse
import com.kronos.volley.toolbox.StringRequest
import io.reactivex.Observable


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