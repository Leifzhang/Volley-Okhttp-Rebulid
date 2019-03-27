package com.kronos.download

import android.content.Context
import android.content.Intent

import java.io.BufferedInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.RandomAccessFile
import java.util.HashMap

import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Created by Leif Zhang on 16/9/29.
 * Email leifzhanggithub@gmail.com
 */
object DownloadManager {
    private var models: HashMap<String, DownloadModel>? = null
    private var config: DownloadConfig? = null


    private val okHttpClient: OkHttpClient?
        get() = config!!.okHttpClient


    init {
        models = HashMap()
    }

    fun setConfig(downloadConfig: DownloadConfig) {
        this.config = downloadConfig
        models = config!!.downloadDb!!.getFromDB(downloadConfig)
    }


    private fun isReady() {
        if (config == null) {
            throw NullPointerException()
        }
    }

    fun pauseAll() {
        for ((_, value) in models!!) {
            value.state = DownloadConstants.DOWNLOAD_PAUSE
        }
    }

    fun pause(url: String) {
        if (models!!.containsKey(url)) {
            models!![url]?.state = DownloadConstants.DOWNLOAD_PAUSE
        }
    }

    fun startAll(context: Context) {
        if (config!!.settingConfig!!.isAutoDownload() || FileUtils.isConnectWIFI(context)) {
            for ((key, model) in models!!) {
                model.state = DownloadConstants.DOWNLOADING
                val intent = Intent(context, DownloadService::class.java)
                intent.putExtra("url", key)
                context.startService(intent)
            }
        }
    }

    fun start(context: Context, url: String) {
        if (models!!.containsKey(url)) {
            val intent = Intent(context, DownloadService::class.java)
            intent.putExtra("url", url)
            context.startService(intent)
        }
    }

    private fun putModel(url: String, model: DownloadModel) {
        models!![url] = model
        saveAll()
    }

    fun getModel(url: String): DownloadModel? {
        return if (models!!.containsKey(url)) {
            models!![url]
        } else {
            null
        }
    }


    @Throws(IOException::class)
    fun startRequest(url: String) {
        val downloadModel = getModel(url) ?: return
        if (downloadModel.check()) {
            downloadModel.state = DownloadConstants.DOWNLOAD_FINISH
            return
        }
        if (downloadModel.downloadLength > downloadModel.totalLength) {
            val file = File(downloadModel.sdCardFile)
            file.delete()
            downloadModel.downloadLength = 0
            downloadModel.progress = 0
            downloadModel.totalLength = 0
        }
        val range = "bytes=" + downloadModel.downloadLength + "-"
        val request = Request.Builder().url(downloadModel.downloadUrl)
                .addHeader("RANGE", range).build()
        val response = okHttpClient!!.newCall(request).execute()
        val responseBody = response.body()
        if (downloadModel.state == 0) {
            downloadModel.state = DownloadConstants.DOWNLOADING
        }
        if (downloadModel.totalLength == 0L) {
            downloadModel.totalLength = responseBody!!.contentLength()
        }
        writeFile(downloadModel, responseBody!!.byteStream())
        if (downloadModel.check()) {
            downloadModel.state = DownloadConstants.DOWNLOAD_FINISH
        }
    }

    @Throws(IOException::class)
    private fun writeFile(model: DownloadModel, input: InputStream) {
        val destFilePath = model.sdCardFile
        if (!FileUtils.createFile(destFilePath)) {
            return
        }
        val randomAccessFile = RandomAccessFile(destFilePath, "rw")
        randomAccessFile.seek(model.downloadLength)
        var readCount: Int
        val len = 1024
        val buffer = ByteArray(len)
        val bufferedInputStream = BufferedInputStream(input, len)
        do {
            readCount = bufferedInputStream.read(buffer)
            if (readCount != -1) {
                if (model.state == DownloadConstants.DOWNLOADING) {
                    randomAccessFile.write(buffer, 0, readCount)
                    model.addDownloadLength(readCount)
                } else {
                    break
                }
            }
        } while (true)
        randomAccessFile.close()
        bufferedInputStream.close()
    }

    fun saveOne(url: String) {
        try {
            config!!.downloadDb!!.saveToDb(models!![url])
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun saveAll() {
        try {
            config!!.downloadDb!!.saveToDb(models)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun remove(url: String) {
        if (models!!.containsKey(url)) {
            models!!.remove(url)
        }
    }

    /* companion object {
         val instance = DownloadManager()

         @JvmOverloads
         fun setDownloadModel(url: String, context: Context, fileName: String? = null) {
             instance.isReady()
             var downloadModel = instance.getModel(url)
             if (downloadModel == null) {
                 downloadModel = DownloadModel()
                 downloadModel.fileName = fileName
                 downloadModel.downloadUrl = url
                 downloadModel.setSuffixName(instance.config!!.settingConfig!!.fileSuffix)
                 downloadModel.setDownloadFolder(instance.config!!.downloadFolder)
                 instance.putModel(url, downloadModel)
             }
             val intent = Intent(context, DownloadService::class.java)
             intent.putExtra("url", url)
             context.startService(intent)
         }
     }*/
    fun setDownloadModel(url: String, context: Context) {
        setDownloadModel(url, context, null)
    }

    fun setDownloadModel(url: String, context: Context, fileName: String? = null) {
        isReady()
        var downloadModel = getModel(url)
        if (downloadModel == null) {
            downloadModel = DownloadModel()
            downloadModel.fileName = fileName
            downloadModel.downloadUrl = url
            downloadModel.setSuffixName(config!!.settingConfig!!.fileSuffix)
            downloadModel.setDownloadFolder(config!!.downloadFolder)
            putModel(url, downloadModel)
        }
        val intent = Intent(context, DownloadService::class.java)
        intent.putExtra("url", url)
        context.startService(intent)
    }
}
