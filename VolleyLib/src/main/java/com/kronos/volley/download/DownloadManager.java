package com.kronos.volley.download;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.kronos.volley.toolbox.FileUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Leif Zhang on 16/9/29.
 * Email leifzhanggithub@gmail.com
 */
public class DownloadManager {
    private String downloadFolder = Environment.getExternalStorageDirectory().getPath() + "/wallstreetcn/";// SD卡路径;
    private static DownloadManager ourInstance = new DownloadManager();
    private HashMap<String, DownloadModel> models;

    public static DownloadManager getInstance() {
        return ourInstance;
    }

    private OkHttpClient okHttpClient;

    private DownloadManager() {
        models = new HashMap<>();
    }

    public void setOkHttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    public static void setDownloadModel(String url, Context context) {
        DownloadModel downloadModel = getInstance().getModel(url);
        if (downloadModel == null) {
            downloadModel = new DownloadModel();
            downloadModel.setDownloadUrl(url);
            getInstance().putModel(url, downloadModel);
        }
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra("url", url);
        context.startService(intent);
    }

    private void putModel(String url, DownloadModel model) {
        models.put(url, model);
    }

    public DownloadModel getModel(String url) {
        if (models.containsKey(url)) {
            return models.get(url);
        } else {
            return null;
        }
    }


    public void startRequest(String url) throws IOException {
        final DownloadModel downloadModel = getModel(url);
        if (downloadModel == null) {
            return;
        }
        if (downloadModel.check()) {
            return;
        }
        String range = "bytes=" + downloadModel.getDownloadLength() + "-";
        final Request request = new Request.Builder().url(downloadModel.getDownloadUrl())
                .addHeader("RANGE", range).build();
        Response response = getOkHttpClient().newCall(request).execute();
        ProgressResponseBody responseBody = new ProgressResponseBody(response.body(),
                new ProgressResponseBody.ProgressListener() {
                    @Override
                    public void update(long bytesRead, long contentLength, boolean done) {
                        Log.i("DownloadManager", "DownloadManager:" + downloadModel.getProgress());
                    }
                });
        downloadModel.setState(DownloadConstants.DOWNLOADING);
        if (downloadModel.getTotalLength() == 0) {
            downloadModel.setTotalLength(responseBody.contentLength());
        }
        writeFile(downloadModel, responseBody.byteStream());
    }

    private void writeFile(DownloadModel model, InputStream input) throws IOException {
        String destFilePath = downloadFolder + model.getDownloadUrl().hashCode() + ".apk";
        if (!FileUtils.createFile(destFilePath)) {
            return;
        }
        RandomAccessFile randomAccessFile = new RandomAccessFile(destFilePath, "rw");
        randomAccessFile.seek(model.getDownloadLength());
        int readCount;
        int len = 1024;
        byte[] buffer = new byte[len];
        BufferedInputStream in = new BufferedInputStream(input, len);
        while ((readCount = in.read(buffer)) != -1) {
            if (model.getState() == DownloadConstants.DOWNLOADING) {
                randomAccessFile.write(buffer, 0, readCount);
                model.addDownloadLength(readCount);
            } else {
                break;
            }
        }
        randomAccessFile.close();
        in.close();
    }


    private OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
                    .build();
        }
        return okHttpClient;
    }


}
