package com.kronos.download

import com.kronos.download.adapter.IObserver

import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe

object RxDownload {

    fun getDownload(url: String): Observable<Int> {
        val downloadModel = DownloadManager.getModel(url)
        val rxObserver = RxObserver(downloadModel)
        val observable = Observable.create(rxObserver)
        downloadModel!!.registerDataSetObserver(rxObserver)
        return observable
    }


    private class RxObserver internal constructor(internal var model: DownloadModel?) : IObserver, ObservableOnSubscribe<Int> {
        private var emitter: ObservableEmitter<Int>? = null

        override fun onChanged() {
            emitter!!.onNext(model?.progress!!)
            if (model!!.check()) {
                emitter!!.onComplete()
            }
        }

        @Throws(Exception::class)
        override fun subscribe(emitter: ObservableEmitter<Int>) {
            this.emitter = emitter
        }
    }
}
