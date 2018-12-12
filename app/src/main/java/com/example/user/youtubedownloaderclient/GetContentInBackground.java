package com.example.user.youtubedownloaderclient;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.net.URL;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class GetContentInBackground extends AsyncTask<String, Integer, byte[]> {

    private ProgressBar progressBar;
    private MainActivity mainActivity;


    public GetContentInBackground(ProgressBar progressBar, MainActivity mainActivity) {
        this.progressBar = progressBar;
        this.mainActivity = mainActivity;
    }

    @Override
    protected byte[] doInBackground(String... urls) {
        try {
            URL url = new URL(urls[0]);
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Call call = client.newCall(request);
            Response response = call.execute();

            return response.body().bytes();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(byte[] s) {
        super.onPostExecute(s);
        progressBar.setVisibility(View.GONE);
        mainActivity.save(s,mainActivity.nameOfFile,mainActivity.format);
        Toast.makeText(mainActivity,"Save in: "+mainActivity.dirOfVideos,Toast.LENGTH_LONG).show();
        System.out.println("READY content !!!!!!!!!!!!!!!!");
    }
}
