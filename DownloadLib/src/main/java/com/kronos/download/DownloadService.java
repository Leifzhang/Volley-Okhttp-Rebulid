package com.kronos.download;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by Leif Zhang on 16/9/29.
 * Email leifzhanggithub@gmail.com
 */
public class DownloadService extends IntentService {

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            String url = intent.getStringExtra("url");
            DownloadManager.getInstance().startRequest(url);
            DownloadManager.getInstance().save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DownloadManager.getInstance().save();
    }
}
