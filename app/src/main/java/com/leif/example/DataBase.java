package com.leif.example;


import com.kronos.download.DownloadModel;
import com.kronos.download.IDownloadDb;

import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;

/**
 * Created by Leif Zhang on 16/9/30.
 * Email leifzhanggithub@gmail.com
 */
public class DataBase implements IDownloadDb {
    @Override
    public void saveToDb(HashMap<String, DownloadModel> models) {
        Observable.just(models).flatMap(stringDownloadModelHashMap ->
                Observable.from(stringDownloadModelHashMap.values())).subscribe(downloadModel -> {
            Realm.getDefaultInstance().executeTransaction(realm -> {
                DownloadRealm downloadRealm = Realm.getDefaultInstance().where(DownloadRealm.class)
                        .equalTo("downloadUrl", downloadModel.getDownloadUrl()).findFirst();
                if (downloadRealm == null) {
                    downloadRealm = new DownloadRealm();
                    downloadRealm.setDownloadUrl(downloadModel.getDownloadUrl());
                }
                downloadRealm.setState(downloadModel.getState());
                downloadRealm.setTotalLength(downloadModel.getTotalLength());
                downloadRealm.setDownloadLength(downloadModel.getDownloadLength());
                downloadRealm.setProgress(downloadModel.getProgress());
                realm.copyToRealmOrUpdate(downloadRealm);
            });
        }, Throwable::printStackTrace);

    }

    @Override
    public HashMap<String, DownloadModel> getFromDB() {
        RealmResults<DownloadRealm> list = Realm.getDefaultInstance().where(DownloadRealm.class).findAll();
        HashMap<String, DownloadModel> hashMap = new HashMap<>();
        for (DownloadRealm downloadRealm : list) {
            DownloadModel downloadModel = new DownloadModel();
            downloadModel.setTotalLength(downloadRealm.getTotalLength());
            downloadModel.setDownloadLength(downloadRealm.getDownloadLength());
            downloadModel.setProgress(downloadRealm.getProgress());
            downloadModel.setDownloadUrl(downloadRealm.getDownloadUrl());
            downloadModel.setState(downloadRealm.getState());
            hashMap.put(downloadModel.getDownloadUrl(), downloadModel);
        }
        return hashMap;
    }
}
