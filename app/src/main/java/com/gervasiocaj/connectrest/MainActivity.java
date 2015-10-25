package com.gervasiocaj.connectrest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.w3c.dom.Text;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    protected static Location mLastLocation;
    protected static String connFailed = "nao";
    protected TextView tvLatitude;
    protected TextView tvLongitude;
    protected TextView tvAltitude;
    protected Button gpsLocation;
    private Button checkConnection;
    private LocationManager locationManager;
    private OkHttpClient client;
    private String url = "http://localhost:8080/RESTful-Server/rf/leaderboard";
    private TextView tvResponse;
    private AccessWeb webAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvAltitude = (TextView) findViewById(R.id.tvAltitude);
        tvLatitude = (TextView) findViewById(R.id.tvLatitude);
        tvLongitude = (TextView) findViewById(R.id.tvLongitude);
        tvResponse = (TextView) findViewById(R.id.tvResponse);
        gpsLocation = (Button) findViewById(R.id.showGPS);
        checkConnection = (Button) findViewById(R.id.checkConnection);

        //mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        //LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        client = new OkHttpClient();
        webAccess = new AccessWeb();

        gpsLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Criteria criteria = new Criteria();
                String provider = locationManager.getBestProvider(criteria, false);
                Location mLastLocation2 = locationManager.getLastKnownLocation(provider);
                if (mLastLocation2 == null) connFailed = "enio esta errado";

                String part1 = mLastLocation2 == null ? "location null" : "location not null";

                if(mLastLocation2 == null) {
                    Toast.makeText(MainActivity.this, part1, Toast.LENGTH_SHORT).show();
                } else {
                    tvAltitude.setText("Altitude: " + mLastLocation2.getAltitude());
                    tvLatitude.setText("Latitude: " + mLastLocation2.getLatitude());
                    tvLongitude.setText("Longitude: " + mLastLocation2.getLongitude());
                }
            }
        });

        // NÃO apertar duas vezes esse botao
        checkConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String part2 = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER) ? "gps available" : "gps unavailable";
                Toast.makeText(MainActivity.this, part2, Toast.LENGTH_LONG).show();

                webAccess.execute(checkConnection.getId());
            }
        });
    }

    public void executeButton(View view) {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("viewID", view.getId());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private class AccessWeb extends AsyncTask<Integer, Void, String> {

        @Override
        protected void onPreExecute() {
            tvResponse.setText("Aguardando Resposta...");
        }

        @Override
        protected String doInBackground(Integer... params) {
            Integer buttonID = params[0];

            if (buttonID == R.id.checkConnection) {
                Request request = new Request.Builder().url(url).build();
                try {
                    Response response = client.newCall(request).execute();
                    return response.body().string();
                } catch (IOException e) {
                    return e.getMessage().toString();
                }
            } else {
                return "couldn't get online resource";
            }
        }

        @Override
        protected void onPostExecute(String string) {
            try {
                tvResponse.setText(string);
            } catch (Throwable throwable) {
                Toast.makeText(MainActivity.this, throwable.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
