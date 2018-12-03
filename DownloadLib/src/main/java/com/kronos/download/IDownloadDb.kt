package com.kronos.download

import java.util.HashMap

/**
 * Created by Leif Zhang on 16/9/30.
 * Email leifzhanggithub@gmail.com
 */
interface IDownloadDb {

    fun saveToDb(model: DownloadModel?)

    fun saveToDb(models: HashMap<String, DownloadModel>?)

    fun getFromDB(config: DownloadConfig): HashMap<String, DownloadModel>
}
