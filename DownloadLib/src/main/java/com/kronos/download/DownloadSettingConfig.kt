package com.kronos.download

/**
 * Created by zhangyang on 2017/6/15.
 */

class DownloadSettingConfig {
    private var autoDownload = false
    internal var fileSuffix = ""

    fun isAutoDownload(): Boolean {
        return autoDownload
    }

    fun setAutoDownload(autoDownload: Boolean): DownloadSettingConfig {
        this.autoDownload = autoDownload
        return this
    }

    fun getFileSuffix(): String {
        return fileSuffix
    }

    fun setFileSuffix(fileSuffix: String): DownloadSettingConfig {
        this.fileSuffix = fileSuffix
        return this
    }
}
