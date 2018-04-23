package com.example.left4candy.geotag;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity{

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private ProgressDialog progressDialog;

    private GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_LOCATION = 1;
    private FusedLocationProviderClient locationProvider;
    LocationRequest locationRequest;
    LocationCallback locationCallback;

    Button signInAskButton;
    Button signUpAskButton;
    RelativeLayout signInLayout;
    RelativeLayout signUpLayout;
    View mView;

    EditText passwordInput;
    EditText emailInput;

    EditText createPasswordInput;
    EditText createEmailInput;
    //GeoTagMapFragment backgroundMap = (GeoTagMapFragment) getFragmentManager().findFragmentById(R.id.mapViewBack);


    public void signInButtonClicked(View view){
        signInAskButton.setVisibility(View.INVISIBLE);
        signUpAskButton.setVisibility(View.INVISIBLE);
        signInLayout.setVisibility(View.VISIBLE);
    }

    public void signInBackButtonClicked(View view){
        signInLayout.setVisibility(View.INVISIBLE);
        signInAskButton.setVisibility(View.VISIBLE);
        signUpAskButton.setVisibility(View.VISIBLE);
    }

    public void signUpButtonClicked(View view){
        signInAskButton.setVisibility(View.INVISIBLE);
        signUpAskButton.setVisibility(View.INVISIBLE);
        signUpLayout.setVisibility(View.VISIBLE);
    }

    public void signUpBackButtonClicked(View view){
        signInAskButton.setVisibility(View.VISIBLE);
        signUpAskButton.setVisibility(View.VISIBLE);
        signUpLayout.setVisibility(View.INVISIBLE);
    }

    public void signInClicked(View view){
        userLogin();
    }

    public void signUpClicked(View view){
        registerUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            //Start next activity if user already logged in
            finish();
            startActivity(new Intent(getApplicationContext(), GeoTagActivity.class));
        }

        progressDialog = new ProgressDialog(this);

        setContentView(R.layout.activity_main);
        locationProvider = LocationServices.getFusedLocationProviderClient(this);
        signInAskButton = findViewById(R.id.signInAsk);
        signUpAskButton = findViewById(R.id.signUpAsk);
        signInLayout = findViewById(R.id.signInLayout);
        signUpLayout = findViewById(R.id.signUpLayout);

        emailInput = findViewById(R.id.username);
        passwordInput = findViewById(R.id.password);
        createEmailInput = findViewById(R.id.signUpEmail);
        createPasswordInput = findViewById(R.id.signUpPassword);


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

    private void userLogin(){
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            //email is empty
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Login in...");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            //User is successfully registered, and starting next activity
                            Toast.makeText(MainActivity.this, "Sign in successful", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(getApplicationContext(), GeoTagActivity.class));
                        }else{
                            Toast.makeText(MainActivity.this, "Could not sign in... please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void registerUser(){
        String email = createEmailInput.getText().toString().trim();
        String password = createPasswordInput.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            //email is empty
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Registering User...");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //User is successfully registered, and starting next activity
                            Toast.makeText(MainActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                            String user_id = mAuth.getCurrentUser().getUid();
                            final DatabaseReference current_user_id = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
                            final DatabaseReference firstTitle = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("title");
                            current_user_id.setValue(true);
                            firstTitle.setValue(getResources().getString(R.string.choose_title));
                            finish();
                            startActivity(new Intent(getApplicationContext(), GeoTagActivity.class));
                        }else{
                            Toast.makeText(MainActivity.this, "Could not register... please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
