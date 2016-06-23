## Volley-Okhttp-Rebuld
It's depends on volley and okhttp3 network lib. And I just modify some code for easy use this to bulid you project.

## Usage
Add this line to your `build.gradle` file under your module directory. 
```
compile 'com.github.leifzhang:VolleyLib:0.1.3'
```
## How to use it
```java
    StringRequest request = new StringRequest(url);
    request.setRequestListener(new RequestResponse.Listener<NetResponse>() {
            @Override
            public void onResponse(NetResponse response) {
            
            }
        }).setErrorListener(new RequestResponse.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
              
            }
        }).setMethod(Method()).setHeader(getHeader()).setApiParser(getParser()).setCacheTime(cacheTime).setIsRefreshNeed(isNeedRefresh);
        request.setRequestBody(getRequestBody());
```
## License
[MIT](https://opensource.org/licenses/MIT)
