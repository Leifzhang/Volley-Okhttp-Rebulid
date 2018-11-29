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


import com.kronos.volley.Request;
import com.kronos.volley.Response;
import com.kronos.volley.VolleyError;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class RequestFuture<T> implements Future<T>, Response.Listener<T>,
       Response.ErrorListener {
    private Request<?> mRequest;
    private boolean mResultReceived = false;
    private T mResult;
    private VolleyError mException;

    public static <E> RequestFuture<E> newFuture() {
        return new RequestFuture<E>();
    }

    private RequestFuture() {}

    public void setRequest(Request<?> request) {
        mRequest = request;
    }

    @Override
    public synchronized boolean cancel(boolean mayInterruptIfRunning) {
        if (mRequest == null) {
            return false;
        }

        if (!isDone()) {
            mRequest.cancel();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {

        try {
            return doGet(null);
        } catch (TimeoutException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public T get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return doGet(TimeUnit.MILLISECONDS.convert(timeout, unit));
    }

    private synchronized T doGet(Long timeoutMs)
            throws InterruptedException, ExecutionException, TimeoutException {
        if (mException != null) {
            throw new ExecutionException(mException);
        }

        if (mResultReceived) {
            return mResult;
        }

        if (timeoutMs == null) {
            wait(0);
        } else if (timeoutMs > 0) {
            wait(timeoutMs);
        }

        if (mException != null) {
            throw new ExecutionException(mException);
        }

        if (!mResultReceived) {
            throw new TimeoutException();
        }

        return mResult;
    }

    @Override
    public boolean isCancelled() {
        if (mRequest == null) {
            return false;
        }
        return mRequest.isCanceled();
    }

    @Override
    public synchronized boolean isDone() {
        return mResultReceived || mException != null || isCancelled();
    }

    @Override
    public synchronized void onResponse(T response) {
        mResultReceived = true;
        mResult = response;
        notifyAll();
    }

    @Override
    public synchronized void onErrorResponse(VolleyError error) {
        mException = error;
        notifyAll();
    }
}

