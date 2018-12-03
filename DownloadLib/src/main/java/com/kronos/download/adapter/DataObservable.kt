package com.kronos.download.adapter

import android.database.Observable

/**
 * Created by Leif Zhang on 16/8/18.
 * Email leifzhanggithub@gmail.com
 */
class DataObservable : Observable<IObserver>() {
    fun notifyChanged() {
        synchronized(mObservers) {
            for (i in mObservers.indices.reversed()) {
                mObservers[i].onChanged()
            }
        }
    }

}
