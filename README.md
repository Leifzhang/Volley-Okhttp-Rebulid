## Volley-OkHttp-Rebuild

基于谷歌volley和OkHttp3网络请求库的重构版本,同时增加文件from上传功能以及网络请求本地cache功能,将请求的cache控制在request中.

sample中有简单的网络库封装逻辑.同时数据解析器会放在线程中执行,这样避免数据在UI中解析造成卡顿,同时有多文件下载.

同时拓展了大文件下载,支持断点续传以及暂停,暂时只支持一个个文件队列下载, 同时使用了intent-service,下载完成后会自动关闭service.

同时修复了http 请求返回eTag 304 无法使用的bug。

## Usage
Add this line to your `build.gradle` file under your module directory. 

```
compile 'com.github.leifzhang:VolleyLib:0.4.5'

compile 'com.github.leifzhang:DownloadLib:0.4.5'
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
###Step 1
配置默认文件 '必须设置'

```java
        DownloadConfig downloadConfig = new DownloadConfig.Builder().setDownloadDb(new DataBase()).builder();
        DownloadManager.getInstance().setConfig(downloadConfig);
```

###Step 2
下载文件只要使用这个就会自动开启
    
```java
   DownloadManager.setDownloadModel(downloadUrl, context); 
```

暂停只需要改变model状态就能完成
    
```java
   DownloadModel model = DownloadManager.getInstance()
                            .getModel(itemEntity.getDownloadUrl());
   if (model.getState() == DownloadConstants.DOWNLOADING) {
      model.setState(DownloadConstants.DOWNLOAD_PAUSE);
   }
```

## License
[MIT](https://opensource.org/licenses/MIT)
