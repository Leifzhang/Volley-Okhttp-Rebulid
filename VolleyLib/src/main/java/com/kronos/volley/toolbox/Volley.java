/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kronos.volley.toolbox;


import android.content.Context;

import com.kronos.volley.Network;
import com.kronos.volley.RequestQueue;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class Volley {

    private static final String DEFAULT_CACHE_DIR = "volley";


    public static RequestQueue newRequestQueue(Context context) {
        File cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        HttpStack stack = new OkHttpStack(client);
        Network network = new BasicNetwork(stack);
        RequestQueue queue = new RequestQueue(new LruDiskCache(cacheDir), network);
        queue.start();
        return queue;
    }

}
