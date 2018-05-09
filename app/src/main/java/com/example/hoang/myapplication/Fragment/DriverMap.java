package com.example.hoang.myapplication.Fragment;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.hoang.myapplication.Adapter.RecyclerListAdapter;
import com.example.hoang.myapplication.InstanceVariants;
import com.example.hoang.myapplication.Model.Driver;
import com.example.hoang.myapplication.Model.Request;
import com.example.hoang.myapplication.Model.ShareCustomer;
import com.example.hoang.myapplication.Model.Trip;
import com.example.hoang.myapplication.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
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
import com.google.android.gms.maps.SupportMapFragment;
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
import com.google.firebase.auth.FirebaseUser;
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

public class DriverMap extends Fragment implements OnMapReadyCallback, View.OnClickListener, RoutingListener {
    private String TAG = "UserMap";
    private CameraPosition mCameraPosition;
    private GoogleMap mMap;

    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private Button btnCurrentPlace;
    private ImageButton btnMenu;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 13;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private int currentProgress = 0;
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
    private ItemTouchHelper mItemTouchHelper;
    private RecyclerListAdapter adapter;
    private List<Request> tripRequests = new ArrayList<>();
    private LatLng pickupLocation;
    private Marker pickupMarker;
    private GeoQuery geoQuery;
    private int radius = 1;
    private int maxRadius = 5;
    private Marker mDriverMarker;
    private DatabaseReference driverLocationRef;
    private ValueEventListener driverLocationRefListener;
    ArrayList<LatLng> latLngsThread = new ArrayList<>();
    private DatabaseReference driveHasEndedRef;
    private ValueEventListener driveHasEndedRefListener;
    ///////////////////////////////////////////////////////////////
    private ImageView imgStatus;
    private Switch btnStatus;
    private ToggleButton btnBottom;
    private TextView txtWhatHappend, txtWallet, txtDriverType, txtAcceptRatin, txtRating, txtCancelRating, txtStatus;
    private ConstraintLayout groupDetail, groupDriverMap1, bottomBar;
    private LinearLayout groupBottomBar;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private List<Polyline> polylines;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};

    private Button mLogout, mSettings, mRideStatus, mHistory;

    private Switch mWorkingSwitch;

    private int status = 0;

    private String tripId = "", destination;
    private LatLng destinationLatLng, pickupLatLng;
    private float rideDistance;

    private Boolean isLoggingOut = false;

    private SupportMapFragment mapFragment;

    private LinearLayout mCustomerInfo;

    private ImageView mCustomerProfileImage;
    private TextView mCustomerName, mCustomerPhone, mCustomerDestination;
    private Boolean isAccept;
    private Driver mDriver;
    private FirebaseUser mUser;
    private boolean mDriverType;
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getActivity().getApplicationContext() != null) {

                    if (!tripId.equals("") && mLastLocation != null && location != null) {
                        rideDistance += mLastLocation.distanceTo(location) / 1000;
                    }
                    mLastLocation = location;


                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));

                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference refRoot = FirebaseDatabase.getInstance().getReference(InstanceVariants.CHILD_DRIVER_AVAIABLE);
                    DatabaseReference refAvailable;
                    if (mDriverType) {
                        refAvailable = refRoot.child(InstanceVariants.CHILD_MOTOR_POSTION);
                    } else {
                        refAvailable = refRoot.child(InstanceVariants.CHILD_CAR_POSTION);
                    }
                    DatabaseReference refWorking = FirebaseDatabase.getInstance().getReference("driversWorking");
                    GeoFire geoFireAvailable = new GeoFire(refAvailable);
                    GeoFire geoFireWorking = new GeoFire(refWorking);

                    switch (tripId) {
                        case "":
                            geoFireWorking.removeLocation(userId);
                            geoFireAvailable.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
                            break;

                        default:
                            geoFireAvailable.removeLocation(userId);
                            geoFireWorking.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
                            break;
                    }
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        View rootView = inflater.inflate(R.layout.my_driver_map, container, false);
        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);
        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getActivity(), null);
        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        //initialize customer_map
        mapView = (MapView) rootView.findViewById(R.id.mapView);
        btnMenu = (ImageButton) rootView.findViewById(R.id.btn_menu);
        btnMenu.setOnClickListener(this);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        showCurrentPlace();
        loadDriverData();
        return rootView;
    }

    private void loadDriverData() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().
                child(InstanceVariants.CHILD_SHARE_USER).
                child(InstanceVariants.CHILD_WORKING_DRIVER).
                child(mUser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mDriver = dataSnapshot.getValue(Driver.class);
                    switch (mDriver.getmService()) {
                        case "HereBike":
                            mDriverType = true;
                            break;
                        case "HereCar":
                            mDriverType = false;
                            break;
                    }
                    txtDriverType.setText(mDriver.getmService());
                    txtRating.setText(mDriver.getRating() + "");
                    txtAcceptRatin.setText(mDriver.getTrip_accept() + "%");
                    txtCancelRating.setText(mDriver.getTrip_cancel() + "%");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private TextView txtMoney, txtDriverTypeAccept, txtTimeRemain;
    private RecyclerView listTrip;
    private Button btnAccept, btnDecline;
    private ConstraintLayout group1, group2, group3;
    private Boolean isStopTime = false;
    private long stopTime;
    private FloatingActionButton btnCall, btnSms, btnCancel;
    private ImageView btnListRequest, btnDirection;
    private TextView txtCustomerName, txtTripCost;
    private Button btnDriverTrip;
    private SeekBar seekStatus;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        // group 1  main screen
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        groupBottomBar = (LinearLayout) getView().findViewById(R.id.groupBottomBar);
        groupDetail = (ConstraintLayout) getView().findViewById(R.id.groupDetail);
        groupDriverMap1 = (ConstraintLayout) getView().findViewById(R.id.groupDriverMap1);
        bottomBar = (ConstraintLayout) getView().findViewById(R.id.bottombar);
        groupDetail.setVisibility(View.GONE);

        btnStatus = (Switch) getView().findViewById(R.id.switchStatus);
        btnBottom = (ToggleButton) getView().findViewById(R.id.toggleBottom);
        imgStatus = (ImageView) getView().findViewById(R.id.imgStatus);
        txtWhatHappend = (TextView) getView().findViewById(R.id.txtWhatHappen);
        txtWallet = (TextView) getView().findViewById(R.id.txtWallet);
        txtDriverType = (TextView) getView().findViewById(R.id.txtDriverType);
        txtAcceptRatin = (TextView) getView().findViewById(R.id.txtAcceptRating);
        txtRating = (TextView) getView().findViewById(R.id.txtRating);
        txtCancelRating = (TextView) getView().findViewById(R.id.txtCancelRate);
        txtStatus = (TextView) getView().findViewById(R.id.txtStatus);
        group1 = (ConstraintLayout) getView().findViewById(R.id.groupDriverMap1);

        // group 2 accept screen
        txtMoney = (TextView) getView().findViewById(R.id.txtMoney);
        txtDriverTypeAccept = (TextView) getView().findViewById(R.id.txtDriverType);
        txtTimeRemain = (TextView) getView().findViewById(R.id.txtTimeRemain);
        listTrip = (RecyclerView) getView().findViewById(R.id.listTrip);
        btnAccept = (Button) getView().findViewById(R.id.btnAccept);
        btnDecline = (Button) getView().findViewById(R.id.btnDecline);
        group2 = (ConstraintLayout) getView().findViewById(R.id.groupDriverMap2);
        // group 3 status screen
        btnCall = (FloatingActionButton) getView().findViewById(R.id.btnCall);
        btnSms = (FloatingActionButton) getView().findViewById(R.id.btnSms);
        btnCancel = (FloatingActionButton) getView().findViewById(R.id.btnCancel);
        btnListRequest = (ImageView) getView().findViewById(R.id.btnListRequest);
        btnDirection = (ImageView) getView().findViewById(R.id.btnMapDirect);
        group3 = (ConstraintLayout) getView().findViewById(R.id.groupDriverMap3);
        txtCustomerName = (TextView) getView().findViewById(R.id.txtCustomerName);
        txtTripCost = (TextView) getView().findViewById(R.id.txtTripCount);
        btnDriverTrip = (Button) getView().findViewById(R.id.btnDriverTrip);
        seekStatus = (SeekBar) getView().findViewById(R.id.seekStatusTrip);

        btnAccept.setOnClickListener(this);
        btnDecline.setOnClickListener(this);
        btnDriverTrip.setOnClickListener(this);
        btnCall.setOnClickListener(this);
        btnSms.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnListRequest.setOnClickListener(this);
        btnDirection.setOnClickListener(this);

        // handle event
        btnStatus.setOnClickListener(this);
        btnStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!isStopTime) {
                        imgStatus.setImageDrawable(getActivity().getDrawable(R.drawable.ic_on));
                        txtStatus.setText("Đang hoạt động");
                        connectDriver();
                    } else {
                        btnStatus.setChecked(false);
                        Toast.makeText(getActivity(), "Bạn đang trong thời gian tạm khóa. Kích hoạt lại sau " + stopTime + " giây nữa.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    imgStatus.setImageDrawable(getActivity().getDrawable(R.drawable.ic_off));
                    txtStatus.setText("Tạm ngưng");
                    disconnectDriver();
                }

            }
        });
        btnBottom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    groupDetail.setVisibility(View.GONE);
                } else {
                    groupDetail.setVisibility(View.VISIBLE);
                }
            }
        });
        updateUI(0);
        polylines = new ArrayList<>();
        getAssignedCustomer();

    }

    private void disconnectDriver() {
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(InstanceVariants.CHILD_DRIVER_AVAIABLE);
        DatabaseReference refAvailable;
        if (mDriverType) {
            refAvailable = ref.child(InstanceVariants.CHILD_MOTOR_POSTION);
        } else {
            refAvailable = ref.child(InstanceVariants.CHILD_CAR_POSTION);
        }

        GeoFire geoFire = new GeoFire(refAvailable);
        geoFire.removeLocation(userId);
    }

    private void getAssignedCustomer() {
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child(InstanceVariants.CHILD_SHARE_USER);
        DatabaseReference refAvailable;
        refAvailable = rootRef.child(InstanceVariants.CHILD_ACCOUNT_DRIVERS).child(driverId).child("customerRequest");


        refAvailable.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    tripId = dataSnapshot.getValue().toString();
                    // todo show acceptdialog
                    showAcceptScreen();
                } else {
                    endRide();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private Trip currentTrip;

    private void showAcceptScreen() {
        isAccept = false;
        currentProgress = 1;
        updateUI(1);
        tripRequests.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference referenceTrip = FirebaseDatabase.getInstance().getReference().child(InstanceVariants.CHILD_TRIPS).child(tripId);
        referenceTrip.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentTrip = dataSnapshot.getValue(Trip.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Query query = reference.child(InstanceVariants.CHILD_REQUEST).orderByChild("tripID").equalTo(tripId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        Request request = new Request();
                        Map<String, Object> driverMap = (Map<String, Object>) issue.getValue();
                        if (driverMap.get("id") != null)
                            request.setId(driverMap.get("id").toString());
                        if (driverMap.get("destinationName") != null)
                            request.setDestinationName(driverMap.get("destinationName").toString());
                        if (driverMap.get("money") != null)
                            request.setMoney((long) driverMap.get("money"));
                        if (driverMap.get("tripID") != null)
                            request.setTripID(driverMap.get("tripID").toString());
                        if (driverMap.get("note") != null)
                            request.setNote(driverMap.get("note").toString());
                        if (driverMap.get("receiverName") != null)
                            request.setReceiverName(driverMap.get("receiverName").toString());
                        if (driverMap.get("receiverNumber") != null)
                            request.setReceiverNumber(driverMap.get("receiverNumber").toString());
                        if (driverMap.get("destination") != null) {
                            Map<String, Object> destination = (Map<String, Object>) driverMap.get("destination");
                          /*  Map<String, Map<String, String>> stringMapMap = (Map<String, Map<String, String>>) driverMap.get("destination");
                            Map<String, String> mlat = stringMapMap.get(0);
                            Map<String, String> mlong = stringMapMap.get(1);*/
                            double lLat = Double.parseDouble(destination.get("latitude").toString());
                            double lLong = Double.parseDouble(destination.get("longitude").toString());
                            request.setDestination(new LatLng(lLat, lLong));
                        }
                        tripRequests.add(request);

                    }
                    adapter = new RecyclerListAdapter(getActivity(), null, tripRequests);
                    listTrip.setAdapter(adapter);
                    listTrip.setLayoutManager(new LinearLayoutManager(getActivity()));
                    showCountDown();
                } else
                    Toast.makeText(getActivity(), "Không tìm thấy dữ liệu chuyến đi!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    CountDownTimer countDownTimer;

    private void showCountDown() {

        countDownTimer = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                txtTimeRemain.setText(millisUntilFinished / 1000 + "");
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                txtTimeRemain.setText("done!");
            }

        }.start();
    }

    private void getAssignedCustomerDestination() {
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference()
                .child(InstanceVariants.CHILD_SHARE_USER)
                .child(InstanceVariants.CHILD_WORKING_DRIVER)
                .child(driverId).child("customerRequest");
        assignedCustomerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // todo add list of request to list
                   /* Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("destination") != null) {
                        destination = map.get("destination").toString();
                        mCustomerDestination.setText("Destination: " + destination);
                    } else {
                        mCustomerDestination.setText("Destination: --");
                    }

                    Double destinationLat = 0.0;
                    Double destinationLng = 0.0;
                    if (map.get("destinationLat") != null) {
                        destinationLat = Double.valueOf(map.get("destinationLat").toString());
                    }
                    if (map.get("destinationLng") != null) {
                        destinationLng = Double.valueOf(map.get("destinationLng").toString());
                        destinationLatLng = new LatLng(destinationLat, destinationLng);
                    }
*/
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private DatabaseReference assignedCustomerPickupLocationRef;
    private ValueEventListener assignedCustomerPickupLocationRefListener;
    private List<Marker> markerList = new ArrayList<>();

    private void getAssignedCustomerPickupLocation() {
        updateUI(1);
        List<LatLng> positions = new ArrayList<>();
        positions.add(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
        markerList.clear();
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
     /*   assignedCustomerPickupLocationRef = FirebaseDatabase.getInstance().getReference().child(InstanceVariants.CHILD_CUSTOMER_REQUEST).child(tripId).child("l");
        assignedCustomerPickupLocationRefListener = assignedCustomerPickupLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && !tripId.equals("")) {
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    if (map.get(0) != null) {
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if (map.get(1) != null) {
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    pickupLatLng = new LatLng(locationLat, locationLng);
                    pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLatLng).title("Điểm nhận hàng").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pickup)));
                    getRouteToMarker(pickupLatLng);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });*/
    }

    private ShareCustomer currentCustomer;

    private void getAssignedCustomerInfo() {
        /*mCustomerInfo.setVisibility(View.VISIBLE);*/
        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference()
                .child(InstanceVariants.CHILD_SHARE_USER).child(InstanceVariants.CHILD_WORKING_CUSTOMER).child(currentTrip.getCustomerid());
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    currentCustomer = dataSnapshot.getValue(ShareCustomer.class);
                    txtCustomerName.setText(currentCustomer.getLast_name()+" "+currentCustomer.getFirst_name());
                    txtTripCost.setText(currentTrip.getMoneySum()+" VND");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private void endRide() {
/*
        mRideStatus.setText("picked customer");
*/
        erasePolylines();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId).child("customerRequest");
        driverRef.removeValue();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(tripId);
        tripId = "";
        rideDistance = 0;

        if (pickupMarker != null) {
            pickupMarker.remove();
        }
        for (int i = 0; i < markerList.size(); i++) {
            markerList.get(i).remove();
        }
        if (assignedCustomerPickupLocationRefListener != null) {
            assignedCustomerPickupLocationRef.removeEventListener(assignedCustomerPickupLocationRefListener);
        }
        /*mCustomerInfo.setVisibility(View.GONE);*/
    /*    mCustomerName.setText("");
        mCustomerPhone.setText("");
        mCustomerDestination.setText("Destination: --");
        mCustomerProfileImage.setImageResource(R.mipmap.ic_default_user);*/
    }

    private void recordRide() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId).child("history");
        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(tripId).child("history");
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference().child("history");
        String requestId = historyRef.push().getKey();
        driverRef.child(requestId).setValue(true);
        customerRef.child(requestId).setValue(true);

        HashMap map = new HashMap();
        map.put("driver", userId);
        map.put("customer", tripId);
        map.put("rating", 0);
        map.put("destination", destination);
        map.put("location/from/lat", pickupLatLng.latitude);
        map.put("location/from/lng", pickupLatLng.longitude);
        map.put("location/to/lat", destinationLatLng.latitude);
        map.put("location/to/lng", destinationLatLng.longitude);
        map.put("distance", rideDistance);
        historyRef.child(requestId).updateChildren(map);
    }

    private void connectDriver() {
        checkLocationPermission();
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        currentProgress = 0;
        mMap.setMyLocationEnabled(true);
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("give permission")
                        .setMessage("give permission message")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.btn_menu:
                DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
                drawer.openDrawer(Gravity.LEFT);
                break;
            case R.id.btnCurrentPlace2:
                getDeviceLocation();
                break;
            case R.id.btnAccept:
                isAccept = true;
                if (isAccept) {
                    currentProgress = 2;
                    countDownTimer.cancel();
                    getAssignedCustomerPickupLocation();
                    getAssignedCustomerDestination();
                    getAssignedCustomerInfo();
                    updateUI(2);
                }
                break;
            case R.id.btnDecline:
                currentProgress = 0;
                showAlertDialog();
                break;
            case R.id.btnCall:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                // Send phone number to intent as data
                intent.setData(Uri.parse("tel:" + currentCustomer.getPhone()));
                // Start the dialer app activity with number
                startActivity(intent);

                break;
            case R.id.btnSms:
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse("sms:" + currentCustomer.getPhone()));
                startActivity(sendIntent);
                break;

        }

    }

    private void updateUI(int mode) {
        switch (mode) {
            case 0:
                group1.setVisibility(View.VISIBLE);
                group2.setVisibility(View.GONE);
                group3.setVisibility(View.GONE);
                break;
            case 1:
                group1.setVisibility(View.GONE);
                group2.setVisibility(View.VISIBLE);
                group3.setVisibility(View.GONE);
                break;
            case 2:
                group1.setVisibility(View.GONE);
                group2.setVisibility(View.GONE);
                group3.setVisibility(View.VISIBLE);
                break;
            case 3:
                group1.setVisibility(View.GONE);
                group2.setVisibility(View.GONE);
                group3.setVisibility(View.GONE);
                break;
            case 4:
                updateUI(currentProgress);
                break;
        }
    }

    public void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Chú ý!");
        builder.setMessage("Từ chối đơn hàng sẽ đưa bạn về trạng thái tạm nghỉ trong 2 phút. Bạn có muốn từ chối đơn hàng này không? ");
        builder.setCancelable(false);
        builder.setPositiveButton("Đổng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                btnStatus.setChecked(false);
                isAccept = false;
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child(InstanceVariants.CHILD_SHARE_USER)
                        .child(InstanceVariants.CHILD_WORKING_DRIVER).child(userId).child("customerRequest");
                driverRef.removeValue();
                group1.setVisibility(View.VISIBLE);
                group2.setVisibility(View.GONE);
                isStopTime = true;
                new CountDownTimer(120000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        //here you can have your logic to set text to edittext
                        stopTime = millisUntilFinished / 1000;
                    }

                    public void onFinish() {
                        isStopTime = false;
                    }

                }.start();

            }
        });
        builder.setNegativeButton("Hủy bỏ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

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
        btnCurrentPlace = (Button) getView().findViewById(R.id.btnCurrentPlace2);
        btnCurrentPlace.setOnClickListener(this);

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
                updateUI(3);
            }
        });
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                Log.d(TAG, "onCameraIdle: ");
                updateUI(4);
            }
        });

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the customer_map.
        updateLocationUI();

        // Get the current location of the device and set the position of the customer_map.
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

    private void getRouteToMarker(LatLng pickupLatLng) {
        if (pickupLatLng != null && mLastLocation != null) {
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(false)
                    .waypoints(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), pickupLatLng)
                    .build();
            routing.execute();
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
            polylines.add(polyline);

            Toast.makeText(getActivity().getApplicationContext(), "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRoutingCancelled() {
    }

    private void erasePolylines() {
        for (Polyline line : polylines) {
            line.remove();
        }
        polylines.clear();
    }
}