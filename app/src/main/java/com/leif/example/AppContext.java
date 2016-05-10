package com.leif.example;

import android.app.Application;
import android.content.Context;

/**
 * Created by zhangyang on 16/5/3.
 */
public class AppContext extends Application {
    static AppContext app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
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
