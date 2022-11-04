package com.leif.example;

import android.app.Application;
import android.content.Context;

import com.kronos.download.DownloadConfig;
import com.kronos.download.DownloadManager;
import com.kronos.download.DownloadSettingConfig;


/**
 * Created by zhangyang on 16/5/3.
 */
public class AppContext extends Application {
    static AppContext app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        DownloadSettingConfig settingConfig = new DownloadSettingConfig().setAutoDownload(true).setFileSuffix(".temp");
        DownloadConfig downloadConfig = new DownloadConfig.Builder().setDownloadDb(new DataBase()).setSettingConfig(settingConfig).builder();
        DownloadManager.INSTANCE.setConfig(downloadConfig);
        DownloadManager.INSTANCE.startAll(this);
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
