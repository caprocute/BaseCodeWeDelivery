package com.example.hoang.myapplication.Fragment;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.GeomagneticField;
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
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.example.hoang.myapplication.Adapter.HistoryArrayAdapter;
import com.example.hoang.myapplication.Adapter.ListRequestAdapter;
import com.example.hoang.myapplication.Adapter.RecyclerListAdapter;
import com.example.hoang.myapplication.InstanceVariants;
import com.example.hoang.myapplication.MailBox.ChatActivity;
import com.example.hoang.myapplication.Model.Driver;
import com.example.hoang.myapplication.Model.Request;
import com.example.hoang.myapplication.Model.ShareCustomer;
import com.example.hoang.myapplication.Model.Trip;
import com.example.hoang.myapplication.R;
import com.example.hoang.myapplication.UI.DriverWorkingActivity;
import com.example.hoang.myapplication.UI.MainActivity;
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
import com.google.android.gms.maps.model.LatLngBounds;
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

import org.angmarch.views.NiceSpinner;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DriverWorkingMap extends Fragment implements OnMapReadyCallback, View.OnClickListener, RoutingListener {
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
    private ListRequestAdapter adapter;
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

    private double bearingBetweenLocations(LatLng latLng1, LatLng latLng2) {

        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getActivity().getApplicationContext() != null) {
                    Log.d(TAG, "onLocationResult: tripid=" + tripId);
                    if (!tripId.equals("") && mLastLocation != null && location != null) {
                        rideDistance += mLastLocation.distanceTo(location) / 1000;
                    }
                    Double bearing = bearingBetweenLocations(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()),
                            new LatLng(location.getLatitude(), location.getLongitude()));
                    mLastLocation = location;

                    GeomagneticField geoField;
                    geoField = new GeomagneticField(
                            Double.valueOf(location.getLatitude()).floatValue(),
                            Double.valueOf(location.getLongitude()).floatValue(),
                            Double.valueOf(location.getAltitude()).floatValue(),
                            System.currentTimeMillis()
                    );
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));

                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference refRoot = FirebaseDatabase.getInstance().getReference(InstanceVariants.CHILD_DRIVER_AVAIABLE);
                    DatabaseReference refAvailable;
                    DatabaseReference refBearing = FirebaseDatabase.getInstance().getReference(InstanceVariants.CHILD_BEARING);
                    refBearing.child(userId).setValue(bearing);

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
    private ListView listRequest;
    private ArrayList<Trip> tripArrayList = new ArrayList<>();
    private ArrayList<Request> requestArrayList = new ArrayList<>();
    private ArrayList<LatLng> latLngArrayList = new ArrayList<>();
    private List<Marker> markerList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        View rootView = inflater.inflate(R.layout.driver_working_map, container, false);
        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);
        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getActivity(), null);
        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        //initialize customer_map
        mapView = (MapView) rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        listRequest = (ListView) rootView.findViewById(R.id.listRequest);
        adapter = new ListRequestAdapter(getActivity(), R.layout.item_list_request, requestArrayList);
        adapter.getFilter().filter("");
        listRequest.setAdapter(adapter);
        loadHistory();
        showCurrentPlace();

        listRequest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDailog(requestArrayList.get(position), position);
            }
        });
        return rootView;
    }

    private Dialog dialog;
    private View layout;
    List<String> dataset = new LinkedList<>(Arrays.asList("Đồ nội thất", "Hàng điện tử", "Vật liệu xây dựng", "Gói hàng nhỏ", "Gói hàng lớn", "Đồ ăn", "Khác"));

    private void showDailog(final Request request, final int postion) {
        dialog = new Dialog(getActivity());

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());

        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = inflater.inflate(R.layout.request_dialog, null);

        Button dialogBack = (Button) layout.findViewById(R.id.btnBack);
        Button dialogConfirm = (Button) layout.findViewById(R.id.btnComfirm);
        Button btnSee = (Button) layout.findViewById(R.id.btnSee);
        Button btnDone = (Button) layout.findViewById(R.id.btnDone);

        dialogConfirm.setVisibility(View.GONE);
        btnSee.setVisibility(View.VISIBLE);
        btnDone.setVisibility(View.VISIBLE);

        final EditText txtReceiverName = (EditText) layout.findViewById(R.id.edtTenNguoiNhan);
        final TextView txtDialogName = (TextView) layout.findViewById(R.id.txtDialogName);
        final EditText txtReceiverPhone = (EditText) layout.findViewById(R.id.edtSoDienThoai);
        final EditText txtNote = (EditText) layout.findViewById(R.id.edtGhiChu);
        final NiceSpinner niceSpinner = (NiceSpinner) layout.findViewById(R.id.niceSpinner);


        niceSpinner.attachDataSource(dataset);
        if (request.getUri() != null && !request.getUri().isEmpty())
            for (int i = 0; i < dataset.size(); i++) {
                if (dataset.get(i).equals(request.getUri()))
                    niceSpinner.setSelectedIndex(i);
            }

        dialogConfirm.setVisibility(View.GONE);
        txtDialogName.setText("Thông tin đơn hàng");
        txtNote.setEnabled(false);
        txtReceiverName.setEnabled(false);
        txtReceiverPhone.setEnabled(false);
        niceSpinner.setEnabled(false);

        if (request.isRequestFilled()) {
            txtNote.setText(request.getNote());
            txtReceiverName.setText(request.getReceiverName());
            txtReceiverPhone.setText(request.getReceiverNumber());
        }
        dialogBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialogConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!PhoneNumberUtils.isGlobalPhoneNumber(txtReceiverPhone.getText().toString()))
                    txtReceiverPhone.setError("Số điện thoại không hợp lệ");
                else if (txtReceiverName.getText().toString().isEmpty())
                    txtReceiverName.setError("Không được để trống");
                else if (txtReceiverPhone.getText().toString().isEmpty())
                    txtReceiverPhone.setError("Không được để trống");
                else {
                    request.setReceiverNumber(txtReceiverPhone.getText().toString());
                    request.setReceiverName(txtReceiverName.getText().toString());
                    request.setNote(txtNote.getText().toString());
                    request.setUri(dataset.get(niceSpinner.getSelectedIndex()));
                    dialog.dismiss();
                }
            }
        });
        btnSee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(request.getDestination().latitude,
                                request.getDestination().longitude), DEFAULT_ZOOM));
                dialog.dismiss();
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog(request);
            }
        });
        dialog.setContentView(layout);
        dialog.show();
    }

    public void showAlertDialog(final Request request) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Chú ý!");
        builder.setMessage("Xác nhận bạn đã giao hàng đến tay người nhận.  ");
        builder.setCancelable(false);
        builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                updateRequest(request);
                dialog.dismiss();
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

    private void updateRequest(Request request) {
        request.setStatus("done");
        long time = System.currentTimeMillis();
        request.setTimeDrop(time + "");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(InstanceVariants.CHILD_REQUEST).child(request.getId());
        ref.setValue(request);
        checkIfTripDone(request);
    }

    private void checkIfTripDone(final Request request) {
        Query query = FirebaseDatabase.getInstance().getReference().child(InstanceVariants.CHILD_REQUEST).orderByChild("tripID").equalTo(request.getTripID());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    int dem = 0;
                    boolean check = true;
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        if (dem != 0) {
                            Request request = new Request();
                            Map<String, Object> driverMap = (Map<String, Object>) issue.getValue();
                            if (driverMap.get("status") != null)
                                request.setStatus(driverMap.get("status").toString());
                            if (!request.getStatus().equals("done")) check = false;
                        } else dem++;

                    }
                    if (check) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Chú ý!");
                        builder.setMessage("Đơn hàng " + request.getTripID() + " đã hoàn thành. Hệ thống sẽ tự động thông báo cho khách hàng.");
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

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

                        DatabaseReference ref = FirebaseDatabase.getInstance()
                                .getReference()
                                .child(InstanceVariants.CHILD_TRIPS)
                                .child(request.getTripID()).child("status");
                        ref.setValue("done");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void loadHistory() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference().child(InstanceVariants.CHILD_TRIPS).orderByChild("driverid").equalTo(user.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getChildrenCount() > 0) {
                    tripArrayList.clear();
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        Trip trip = issue.getValue(Trip.class);
                        if (trip.getStatus().equals("working")) {
                            tripArrayList.add(trip);
                        }
                    }
                    requestArrayList.clear();
                    mMap.clear();
                    markerList.clear();
                    latLngArrayList.clear();
                    for (int i = 0; i < tripArrayList.size(); i++) {
                        loadRequest(tripArrayList.get(i).getId());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadRequest(String id) {
        Query query = FirebaseDatabase.getInstance().getReference().child(InstanceVariants.CHILD_REQUEST).orderByChild("tripID").equalTo(id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot != null && dataSnapshot.getChildrenCount() > 0) {
                    int dem = 0;
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        if (dem != 0) {
                            Request request = new Request();
                            Map<String, Object> driverMap = (Map<String, Object>) issue.getValue();
                            if (driverMap.get("id") != null)
                                request.setId(driverMap.get("id").toString());
                            if (driverMap.get("status") != null)
                                request.setStatus(driverMap.get("status").toString());
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
                                double lLat = Double.parseDouble(destination.get("latitude").toString());
                                double lLong = Double.parseDouble(destination.get("longitude").toString());
                                request.setDestination(new LatLng(lLat, lLong));
                            }

                            if (request.getStatus().equals("working")) {
                                Iterator<Request> iterRe = requestArrayList.iterator();
                                while (iterRe.hasNext()) {
                                    Request str = iterRe.next();
                                    if (str.getId().equals(request.getId())) {
                                        iterRe.remove();
                                    }
                                }
                                requestArrayList.add(request);
                                latLngArrayList.add(request.getDestination());
                                addMarker(request);
                            } else {
                                if (markerList != null && requestArrayList != null) {
                                    Iterator<Marker> iter = markerList.iterator();
                                    while (iter.hasNext()) {
                                        Marker str = iter.next();
                                        if (str.getTag() != null && str.getTag().equals(request.getId())) {
                                            str.remove();
                                            iter.remove();
                                        }
                                    }
                                    Iterator<Request> iterRe = requestArrayList.iterator();
                                    while (iterRe.hasNext()) {
                                        Request str = iterRe.next();
                                        if (str.getId().equals(request.getId())) {
                                            iterRe.remove();
                                        }
                                    }
                                }
                            }


                        } else dem++;
                    }
                }
                adapter.notifyDataSetChanged();
                zoomRoute(mMap, latLngArrayList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void zoomRoute(GoogleMap googleMap, List<LatLng> lstLatLngRoute) {

        if (googleMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 100;
        LatLngBounds latLngBounds = boundsBuilder.build();

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));
    }

    private void addMarker(Request request) {
        if (isAdded()) {
            int height = 75;
            int width = 75;
            BitmapDrawable bitmapdraw = new BitmapDrawable();

            bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_marker_destination);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
            LatLng driverLocation = new LatLng(request.getDestination().latitude, request.getDestination().longitude);
            final Marker marker = mMap.addMarker(new MarkerOptions().position(driverLocation).flat(true));
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
            marker.setTag(request.getId());
            markerList.add(marker);
        }
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
            case R.id.btnCurrentPlace2:
                getDeviceLocation();
                break;
        }

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
            }
        });
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                Log.d(TAG, "onCameraIdle: ");
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