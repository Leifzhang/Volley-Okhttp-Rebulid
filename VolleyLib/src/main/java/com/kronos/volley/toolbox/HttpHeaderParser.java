/*
 * Copyright (C) 2011 The Android Open Source Project
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


import com.kronos.volley.Cache;
import com.kronos.volley.NetworkResponse;

import java.util.Map;

/**
 * Utility methods for parsing HTTP headers.
 */
public class HttpHeaderParser {

    /**
     * Extracts a {@link Cache.Entry} from a {@link NetworkResponse}.
     *
     * @param response The network response to parse headers from
     * @return a cache entry for the given response, or null if the response is not cacheable.
     */
    public static Cache.Entry parseCacheHeaders(NetworkResponse response) {
        long now = System.currentTimeMillis();

        Map<String, String> headers = response.headers;

        long serverDate = 0;
        long serverExpires = 0;
        long softExpire = 0;
        long maxAge = 0;
        boolean hasCacheControl = false;

        String serverEtag = null;
        String headerValue;


        headerValue = headers.get("Cache-Control".toLowerCase());
        if (headerValue != null) {
            hasCacheControl = true;
            String[] tokens = headerValue.split(",");
            for (int i = 0; i < tokens.length; i++) {
                String token = tokens[i].trim();
                if (token.startsWith("max-age=")) {
                    try {
                        maxAge = Long.parseLong(token.substring(8));
                    } catch (Exception e) {
                    }
                } else if (token.equals("must-revalidate".toLowerCase()) ||
                        token.equals("proxy-revalidate".toLowerCase())) {
                    maxAge = 0;
                }
            }
        }
        String ETag = "ETag".toLowerCase();

        serverEtag = headers.get(ETag);

        // Cache-Control takes precedence over an Expires header, even if both exist and Expires
        // is more restrictive.
        if (hasCacheControl) {
            softExpire = now + maxAge * 1000;
        } else if (serverDate > 0 && serverExpires >= serverDate) {
            // Default semantic for Expire header in HTTP specification is softExpire.
            softExpire = now + (serverExpires - serverDate);
        } else {
            softExpire = now;
        }

        Cache.Entry entry = new Cache.Entry();
        entry.data = response.data;
        entry.etag = serverEtag;
        entry.softTtl = softExpire + response.cacheTime;
        entry.ttl = entry.softTtl;
        entry.serverDate = serverDate;
        entry.responseHeaders = headers;

        return entry;
    }


}
