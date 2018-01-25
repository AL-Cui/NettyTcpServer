package com.sharp.netty.common;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

public class OkHttpClientUtils {
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private static final Logger logger = LoggerFactory.getLogger(OkHttpClientUtils.class);
    private static final OkHttpClient okHttpClient = new OkHttpClient();


    public static String sendHttpGetRequest(String url) {
        String resultString = null;
        Request.Builder requestBuilder = new Request.Builder().url(url);
        requestBuilder.method("GET",null);
        Request request = requestBuilder.build();
        Call call = okHttpClient.newCall(request);
        Response response;
        try {
            response = call.execute();
            resultString = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
       
        logger.info("Http请求的结果="+resultString);
        return  resultString;

    }

    public static String sendHttpPostRequest(String url, String body) {
        String resultString = null;

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(JSON,body))
                .build();
        Response response = null;
        try {
          response = okHttpClient.newCall(request).execute();
          resultString = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            
        }

        return resultString;

    }
}
