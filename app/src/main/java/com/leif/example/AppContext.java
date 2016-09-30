package com.leif.example;

import android.app.Application;
import android.content.Context;

import com.kronos.volley.download.DownloadManager;

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
        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        DownloadManager.getInstance().setDownloadDb(new DataBase());
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
