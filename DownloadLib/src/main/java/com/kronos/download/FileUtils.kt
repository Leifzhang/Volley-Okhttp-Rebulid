package com.kronos.download

import android.content.Context
import android.net.ConnectivityManager

import java.io.File
import java.io.IOException

/**
 * Created by Leif Zhang on 16/9/29.
 * Email leifzhanggithub@gmail.com
 */
object FileUtils {

    fun createFile(filePath: String?): Boolean {
        try {
            val file = File(filePath)
            if (!file.exists()) {
                if (!file.parentFile.exists()) {
                    file.parentFile.mkdirs()
                }

                return file.createNewFile()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return true
    }


    fun isConnectWIFI(context: Context): Boolean {
        val connectManager = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connectManager.activeNetworkInfo
        return info != null && info.type == ConnectivityManager.TYPE_WIFI
    }


}
