package com.example.left4candy.geotag;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity{

    private GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_LOCATION = 1;
    private FusedLocationProviderClient locationProvider;
    LocationRequest locationRequest;
    LocationCallback locationCallback;

    Button signInAskButton;
    Button signUpAskButton;
    RelativeLayout signIn;
    View mView;

    EditText passwordInput;
    EditText usernameInput;
    private String username;
    private String password;
    //MapFragment backgroundMap = (MapFragment) getFragmentManager().findFragmentById(R.id.mapViewBack);


    public void signInButtonClicked(View view){
        signInAskButton.setVisibility(View.INVISIBLE);
        signUpAskButton.setVisibility(View.INVISIBLE);
        signIn.setVisibility(View.VISIBLE);
    }

    public void signUpButtonClicked(View view){
        signInAskButton.setVisibility(View.INVISIBLE);
        signUpAskButton.setVisibility(View.INVISIBLE);
        signIn.setVisibility(View.VISIBLE);
    }

    public void signInClicked(View view){
        //checkLogin(username, password);
        password = passwordInput.getText().toString();
        username = usernameInput.getText().toString();
        System.out.println("user: " + username + "\npass: " + password);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationProvider = LocationServices.getFusedLocationProviderClient(this);
        signInAskButton = findViewById(R.id.signInAsk);
        signUpAskButton = findViewById(R.id.signUpAsk);
        signIn = findViewById(R.id.signInLayout);

        usernameInput = findViewById(R.id.username);
        passwordInput = findViewById(R.id.password);


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

        createLocationrequest();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    Log.d("MainActivity", "lat: " + location.getLatitude() + " ,long: " + location.getLongitude());
                }
            }
        };
    }

    public void checkLogin(String username, String password){

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

    void createLocationrequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
}
