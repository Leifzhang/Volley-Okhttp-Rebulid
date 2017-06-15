package com.leif.moudle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leif Zhang on 2016/10/8.
 * Email leifzhanggithub@gmail.com
 */
public class ItemEntity {
    private String title;
    private String downloadUrl;

    public ItemEntity(String title, String downloadUrl) {
        this.title = title;
        this.downloadUrl = downloadUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public static List<ItemEntity> getEntityList() {
        List<ItemEntity> list = new ArrayList<>();
        ItemEntity entity = new ItemEntity("视频1", "http://wpimg.wallstcn.com/83a85415-64b9-45ca-9a6c-ecf508ba7d4d");
        list.add(entity);
        entity = new ItemEntity("梁非凡", "https://wpimg.wallstcn.com/9bb79831-360c-4d7c-8fb6-eb2b292eacdd");
        list.add(entity);
        entity = new ItemEntity("短视频", "http://streaming.wallstcn.com/%E9%95%BF%E8%B0%88%E5%B0%8F.mp4");
        list.add(entity);
        return list;
    }
}
