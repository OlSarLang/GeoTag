package com.example.left4candy.geotag;


import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GeoTagMapFragment extends Fragment {
    private static final String TAG = "MapTabFragment";

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    private String userID = user.getUid();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabase = database.getReference();

    private List<GeoMarker> mGeoMarkerList;
    private GeoMarker geoMarker;

    private GeoMarker gm;
    private Marker marker;
    private LatLng markerPos;

    private ToggleButton addMapMarkerButton;

    private LatLng place;
    private double placeLat;
    private double placeLng;
    private double lat;
    private double lng;
    private String newGeoMarkerId;
    private DatabaseReference geoMarkerRef;
    private DatabaseReference geoMarkerDatabaseRef = mDatabase.child("Users").child(userID).child("geoMarkers");
    private GoogleMap map;
    MapView mapView;
    View mView;

    public GeoTagMapFragment(){

    }

    public static GeoTagMapFragment newInstance(){
        GeoTagMapFragment fragment = new GeoTagMapFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView =  inflater.inflate(R.layout.mapfragment, container, false);
        mapView = (MapView) mView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        addMapMarkerButton = mView.findViewById(R.id.addMapMarkerButton);

        mGeoMarkerList = new ArrayList<>();

        geoMarkerDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mGeoMarkerList.clear();
                for(DataSnapshot children : dataSnapshot.getChildren()){
                    geoMarker = children.getValue(GeoMarker.class);
                    mGeoMarkerList.add(geoMarker);

                    markerPos = new LatLng(geoMarker.getGeoMarkerLat(), geoMarker.getGeoMarkerLong());
                    Log.d("reached", "Reached");
                    if(geoMarker.getGeoMarkerColor() == "green") {
                        marker = map.addMarker(new MarkerOptions().position(markerPos).title(geoMarker.getGeoMarkerName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.green)));
                    }else {
                        marker = map.addMarker(new MarkerOptions().position(markerPos).title(geoMarker.getGeoMarkerName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.red)));
                    }
                    Log.d("created", "Marker created");
                    marker.setTag(0);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        try{
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(mGeoMarkerList.isEmpty()){
            place = new LatLng(59.345396, 18.023425);
        }else{
            placeLat = mGeoMarkerList.get(0).getGeoMarkerLat();
            placeLng = mGeoMarkerList.get(0).getGeoMarkerLong();
            place = new LatLng(placeLat, placeLng);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 15));
        }

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                map.setMyLocationEnabled(true);
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 15));
                map.getUiSettings().setMyLocationButtonEnabled(true);


                map.setOnMapClickListener(new OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        lat = latLng.latitude;
                        lng = latLng.longitude;
                        if(addMapMarkerButton.isChecked()){
                            addGeoMarker();
                        }
                    }
                });
            }
        });

        addMapMarkerButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(addMapMarkerButton.isChecked()) {
                    Log.d("Checked", "checked");
                    addMapMarkerButton.setBackgroundResource(R.color.primaryDarkColor);
                }else{
                    addMapMarkerButton.setBackgroundResource(R.color.primaryLightColor);
                }
            }
        });
            return mView;
    }

    public void addGeoMarker(){
        final AlertDialog.Builder aBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.dialoggeomarker, null);
        final EditText chooseTopName = mView.findViewById(R.id.chooseTopName);
        final EditText chooseNameOne = mView.findViewById(R.id.chooseName1);
        final EditText chooseNameTwo = mView.findViewById(R.id.chooseName2);
        final EditText chooseNameThree = mView.findViewById(R.id.chooseName3);
        final EditText chooseFieldOne = mView.findViewById(R.id.chooseField1);
        final EditText chooseFieldTwo = mView.findViewById(R.id.chooseField2);
        final EditText chooseFieldThree = mView.findViewById(R.id.chooseField3);
        final CheckBox checkBoxChooseRed = mView.findViewById(R.id.checkBoxChooseRed);
        final CheckBox checkBoxChooseGreen = mView.findViewById(R.id.checkBoxChooseGreen);
        Button cancelCreateMarker = mView.findViewById(R.id.cancelCreateMarker);
        Button saveCreateMarker = mView.findViewById(R.id.saveCreateMarker);

        aBuilder.setView(mView);
        final AlertDialog dialog = aBuilder.create();
        Log.d("AlertDialogCreated", "Alert Dialog has been created");

        cancelCreateMarker.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                addMapMarkerButton.setChecked(false);
                dialog.dismiss();
            }
        });

        saveCreateMarker.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                final String geoMarkerName;
                String geoMarkerColor = "red";
                final double geoMarkerLat = lat;
                final double geoMarkerLong = lng;
                final String firstName;
                final String secondName;
                final String thirdName;
                final String firstField;
                final String secondField;
                final String thirdField;
                if(!chooseTopName.getText().toString().isEmpty()){
                    if(checkBoxChooseGreen.isChecked() && !checkBoxChooseRed.isChecked() || !checkBoxChooseGreen.isChecked() && checkBoxChooseRed.isChecked()){
                        if(checkBoxChooseGreen.isChecked()){
                            geoMarkerColor = "green";
                        }else if(checkBoxChooseRed.isChecked()){
                            geoMarkerColor = "red";
                        }
                        geoMarkerName = chooseTopName.getText().toString();
                        firstName = chooseNameOne.getText().toString();
                        secondName = chooseNameTwo.getText().toString();
                        thirdName = chooseNameThree.getText().toString();
                        firstField = chooseFieldOne.getText().toString();
                        secondField = chooseFieldTwo.getText().toString();
                        thirdField = chooseFieldThree.getText().toString();

                        geoMarker = new GeoMarker(geoMarkerName, geoMarkerColor, lat, lng);
                        geoMarker.setFirstName(firstName);
                        geoMarker.setSecondName(secondName);
                        geoMarker.setThirdName(thirdName);
                        geoMarker.setFirstField(firstField);
                        geoMarker.setSecondField(secondField);
                        geoMarker.setThirdField(thirdField);
                        mGeoMarkerList.add(geoMarker);
                        newGeoMarkerId = Integer.toString(geoMarker.getGeoMarkerId());
                        geoMarkerRef = mDatabase.child("Users").child(userID).child("geoMarkers").child(newGeoMarkerId);

                        geoMarkerRef.setValue(geoMarker);

                        Toast.makeText(getContext(), R.string.markeradded, Toast.LENGTH_SHORT).show();
                        addMapMarkerButton.setChecked(false);
                        dialog.dismiss();
                    }
                    return;
                }else{
                    Toast.makeText(getContext(), R.string.edittitleempty, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
        dialog.show();
    }

    public void loadMarkers(){
        //map.setOnMarkerClickListener(this);
        for(int i = 0; i < mGeoMarkerList.size(); i++){
            gm = mGeoMarkerList.get(i);
            markerPos = new LatLng(gm.getGeoMarkerLat(), gm.getGeoMarkerLong());
            Log.d("reached", "Reached");
            if(gm.getGeoMarkerColor() == "green") {
                marker = map.addMarker(new MarkerOptions().position(markerPos).title(gm.getGeoMarkerName()));
            }else {
                marker = map.addMarker(new MarkerOptions().position(markerPos).title(gm.getGeoMarkerName()));
            }
            Log.d("created", "Marker created");
            marker.setTag(0);
        }
    }

    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
        Integer clickCount = (Integer) marker.getTag();

        // Check if a click count was set, then display the click count.
        if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}
