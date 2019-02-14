package com.kronos.rxadapter

import com.kronos.volley.Response
import com.kronos.volley.VolleyError
import com.kronos.volley.toolbox.NetResponse
import com.kronos.volley.toolbox.StringRequest

import rx.Observable
import rx.Subscriber

/**
 * Created by Leif Zhang on 2017/4/1.
 * Email leifzhanggithub@gmail.com
 */

class RequestAdapter(private val request: StringRequest) : Response.Listener<NetResponse>, Response.ErrorListener, Observable.OnSubscribe<NetResponse> {
    private var netResponse: NetResponse? = null
    private var isReady = false
    private var isFinish = false
    private var volleyError: VolleyError? = null
    private var subscriber: Subscriber<in NetResponse>? = null

    init {
        request.setRequestListener(this).errorListener = this
    }

    override fun onErrorResponse(error: VolleyError) {
        volleyError = error
        isFinish = true
        isReady = true
        subscriber!!.onError(volleyError)
    }

    override fun onResponse(response: NetResponse) {
        try {
            netResponse = response
            isReady = true
            isFinish = !request.isRefreshNeed || !response.isCache
            subscriber!!.onNext(netResponse)
            if (isFinish) {
                subscriber!!.onCompleted()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            subscriber?.onError(e)
        }

    }

    override fun call(subscriber: Subscriber<in NetResponse>) {
        this.subscriber = subscriber
    }
}
