package com.kronos.download

import android.os.Environment
import android.text.TextUtils

import okhttp3.OkHttpClient

/**
 * Created by Leif Zhang on 2016/10/8.
 * Email leifzhanggithub@gmail.com
 */
class DownloadConfig {
    var downloadFolder: String? = null
    var okHttpClient: OkHttpClient? = null
    var downloadDb: IDownloadDb? = null
    var settingConfig: DownloadSettingConfig? = null

    class Builder {
        private var downloadFolder: String? = null
        private var okHttpClient: OkHttpClient? = null
        private var downloadDb: IDownloadDb? = null
        private var settingConfig: DownloadSettingConfig? = null

        fun setDownloadDb(downloadDb: IDownloadDb): Builder {
            this.downloadDb = downloadDb
            return this
        }

        fun setDownloadFolder(downloadFolder: String): Builder {
            this.downloadFolder = downloadFolder
            return this
        }

        fun setOkHttpClient(okHttpClient: OkHttpClient): Builder {
            this.okHttpClient = okHttpClient
            return this
        }

        fun setSettingConfig(settingConfig: DownloadSettingConfig): Builder {
            this.settingConfig = settingConfig
            return this
        }

        fun builder(): DownloadConfig {
            val downloadConfig = DownloadConfig()
            if (TextUtils.isEmpty(downloadFolder)) {
                downloadFolder = Environment.getExternalStorageDirectory().path + "/wallstreetcn/"
            }
            downloadConfig.downloadFolder = downloadFolder
            if (okHttpClient == null) {
                okHttpClient = OkHttpClient.Builder()
                        .build()
            }
            downloadConfig.okHttpClient = okHttpClient
            if (downloadDb == null) {
                throw NullPointerException()
            }
            downloadConfig.downloadDb = downloadDb
            if (settingConfig == null) {
                settingConfig = DownloadSettingConfig()
            }
            downloadConfig.settingConfig = settingConfig
            return downloadConfig
        }
    }
}
