package com.example.user.youtubedownloaderclient.instruments;

import android.content.Context;
import android.net.ConnectivityManager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Web {

    public static Map<String, String> splitQuery(URL url) throws UnsupportedEncodingException {
        Map<String, String> queryPairs = new LinkedHashMap<>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            queryPairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return queryPairs;
    }

    public static String fileToString(URL url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();
        return response.body().string();
    }


    public static String[] explode(String response, String regex) {
        return response.split(regex);
    }


    public static String cutFromStreamUrl(String stream, String subString){
        char[] streamChar = stream.toCharArray();
        int indexOfStartURL = stream.indexOf(subString);
        int indexOfEndURL = stream.indexOf(';');
        if (indexOfEndURL != -1){
            char[] URL = new char[indexOfEndURL - subString.length()-indexOfStartURL];
            System.arraycopy(streamChar,indexOfStartURL+subString.length(), URL,0, indexOfEndURL-subString.length()-indexOfStartURL);
            System.out.println("cut from stream url -> "+String.valueOf(URL));
            return String.valueOf(URL);
        }else {
            char[] URL = new char[stream.length() - subString.length()-indexOfStartURL];
            System.arraycopy(streamChar,indexOfStartURL+subString.length(), URL,0, streamChar.length-subString.length()-indexOfStartURL);
            System.out.println("cut from stream url -> "+String.valueOf(URL));
            return String.valueOf(URL);
        }
    }
}
