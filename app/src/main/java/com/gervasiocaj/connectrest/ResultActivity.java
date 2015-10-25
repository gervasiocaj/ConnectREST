package com.gervasiocaj.connectrest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import util.GPSManager;

public class ResultActivity extends AppCompatActivity {

    private static final String SERVER_URL = "http://roundfight-server.herokuapp.com/rf";
    private Intent intent;
    GPSManager gps;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        gps = new GPSManager(this);
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
    protected String getServer(String url) {
        String result;
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = client.newCall(request).execute();
            result = response.body().string();
        } catch (IOException e) {
            result = "Could not connect";
        }
        return result;
    }

    protected boolean postServer(String url) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "");
        Request request = new Request.Builder().url(url).post(body).build();
        try {
            client.newCall(request).execute();
            return true;
        } catch (IOException e) {
        }
        return false;
    }

    private class AccessWeb extends AsyncTask<Integer, Void, String> {

        @Override
        protected void onPreExecute() {
            pDialog = ProgressDialog.show(ResultActivity.this, "Loading...", "Conneccting, please wait...", false, false);
        }

        @Override
        protected String doInBackground(Integer... params) {
            Integer buttonID = params[0];
            String result = "";

            switch (buttonID) {
                case R.id.checkConnection:
                    result = getServer(SERVER_URL);
                    try {
                        result = new JSONObject(result).getString("data");
                    } catch (JSONException e) {
                    }
                    break;
                case R.id.getLeaderboard:
                    result = getServer(SERVER_URL + "/leaderboard");
                    try {
                        result = new JSONArray(result).toString();
                    } catch (JSONException e) {
                    }
                    break;
                case R.id.getUserLeaderboard:
                    result = getServer(SERVER_URL + "/leaderboard/" + "me");
                    try {
                        result = new JSONArray(result).toString();
                    } catch (JSONException e) {
                    }
                    break;
                case R.id.getMultiplier:
                    result = getServer(SERVER_URL + "/multiplier/" + gps.getLatitude() + "/" + gps.getLongitude());
                    try {
                        result = new JSONObject(result).toString();
                    } catch (JSONException e) {
                    }
                    break;
                case R.id.postScore:
                    result = "Score update failed";
                    if (postServer(SERVER_URL + "/" + "gervasio" + "/" + "4000"))
                        result = "Score updated sucessfully";
                    break;
                case R.id.showGPS:
                    if (gps.canGetLocation())
                        result = "showing gps: "
                                + "Latitude: " + gps.getLatitude() + ", "
                                + "Longitude: " + gps.getLongitude();
                    result = "Not able to reach Internet connection or GPS connection.";
            }
            pDialog.dismiss();
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            TextView textView = (TextView) findViewById(R.id.resultView);
            textView.setText(result);
        }
    }
}

