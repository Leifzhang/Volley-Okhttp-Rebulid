package com.kronos.download;

import com.kronos.download.adapter.IObserver;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class RxDownload {

    public static Observable<Integer> getDownload(String url) {
        DownloadModel downloadModel = DownloadManager.getInstance().getModel(url);
        RxObserver rxObserver = new RxObserver(downloadModel);
        Observable<Integer> observable = Observable.create(rxObserver);
        downloadModel.registerDataSetObserver(rxObserver);
        return observable;
    }


    private static class RxObserver implements IObserver, ObservableOnSubscribe<Integer> {
        private ObservableEmitter<Integer> emitter;
        DownloadModel model;

        RxObserver(DownloadModel model) {
            this.model = model;
        }

        @Override
        public void onChanged() {
            emitter.onNext(model.getProgress());
            if (model.check()) {
                emitter.onComplete();
            }
        }

        @Override
        public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
            this.emitter = emitter;
        }
    }
}
