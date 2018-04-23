package com.example.left4candy.geotag;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class GeoTagActivity extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private static final int PICK_IMAGE_REQUEST = 1234;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference mStorage = storage.getReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabase = database.getReference();

    private static final int GALLERY_INTENT = 2;
    private Uri filePath;
    private ImageView staticPicture;

    TextView databaseNameTextView;
    private TitleName title;
    String tempTitle;

    FirebaseUser user = mAuth.getCurrentUser();
    private String userID = user.getUid();
    private DatabaseReference titleNameRef = mDatabase.child("Users").child(userID);

    LocationRequest locationRequest;
    LocationCallback locationCallback;
    private static final int REQUEST_LOCATION = 1;
    private FusedLocationProviderClient locationProvider;
    GeoTagMapFragment geoTagMapFragment;
    PictureFragment pictureFragment;

    private ImageButton mSelectImage;

    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;

    private TextView textViewUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_tag);
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View pView = inflater.inflate(R.layout.picturefragment, null);

        databaseNameTextView = (TextView) findViewById(R.id.databaseNameTextView);

        locationProvider = LocationServices.getFusedLocationProviderClient(this);

        pictureFragment = new PictureFragment();
        geoTagMapFragment = new GeoTagMapFragment();

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.fragmentView);
        setupViewPager(mViewPager);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){}
            @Override
            public void onPageSelected(int position) {
                   if(position==0){
                    mSelectImage.setVisibility(View.INVISIBLE);
                   }else if(position==1){
                    mSelectImage.setVisibility(View.VISIBLE);
                   }
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        staticPicture = pView.findViewById(R.id.pictureView);

        mSelectImage = findViewById(R.id.chooseImage);
        if(mAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

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
        createLocationRequest();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    Log.d("MainActivity", "lat: " + location.getLatitude() + " ,long: " + location.getLongitude());
                }
            }
        };

        titleNameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    tempTitle = ds.getValue(String.class);
                    title.setTitle(tempTitle);
                    databaseNameTextView.setText(tempTitle);

                    Log.d("TAG", tempTitle);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

    private void setupViewPager(ViewPager viewPager){
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addMapFragment(geoTagMapFragment, "GPS Map");
        adapter.addPictureFragment(pictureFragment, "Static");
        viewPager.setAdapter(adapter);

    }

    private void getUploadedPicture(){

    }

    private void uploadFile(){

        if(filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            StorageReference riversRef = mStorage.child("Photos").child(filePath.getLastPathSegment());
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Uri downloadUri = taskSnapshot.getDownloadUrl();
                            Toast.makeText(getApplicationContext(), "File Uploaded and ready for use :)", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage((int) progress + "% Uploaded...");
                        }
                    });
        }else{

        }
    }

    public void chooseImageClicked(View view){
        try{
            if(ActivityCompat.checkSelfPermission(GeoTagActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(GeoTagActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_IMAGE_REQUEST);
            }else{
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select an Image"), PICK_IMAGE_REQUEST);
            }
    }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            filePath = data.getData();

            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
                staticPicture.setBackgroundDrawable(bitmapDrawable);
                uploadFile();
            } catch(java.io.IOException e){
                e.printStackTrace();
            }
        }
    }

    public void changeDataBaseName(View view){
        final AlertDialog.Builder aBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialogdatabasetitle, null);
        final EditText aTitle = (EditText) mView.findViewById(R.id.editTitle);
        Button aCancelButton = (Button) mView.findViewById(R.id.cancel);
        Button aSaveButton = (Button) mView.findViewById(R.id.save);
        aBuilder.setView(mView);
        final AlertDialog dialog = aBuilder.create();

        aCancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        aSaveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(!aTitle.getText().toString().isEmpty()){
                    Toast.makeText(GeoTagActivity.this, R.string.edittitlechanged, Toast.LENGTH_SHORT).show();
                    String tempTitleTwo = tempTitle;
                    titleNameRef.setValue(tempTitleTwo);
                    dialog.dismiss();
                }else{
                    Toast.makeText(GeoTagActivity.this, R.string.edittitleempty, Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    @Override
    protected void onStart(){
        super.onStart();
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
