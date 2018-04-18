package com.example.domin.multism;

import android.os.AsyncTask;
import android.util.Log;

import java.net.URL;
import java.net.URLConnection;

/**
 * Created by domin on 07.03.2018.
 */

public class ping extends AsyncTask<String, Void, Void> {

        private Exception exception;

        protected Void doInBackground(String... urls) {
            boolean tak = isConnectedToServer(urls[0],200);
            Log.e("połączono", String.valueOf(tak));
            return null;
        }

    public boolean isConnectedToServer(String url, int timeout) {
        try{
            URL myUrl = new URL(url);
            URLConnection connection = myUrl.openConnection();
            connection.setConnectTimeout(timeout);
            connection.connect();
            return true;
        } catch (Exception e) {
            Log.e("połączono", String.valueOf(e));
            // Handle your exceptions
            return false;
        }
    }
        protected void onPostExecute(String feed) {
            // TODO: check this.exception
            // TODO: do something with the feed
        }
    }


