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
        ItemEntity entity = new ItemEntity("美丽家", "http://download.apk8.com/d2/soft/meilijia.apk");
        list.add(entity);
        entity = new ItemEntity("果然方便", "http://download.apk8.com/d2/soft/guoranfangbian.apk");
        list.add(entity);
        entity = new ItemEntity("薄荷", "http://download.apk8.com/d2/soft/bohe.apk");
        list.add(entity);
        return list;
    }
}
