package com.kronos.download.adapter

/**
 * Created by Leif Zhang on 16/8/18.
 * Email leifzhanggithub@gmail.com
 */
interface IModelObservable {
    fun registerDataSetObserver(observer: IObserver)

    fun unregisterDataSetObserver(observer: IObserver)

    fun notifyDataChange()

    fun unregisterAll()
}
