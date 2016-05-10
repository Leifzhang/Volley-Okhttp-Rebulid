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

import android.os.Handler;
import android.text.TextUtils;

import java.util.concurrent.Executor;

/**
 * Delivers responses and errors.
 */
public class ExecutorDelivery implements ResponseDelivery {
    /** Used for posting responses, typically to the main thread. */
    private final Executor mResponsePoster;

    /**
     * Creates a new response delivery interface.
     * @param handler {@link Handler} to post responses on
     */
    public ExecutorDelivery(final Handler handler) {
        // Make an Executor that just wraps the handler.
        mResponsePoster = new Executor() {
            @Override
            public void execute(Runnable command) {
                handler.post(command);
            }
        };
    }

    /**
     * Creates a new response delivery interface, mockable version
     * for testing.
     * @param executor For running delivery tasks
     */
    public ExecutorDelivery(Executor executor) {
        mResponsePoster = executor;
    }

    @Override
    public void postResponse(Request<?> request, RequestResponse<?> requestResponse) {
        postResponse(request, requestResponse, null);
    }

    @Override
    public void postResponse(Request<?> request, RequestResponse<?> requestResponse, Runnable runnable) {
        request.markDelivered();
        request.addMarker("post-requestResponse");
        mResponsePoster.execute(new ResponseDeliveryRunnable(request, requestResponse, runnable));
    }

    @Override
    public void postError(Request<?> request, VolleyError error) {
        String errStr = error == null || error.networkResponse == null || TextUtils.isEmpty(error.networkResponse.errorResponseString) ? 
                "<unparsed>" : error.networkResponse.errorResponseString;
        request.addMarker("post-error: " + errStr);
        RequestResponse<?> requestResponse = RequestResponse.error(error);
        mResponsePoster.execute(new ResponseDeliveryRunnable(request, requestResponse, null));
    }

    /**
     * A Runnable used for delivering network responses to a listener on the
     * main thread.
     */
    @SuppressWarnings("rawtypes")
    private class ResponseDeliveryRunnable implements Runnable {
        private final Request mRequest;
        private final RequestResponse mRequestResponse;
        private final Runnable mRunnable;

        public ResponseDeliveryRunnable(Request request, RequestResponse requestResponse, Runnable runnable) {
            mRequest = request;
            mRequestResponse = requestResponse;
            mRunnable = runnable;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            // If this request has canceled, finish it and don't deliver.
            if (mRequest.isCanceled()) {
                mRequest.finish("canceled-at-delivery");
                return;
            }

            // Deliver a normal response or error, depending.
            if (mRequestResponse.isSuccess()) {
                mRequest.deliverResponse(mRequestResponse.result, mRequestResponse.intermediate);
            } else {
                mRequest.deliverError(mRequestResponse.error);
            }

            // If this is an intermediate response, add a marker, otherwise we're done
            // and the request can be finished.
            if (mRequestResponse.intermediate) {
                mRequest.addMarker("intermediate-response");
            } else {
                mRequest.finish("done");
            }

            // If we have been provided a post-delivery runnable, run it.
            if (mRunnable != null) {
                mRunnable.run();
            }
       }
    }
}
