package com.kronos.volley.download;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Leif Zhang on 16/9/29.
 * Email leifzhanggithub@gmail.com
 */
public class DownloadModel {
    private String downloadUrl = "http://download.apk8.com/d2/soft/meilijia.apk";
    private String downloadFolder = Environment.getExternalStorageDirectory().getPath() + "/wallstreetcn/";
    private String sdFile;
    private int progress;
    private int state;
    private long totalLength;
    private long downloadLength = 0;

    public String getSdCardFile() {
        if (TextUtils.isEmpty(sdFile)) {
            sdFile = downloadFolder + downloadUrl.hashCode() + getSuffix(downloadUrl);
        }
        return sdFile;
    }

    public void addDownloadLength(int length) {
        this.downloadLength += length;
        int curProgress = Math.round(downloadLength * 100f / totalLength);
        if (curProgress != progress) {
            progress = curProgress;
        }
    }

    public long getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(long totalLength) {
        this.totalLength = totalLength;
    }

    public long getDownloadLength() {
        try {
            File file = new File(getSdCardFile());
            downloadLength = file.length();
            return downloadLength;
        } catch (Exception e) {
            e.printStackTrace();
            return downloadLength = 0;
        }
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public int getProgress() {
        return progress;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    private String getSuffix(String url) {
        String suffixes = "avi|mpeg|3gp|mp3|mp4|wav|jpeg|gif|jpg|png|apk|exe|txt|html|zip|java|doc";
        Pattern pat = Pattern.compile("[\\w]+[\\.](" + suffixes + ")");//正则判断
        Matcher mc = pat.matcher(url);//条件匹配
        String suffix = "";
        while (mc.find()) {
            suffix = mc.group();//截取文件名后缀名

        }
        if (!TextUtils.isEmpty(suffix)) {
            suffix = suffix.substring(suffix.indexOf("."), suffix.length());
        } else {
            suffix = ".temp";
        }
        return suffix;
    }

    public boolean check() {
        return getDownloadLength() == getTotalLength() && getTotalLength() != 0;
    }

}
