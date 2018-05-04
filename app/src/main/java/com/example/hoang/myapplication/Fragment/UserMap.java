package com.example.hoang.myapplication.Fragment;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hoang.myapplication.Adapter.OnStartDragListener;
import com.example.hoang.myapplication.Adapter.RecyclerListAdapter;
import com.example.hoang.myapplication.Adapter.SimpleItemTouchHelperCallback;
import com.example.hoang.myapplication.InstanceVariants;
import com.example.hoang.myapplication.Model.DriverPostion;
import com.example.hoang.myapplication.Model.Request;
import com.example.hoang.myapplication.Model.Trip;
import com.example.hoang.myapplication.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class UserMap extends Fragment implements OnMapReadyCallback, View.OnClickListener, OnStartDragListener {
    private String TAG = "UserMap";
    private CameraPosition mCameraPosition;
    private GoogleMap mMap;

    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private Button btnCurrentPlace;
    private Button btnTestDriver;
    private Button btnNearDriver;
    private ImageButton btnMenu;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;
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
    private List<Request> tripRequests = new ArrayList<>();
    private CoordinatorLayout optionUI;
    private ImageView imgBikeMode, imgCarMode;
    private FloatingActionButton btnRequest, btnCall, btnSMS;
    private Boolean requestTrip = false;
    // true is bike mode, false is car mode
    private boolean vehicleMode = true;
    // variants for request a trip
    private LatLng pickupLocation;
    private Marker pickupMarker;
    private GeoQuery geoQuery;
    private int radius = 1;
    private int maxRadius = 5;
    private Boolean driverFound = false;
    private String driverFoundID;
    private String currentTripID;
    private ConstraintLayout groupFindDriver, groupDriverInfor;
    private LinearLayout groupListRequest;
    private ImageView imgDriver;
    private TextView txtDriverName, txtVehicleDec, txtStatus;
    private RatingBar ratingDriver;
    private ProgressBar progressBar;
    private Marker mDriverMarker;
    private DatabaseReference driverLocationRef;
    private ValueEventListener driverLocationRefListener;
    ArrayList<LatLng> latLngsThread = new ArrayList<>();
    private DatabaseReference driveHasEndedRef;
    private ValueEventListener driveHasEndedRefListener;

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    private void initDestinationRecycle() {

        Request request = new Request("1", "12391209310", null, null, null, null, 0, null);
        tripRequests.add(request);
        tripRequests.add(request);


        adapter = new RecyclerListAdapter(getActivity(), this, tripRequests);

        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.listDestionation);
        txtAdd = (TextView) getView().findViewById(R.id.txtAdd);
        txtRemove = (TextView) getView().findViewById(R.id.txtDelete);
        txtOptimze = (TextView) getView().findViewById(R.id.txtOptimize);

        txtAdd.setOnClickListener(this);
        txtRemove.setOnClickListener(this);
        txtOptimze.setOnClickListener(this);
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

        btnRequest = (FloatingActionButton) getView().findViewById(R.id.btn_request);
        btnCall = (FloatingActionButton) getView().findViewById(R.id.btnCall);
        btnSMS = (FloatingActionButton) getView().findViewById(R.id.btnSMS);
        txtDriverName = (TextView) getView().findViewById(R.id.txtDriverName);
        txtVehicleDec = (TextView) getView().findViewById(R.id.txtVehiceDec);
        txtStatus = (TextView) getView().findViewById(R.id.txtStatus);
        ratingDriver = (RatingBar) getView().findViewById(R.id.ratingBarDriver);
        groupDriverInfor = (ConstraintLayout) getView().findViewById(R.id.groupDriverInfor);
        groupFindDriver = (ConstraintLayout) getView().findViewById(R.id.groupFindDriver);
        groupListRequest = (LinearLayout) getView().findViewById(R.id.groupListRequest);
        progressBar = (ProgressBar) getView().findViewById(R.id.progressLoading);
        btnRequest.setOnClickListener(this);
        btnCall.setOnClickListener(this);
        btnSMS.setOnClickListener(this);
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

    private void updateUI(boolean check) {
        optionUI = (CoordinatorLayout) getView().findViewById(R.id.listDestionationGroup);
        if (check) optionUI.setVisibility(View.VISIBLE);
        else optionUI.setVisibility(View.GONE);

    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        btnCurrentPlace = (Button) getView().findViewById(R.id.btnCurrentPlace);
        btnTestDriver = (Button) getView().findViewById(R.id.test_driver);
        btnNearDriver = (Button) getView().findViewById(R.id.btnNearDriver);
        imgBikeMode = (ImageView) getView().findViewById(R.id.img_bike_mode);
        imgCarMode = (ImageView) getView().findViewById(R.id.img_car_mode);

        imgCarMode.setOnClickListener(this);
        imgBikeMode.setOnClickListener(this);
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
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                Log.d(TAG, "onCameraMove: ");
                updateUI(false);
            }
        });
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                Log.d(TAG, "onCameraIdle: ");
                updateUI(true);
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
        BitmapDrawable bitmapdraw = new BitmapDrawable();
        if (!vehicleMode) {
            bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_car_marker);

        } else {
            bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_moto_marker);
        }
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
            case R.id.btn_menu:
                DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
                drawer.openDrawer(Gravity.LEFT);
                break;
            case R.id.txtAdd:
                if (tripRequests.size() < 5) {
                    Request request = new Request("6", "12391209310", null, null, null, null, 0, null);
                    tripRequests.add(request);
                    adapter.notifyDataSetChanged();
                } else
                    Toast.makeText(getActivity(), "Tối đa 5 đơn hàng", Toast.LENGTH_SHORT).show();
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
            case R.id.img_bike_mode:
                if (!requestTrip) {
                    getDeviceLocation();
                    for (Marker marker : markerList) {
                        marker.remove();
                    }
                    markerList.clear();
                    setVehicleMode(true);
                    getNearestDriver();
                }
                break;
            case R.id.img_car_mode:
                if (!requestTrip) {
                    getDeviceLocation();
                    for (Marker marker : markerList) {
                        marker.remove();
                    }
                    markerList.clear();
                    setVehicleMode(false);
                    getNearestDriver();
                }
                break;
            case R.id.btn_request:
                if (!requestTrip) {
                    if (!requestTrip())
                        Toast.makeText(getActivity(), "Vui lòng cung cấp đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                } else endRide();
                break;
            case R.id.btnCurrentPlace:
                getDeviceLocation();

                break;
        }
    }

    private void endRide() {
        requestTrip = false;
        geoQuery.removeAllListeners();
        driverLocationRef.removeEventListener(driverLocationRefListener);
        driveHasEndedRef.removeEventListener(driveHasEndedRefListener);
        if (driverFoundID != null) {
            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
            driverRef.removeValue();
            driverFoundID = null;

        }
        driverFound = false;
        radius = 1;
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);

        if (pickupMarker != null) {
            pickupMarker.remove();
        }
      /*  if (mDriverMarker != null){
            mDriverMarker.remove();
        }
        mRequest.setText("call Uber");

        mDriverInfo.setVisibility(View.GONE);
        mDriverName.setText("");
        mDriverPhone.setText("");
        mDriverCar.setText("Destination: --");
        mDriverProfileImage.setImageResource(R.mipmap.ic_default_user);*/
    }

    private boolean requestTrip() {
        if (tripRequests != null) {
            if (checkListRequest()) {

                requestTrip = true;

                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference(InstanceVariants.CHILD_CUSTOMER_REQUEST);

                GeoFire geoFire = new GeoFire(ref);
                geoFire.setLocation(userId, new GeoLocation(tripRequests.get(0).getDestination().latitude, tripRequests.get(0).getDestination().longitude));

                pickupLocation = new LatLng(tripRequests.get(0).getDestination().latitude, tripRequests.get(0).getDestination().longitude);
                pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Pickup Here").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pickup)));
                putRequest();
                getClosestDriver();
            } else return false;
        } else return false;
        return true;
    }

    private void putRequest() {
        DatabaseReference rootTrip = FirebaseDatabase.getInstance().getReference().child(InstanceVariants.CHILD_TRIPS);
        DatabaseReference rootRequest = FirebaseDatabase.getInstance().getReference().child(InstanceVariants.CHILD_REQUEST);
        final Trip trip = new Trip();
        currentTripID = rootTrip.push().getKey();
        trip.setId(currentTripID);
        trip.setCustomerid(FirebaseAuth.getInstance().getCurrentUser().getUid());
        rootTrip.child(currentTripID).setValue(trip);

        for (int i = 0; i < tripRequests.size(); i++) {
            tripRequests.get(i).setTripID(currentTripID);
            String requestId = rootRequest.push().getKey();
            tripRequests.get(i).setId(requestId);
            rootRequest.child(requestId).setValue(tripRequests.get(i));
        }
    }

    private void getClosestDriver() {
        DatabaseReference rootLocation = FirebaseDatabase.getInstance().getReference().child(InstanceVariants.CHILD_DRIVER_AVAIABLE);
        DatabaseReference driverLocation;

        if (vehicleMode) {
            driverLocation = rootLocation.child(InstanceVariants.CHILD_MOTOR_POSTION);
        } else {
            driverLocation = rootLocation.child(InstanceVariants.CHILD_CAR_POSTION);
        }
        setFindDriverUI(2);

        GeoFire geoFire = new GeoFire(driverLocation);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickupLocation.latitude, pickupLocation.longitude), radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!driverFound && requestTrip) {

                    DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance()
                            .getReference().child(InstanceVariants.CHILD_WORKING).child(InstanceVariants.CHILD_WORKING_DRIVER).child(key);

                    mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                //todo remove driver from driver postion
                                Map<String, Object> driverMap = (Map<String, Object>) dataSnapshot.getValue();

                                if (driverFound) {
                                    return;
                                }

                                driverFound = true;
                                driverFoundID = dataSnapshot.getKey();

                                DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().
                                        child(InstanceVariants.CHILD_WORKING).
                                        child(InstanceVariants.CHILD_WORKING_DRIVER).
                                        child(driverFoundID).
                                        child("customerRequest");
                                String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                driverRef.setValue(currentTripID);

                                getDriverLocation();
                                getDriverInfo();
                                getHasRideEnded();
/*
                                mRequest.setText("Looking for Driver Location....");
*/

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!driverFound) {
                    if (radius <= 5) {
                        radius++;
                        getClosestDriver();
                    } else {
                        //Todo: UPDATE STATUS NOT FOUND DRIVER
                        setFindDriverUI(0);
                        Toast.makeText(getActivity(), "Không thể tìm thấy tài xế phù hợp với bạn", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }


    private void getDriverLocation() {
        driverLocationRef = FirebaseDatabase.getInstance().getReference().child("driversWorking").child(driverFoundID).child("l");
        driverLocationRefListener = driverLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && requestTrip) {
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    if (map.get(0) != null) {
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if (map.get(1) != null) {
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng driverLatLng = new LatLng(locationLat, locationLng);
                    if (mDriverMarker != null) {
                        mDriverMarker.remove();
                    }
                    Location loc1 = new Location("");
                    loc1.setLatitude(pickupLocation.latitude);
                    loc1.setLongitude(pickupLocation.longitude);

                    Location loc2 = new Location("");
                    loc2.setLatitude(driverLatLng.latitude);
                    loc2.setLongitude(driverLatLng.longitude);

                    float distance = loc1.distanceTo(loc2);

                    if (distance < 100) {
                        txtStatus.setText("Tài xế đã đến!");
                    } else {
                        txtStatus.setText("Tài xế đang trên đường đến nhận hàng " + String.valueOf(distance));
                    }


                    if (vehicleMode) {
                        mDriverMarker = mMap.addMarker(new MarkerOptions().
                                position(driverLatLng).title("your driver").
                                icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_moto_marker)));
                    } else {
                        mDriverMarker = mMap.addMarker(new MarkerOptions().
                                position(driverLatLng).title("your driver").
                                icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car_marker)));
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private void getDriverInfo() {
        setFindDriverUI(1);
        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference()
                .child(InstanceVariants.CHILD_ACCOUNT).child(InstanceVariants.CHILD_WORKING_DRIVER).child(driverFoundID);
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    if (dataSnapshot.child("name") != null) {
                        txtDriverName.setText(dataSnapshot.child("name").getValue().toString());
                    }
                    if (dataSnapshot.child("phone") != null) {
                        txtVehicleDec.setText(dataSnapshot.child("phone").getValue().toString());
                    }
                    if (dataSnapshot.child("profileImageUrl").getValue() != null) {
                        Glide.with(getContext()).load(dataSnapshot.child("profileImageUrl").getValue().toString()).into(imgDriver);
                    }

                    int ratingSum = 0;
                    float ratingsTotal = 0;
                    float ratingsAvg = 0;
                    for (DataSnapshot child : dataSnapshot.child("rating").getChildren()) {
                        ratingSum = ratingSum + Integer.valueOf(child.getValue().toString());
                        ratingsTotal++;
                    }
                    if (ratingsTotal != 0) {
                        ratingsAvg = ratingSum / ratingsTotal;
                        ratingDriver.setRating(ratingsAvg);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private void getHasRideEnded() {
        driveHasEndedRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest").child("customerRideId");
        driveHasEndedRefListener = driveHasEndedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                } else {
                    endRide();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void setFindDriverUI(int check) {
        switch (check) {
            case 0:
                groupListRequest.setVisibility(View.VISIBLE);
                groupFindDriver.setVisibility(View.GONE);
                groupDriverInfor.setVisibility(View.GONE);
                btnSMS.setVisibility(View.GONE);
                btnCall.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                txtAdd.setVisibility(View.VISIBLE);
                txtRemove.setVisibility(View.VISIBLE);
                txtOptimze.setVisibility(View.VISIBLE);
                txtStatus.setVisibility(View.GONE);
                break;
            case 1:
                groupListRequest.setVisibility(View.GONE);
                groupFindDriver.setVisibility(View.VISIBLE);
                groupDriverInfor.setVisibility(View.GONE);
                btnSMS.setVisibility(View.GONE);
                btnCall.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                txtAdd.setVisibility(View.GONE);
                txtRemove.setVisibility(View.GONE);
                txtOptimze.setVisibility(View.GONE);
                txtStatus.setVisibility(View.VISIBLE);
                break;
            case 2:
                groupListRequest.setVisibility(View.GONE);
                groupFindDriver.setVisibility(View.VISIBLE);
                groupDriverInfor.setVisibility(View.VISIBLE);
                btnSMS.setVisibility(View.VISIBLE);
                btnCall.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                txtAdd.setVisibility(View.GONE);
                txtRemove.setVisibility(View.GONE);
                txtOptimze.setVisibility(View.GONE);
                txtStatus.setVisibility(View.VISIBLE);
                break;
        }
    }

    private boolean checkListRequest() {
        if (tripRequests == null) return false;
        if (!tripRequests.get(0).isStartPointAndItDone()) return false;
        for (int i = 1; i < tripRequests.size(); i++) {
            if (!tripRequests.get(i).isRequestDone()) return false;
        }
        return true;
    }

    private void setVehicleMode(Boolean mode) {
        vehicleMode = mode;
        getDeviceLocation();
        if (vehicleMode) {
            imgBikeMode.setImageDrawable(getActivity().getDrawable(R.drawable.ic_motor_mode));
            imgCarMode.setImageDrawable(getActivity().getDrawable(R.drawable.ic_car_mode_grey));
        } else {
            imgBikeMode.setImageDrawable(getActivity().getDrawable(R.drawable.ic_motor_mode_gray));
            imgCarMode.setImageDrawable(getActivity().getDrawable(R.drawable.ic_car_mode));
        }
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

    private List<Marker> markerList = new ArrayList<>();

    private void getNearestDriver() {
        DatabaseReference rootLocation = FirebaseDatabase.getInstance().getReference().child(InstanceVariants.CHILD_DRIVER_AVAIABLE);
        DatabaseReference driverLocation;

        if (vehicleMode) {
            driverLocation = rootLocation.child(InstanceVariants.CHILD_MOTOR_POSTION);
        } else {
            driverLocation = rootLocation.child(InstanceVariants.CHILD_CAR_POSTION);
        }
        setFindDriverUI(2);

        GeoFire geoFire = new GeoFire(driverLocation);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), 10);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                //TODO QUERY NEAR DRIVER

                for (Marker marker : markerList) {
                    if (marker.getTag().equals(key)) return;
                }
                int height = 150;
                int width = 75;
                BitmapDrawable bitmapdraw = new BitmapDrawable();
                if (!vehicleMode) {
                    bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_car_marker);

                } else {
                    bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_moto_marker);
                }
                Bitmap b = bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                Random random = new Random();
                LatLng driverLocation = new LatLng(location.latitude, location.longitude);
                Marker marker = mMap.addMarker(new MarkerOptions().position(driverLocation));
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                marker.setTag(key);
                markerList.add(marker);
            }

            @Override
            public void onKeyExited(String key) {
                for (Marker marker : markerList) {
                    if (marker.getTag().equals(key)) {
                        marker.remove();
                        markerList.remove(marker);
                        return;
                    }
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                for (Marker marker : markerList) {
                    marker.setPosition(new LatLng(location.latitude, location.longitude));
                }
            }

            @Override
            public void onGeoQueryReady() {
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    public void updateRequestList(int number, Request request) {
        if (number >= 0 && number < tripRequests.size()) {
            this.tripRequests.set(number, request);
            adapter.notifyDataSetChanged();
        }
    }
}