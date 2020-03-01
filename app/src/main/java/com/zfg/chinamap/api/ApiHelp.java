package com.zfg.chinamap.api;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ApiHelp {

    public static final int HTTP_OK = 200;

    public static final String API_KEY = "65d9cccedd629f24387195a66c4e90c5";

    public static final String ALL_DATA = "http://api.tianapi.com/txapi/ncovcity/index?key=" + API_KEY;

    public static void getAllData(Callback callback) {

        String url = ALL_DATA;

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url).get()
                .addHeader("content-type", "application/json")
                .build();
        client.newCall(request).enqueue(callback);
    }
}
