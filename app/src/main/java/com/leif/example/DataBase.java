package com.leif.example;


import androidx.annotation.Nullable;

import com.kronos.download.DownloadConfig;
import com.kronos.download.DownloadModel;
import com.kronos.download.IDownloadDb;

import java.util.HashMap;

/**
 * Created by Leif Zhang on 16/9/30.
 * Email leifzhanggithub@gmail.com
 */
public class DataBase implements IDownloadDb {
    @Override
    public void saveToDb(@Nullable DownloadModel model) {

    }

    @Override
    public void saveToDb(@Nullable HashMap<String, DownloadModel> models) {

    }

    @Override
    public HashMap<String, DownloadModel> getFromDB(DownloadConfig config) {
        return new HashMap<>();
    }


}
