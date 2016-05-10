package com.kronos.volley.toolbox;

import android.util.LruCache;

import java.io.File;

/**
 * Created by zhangyang on 16/5/3.
 */
public class LruDiskCache extends DiskBasedCache {
    private LruCache<String, Entry> lruCache;

    public LruDiskCache(File rootDirectory, int maxCacheSizeInBytes) {
        super(rootDirectory, maxCacheSizeInBytes);
        lruCache = new LruCache<>(maxCacheSizeInBytes);
    }

    public LruDiskCache(File rootDirectory) {
        this(rootDirectory, DEFAULT_DISK_USAGE_BYTES);
    }

    @Override
    public synchronized void put(String key, Entry entry) {
        super.put(key, entry);
        lruCache.put(key, entry);
    }

    @Override
    public synchronized Entry get(String key) {
        Entry entry = lruCache.get(key);
        if (entry == null)
            entry = super.get(key);
        return entry;
    }

    @Override
    public synchronized void clear() {
        super.clear();
        lruCache.evictAll();
    }
}
