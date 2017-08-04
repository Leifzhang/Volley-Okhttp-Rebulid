package com.leif.example;


import com.kronos.download.DownloadModel;
import com.kronos.download.IDownloadDb;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Leif Zhang on 16/9/30.
 * Email leifzhanggithub@gmail.com
 */
public class DataBase implements IDownloadDb {
    @Override
    public void saveToDb(DownloadModel model) {
        Realm.getDefaultInstance().executeTransaction(realm -> {
            DownloadRealm downloadRealm = Realm.getDefaultInstance().where(DownloadRealm.class)
                    .equalTo("downloadUrl", model.getDownloadUrl()).findFirst();
            if (downloadRealm == null) {
                downloadRealm = new DownloadRealm();
                downloadRealm.setDownloadUrl(model.getDownloadUrl());
            }
            downloadRealm.setState(model.getState());
            downloadRealm.setTotalLength(model.getTotalLength());
            downloadRealm.setDownloadLength(model.getDownloadLength());
            downloadRealm.setProgress(model.getProgress());
            realm.copyToRealmOrUpdate(downloadRealm);
        });
    }

    @Override
    public void saveToDb(HashMap<String, DownloadModel> models) {
        Observable.just(models).subscribe(modelHashMap -> Realm.getDefaultInstance().executeTransaction(realm -> {
            for (Map.Entry<String, DownloadModel> entry : modelHashMap.entrySet()) {
                DownloadModel downloadModel = entry.getValue();
                DownloadRealm finalDownloadDataRealmEntity = realm.where(DownloadRealm.class)
                        .equalTo("downloadUrl", downloadModel.getDownloadUrl()).findFirst();
                if (finalDownloadDataRealmEntity == null) {
                    finalDownloadDataRealmEntity = new DownloadRealm();
                    finalDownloadDataRealmEntity.setDownloadUrl(downloadModel.getDownloadUrl());
                }
                finalDownloadDataRealmEntity.setState(downloadModel.getState());
                finalDownloadDataRealmEntity.setTotalLength(downloadModel.getTotalLength());
                finalDownloadDataRealmEntity.setDownloadLength(downloadModel.getDownloadLength());
                finalDownloadDataRealmEntity.setProgress(downloadModel.getProgress());
                // finalDownloadDataRealmEntity.setFileName(downloadModel.getFileName());
                realm.copyToRealmOrUpdate(finalDownloadDataRealmEntity);
            }
        }), Throwable::printStackTrace);
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
