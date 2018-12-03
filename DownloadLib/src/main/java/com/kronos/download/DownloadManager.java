package com.kronos.download;

import android.content.Context;
import android.content.Intent;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by Leif Zhang on 16/9/29.
 * Email leifzhanggithub@gmail.com
 */
public class DownloadManager {
    private static DownloadManager ourInstance = new DownloadManager();
    private HashMap<String, DownloadModel> models;
    private DownloadConfig config;

    public static DownloadManager getInstance() {
        return ourInstance;
    }


    private DownloadManager() {
        models = new HashMap<>();
    }

    public void setConfig(DownloadConfig downloadConfig) {
        this.config = downloadConfig;
        models = config.getDownloadDb().getFromDB(downloadConfig);
    }

    public static void setDownloadModel(String url, Context context) {
        setDownloadModel(url, context, null);
    }

    public static void setDownloadModel(String url, Context context, String fileName) {
        getInstance().isReady();
        DownloadModel downloadModel = getInstance().getModel(url);
        if (downloadModel == null) {
            downloadModel = new DownloadModel();
            downloadModel.setFileName(fileName);
            downloadModel.setDownloadUrl(url    );
            downloadModel.setSuffixName(getInstance().config.getSettingConfig().getFileSuffix());
            downloadModel.setDownloadFolder(getInstance().config.getDownloadFolder());
            getInstance().putModel(url, downloadModel);
        }
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra("url", url);
        context.startService(intent);
    }


    private void isReady() {
        if (config == null) {
            throw new NullPointerException();
        }
    }

    public void pauseAll() {
        for (Map.Entry<String, DownloadModel> entry : models.entrySet()) {
            entry.getValue().setState(DownloadConstants.DOWNLOAD_PAUSE);
        }
    }

    public void pause(String url) {
        if (models.containsKey(url)) {
            models.get(url).setState(DownloadConstants.DOWNLOAD_PAUSE);
        }
    }

    public void startAll(Context context) {
        if (config.getSettingConfig().isAutoDownload() || FileUtils.isConnectWIFI(context)) {
            for (Map.Entry<String, DownloadModel> entry : models.entrySet()) {
                DownloadModel model = entry.getValue();
                model.setState(DownloadConstants.DOWNLOADING);
                Intent intent = new Intent(context, DownloadService.class);
                intent.putExtra("url", entry.getKey());
                context.startService(intent);
            }
        }
    }

    public void start(Context context, String url) {
        if (models.containsKey(url)) {
            Intent intent = new Intent(context, DownloadService.class);
            intent.putExtra("url", url);
            context.startService(intent);
        }
    }

    private void putModel(String url, DownloadModel model) {
        models.put(url, model);
        saveAll();
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
            downloadModel.setState(DownloadConstants.DOWNLOAD_FINISH);
            return;
        }
        if (downloadModel.getDownloadLength() > downloadModel.getTotalLength()) {
            File file = new File(downloadModel.getSdCardFile());
            file.delete();
            downloadModel.setProgress(0);
            downloadModel.setTotalLength(0);
        }
        String range = "bytes=" + downloadModel.getDownloadLength() + "-";
        final Request request = new Request.Builder().url(downloadModel.getDownloadUrl())
                .addHeader("RANGE", range).build();
        Response response = getOkHttpClient().newCall(request).execute();
        ResponseBody responseBody = response.body();
        if (downloadModel.getState() == 0) {
            downloadModel.setState(DownloadConstants.DOWNLOADING);
        }
        if (downloadModel.getTotalLength() == 0) {
            downloadModel.setTotalLength(responseBody.contentLength());
        }
        writeFile(downloadModel, responseBody.byteStream());
        if (downloadModel.check()) {
            downloadModel.setState(DownloadConstants.DOWNLOAD_FINISH);
        }
    }

    private void writeFile(DownloadModel model, InputStream input) throws IOException {
        String destFilePath = model.getSdCardFile();
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
        return config.getOkHttpClient();
    }

    public void saveOne(String url) {
        try {
            config.getDownloadDb().saveToDb(models.get(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void saveAll() {
        try {
            config.getDownloadDb().saveToDb(models);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void remove(String url) {
        if (models.containsKey(url)) {
            models.remove(url);
        }
    }

}
