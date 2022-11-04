package com.kronos.volley;


import org.jetbrains.annotations.NotNull;

public class Response<T> {

    public interface Listener<T> {

        void onResponse(@NotNull T response);
    }


    public interface ErrorListener {

        void onErrorResponse(@NotNull VolleyError error);
    }


    public static <T> Response<T> success(T result, Cache.Entry cacheEntry) {
        return new Response<T>(result, cacheEntry);
    }


    public static <T> Response<T> error(VolleyError error) {
        return new Response<T>(error);
    }


    public final T result;

    /**
     * Cache metadata for this response, or null in the case of error.
     */
    public final Cache.Entry cacheEntry;

    /**
     * Detailed error information if <code>errorCode != OK</code>.
     */
    public final VolleyError error;

    /**
     * True if this response was a soft-expired one and a second one MAY be coming.
     */
    public boolean intermediate = false;


    public boolean isSuccess() {
        return error == null;
    }


    private Response(T result, Cache.Entry cacheEntry) {
        this.result = result;
        this.cacheEntry = cacheEntry;
        this.error = null;
    }

    private Response(VolleyError error) {
        this.result = null;
        this.cacheEntry = null;
        this.error = error;
    }
}
