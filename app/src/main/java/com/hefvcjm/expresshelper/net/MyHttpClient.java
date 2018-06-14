package com.hefvcjm.expresshelper.net;


import android.os.Looper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Iterator;

/**
 * Created by win10 on 2018/5/3.
 */

public class MyHttpClient {

    private static final String TAG = "MyHttpClient";

    private URL url;
    private JSONObject headers;
    private JSONObject contents;
    private ResponseListener responseListener;

    public MyHttpClient(String url, JSONObject headers, JSONObject contents, ResponseListener responseListener) {
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        this.headers = headers;
        this.contents = contents;
        this.responseListener = responseListener;
    }

    public void post() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 此处的urlConnection对象实际上是根据URL的
                    // 请求协议(此处是http)生成的URLConnection类
                    // 的子类HttpURLConnection,故此处最好将其转化
                    // 为HttpURLConnection类型的对象,以便用到
                    // HttpURLConnection更多的API.如下:
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    // 设定请求的方法为"POST"，默认是GET
                    connection.setRequestMethod("POST");
                    // 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在
                    // http正文内，因此需要设为true, 默认情况下是false;
                    connection.setDoOutput(true);
                    // 设置是否从httpUrlConnection读入，默认情况下是true;
                    connection.setDoInput(true);
                    // Post 请求不能使用缓存
                    connection.setUseCaches(false);
                    //连接超时3秒
                    connection.setConnectTimeout(3 * 1000);
                    Iterator<String> header_keys = headers.keys();
                    while (header_keys.hasNext()) {
                        //设置请求头
                        String key = header_keys.next();
                        connection.setRequestProperty(key, headers.getString(key));//设置参数类型是json格式
                    }
                    // 连接，从上述第2条中url.openConnection()至此的配置必须要在connect之前完成，
                    connection.connect();

                    String body = contents.toString();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                    writer.write(body);
                    writer.close();
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        String result = "";
                        if (inputStream != null) {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                            String line;
                            while ((line = reader.readLine()) != null) {
                                result += line;
                            }
                        }
                        Log.d(TAG, "result:" + result);
                        Looper.prepare();
                        responseListener.onResponse(result);
                        Looper.loop();
                        return;
                    } else {
                        Looper.prepare();
                        responseListener.onResponse(null);
                        Looper.loop();
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    String err = "error";
                    if (e instanceof SocketTimeoutException) {
                        err = "timeout";
                    }
                    try {
                        Looper.prepare();
                        responseListener.onResponse(new JSONObject().put("msg", err).toString());
                        Looper.loop();
                        return;
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public interface ResponseListener {
        void onResponse(String result);
    }
}
