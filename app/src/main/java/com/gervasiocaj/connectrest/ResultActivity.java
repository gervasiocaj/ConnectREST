package com.gervasiocaj.connectrest;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class ResultActivity extends AppCompatActivity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        intent = getIntent();
        new AccessWeb().execute(intent.getIntExtra("viewID", 0));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected OkHttpClient client = new OkHttpClient();
    protected String run(String url) {
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
        }
        return "couldn't get online resource";
    }

    private class AccessWeb extends AsyncTask<Integer, Void, String> {

        @Override
        protected String doInBackground(Integer... params) {
            Integer buttonID = params[0];

            switch (buttonID) {
                case R.id.checkConnection:
                    return run("http://localhost:8080/RESTful-Server/rf/leaderboard");
                case R.id.getLeaderboard:
                    break;
                case R.id.getUserLeaderboard:
                    break;
                case R.id.getMultiplier:
                    break;
                case R.id.postScore:
                    break;
                case R.id.showGPS:
                    if (MainActivity.mLastLocation != null)
                        return "showing gps: "
                                + String.valueOf(MainActivity.mLastLocation.getLatitude())
                                + ","
                                + String.valueOf(MainActivity.mLastLocation.getLongitude());
                    return "not yet connected, failed: " + MainActivity.connFailed;
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            TextView textView = (TextView) findViewById(R.id.resultView);
            textView.setText(result);
        }
    }
}

