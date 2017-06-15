package com.kronos.download;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.File;
import java.io.IOException;

/**
 * Created by Leif Zhang on 16/9/29.
 * Email leifzhanggithub@gmail.com
 */
public class FileUtils {

    public static boolean createFile(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }

                return file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }



    public static Boolean isConnectWIFI(Context context) {
        ConnectivityManager connectManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectManager.getActiveNetworkInfo();
        return info != null && info.getType() == ConnectivityManager.TYPE_WIFI;
    }


}
