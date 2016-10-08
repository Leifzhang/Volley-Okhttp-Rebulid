package com.kronos.download;

import android.os.Environment;
import android.text.TextUtils;

import okhttp3.OkHttpClient;

/**
 * Created by Leif Zhang on 2016/10/8.
 * Email leifzhanggithub@gmail.com
 */
public class DownloadConfig {
    private String downloadFolder;
    private OkHttpClient okHttpClient;
    private IDownloadDb downloadDb;

    public IDownloadDb getDownloadDb() {
        return downloadDb;
    }

    public void setDownloadDb(IDownloadDb downloadDb) {
        this.downloadDb = downloadDb;
    }

    public String getDownloadFolder() {
        return downloadFolder;
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public void setDownloadFolder(String downloadFolder) {
        this.downloadFolder = downloadFolder;
    }

    public void setOkHttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    public static class Builder {
        private String downloadFolder;
        private OkHttpClient okHttpClient;
        private IDownloadDb downloadDb;

        public Builder setDownloadDb(IDownloadDb downloadDb) {
            this.downloadDb = downloadDb;
            return this;
        }

        public Builder setDownloadFolder(String downloadFolder) {
            this.downloadFolder = downloadFolder;
            return this;
        }

        public Builder setOkHttpClient(OkHttpClient okHttpClient) {
            this.okHttpClient = okHttpClient;
            return this;
        }

        public DownloadConfig builder() {
            DownloadConfig downloadConfig = new DownloadConfig();
            if (TextUtils.isEmpty(downloadFolder)) {
                downloadFolder = Environment.getExternalStorageDirectory().getPath() + "/wallstreetcn/";
            }
            downloadConfig.setDownloadFolder(downloadFolder);
            if (okHttpClient == null) {
                okHttpClient = new OkHttpClient.Builder()
                        .build();
            }
            downloadConfig.setOkHttpClient(okHttpClient);
            if (downloadDb == null) {
                throw new NullPointerException();
            }
            downloadConfig.setDownloadDb(downloadDb);
            return downloadConfig;
        }
    }
}
