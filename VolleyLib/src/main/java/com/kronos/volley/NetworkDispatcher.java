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

package com.kronos.volley;

import android.annotation.TargetApi;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Process;

import java.util.concurrent.BlockingQueue;

public class NetworkDispatcher extends Thread {
    /**
     * The queue of requests to service.
     */
    private final BlockingQueue<Request<?>> mQueue;
    /**
     * The network interface for processing requests.
     */
    private final Network mNetwork;
    /**
     * The cache to write to.
     */
    private final Cache mCache;
    /**
     * For posting responses and errors.
     */
    private final ResponseDelivery mDelivery;
    /**
     * Used for telling us to die.
     */
    private volatile boolean mQuit = false;

    /**
     * Creates a new network dispatcher thread.  You must call {@link #start()}
     * in order to begin processing.
     *
     * @param queue    Queue of incoming requests for triage
     * @param network  Network interface to use for performing requests
     * @param cache    Cache interface to use for writing responses to cache
     * @param delivery Delivery interface to use for posting responses
     */
    public NetworkDispatcher(BlockingQueue<Request<?>> queue,
                             Network network, Cache cache,
                             ResponseDelivery delivery) {
        mQueue = queue;
        mNetwork = network;
        mCache = cache;
        mDelivery = delivery;
    }

    /**
     * Forces this dispatcher to quit immediately.  If any requests are still in
     * the queue, they are not guaranteed to be processed.
     */
    public void quit() {
        mQuit = true;
        interrupt();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void addTrafficStatsTag(Request<?> request) {
        // Tag the request (if API >= 14)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            TrafficStats.setThreadStatsTag(request.getTrafficStatsTag());
        }
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        Request<?> request;
        while (true) {
            try {
                // Take a request from the queue.
                request = mQueue.take();
            } catch (InterruptedException e) {
                // We may have been interrupted because it was time to quit.
                if (mQuit) {
                    return;
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                continue;
            }

            try {
                request.addMarker("network-queue-take");

                // If the request was cancelled already, do not perform the
                // network request.
                if (request.isCanceled()) {
                    request.finish("network-discard-cancelled");
                    continue;
                }

                addTrafficStatsTag(request);

                // Perform the network request.
                NetworkResponse networkResponse = mNetwork.performRequest(request);
                request.addMarker("network-http-complete");
                networkResponse.cacheTime = request.getCacheTime();
                // If the server returned 304 AND we delivered a requestResponse already,
                // we're done -- don't deliver a second identical requestResponse.
                if (networkResponse.notModified && request.hasHadResponseDelivered()) {
                    request.finish("not-modified");
                    mCache.update(request.getCacheKey(), request.getCacheTime());
                    continue;
                }

                // Parse the requestResponse here on the worker thread.
                RequestResponse<?> requestResponse = request.parseNetworkResponse(networkResponse);

                request.addMarker("network-parse-complete");
                // Write to cache if applicable.
                // TODO: Only update cache metadata instead of entire record for 304s.
                if (request.shouldCache() && requestResponse.cacheEntry != null) {
                    mCache.put(request.getCacheKey(), requestResponse.cacheEntry);
                    request.addMarker("network-cache-written");
                }

                // Post the requestResponse back.
                request.markDelivered();
                mDelivery.postResponse(request, requestResponse);
            } catch (ParseError volleyError) {
                VolleyLog.e(volleyError, "Unhandled exception %s", volleyError.toString());
                parseAndDeliverNetworkError(request, volleyError);
                mCache.remove(request.getCacheKey());
            } catch (VolleyError volleyError) {
                VolleyLog.e(volleyError, "Unhandled exception %s", volleyError.toString());
                parseAndDeliverNetworkError(request, volleyError);
            } catch (Exception e) {
                VolleyLog.e(e, "Unhandled exception %s", e.toString());
                mDelivery.postError(request, new VolleyError(e));
                mCache.remove(request.getCacheKey());
            }
        }
    }

    private void parseAndDeliverNetworkError(Request<?> request, VolleyError error) {
        error = request.parseNetworkError(error);
        mDelivery.postError(request, error);
    }
}
