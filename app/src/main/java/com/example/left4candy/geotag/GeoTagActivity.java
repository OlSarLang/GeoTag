package com.example.left4candy.geotag;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GeoTagActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    private static final int REQUEST_LOCATION = 1;
    private FusedLocationProviderClient locationProvider;

    private TextView textViewUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_tag);
        locationProvider = LocationServices.getFusedLocationProviderClient(this);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

        FirebaseUser user = mAuth.getCurrentUser();

        //textViewUserEmail = (TextView)  findViewById(R.id.textViewUserEmail);
        //textViewUserEmail.setText("Welcome " + user.getEmail());
        Toast.makeText(this, "Welcome " + user.getEmail(), Toast.LENGTH_SHORT).show();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            Log.d("MainActivity", "no permission");

            // ask for permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION );


        } else {

            locationProvider.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        double lat = location.getLatitude();
                        double lng = location.getLongitude();

                        Log.d("MainActivity", "lat: " + lat + " ,long: " + lng);
                    }

                }
            });
        }

        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.map, new MapFragment().commit());

        createLocationRequest();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    Log.d("MainActivity", "lat: " + location.getLatitude() + " ,long: " + location.getLongitude());
                }
            }
        };
        //logoutButton = (Button) findViewById(R.id.logoutButton);
    }

    public void logoutButtonClicked(View view){
        mAuth.signOut();
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            locationProvider.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private void stopLocationUpdates() {
        locationProvider.removeLocationUpdates(locationCallback);
    }

    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResult) {
        if ( requestCode == REQUEST_LOCATION ) {
            if (grantResult.length == 1 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                locationProvider.requestLocationUpdates(locationRequest, locationCallback, null);
            } else {
                //permission denied by user
            }
        }
    }

    void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }
}
