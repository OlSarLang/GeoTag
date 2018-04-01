package com.example.left4candy.geotag;

/*
public class MapFragment extends Fragment implements OnMapReadyCallback{

    MapView mapView;
    GoogleMap mMap;
    double lat;
    double lng;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_main, container, false);
        return v;

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        try{
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mainMap);
            mapFragment.getMapAsync(MapFragment.this);
        }catch (Exception e){
            e.printStackTrace();
        }

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.mapViewBack);

        //Gets the MapView from the XML layou and creates it
        mapView = (MapView) this.findViewById(R.id.mapViewBack);
        mapView.onCreate(savedInstanceState);

        //Gets the GoogleMap from the MapView and does initialization
        mMap = mapView.getMapAsync(OnMapReadyCallback);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setMyLocationEnabled(false);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng nacka = new LatLng(lat, lng);
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLng(nacka));
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
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
*/