package com.example.user.youtubedownloaderclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.youtubedownloaderclient.instruments.Web;

import net.rdrei.android.dirchooser.DirectoryChooserActivity;
import net.rdrei.android.dirchooser.DirectoryChooserConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_DIRECTORY = 0;
    public static final String TAG = "DirChooserSample";

    GetContentInBackground getContentInBackground;
    VideoUrlGetter videoUrlGetter;

    byte[] response;
    //String rootFolder = "/storage/emulated/0/Android/data";
    String dirOfVideos;
    String nameOfFile = "Default";
    String[] formats = {".mp4", ".mp3"};
    String format = ".mp4";

    String url;
    EditText input = null;
    TextView path = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button downloadBtn = findViewById(R.id.btnDownload);
        Button cleanBtn = findViewById(R.id.btnClean);
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        input = findViewById(R.id.input);

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    if (isNetworkAvailable()) {
                        URL url = null;
                        try {
                            getContentInBackground = new GetContentInBackground(progressBar, MainActivity.this);
                            videoUrlGetter = new VideoUrlGetter(progressBar, getContentInBackground, MainActivity.this);
                            url = new URL(input.getText().toString());
                            String id = Web.splitQuery(url).get("v");
                            videoUrlGetter.execute(id);
                        } catch (MalformedURLException e) {
                            Toast.makeText(MainActivity.this,"Invalid link", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            Toast.makeText(MainActivity.this,"Invalid link", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Turn on internet connection", Toast.LENGTH_SHORT).show();
                    }
            }
        });

        cleanBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                input.setText("");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_DIRECTORY) {
            Log.i(TAG, String.format("Return from DirChooser with result %d",
                    resultCode));

            if (resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {
                path
                        .setText(data
                                .getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR));
                dirOfVideos = data.getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR);
                SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("path", dirOfVideos);
                editor.commit();
            }
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void save(byte[] response, String title, String format){
        File f = new File(dirOfVideos);
        f.mkdir();
        try {
            FileOutputStream fos = new FileOutputStream(dirOfVideos + "/" + title + format);
            fos.write(response);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
