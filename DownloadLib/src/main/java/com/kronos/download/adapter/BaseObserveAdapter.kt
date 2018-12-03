package com.kronos.download.adapter

/**
 * Created by Leif Zhang on 16/8/18.
 * Email leifzhanggithub@gmail.com
 */
open class BaseObserveAdapter : IModelObservable {
    private val mDataSetObservable: DataObservable = DataObservable()

    override fun registerDataSetObserver(observer: IObserver) {
        mDataSetObservable.registerObserver(observer)
    }

    override fun unregisterDataSetObserver(observer: IObserver) {
        mDataSetObservable.unregisterObserver(observer)
    }

    override fun notifyDataChange() {
        mDataSetObservable.notifyChanged()
    }

    override fun unregisterAll() {
        mDataSetObservable.unregisterAll()
    }

}
