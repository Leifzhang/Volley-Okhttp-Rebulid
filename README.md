## Volley-OkHttp-Rebuild

基于谷歌volley和OkHttp3网络请求库的重构版本,同时增加文件from上传功能以及网络请求本地cache功能,将请求的cache控制在request中.

sample中有简单的网络库封装逻辑.同时数据解析器会放在线程中执行,这样避免数据在UI中解析造成卡顿.

## Usage
Add this line to your `build.gradle` file under your module directory. 
```
compile 'com.github.leifzhang:VolleyLib:0.2.0'
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
        }).setMethod(Method()).setHeader(getHeader()).setApiParser(getParser())
        .setCacheTime(cacheTime).setIsRefreshNeed(isNeedRefresh);
        request.setRequestBody(getRequestBody());
```
## License
[MIT](https://opensource.org/licenses/MIT)
