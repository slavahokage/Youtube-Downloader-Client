package com.example.user.youtubedownloaderclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.youtubedownloaderclient.instruments.Web;

import net.rdrei.android.dirchooser.DirectoryChooserActivity;
import net.rdrei.android.dirchooser.DirectoryChooserConfig;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class VideoUrlGetter extends AsyncTask<String, Integer, String> {

    private String idOfVideo;
    private String response;
    private ProgressBar progressBar;
    private GetContentInBackground getContentInBackground;
    private MainActivity mainActivity;

    public VideoUrlGetter(ProgressBar progressBar, GetContentInBackground getContentInBackground, MainActivity mainActivity) {
        this.progressBar = progressBar;
        this.getContentInBackground = getContentInBackground;
        this.mainActivity = mainActivity;
    }

    @Override
    protected String doInBackground(String... id) {
        idOfVideo = id[0];
        String url = getVideoInfo();
        return url;
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
    protected void onPostExecute(final String s) {
        super.onPostExecute(s);
        progressBar.setVisibility(View.GONE);
        System.out.println("Post execute -> " + s);

        if (s == null) {
            Toast.makeText(mainActivity, "Sorry, i can't download this video. I can't find download link", Toast.LENGTH_LONG).show();
        } else {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(mainActivity);
            View mView = mainActivity.getLayoutInflater().inflate(R.layout.dialog_download, null);
            final EditText label = mView.findViewById(R.id.label);
            Button submit = mView.findViewById(R.id.btnLogin);
            Spinner spinner = mView.findViewById(R.id.format);
            Button path = mView.findViewById(R.id.btnPath);
            TextView textView = mView.findViewById(R.id.path);
            mainActivity.path = textView;

            SharedPreferences sharedPref = mainActivity.getPreferences(Context.MODE_PRIVATE);
            String defaultPath = sharedPref.getString("path", "");

            textView.setText(defaultPath);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(mainActivity, android.R.layout.simple_spinner_item, mainActivity.formats);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            mBuilder.setView(mView);
            final AlertDialog dialog = mBuilder.create();
            dialog.show();
            submit.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    mainActivity.nameOfFile = label.getText().toString();
                    if (!mainActivity.isNetworkAvailable()) {
                        Toast.makeText(mainActivity, "Turn on internet connection", Toast.LENGTH_SHORT).show();
                    } else if (mainActivity.dirOfVideos == null) {
                        Toast.makeText(mainActivity, "Choose a path to download", Toast.LENGTH_SHORT).show();
                    } else if (label.getText().toString().equals("")) {
                        Toast.makeText(mainActivity, "Set name for file", Toast.LENGTH_SHORT).show();
                    } else {
                        getContentInBackground.execute(s);
                        dialog.dismiss();
                    }
                }
            });

            path.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    final Intent chooserIntent = new Intent(
                            mainActivity,
                            DirectoryChooserActivity.class);

                    final DirectoryChooserConfig config = DirectoryChooserConfig.builder()
                            .newDirectoryName("DirChooserSample")
                            .allowReadOnlyDirectory(true)
                            .allowNewDirectoryNameModification(true)
                            .build();

                    chooserIntent.putExtra(
                            DirectoryChooserActivity.EXTRA_CONFIG,
                            config);

                    mainActivity.startActivityForResult(chooserIntent, mainActivity.REQUEST_DIRECTORY);
                }
            });

            AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    String item = (String) parent.getItemAtPosition(position);
                    mainActivity.format = item;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };
            spinner.setOnItemSelectedListener(itemSelectedListener);


            System.out.println("READY video url !!!!!!!!!!!!!!!!");
        }
    }

    private String getVideoInfo() {
        try {
            URL url = new URL("http://www.youtube.com/get_video_info?video_id=" + idOfVideo + "&el=embedded&ps=default&eurl=&gl=US&hl=en");
            response = Web.fileToString(url);
            String[] explode = Web.explode(response, "&");
            Map<String, String> parameters = new HashMap<>();

            for (String e : explode) {
                String[] keyValue = Web.explode(e, "=");
                if (keyValue .length == 2) {
                    parameters.put(keyValue [0], java.net.URLDecoder.decode(keyValue [1], "UTF-8"));
                }
            }

            String[] streams;
            try{
                 streams = Web.explode((java.net.URLDecoder.decode(parameters.get("url_encoded_fmt_stream_map"), "UTF-8")), ",");
            }catch (Exception ex){
                return null;
            }

            String urlForDownload = null;

            for (String stream : streams) {
                System.out.println("Stream -> "+stream);
            }

            for (String stream :
                    streams) {
                    urlForDownload = Web.cutFromStreamUrl(stream,"url=");
                    if (urlForDownload.contains("http")){
                        break;
                }
            }



            return urlForDownload;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}