package com.kronos.download;

import java.util.HashMap;

/**
 * Created by Leif Zhang on 16/9/30.
 * Email leifzhanggithub@gmail.com
 */
public interface IDownloadDb {
    void saveToDb(HashMap<String, DownloadModel> models);

    HashMap<String, DownloadModel> getFromDB();
}
