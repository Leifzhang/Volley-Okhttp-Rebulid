package com.kronos.download.adapter;

/**
 * Created by Leif Zhang on 16/8/18.
 * Email leifzhanggithub@gmail.com
 */
public class BaseObserveAdapter implements IModelObservable {
    private final DataObservable mDataSetObservable;

    public BaseObserveAdapter() {
        mDataSetObservable = new DataObservable();
    }

    public void registerDataSetObserver(IObserver observer) {
        mDataSetObservable.registerObserver(observer);
    }

    public void unregisterDataSetObserver(IObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
    }

    @Override
    public void notifyDataChange() {
        mDataSetObservable.notifyChanged();
    }

    @Override
    public void unregisterAll() {
        mDataSetObservable.unregisterAll();
    }

}
