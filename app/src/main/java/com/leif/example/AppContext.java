package com.leif.example;

import android.app.Application;
import android.content.Context;

import com.kronos.download.DownloadConfig;
import com.kronos.download.DownloadManager;
import com.kronos.download.DownloadSettingConfig;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by zhangyang on 16/5/3.
 */
public class AppContext extends Application {
    static AppContext app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        DownloadSettingConfig settingConfig = new DownloadSettingConfig().setAutoDownload(true).setFileSuffix(".temp");
        DownloadConfig downloadConfig = new DownloadConfig.Builder().setDownloadDb(new DataBase()).setSettingConfig(settingConfig).builder();
        DownloadManager.getInstance().setConfig(downloadConfig);
        DownloadManager.getInstance().startAll(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }


    public static synchronized AppContext getApplication() {
        return app;
    }

}
