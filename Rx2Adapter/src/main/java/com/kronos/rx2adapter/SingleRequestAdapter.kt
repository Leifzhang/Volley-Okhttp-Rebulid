package com.kronos.rx2adapter

import com.kronos.volley.Response
import com.kronos.volley.VolleyError
import com.kronos.volley.toolbox.NetResponse
import com.kronos.volley.toolbox.StringRequest
import io.reactivex.SingleEmitter
import io.reactivex.SingleOnSubscribe

/**
 *
 *  @Author LiABao
 *  @Since 2022/11/4
 *
 */
class SingleRequestAdapter internal constructor(private val request: StringRequest) :
    Response.Listener<NetResponse>, Response.ErrorListener, SingleOnSubscribe<NetResponse> {
    init {
        request.setRequestListener(this)
        request.errorListener = this
    }

    private lateinit var mEmitter: SingleEmitter<NetResponse>

    override fun subscribe(emitter: SingleEmitter<NetResponse>) {
        mEmitter = emitter
    }

    override fun onResponse(response: NetResponse) {
        mEmitter.onSuccess(response)
    }

    override fun onErrorResponse(error: VolleyError) {
        mEmitter.onError(error)
    }
}