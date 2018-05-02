package com.example.left4candy.geotag;


import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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

import com.google.android.gms.common.util.CrashUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeoTagMapFragment extends Fragment {
    private static final String TAG = "MapTabFragment";

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    private String userID = user.getUid();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabase = database.getReference();
    private DatabaseReference currentDbIdRef = mDatabase.child("Users").child(userID).child("countId");

    private List<GeoMarker> mGeoMarkerList;
    private Map<String, Integer> markerList;
    private List<Marker> mapMarkerList;
    private GeoMarker geoMarker;
    private LatLng markerPos;

    private ToggleButton addMapMarkerButton;

    private LatLng place;
    private double placeLat;
    private double placeLng;
    private double lat;
    private double lng;
    private String newGeoMarkerId;
    private String oldGeoMarkerId;
    private int currentDbId;
    private DatabaseReference geoMarkerRef;
    private DatabaseReference geoMarkerDatabaseRef = mDatabase.child("Users").child(userID).child("geoMarkers");
    private GoogleMap map;
    MapView mapView;
    View mView;

    private int height = 80;
    private int width = 80;

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

        BitmapDrawable bitmapRed = (BitmapDrawable)getResources().getDrawable(R.drawable.red);
        BitmapDrawable bitmapGreen = (BitmapDrawable)getResources().getDrawable(R.drawable.green);
        BitmapDrawable bitmapBlue = (BitmapDrawable)getResources().getDrawable(R.drawable.blue);
        Bitmap r = bitmapRed.getBitmap();
        Bitmap g = bitmapGreen.getBitmap();
        Bitmap b = bitmapBlue.getBitmap();
        final Bitmap smallRed = Bitmap.createScaledBitmap(r, width, height, false);
        final Bitmap smallGreen = Bitmap.createScaledBitmap(g, width, height, false);
        final Bitmap smallBlue = Bitmap.createScaledBitmap(b, width, height, false);

        addMapMarkerButton = mView.findViewById(R.id.addMapMarkerButton);

        mGeoMarkerList = new ArrayList<>();
        markerList = new HashMap<String, Integer>();
        mapMarkerList = new ArrayList<Marker>();

        currentDbIdRef.setValue(0);

        geoMarkerDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mGeoMarkerList.clear();
                mapMarkerList.clear();
                markerList.clear();
                map.clear();
                currentDbId = 0;
                for(DataSnapshot children : dataSnapshot.getChildren()){
                    geoMarker = children.getValue(GeoMarker.class);

                    markerPos = new LatLng(geoMarker.getGeoMarkerLat(), geoMarker.getGeoMarkerLong());
                    Log.d("reached", "Reached");
                    if(geoMarker.getGeoMarkerColor() == "green") {
                        Marker mkr  = map.addMarker(new MarkerOptions().position(markerPos).title(geoMarker.getGeoMarkerName()).icon(BitmapDescriptorFactory.fromBitmap(smallGreen)).flat(true));
                        mapMarkerList.add(mkr);
                        mGeoMarkerList.add(geoMarker);
                        currentDbId += 1;
                        geoMarker.setGeoMarkerId(currentDbId);
                        geoMarker.setCountId(currentDbId);
                        currentDbIdRef.setValue(currentDbId);
                        markerList.put(mkr.getId(), geoMarker.getGeoMarkerId());
                    }else{
                        Marker mkr = map.addMarker(new MarkerOptions().position(markerPos).title(geoMarker.getGeoMarkerName()).icon(BitmapDescriptorFactory.fromBitmap(smallRed)).flat(true));
                        mapMarkerList.add(mkr);
                        mGeoMarkerList.add(geoMarker);
                        currentDbId += 1;
                        geoMarker.setGeoMarkerId(currentDbId);
                        geoMarker.setCountId(currentDbId);
                        currentDbIdRef.setValue(currentDbId);
                        markerList.put(mkr.getId(), geoMarker.getGeoMarkerId());
                    }
                    Log.d("created", "Marker created");
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

                map.setOnMarkerClickListener(new OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        int id = markerList.get(marker.getId());
                        Log.d("id", String.valueOf(id));
                        editGeoMarker(id);
                        return false;
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
                        geoMarker.setGeoMarkerColor(geoMarkerColor);
                        geoMarker.setCountId(currentDbId);
                        geoMarker.setGeoMarkerId(currentDbId);
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

    public void editGeoMarker(final int id) {


        int index = -1;

        for (int i = 0; i < mGeoMarkerList.size(); i++) {
            if (  mGeoMarkerList.get(i).getGeoMarkerId() == id) {
                index = i;
            }
        }


        geoMarker = mGeoMarkerList.get(index);
        geoMarker.setGeoMarkerId(id);
        Log.d("editmarker", String.valueOf(geoMarker.getGeoMarkerId()));
        String gMC = geoMarker.getGeoMarkerColor();
        final int gMID = geoMarker.getGeoMarkerId();
        final int oldCountId = geoMarker.getCountId();
        double gMLng = geoMarker.getGeoMarkerLong();
        double gMLat = geoMarker.getGeoMarkerLat();
        String gMN = geoMarker.getGeoMarkerName();
        String fF = geoMarker.getFirstField();
        String fN = geoMarker.getFirstName();
        String sF = geoMarker.getSecondField();
        String sN = geoMarker.getSecondName();
        String tF = geoMarker.getThirdField();
        String tN = geoMarker.getThirdName();

        final AlertDialog.Builder aBuilder = new AlertDialog.Builder(getContext());
        View nView = getLayoutInflater().inflate(R.layout.dialogeditgeomarker, null);
        final EditText editTopName = nView.findViewById(R.id.editTopName); editTopName.setText(gMN);
        final EditText editNameOne = nView.findViewById(R.id.editName1); editNameOne.setText(fN);
        final EditText editNameTwo = nView.findViewById(R.id.editName2); editNameTwo.setText(sN);
        final EditText editNameThree = nView.findViewById(R.id.editName3); editNameThree.setText(tN);
        final EditText editFieldOne = nView.findViewById(R.id.editField1); editFieldOne.setText(fF);
        final EditText editFieldTwo = nView.findViewById(R.id.editField2); editFieldTwo.setText(sF);
        final EditText editFieldThree = nView.findViewById(R.id.editField3); editFieldThree.setText(tF);
        final CheckBox checkBoxEditRed = nView.findViewById(R.id.checkBoxEditRed); if (geoMarker.getGeoMarkerColor() == "red"){checkBoxEditRed.setChecked(true);}
        final CheckBox checkBoxEditGreen = nView.findViewById(R.id.checkBoxEditGreen); if (geoMarker.getGeoMarkerColor() == "green"){checkBoxEditRed.setChecked(true);}
        Button cancelEditMarker = nView.findViewById(R.id.cancelEditMarker);
        Button saveEditMarker = nView.findViewById(R.id.saveEditMarker);


        aBuilder.setView(nView);
        final AlertDialog dialog = aBuilder.create();

        Log.d("AlertDialogCreated", "Alert Dialog has been created");

        cancelEditMarker.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        saveEditMarker.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                final String geoMarkerName;
                String geoMarkerColor = "red";
                final double geoMarkerLat = geoMarker.getGeoMarkerLat();
                final double geoMarkerLong = geoMarker.getGeoMarkerLong();
                final String firstName;
                final String secondName;
                final String thirdName;
                final String firstField;
                final String secondField;
                final String thirdField;
                if(!editTopName.getText().toString().isEmpty()){
                    if(checkBoxEditGreen.isChecked() && !checkBoxEditRed.isChecked() || !checkBoxEditGreen.isChecked() && checkBoxEditRed.isChecked()){
                        if(checkBoxEditGreen.isChecked()){
                            geoMarkerColor = "green";
                        }else if(checkBoxEditRed.isChecked()){
                            geoMarkerColor = "red";
                        }
                        geoMarkerName = editTopName.getText().toString();
                        firstName = editNameOne.getText().toString();
                        secondName = editNameTwo.getText().toString();
                        thirdName = editNameThree.getText().toString();
                        firstField = editFieldOne.getText().toString();
                        secondField = editFieldTwo.getText().toString();
                        thirdField = editFieldThree.getText().toString();

                        geoMarker.setGeoMarkerId(id);
                        geoMarker.setGeoMarkerLat(geoMarker.getGeoMarkerLat());
                        geoMarker.setGeoMarkerLong(geoMarker.getGeoMarkerLong());
                        geoMarker.setGeoMarkerName(geoMarkerName);
                        geoMarker.setGeoMarkerColor(geoMarkerColor);
                        geoMarker.setFirstName(firstName);
                        geoMarker.setSecondName(secondName);
                        geoMarker.setThirdName(thirdName);
                        geoMarker.setFirstField(firstField);
                        geoMarker.setSecondField(secondField);
                        geoMarker.setThirdField(thirdField);

                        oldGeoMarkerId = String.valueOf(gMID-1);
                        geoMarkerRef = mDatabase.child("Users").child(userID).child("geoMarkers").child(oldGeoMarkerId);
                        Log.d("Current Id", oldGeoMarkerId);
                        geoMarkerRef.setValue(geoMarker);

                        Toast.makeText(getContext(), R.string.markeradded, Toast.LENGTH_SHORT).show();
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
