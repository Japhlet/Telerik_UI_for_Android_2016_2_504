package com.telerik.android.data;

import android.os.AsyncTask;

import com.telerik.android.common.Procedure;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class LoadJSONTask extends AsyncTask {

    private Procedure finishedListener;

    @Override
    protected Object doInBackground(Object[] params) {
        StringBuilder builder = new StringBuilder();

        try {
            URLConnection con = ((URL)params[0]).openConnection();
            InputStream input = con.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(input, "utf-8"), 8);

            String line;
            while((line = reader.readLine()) != null) {
                builder.append(line);
            }

            reader.close();

        } catch (IOException ex) {
            throw new Error(ex);
        }

        return builder.toString();
    }

    @Override
    protected void onPostExecute(Object jsonArray) {
        super.onPostExecute(jsonArray.toString());

        if(finishedListener == null) {
            return;
        }

        finishedListener.apply(jsonArray.toString());
    }

    public void setFinishedListener(Procedure finishedListener) {
        this.finishedListener = finishedListener;
    }
}
