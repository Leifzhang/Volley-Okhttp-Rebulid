package com.kronos.rx2adapter

import com.kronos.volley.Response
import com.kronos.volley.VolleyError
import com.kronos.volley.toolbox.NetResponse
import com.kronos.volley.toolbox.StringRequest

import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe

/**
 * Created by Leif Zhang on 2017/4/1.
 * Email leifzhanggithub@gmail.com
 */

class RequestAdapter internal constructor(private val request: StringRequest) : Response.Listener<NetResponse>,
        Response.ErrorListener, ObservableOnSubscribe<NetResponse> {
    private var netResponse: NetResponse? = null
    private var isFinish = false
    private var volleyError: VolleyError? = null
    private var emitter: ObservableEmitter<NetResponse>? = null


    init {
        request.setRequestListener(this).errorListener = this
    }

    override fun onErrorResponse(error: VolleyError) {
        volleyError = error
        isFinish = true
        emitter?.onError(volleyError!!)
    }

    override fun onResponse(response: NetResponse) {
        try {
            netResponse = response
            isFinish = !request.isRefreshNeed || !response.isCache
            emitter?.onNext(netResponse!!)
            if (isFinish) {
                emitter?.onComplete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emitter?.onError(e)
        }

    }

    @Throws(Exception::class)
    override fun subscribe(e: ObservableEmitter<NetResponse>) {
        emitter = e
        if (netResponse != null) {
            emitter?.onNext(netResponse!!)
            if (isFinish) {
                emitter?.onComplete()
            }
        }
        if (volleyError != null) {
            emitter?.onError(volleyError!!)
        }
    }
}
