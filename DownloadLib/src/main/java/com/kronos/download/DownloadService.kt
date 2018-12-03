package com.kronos.download

import android.app.IntentService
import android.content.Intent

/**
 * Created by Leif Zhang on 16/9/29.
 * Email leifzhanggithub@gmail.com
 */
class DownloadService : IntentService("DownloadService") {

    override fun onHandleIntent(intent: Intent?) {
        try {
            val url = intent!!.getStringExtra("url")
            DownloadManager.startRequest(url)
            DownloadManager.saveOne(url)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onDestroy() {
        try {
            DownloadManager.saveAll()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        super.onDestroy()
    }
}
