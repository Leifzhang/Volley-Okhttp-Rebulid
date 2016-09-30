package com.kronos.volley.download;

import android.app.IntentService;
import android.content.Intent;

import java.io.IOException;

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
