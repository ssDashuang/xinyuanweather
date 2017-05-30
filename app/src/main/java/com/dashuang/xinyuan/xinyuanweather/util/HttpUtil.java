package com.dashuang.xinyuan.xinyuanweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by chx on 2017/5/30.
 */

public class HttpUtil {

    public static void sendOkHttpRequest(String url,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(callback);
    }
}
