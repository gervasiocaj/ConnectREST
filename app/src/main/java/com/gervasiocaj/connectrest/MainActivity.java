package com.gervasiocaj.connectrest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity {

    protected static Location mLastLocation;
    protected static String connFailed = "nao";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        Location mLastLocation2 = locationManager.getLastKnownLocation(provider);
        if (mLastLocation2 == null) connFailed = "enio esta errado";

        String part1 = mLastLocation2 == null ? "location null" : "location not null";
        String part2 = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ? "gps available" : "gps unavailable";
        Toast.makeText(this, part1 + ", " + part2, Toast.LENGTH_SHORT).show();
    }


    public void executeButton(View view) {
        Intent intent = new Intent(MainActivity.this, ResultActivity.class);
        intent.putExtra("viewID", view.getId());
        MainActivity.this.startActivity(intent);
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

}
