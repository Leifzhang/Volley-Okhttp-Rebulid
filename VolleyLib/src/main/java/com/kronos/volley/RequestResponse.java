package com.kronos.volley;


public class RequestResponse<T> {

    public interface Listener<T> {

        void onResponse(T response);
    }


    public interface ErrorListener {

        void onErrorResponse(VolleyError error);
    }


    public static <T> RequestResponse<T> success(T result, Cache.Entry cacheEntry) {
        return new RequestResponse<T>(result, cacheEntry);
    }


    public static <T> RequestResponse<T> error(VolleyError error) {
        return new RequestResponse<T>(error);
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


    private RequestResponse(T result, Cache.Entry cacheEntry) {
        this.result = result;
        this.cacheEntry = cacheEntry;
        this.error = null;
    }

    private RequestResponse(VolleyError error) {
        this.result = null;
        this.cacheEntry = null;
        this.error = error;
    }
}
