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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

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


    private TextView traveledDistanceValueTextView;
    private Chronometer timePassedChronometer;
    private EditText searchEditText;

    private double traveledDistance;
    private double ticks;

    private String searchText;

    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timePassedChronometer = findViewById(R.id.timePassedChronometer);
        searchEditText = findViewById(R.id.searchEditText);

        // adiciona o listener no botão
        FloatingActionButton fab = findViewById(R.id.searchButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Pega o valor que foi digitado no inputText
                searchText = searchEditText.getText().toString();

                Uri uri = Uri.parse(
                        String.format(
                                Locale.getDefault(),
                                "geo:%f,%f?q=%s",
                                latitude,
                                longitude,
                                searchText
                        )
                );
                Intent intent = new Intent (Intent.ACTION_VIEW,
                        uri);
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });
    }

    // solicita ao usuário a permissão para usar o gps
    public void giveGpsPermission(View v) {

        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            // Exibe toast informando que a permissão já foi concedida
            Toast.makeText(this ,
                    getString(R.string.permission_already_granted),
                    Toast.LENGTH_SHORT)
                    .show();
        }
        else{
            ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                GPS_REQUEST_CODE
            );
        }
    }

    // Método para ligar o GPS
    public void turnGpsOn(View v) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0, 0, locationListener);
        }
        else {
            Toast.makeText(this ,
                    getString(R.string.impossible_to_continue_permission_needed),
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }

    // Desliga GPS
    public void turnGpsOff(View v){
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.removeUpdates(locationListener);
        }
        else {
            Toast.makeText(this ,
                    getString(R.string.gps_already_turned_off),
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }


    public void startRoute(View v){

    }


    public void endRoute(View v){

    }


    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(locationListener);
    }
}
