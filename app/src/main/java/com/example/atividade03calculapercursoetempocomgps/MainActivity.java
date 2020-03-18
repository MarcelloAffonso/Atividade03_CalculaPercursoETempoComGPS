package com.example.atividade03calculapercursoetempocomgps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.os.SystemClock;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int GPS_REQUEST_CODE = 1001;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private Location actualLocation;
    private Location lastLocation;

    private TextView traveledDistanceValueTextView;
    private Chronometer timePassedChronometer;
    private EditText searchEditText;

    private boolean isGpsOn;

    private double traveledDistance = 0D;

    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        traveledDistanceValueTextView = findViewById(R.id.traveledDistanceValueTextView);

        timePassedChronometer = findViewById(R.id.timePassedChronometer);
        searchEditText = findViewById(R.id.searchEditText);

        timePassedChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {

                traveledDistance += lastLocation.distanceTo(actualLocation);
                traveledDistanceValueTextView.setText(String.format(Locale.getDefault(), "%.2f",traveledDistance));
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                latitude = location.getLatitude();
                longitude = location.getLongitude();

                // A ultima localização passa a ser a atual
                if(isGpsOn) {
                    actualLocation = location;
                    traveledDistance = lastLocation.distanceTo(actualLocation);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };


        FloatingActionButton fab = findViewById(R.id.searchButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri =
                        Uri.parse(String.format("geo:%f,%f?q=" +
                                searchEditText.getText(),
                                latitude,
                                longitude))
                                ;
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                // vai pra tela mostrando o mapa
                startActivity(mapIntent);
            }
        });
    }


        // solicita ao usuário a permissão para usar o gps
        public void giveGpsPermission (View v){
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,
                        getString(R.string.permission_already_granted),
                        Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        GPS_REQUEST_CODE
                );
            }
        }

        // Método para ligar o GPS
        public void turnGpsOn (View v){
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        0, 0, locationListener);
                isGpsOn = true;
            }
            else {
                Toast.makeText(this,
                        getString(R.string.impossible_to_continue_permission_needed),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }

        // Desliga GPS
        public void turnGpsOff (View v){
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.removeUpdates(locationListener);
                isGpsOn = false;
            }
            else {
                Toast.makeText(this,
                        getString(R.string.gps_already_turned_off),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }


        // inicia a rota
        public void startRoute (View v){
            if ((ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    && isGpsOn) {

                // incializa as duas variaveis
                this.lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                this.actualLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                // inicia o cronometro
                timePassedChronometer.start();
            }
            else {
                Toast.makeText(this,
                        getString(R.string.impossible_to_continue_turn_on_the_gps),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }

        // finaliza a rota
        public void endRoute (View v){
            if(isGpsOn){
                // interrompe a execucao do gps
                isGpsOn = false;

                // para o cronometro
                timePassedChronometer.stop();
                Toast.makeText(this ,
                        getString(R.string.traveled_distance) +
                                ":" +
                                traveledDistance +
                                "\n" +
                                getString(R.string.time_passed) +
                                timePassedChronometer.getText(), Toast.LENGTH_SHORT
                ).show();

                timePassedChronometer.setBase(SystemClock.elapsedRealtime());

            }
        }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GPS_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this,  getString(R.string.impossible_to_continue_permission_needed),
                        Toast.LENGTH_SHORT).show();

            }
        }
    }

}
