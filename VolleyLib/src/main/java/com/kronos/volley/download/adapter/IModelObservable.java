package com.kronos.volley.download.adapter;

/**
 * Created by Leif Zhang on 16/8/18.
 * Email leifzhanggithub@gmail.com
 */
public interface IModelObservable {
    void registerDataSetObserver(IObserver observer);

    void unregisterDataSetObserver(IObserver observer);

    void notifyDataChange();
}
