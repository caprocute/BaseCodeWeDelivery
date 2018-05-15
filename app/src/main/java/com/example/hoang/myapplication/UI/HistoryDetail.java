package com.example.hoang.myapplication.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hoang.myapplication.Adapter.HistoryDetailAdapter;
import com.example.hoang.myapplication.Adapter.RecyclerListAdapter;
import com.example.hoang.myapplication.InstanceVariants;
import com.example.hoang.myapplication.Model.Account;
import com.example.hoang.myapplication.Model.Driver;
import com.example.hoang.myapplication.Model.Request;
import com.example.hoang.myapplication.Model.Trip;
import com.example.hoang.myapplication.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class HistoryDetail extends AppCompatActivity {
    private Trip mTrip;
    private TextView txtTripId, txtVehicle, txtDriverName, txtTripType, txtCost, txtTitle;
    private ImageView imgDriver;
    private ListView listRe;
    private RatingBar ratingBar3;
    private Button btnSend;
    private Driver mDriver;
    private Account mCustomer;
    private ArrayList<Request> requests = new ArrayList<>();
    private HistoryDetailAdapter adapter;
    Boolean loginMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        mTrip = intent.getParcelableExtra("package");
        getSupportActionBar().setTitle(convertTime(mTrip.getPickupTime()));

        txtDriverName = (TextView) findViewById(R.id.txtDriverName);
        txtTripId = (TextView) findViewById(R.id.txtTripId);
        txtVehicle = (TextView) findViewById(R.id.txtVehicle);
        txtTripType = (TextView) findViewById(R.id.txtTripType);
        txtCost = (TextView) findViewById(R.id.txtCost);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        imgDriver = (ImageView) findViewById(R.id.imgDriver);
        listRe = (ListView) findViewById(R.id.listRe);
        ratingBar3 = (RatingBar) findViewById(R.id.ratingBar3);
        btnSend = (Button) findViewById(R.id.btnSend);

        txtTripId.setText(mTrip.getId());
        String type = mTrip.getDrivingMode();
        if (mTrip.getExpressMode()) type += " - Giao hàng nhanh";
        else type += " - Giao tiết hàng kiệm";
        txtTripType.setText(type);
        txtCost.setText(mTrip.getMoneySum() + " VNĐ");

        ratingBar3.setRating(mTrip.getCustomerRating());
        if (mTrip.getCustomerRating() != 0) ratingBar3.setEnabled(false);
        adapter = new HistoryDetailAdapter(this, R.layout.item_history_requet_detail, requests);
        listRe.setAdapter(adapter);
        loadRequest();
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTrip.getCustomerRating() == 0) {
                    if (ratingBar3.getRating() == 0) {
                        Toast.makeText(HistoryDetail.this, "Vui lòng đánh giá trước khi gửi", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Toast.makeText(HistoryDetail.this, "Đã gửi", Toast.LENGTH_SHORT).show();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(InstanceVariants.CHILD_TRIPS).child(mTrip.getId()).child("customerRating");
                    reference.setValue(ratingBar3.getRating());
                    ratingBar3.setEnabled(false);
                } else {
                    Toast.makeText(HistoryDetail.this, "Bạn đã thực hiện đánh giá rồi", Toast.LENGTH_SHORT).show();
                }
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences(UserTypeActivity.LOGIN_MODE, Context.MODE_PRIVATE);
        loginMode = sharedPreferences.getBoolean(UserTypeActivity.LOGIN_MODE, true);
        if (loginMode) {
            txtTitle.setText("Thông tin tài xế");
            loadDriverData();
        } else {
            loadCustomerData();
            txtTitle.setText("Thông tin khách hàng");
        }
    }

    private void loadDriverData() {
        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference()
                .child(InstanceVariants.CHILD_SHARE_USER).child(InstanceVariants.CHILD_WORKING_DRIVER).child(mTrip.getDriverid());
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    mDriver = dataSnapshot.getValue(Driver.class);
                    txtDriverName.setText(mDriver.getmName());
                    txtVehicle.setText(mDriver.getmCar());
                    if (mDriver.getmProfileImageUrl() != null || !mDriver.getmProfileImageUrl().isEmpty())
                        Glide.with(HistoryDetail.this).load(mDriver.getmProfileImageUrl()).into(imgDriver);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void loadCustomerData() {
        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference()
                .child(InstanceVariants.CHILD_SHARE_USER).child(InstanceVariants.CHILD_WORKING_CUSTOMER).child(mTrip.getCustomerid());
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    mCustomer = dataSnapshot.getValue(Account.class);
                    txtDriverName.setText(mCustomer.getFirst_name() + " " + mCustomer.getLast_name());
                    txtVehicle.setText(mCustomer.getPhone());
                    if (mCustomer.getAvartar() != null || !mCustomer.getAvartar().isEmpty())
                        Glide.with(HistoryDetail.this).load(mCustomer.getAvartar()).into(imgDriver);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void loadRequest() {
        Query query = FirebaseDatabase.getInstance().getReference().child(InstanceVariants.CHILD_REQUEST).orderByChild("tripID").equalTo(mTrip.getId());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    requests.clear();
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
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
                        requests.add(request);

                    }
                    adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private String convertTime(long number) {
        // or you already have long value of date, use this instead of milliseconds variable.
        String dateString = DateFormat.format("yyyy-MM-dd hh:mm:ss a", new Date(number)).toString();
        return dateString;
    }
}
