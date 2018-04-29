package com.example.hoang.myapplication.Fragment;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoang.myapplication.Adapter.OnStartDragListener;
import com.example.hoang.myapplication.Adapter.RecyclerListAdapter;
import com.example.hoang.myapplication.Adapter.SimpleItemTouchHelperCallback;
import com.example.hoang.myapplication.Model.DriverPostion;
import com.example.hoang.myapplication.Model.TripRequest;
import com.example.hoang.myapplication.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MapFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener, OnStartDragListener {
    private String TAG = "MapFragment";
    private CameraPosition mCameraPosition;
    private GoogleMap mMap;
    private final String CHILD_DRIVER_POSTION = "DRIVER_POSTION";
    private final String CHILD_CAR_POSTION = "CAR_POSTION";
    private final String CHILD_MOTOR_POSTION = "MOTOR_POSTION";
    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private Button btnCurrentPlace;
    private Button btnTestDriver;
    private Button btnNearDriver;
    private ImageButton btnMenu;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 13;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 5;
    private String[] mLikelyPlaceNames;
    private String[] mLikelyPlaceAddresses;
    private String[] mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;
    private MapView mapView;
    private RecyclerView lisDestination;
    private Map<String, DriverPostion> driverPostions = new HashMap<>();
    private ItemTouchHelper mItemTouchHelper;
    private TextView txtAdd, txtRemove, txtOptimze;
    private RecyclerListAdapter adapter;
    List<TripRequest> tripRequests = new ArrayList<>();

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    private void initDestinationRecycle() {

        TripRequest request = new TripRequest("1", "12391209310", null, null, null, null, 0, null);
        TripRequest request3 = new TripRequest("2", "12391209310", null, null, null, null, 0, null);
        TripRequest request2 = new TripRequest("3", "12391209310", null, null, null, null, 0, null);
        TripRequest request4 = new TripRequest("4", "12391209310", null, null, null, null, 0, null);
        TripRequest request5 = new TripRequest("5", "12391209310", null, null, null, null, 0, null);
        tripRequests.add(request);
        tripRequests.add(request2);
        tripRequests.add(request3);
        tripRequests.add(request4);
        tripRequests.add(request5);


        adapter = new RecyclerListAdapter(getActivity(), this, tripRequests);

        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.listDestionation);
        txtAdd = (TextView) getView().findViewById(R.id.txtAdd);
        txtRemove = (TextView) getView().findViewById(R.id.txtDelete);
        txtOptimze = (TextView) getView().findViewById(R.id.txtOptimize);

        txtAdd.setOnClickListener(this);
        txtRemove.setOnClickListener(this);
        txtOptimze.setOnClickListener(this);
//        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        View rootView = inflater.inflate(R.layout.map, container, false);
        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);
        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getActivity(), null);
        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        //initialize map
        mapView = (MapView) rootView.findViewById(R.id.mapView);
        btnMenu = (ImageButton) rootView.findViewById(R.id.btn_menu);
        lisDestination = (RecyclerView) rootView.findViewById(R.id.listDestionation);
        btnMenu.setOnClickListener(this);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        showCurrentPlace();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initDestinationRecycle();
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override

    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        btnCurrentPlace = (Button) getView().findViewById(R.id.btnCurrentPlace);
        btnTestDriver = (Button) getView().findViewById(R.id.test_driver);
        btnNearDriver = (Button) getView().findViewById(R.id.btnNearDriver);
        btnCurrentPlace.setOnClickListener(this);
        btnTestDriver.setOnClickListener(this);
        btnNearDriver.setOnClickListener(this);
        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    /**
     * Prompts the user to select the current place from a list of likely places, and shows the
     * current place on the map - provided the user has granted location permission.
     */
    private void showCurrentPlace() {
        if (mMap == null) {
            return;
        }

        if (mLocationPermissionGranted) {
            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            @SuppressWarnings("MissingPermission") final Task<PlaceLikelihoodBufferResponse> placeResult =
                    mPlaceDetectionClient.getCurrentPlace(null);
            placeResult.addOnCompleteListener
                    (new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();

                                // Set the count, handling cases where less than 5 entries are returned.
                                int count;
                                if (likelyPlaces.getCount() < M_MAX_ENTRIES) {
                                    count = likelyPlaces.getCount();
                                } else {
                                    count = M_MAX_ENTRIES;
                                }

                                int i = 0;
                                mLikelyPlaceNames = new String[count];
                                mLikelyPlaceAddresses = new String[count];
                                mLikelyPlaceAttributions = new String[count];
                                mLikelyPlaceLatLngs = new LatLng[count];

                                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                                    // Build a list of likely places to show the user.
                                    mLikelyPlaceNames[i] = (String) placeLikelihood.getPlace().getName();
                                    mLikelyPlaceAddresses[i] = (String) placeLikelihood.getPlace()
                                            .getAddress();
                                    mLikelyPlaceAttributions[i] = (String) placeLikelihood.getPlace()
                                            .getAttributions();
                                    mLikelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();

                                    i++;
                                    if (i > (count - 1)) {
                                        break;
                                    }
                                }

                                // Release the place likelihood buffer, to avoid memory leaks.
                                likelyPlaces.release();

                                // Show a dialog offering the user the list of likely places, and add a
                                // marker at the selected place.
                                openPlacesDialog();

                            } else {
                                Log.e(TAG, "Exception: %s", task.getException());
                            }
                        }
                    });
        } else {
            // The user has not granted permission.
            Log.i(TAG, "The user did not grant location permission.");

            // Add a default marker, because the user hasn't selected a place.
            mMap.addMarker(new MarkerOptions()
                    .title(getString(R.string.default_info_title))
                    .position(mDefaultLocation)
                    .snippet(getString(R.string.default_info_snippet)))
                    .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car_marker));
            ;

            // Prompt the user for permission.
            getLocationPermission();
        }
    }

    private void openPlacesDialog() {
        // Ask the user to choose the place where they are now.
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // The "which" argument contains the position of the selected item.
                LatLng markerLatLng = mLikelyPlaceLatLngs[which];
                String markerSnippet = mLikelyPlaceAddresses[which];
                if (mLikelyPlaceAttributions[which] != null) {
                    markerSnippet = markerSnippet + "\n" + mLikelyPlaceAttributions[which];
                }
                int height = 200;
                int width = 100;
                BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_car_marker);
                Bitmap b = bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                // Add a marker for the selected place, with an info window
                // showing information about that place.
                mMap.addMarker(new MarkerOptions()
                        .title(mLikelyPlaceNames[which])
                        .position(markerLatLng)
                        .snippet(markerSnippet))
                        .setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));

                // Position the map's camera at the location of the marker.
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,
                        DEFAULT_ZOOM));
            }
        };

        // Display the dialog.
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.pick_place)
                .setItems(mLikelyPlaceNames, listener)
                .show();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void addCarMarker(LatLng latLng, Float bearing) {
        int height = 150;
        int width = 75;
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_car_marker);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        Random random = new Random();
        bearing = 0 + (360 - 0) * random.nextFloat();

        mMap.addMarker(new MarkerOptions()
                .title("top")
                .position(latLng)
                .rotation(bearing)
                .flat(true)
                .snippet(null))
                .setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.btnCurrentPlace:
                getDeviceLocation();
                break;
            case R.id.test_driver:
                setFakeDriver();
                break;
            case R.id.btnNearDriver:
                getNearestDriver();
                break;
            case R.id.btn_menu:
                DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
                drawer.openDrawer(Gravity.LEFT);
                break;
            case R.id.txtAdd:
                if (tripRequests.size() < 5) {
                    TripRequest request = new TripRequest("6", "12391209310", null, null, null, null, 0, null);
                    tripRequests.add(request);
                    adapter.notifyDataSetChanged();
                } else
                    Toast.makeText(getActivity(), "Quá số đơn hàng cho phép", Toast.LENGTH_SHORT).show();
                break;
            case R.id.txtDelete:
                if (tripRequests.size() > 2) {
                    tripRequests.remove(tripRequests.size() - 1);
                    adapter.notifyDataSetChanged();
                } else
                    Toast.makeText(getActivity(), "Cần tối thiểu điểm đi và điểm đến", Toast.LENGTH_SHORT).show();
                break;
            case R.id.txtOptimize:

                break;
        }
    }

    ArrayList<LatLng> latLngsThread = new ArrayList<>();

    private void createRandomDriver(double minx, double miny, double maxx, double maxy) {
        ArrayList<LatLng> latLngs = new ArrayList<>();
        final DatabaseReference root = FirebaseDatabase.getInstance().getReference().child(CHILD_DRIVER_POSTION);
        final DatabaseReference rootCar = root.child(CHILD_CAR_POSTION);
        rootCar.removeValue();
        Random random = new Random();
        mMap.clear();
        for (int i = 0; i < 10; i++) {
            double xcode = minx + (maxx - minx) * random.nextDouble();
            double ycode = miny + (maxy - miny) * random.nextDouble();
            LatLng latLng = new LatLng(ycode, xcode);
            addCarMarker(latLng, null);
            latLngs.add(latLng);
        }
        DriverPostion driverPostion1 = new DriverPostion();
        DriverPostion driverPostion2 = new DriverPostion();
        DriverPostion driverPostion3 = new DriverPostion();
        DriverPostion driverPostion4 = new DriverPostion();
        DriverPostion driverPostion5 = new DriverPostion();

        driverPostion1.setDriverID("DR1" + true);
        driverPostion2.setDriverID("DR2" + true);
        driverPostion3.setDriverID("DR3" + false);
        driverPostion4.setDriverID("DR4" + false);
        driverPostion5.setDriverID("DR5" + false);

        driverPostion1.setLangtitude(17.449903);
        driverPostion1.setLongitude(106.603251);
        driverPostion1.setCoordi(driverPostion1.getLangtitude() + "|" + driverPostion1.getLongitude());
        driverPostion1.setBearing(0);

        driverPostion2.setLangtitude(17.444294);
        driverPostion2.setLongitude(106.598487);
        driverPostion2.setCoordi(driverPostion2.getLangtitude() + "|" + driverPostion2.getLongitude());
        driverPostion2.setBearing(0);

        driverPostion3.setLangtitude(17.535421);
        driverPostion3.setLongitude(106.564076);
        driverPostion3.setCoordi(driverPostion3.getLangtitude() + "|" + driverPostion3.getLongitude());
        driverPostion3.setBearing(0);

        driverPostion4.setLangtitude(17.541407);
        driverPostion4.setLongitude(106.615934);
        driverPostion4.setCoordi(driverPostion4.getLangtitude() + "|" + driverPostion4.getLongitude());
        driverPostion4.setBearing(0);

        driverPostion5.setLangtitude(17.370698);
        driverPostion5.setLongitude(106.748358);
        driverPostion5.setCoordi(driverPostion5.getLangtitude() + "|" + driverPostion5.getLongitude());
        driverPostion5.setBearing(0);

        rootCar.child(driverPostion1.getDriverID()).setValue(driverPostion1);
        rootCar.child(driverPostion2.getDriverID()).setValue(driverPostion2);
        rootCar.child(driverPostion3.getDriverID()).setValue(driverPostion3);
        rootCar.child(driverPostion4.getDriverID()).setValue(driverPostion4);
        rootCar.child(driverPostion5.getDriverID()).setValue(driverPostion5);

        addCarMarker(new LatLng(driverPostion1.getLangtitude(), driverPostion1.getLongitude()), null);
        addCarMarker(new LatLng(driverPostion2.getLangtitude(), driverPostion2.getLongitude()), null);
        addCarMarker(new LatLng(driverPostion3.getLangtitude(), driverPostion3.getLongitude()), null);
        addCarMarker(new LatLng(driverPostion4.getLangtitude(), driverPostion4.getLongitude()), null);
        addCarMarker(new LatLng(driverPostion5.getLangtitude(), driverPostion5.getLongitude()), null);
        // put driver to database
        for (int i = 0; i < 10; i++) {

            float xcode = 0 + (360 - 0) * random.nextFloat();

            DriverPostion driverPostion = new DriverPostion();

            driverPostion.setDriverID("DR" + i);
            driverPostion.setLangtitude(latLngs.get(i).latitude);
            driverPostion.setLongitude(latLngs.get(i).longitude);
            driverPostion.setCoordi(driverPostion.getLangtitude() + "|" + driverPostion.getLongitude());
            driverPostion.setBearing(xcode);
            driverPostion.setDriverType(1);
            driverPostion.setStatus(0);
            rootCar.child(driverPostion.getDriverID()).setValue(driverPostion);
        }
        latLngsThread = latLngs;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int j = 0; j < 60; j++) {
                    SystemClock.sleep(1000);
                    Log.d("hieuhk", "run: " + j);
                    for (int i = 0; i < 10; i++) {

                        Random random = new Random();
                        latLngsThread.set(i, new LatLng(latLngsThread.get(i).latitude + 0.01, latLngsThread.get(i).longitude + 0.01));
                        float xcode = 0 + (360 - 0) * random.nextFloat();
                        //nghỉ 200 mili second

                        DriverPostion driverPostion = new DriverPostion();
                        driverPostion.setDriverID("DR" + i);
                        driverPostion.setLangtitude(latLngsThread.get(i).latitude);
                        driverPostion.setLongitude(latLngsThread.get(i).longitude);
                        driverPostion.setCoordi(driverPostion.getLangtitude() + "|" + driverPostion.getLongitude());
                        driverPostion.setDriverType(1);
                        driverPostion.setStatus(0);
                        rootCar.child(driverPostion.getDriverID()).setValue(driverPostion);
                    }
                }
            }
        });
        //start thread
        thread.start();
    }

    private void setFakeDriver() {

        LatLng lineTop = calculatorMaxdistance(0.0);
        LatLng lineLeft = calculatorMaxdistance(-90.0);
        LatLng lineRight = calculatorMaxdistance(90.0);
        LatLng lineBelow = calculatorMaxdistance(-180.0);

        double maxX = lineRight.longitude;
        double minX = lineLeft.longitude;
        double maxY = lineTop.latitude;
        double minY = lineBelow.latitude;

        createRandomDriver(minX, minY, maxX, maxY);
    }

    private LatLng calculatorMaxdistance(double bearing) {
        if (mLastKnownLocation == null) return null;

        double lat1rad = degreesToRadians(mLastKnownLocation.getLatitude());    // latitude to radian
        double long1rad = degreesToRadians(mLastKnownLocation.getLongitude());  // longitude to radian

        double d = 5000;    // distance of radar
        double R = 6371e3;  // earth R
        double brng = degreesToRadians(bearing);  //bearing to radian

        double lat2rad = Math.asin(Math.sin(lat1rad) * Math.cos(d / R) + Math.cos(lat1rad) * Math.sin(d / R) * Math.cos(brng));
        double logn2rad = long1rad + Math.atan2(Math.sin(brng) * Math.sin(d / R) * Math.cos(lat1rad), Math.cos(d / R) - Math.sin(lat1rad) * Math.sin(lat2rad));

        double lat2de = radianToDegree(lat2rad);
        double long2de = radianToDegree(logn2rad);

        LatLng coordinate = new LatLng(lat2de, long2de);

        return coordinate;
    }

    private double degreesToRadians(Double degrees) {
        return degrees * Math.PI / 180;
    }

    private double radianToDegree(Double rad) {
        return (180 / Math.PI) * rad;
    }

    private void updateMarker(Map<String, DriverPostion> map) {
        if (!map.isEmpty()) {
            mMap.clear();
            for (DriverPostion driverPostion : map.values()) {
                addCarMarker(new LatLng(driverPostion.getLangtitude(), driverPostion.getLongitude()), driverPostion.getBearing());
            }
        }

    }

    private void getNearestDriver() {
        final DatabaseReference root = FirebaseDatabase.getInstance().getReference().child(CHILD_DRIVER_POSTION);
        final DatabaseReference rootCar = root.child(CHILD_CAR_POSTION);
        getDeviceLocation();
        LatLng lineTop = calculatorMaxdistance(0.0);
        LatLng lineLeft = calculatorMaxdistance(-90.0);
        LatLng lineRight = calculatorMaxdistance(90.0);
        LatLng lineBelow = calculatorMaxdistance(-180.0);

        double maxY = lineRight.longitude;
        double minY = lineLeft.longitude;
        double maxX = lineTop.latitude;
        double minX = lineBelow.latitude;
        Query querryGetNearestDriver = rootCar.orderByChild("coordi").startAt(minX + "|" + minY).endAt(maxX + "|" + maxY);

        querryGetNearestDriver.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int dem = 0;
                    driverPostions.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        DriverPostion driverPostion = postSnapshot.getValue(DriverPostion.class);
                        driverPostions.put(driverPostion.getDriverID(), driverPostion);
                        dem++;
                    }
                    if (dem > 2)
                        updateMarker(driverPostions);
                    else mMap.clear();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        rootCar.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}