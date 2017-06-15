package com.kronos.download;

/**
 * Created by zhangyang on 2017/6/15.
 */

public class DownloadSettingConfig {
    private boolean autoDownload = false;
    private String fileSuffix = "";

    public boolean isAutoDownload() {
        return autoDownload;
    }

    public DownloadSettingConfig setAutoDownload(boolean autoDownload) {
        this.autoDownload = autoDownload;
        return this;
    }

    public String getFileSuffix() {
        return fileSuffix;
    }

    public DownloadSettingConfig setFileSuffix(String fileSuffix) {
        this.fileSuffix = fileSuffix;
        return this;
    }
}
