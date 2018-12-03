package com.kronos.download

import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.text.TextUtils

import com.kronos.download.adapter.BaseObserveAdapter

import java.io.File
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.util.regex.Pattern

/**
 * Created by Leif Zhang on 16/9/29.
 * Email leifzhanggithub@gmail.com
 */
class DownloadModel : BaseObserveAdapter() {
    var downloadUrl = ""
    private var downloadFolder = Environment.getExternalStorageDirectory().path + "/wallstreetcn/"
    private var sdFile: String? = null
    var fileName: String? = null
    private var suffixName: String? = null

    var progress: Int = 0
    var state: Int = 0
    var totalLength: Long = 0
    internal var downloadLength: Long = 0

    private val handler = Handler(Looper.getMainLooper(), Handler.Callback { msg ->
        when (msg.what) {
            10 -> notifyDataChange()
        }
        true
    })

    val sdCardFile: String?
        get() {
            if (TextUtils.isEmpty(sdFile)) {
                sdFile = downloadFolder + downloadUrl.hashCode() + getSuffix(downloadUrl)
            }
            return sdFile
        }

    fun setSuffixName(suffixName: String) {
        this.suffixName = suffixName
    }

    fun setDownloadFolder(downloadFolder: String?) {
        this.downloadFolder = downloadFolder.toString()
    }

    fun addDownloadLength(length: Int) {
        this.downloadLength += length.toLong()
        val curProgress = Math.round(downloadLength * 100f / totalLength)
        if (curProgress != progress) {
            progress = curProgress
            handler.sendEmptyMessage(10)
        }
    }

    fun getDownloadLength(): Long {
        try {
            val file = File(sdCardFile)
            downloadLength = if (!file.exists()) 0 else file.length()
            return downloadLength
        } catch (e: Exception) {
            e.printStackTrace()
            return downloadLength
        }

    }

    private fun getSuffix(url: String): String? {
        var url = url
        try {
            url = URLDecoder.decode(url, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        val suffixes = "avi|mpeg|3gp|mp3|mp4|wav|jpeg|gif|jpg|png|apk|exe|txt|html|zip|java|doc"
        val pat = Pattern.compile("[\\w]+[\\.]($suffixes)")//正则判断
        val mc = pat.matcher(url)//条件匹配
        var suffix = ""
        while (mc.find()) {
            suffix = mc.group()//截取文件名后缀名
        }
        if (!TextUtils.isEmpty(suffix)) {
            fileName = if (TextUtils.isEmpty(fileName)) suffix.substring(0, suffix.indexOf(".")) else fileName
            suffix = suffix.substring(suffix.indexOf("."), suffix.length)
        } else {
            fileName = if (TextUtils.isEmpty(fileName)) "未知文件" else fileName
            suffix = ".temp"
        }
        return if (TextUtils.isEmpty(suffixName)) {
            suffix
        } else {
            suffixName
        }
    }

    fun check(): Boolean {
        return getDownloadLength() == totalLength && totalLength != 0L
    }

       fun setDownloadLength(downloadLength: Long) {
           this.downloadLength = downloadLength
       }

    fun deleteFile() {
        val file = File(sdCardFile)
        file.delete()
    }
}
