package com.example.kaku.here.Fragment;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.kaku.here.Adapter.HttpConnection;
import com.example.kaku.here.Adapter.OnStartDragListener;
import com.example.kaku.here.Adapter.PathJSONParser;
import com.example.kaku.here.Adapter.RecyclerListAdapter;
import com.example.kaku.here.Adapter.SimpleItemTouchHelperCallback;
import com.example.kaku.here.InstanceVariants;
import com.example.kaku.here.Model.Driver;
import com.example.kaku.here.Model.DriverPostion;
import com.example.kaku.here.Model.Request;
import com.example.kaku.here.Model.Trip;
import com.example.kaku.here.R;
import com.example.kaku.here.UI.TripReuqestActivity;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class UserMap extends Fragment implements OnMapReadyCallback, View.OnClickListener, OnStartDragListener, RoutingListener {
    private String TAG = "UserMap";
    private CameraPosition mCameraPosition;
    private GoogleMap mMap;
    public static final int REQUEST_TRIP_COMPLETE = 0x9334;
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
    private Location mLastKnownLocation = new Location(LocationManager.GPS_PROVIDER);
    ;

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
    private int radius = 5;
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
    private List<Polyline> polylines;
    private Boolean isOnOtimize = false;
    private int optimzeDistance;

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
        } else {

            mLastKnownLocation.setLatitude(mDefaultLocation.latitude);
            mLastKnownLocation.setLongitude(mDefaultLocation.longitude);
        }
        View rootView = inflater.inflate(R.layout.customer_map, container, false);
        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);
        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getActivity(), null);
        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        //initialize customer_map
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
        polylines = new ArrayList<>();
        getNearestDriver();

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

        // Turn on the My Location layer and the related control on the customer_map.
        updateLocationUI();

        // Get the current location of the device and set the position of the customer_map.
        getDeviceLocation();
        getNearestDriver();

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
                            // Set the customer_map's camera position to the current location of the device.
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
     * current place on the customer_map - provided the user has granted location permission.
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

                // Position the customer_map's camera at the location of the marker.
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
     * Updates the customer_map's UI settings based on whether the user has granted location permission.
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
                    drawMapRoute(tripRequests);
                } else
                    Toast.makeText(getActivity(), "Tối đa 5 đơn hàng", Toast.LENGTH_SHORT).show();
                break;
            case R.id.txtDelete:
                if (tripRequests.size() > 2) {
                    tripRequests.remove(tripRequests.size() - 1);
                    adapter.notifyDataSetChanged();
                    drawMapRoute(tripRequests);
                } else
                    Toast.makeText(getActivity(), "Cần tối thiểu điểm đi và điểm đến", Toast.LENGTH_SHORT).show();
                break;
            case R.id.txtOptimize:
                String url = getMapsApiDirectionsUrl();
                ReadTask downloadTask = new ReadTask();
                downloadTask.execute(url);
                /*optimizeTrip();*/
                break;
            case R.id.img_bike_mode:
                if (!requestTrip) {
                    endRide();
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
                    endRide();
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
            case R.id.btnCall:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                // Send phone number to intent as data
                intent.setData(Uri.parse("tel:" + currentDriver.getmPhone()));
                // Start the dialer app activity with number
                startActivity(intent);

                break;
            case R.id.btnSMS:
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse("sms:" + currentDriver.getmPhone()));
                startActivity(sendIntent);
                break;
        }
    }

    private void endRide() {
        requestTrip = false;
        geoQuery.removeAllListeners();
        if (driverLocationRef != null)
            driverLocationRef.removeEventListener(driverLocationRefListener);
        if (mDriverFoundDatabase != null)
            mDriverFoundDatabase.removeEventListener(mDriverFoundEventListener);
        if (driveHasEndedRef != null)
            driveHasEndedRef.removeEventListener(driveHasEndedRefListener);
        if (driverFoundID != null) {
            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child(InstanceVariants.CHILD_SHARE_USER)
                    .child(InstanceVariants.CHILD_WORKING_DRIVER).child(driverFoundID).child("customerRequest");
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

    private Trip currentTrip = new Trip();

    private boolean requestTrip() {
        if (tripRequests != null) {
            if (checkListRequest()) {

                requestTrip = true;
                drawMapRoute(tripRequests);
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                currentTrip = new Trip();
                currentTrip.setCustomerid(FirebaseAuth.getInstance().getCurrentUser().getUid());
                currentTrip.setDistanceSum(tripDistance);
                Intent intent = new Intent(getActivity(), TripReuqestActivity.class);
                intent.putExtra("trip", currentTrip);
                getActivity().startActivityForResult(intent, REQUEST_TRIP_COMPLETE);

               /* putRequest();
                getClosestDriver();*/
            } else return false;
        } else return false;
        return true;
    }

    private DatabaseReference mDriverFoundDatabase;
    private ValueEventListener mDriverFoundEventListener;

    private void getClosestDriver() {
        DatabaseReference rootLocation = FirebaseDatabase.getInstance().getReference().child(InstanceVariants.CHILD_DRIVER_AVAIABLE);
        final DatabaseReference driverLocation;
        pickupLocation = tripRequests.get(0).getDestination();
        if (vehicleMode) {
            driverLocation = rootLocation.child(InstanceVariants.CHILD_MOTOR_POSTION);
        } else {
            driverLocation = rootLocation.child(InstanceVariants.CHILD_CAR_POSTION);
        }
        setFindDriverUI(1);

        GeoFire geoFire = new GeoFire(driverLocation);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickupLocation.latitude, pickupLocation.longitude), radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!driverFound) {

                    mDriverFoundDatabase = FirebaseDatabase.getInstance()
                            .getReference().child(InstanceVariants.CHILD_SHARE_USER).child(InstanceVariants.CHILD_WORKING_DRIVER).child(key);

                    mDriverFoundEventListener = new ValueEventListener() {
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
                                DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference()
                                        .child(InstanceVariants.CHILD_SHARE_USER).child(InstanceVariants.CHILD_WORKING_DRIVER).child(driverFoundID).child("customerRequest");
                                driverRef.setValue(currentTripID);

                                getDriverLocation();
                                getDriverInfo();
                                /*getHasRideEnded();*/
                                txtStatus.setText("Looking for Driver Location....");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    };
                    mDriverFoundDatabase.addValueEventListener(mDriverFoundEventListener);
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
                    if (radius <= 100) {
                        radius++;
                        getClosestDriver();
                    } else {
                        //Todo: UPDATE STATUS NOT FOUND DRIVER
                        setFindDriverUI(0);
                        radius = 5;
                        Toast.makeText(getActivity(), "Không thể tìm thấy tài xế phù hợp với bạn", Toast.LENGTH_SHORT).show();
                        requestTrip = false;
                        removeRequestAndTrip();
                        endRide();
                    }
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void removeRequestAndTrip() {
        DatabaseReference refTrip = FirebaseDatabase.getInstance().getReference().child(InstanceVariants.CHILD_TRIPS).child(currentTripID);
        refTrip.removeValue();
        Query query = FirebaseDatabase.getInstance().getReference().child(InstanceVariants.CHILD_REQUEST).orderByChild("tripID").equalTo(currentTripID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        Request request = new Request();
                        Map<String, Object> driverMap = (Map<String, Object>) issue.getValue();
                        if (driverMap.get("id") != null)
                            FirebaseDatabase.getInstance().getReference().child(InstanceVariants.CHILD_REQUEST).child(driverMap.get("id").toString()).removeValue();

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
                        txtStatus.setText("Tài xế đang trên đường đến nhận hàng. Còn khoảng " + String.valueOf(distance/1000)+" km");
                    }

                    int height = 150;
                    int width = 75;

                    BitmapDrawable bitmapdraw = new BitmapDrawable();

                    if (vehicleMode) {
                        bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_moto_marker);
                    } else {
                        bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_car_marker);
                    }
                    Bitmap b = bitmapdraw.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                    mDriverMarker = mMap.addMarker(new MarkerOptions().
                            position(driverLatLng).title("Tài xế của bạn").
                            icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private Driver currentDriver;

    private void getDriverInfo() {
        setFindDriverUI(2);
        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference()
                .child(InstanceVariants.CHILD_SHARE_USER).child(InstanceVariants.CHILD_WORKING_DRIVER).child(driverFoundID);
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    currentDriver = dataSnapshot.getValue(Driver.class);
                    txtDriverName.setText(currentDriver.getmName());
                    txtVehicleDec.setText(currentDriver.getmCar());
                    ratingDriver.setRating(currentDriver.getRating());
                    imgDriver = (ImageView) getView().findViewById(R.id.imgDriverCustomerMap);
                    if (currentDriver.getmProfileImageUrl() != null || !currentDriver.getmProfileImageUrl().isEmpty())
                        Glide.with(getActivity()).load(currentDriver.getmProfileImageUrl()).into(imgDriver);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private void getHasRideEnded() {
        driveHasEndedRef = FirebaseDatabase.getInstance().getReference().child(InstanceVariants.CHILD_SHARE_USER)
                .child(InstanceVariants.CHILD_WORKING_DRIVER).child(driverFoundID).child("customerRequest")/*.child("customerRideId")*/;
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
                btnRequest.setImageResource(R.drawable.ic_right_arrow_white);
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
                btnRequest.setImageResource(R.drawable.ic_cancel);
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
                btnRequest.setImageResource(R.drawable.ic_cancel);
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
        /*setFindDriverUI(2);*/

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
                    if (marker.getTag() != null && marker.getTag().equals(key)) {
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
        drawMapRoute(tripRequests);
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        if (e != null) {
            Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {
    }

    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};
    private int tripDistance = 0;

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the customer_map.
        for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polyline.setTag(route.get(i).getDistanceText());
            polylines.add(polyline);
            if (!isOnOtimize)
                tripDistance = route.get(i).getDistanceValue();
            else optimzeDistance = route.get(i).getDistanceValue();
        }

    }

    @Override
    public void onRoutingCancelled() {
    }


    private void drawMapRoute(List<Request> tripRequests) {
        for (Request request : tripRequests) {
            if (request.getDestination() == null) return;
        }
        List<LatLng> positions = new ArrayList<>();
        for (Marker marker : markerList) {
            marker.remove();
        }
        int height = 75;
        int width = 75;
        BitmapDrawable bitmapdraw = new BitmapDrawable();
        bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_pickup);

        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        for (Request request : tripRequests) {
            positions.add(request.getDestination());
            markerList.add(mMap.addMarker(new MarkerOptions().
                    position(request.getDestination()).title(request.getDestinationName()).
                    icon(BitmapDescriptorFactory.fromBitmap(smallMarker))));

        }
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(positions)
                .build();
        routing.execute();
        for (int i = 0; i < markerList.size(); i++) {
            markerList.get(i).showInfoWindow();
        }
    }

    private void optimizeTrip() {
        drawMapRoute(tripRequests);
        List<Request> list = new ArrayList<>(tripRequests);
        printPermutation(list, 0, true);
    }

    private void printPermutation(List<Request> array, int start, boolean display) {
        if (display) {
            isOnOtimize = true;
            drawMapRoute(array);
            isOnOtimize = false;
            if (optimzeDistance < tripDistance) {
                tripRequests.clear();
                tripRequests = new ArrayList<>(array);
                adapter.notifyDataSetChanged();
                drawMapRoute(tripRequests);
            }
        }

        for (int j = start; j < array.size(); j++) {
            Request temp = array.get(start);
            array.set(start, array.get(j));
            array.set(j, temp);
            if (j == start) {
                printPermutation(array, start + 1, false);
            } else {
                printPermutation(array, start + 1, true);
            }
            temp = array.get(start);
            array.set(start, array.get(j));
            array.set(j, temp);
        }
    }

    private String getMapsApiDirectionsUrl() {
        String waypoints = "waypoints=optimize:true";
        for (Request request : tripRequests) {
            waypoints = waypoints + "|" + request.getDestination().latitude + "," + request.getDestination().longitude;
        }
        String sensor = "sensor=false";
        String params = waypoints + "&" + sensor;
        String output = "json?origin=hanoi,SA&destination=hanoi,SA&";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params;
        return url;
    }


    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }

    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            // traversing through routes
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(2);
                polyLineOptions.color(Color.BLUE);
            }

            mMap.addPolyline(polyLineOptions);
        }
    }

    public void updateTripAndSendRequest(Trip trip) {
        currentTrip = trip;
        putRequest();
        getClosestDriver();
        btnRequest.setImageResource(R.drawable.ic_cancel);
    }

    private void putRequest() {
        DatabaseReference rootTrip = FirebaseDatabase.getInstance().getReference().child(InstanceVariants.CHILD_TRIPS);
        DatabaseReference rootRequest = FirebaseDatabase.getInstance().getReference().child(InstanceVariants.CHILD_REQUEST);
        currentTripID = rootTrip.push().getKey();
        currentTrip.setId(currentTripID);
        rootTrip.child(currentTripID).setValue(currentTrip);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(InstanceVariants.CHILD_CUSTOMER_REQUEST);

        GeoFire geoFire = new GeoFire(ref);
        geoFire.setLocation(currentTripID, new GeoLocation(tripRequests.get(0).getDestination().latitude, tripRequests.get(0).getDestination().longitude));


        for (int i = 0; i < tripRequests.size(); i++) {
            tripRequests.get(i).setTripID(currentTripID);
            String requestId = rootRequest.push().getKey();
            tripRequests.get(i).setId(requestId);
            rootRequest.child(requestId).setValue(tripRequests.get(i));
        }
    }

    public void resetRequestStatus() {
        requestTrip = false;
    }
}