package com.example.hoang.myapplication.UI;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hoang.myapplication.Adapter.FavoriteDriverAdapter;
import com.example.hoang.myapplication.InstanceVariants;
import com.example.hoang.myapplication.Model.Driver;
import com.example.hoang.myapplication.Model.Gift;
import com.example.hoang.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.roger.catloadinglibrary.CatLoadingView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FavoriteDriverActivity extends AppCompatActivity {
    private ListView listView;
    private EditText edt;
    private ImageView img;
    private ArrayList<Driver> drivers = new ArrayList<>();
    private FavoriteDriverAdapter adapter;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_driver);
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

        listView = (ListView) findViewById(R.id.lst_favotite_driver);
        edt = (EditText) findViewById(R.id.editPhone);
        img = (ImageView) findViewById(R.id.imgfind);
        adapter = new FavoriteDriverAdapter(this, R.layout.item_favorite_driver, drivers);
        listView.setAdapter(adapter);

        loadListDriver();

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PhoneNumberUtils.isGlobalPhoneNumber(edt.getText().toString())) {
                    loadDriverData(edt.getText().toString());
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDriverDialog(position);
            }
        });

    }

    Dialog dialog;

    private void showDriverDialog(final int position) {
        dialog = new Dialog(this);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_detail_driver, null);

        Button dialogBack = (Button) layout.findViewById(R.id.back);
        Button dialogMake = (Button) layout.findViewById(R.id.btnMake);
        Button dialogDelete = (Button) layout.findViewById(R.id.btnXoa);
        FloatingActionButton call = (FloatingActionButton) layout.findViewById(R.id.btncall);
        FloatingActionButton sms = (FloatingActionButton) layout.findViewById(R.id.btnsms);

        final EditText txtReceiverName = (EditText) layout.findViewById(R.id.name);
        final EditText txtReceiverPhone = (EditText) layout.findViewById(R.id.phone);
        final EditText txtCar = (EditText) layout.findViewById(R.id.car);

        RatingBar ratingBar = (RatingBar) layout.findViewById(R.id.ratingBar2);
        ratingBar.setRating(drivers.get(position).getRating());

        txtCar.setText(drivers.get(position).getmCar().toString());
        txtReceiverName.setText(drivers.get(position).getmName().toString());
        txtReceiverPhone.setText(drivers.get(position).getmPhone().toString());

        ImageView imgPic = (ImageView) layout.findViewById(R.id.profileImage);
        if (drivers.get(position).getmProfileImageUrl() != null && !drivers.get(position).getmProfileImageUrl().isEmpty()) {
            Glide.with(this).load(drivers.get(position).getmProfileImageUrl()).into(imgPic);
        }
        dialogBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialogMake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FavoriteDriverActivity.this, MainActivity.class);
                intent.putExtra("package", drivers.get(position));
                startActivity(intent);
                finish();
                dialog.dismiss();
            }
        });
        dialogDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog(position);
            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                // Send phone number to intent as data
                intent.setData(Uri.parse("tel:" + drivers.get(position).getmPhone()));
                // Start the dialer app activity with number
                startActivity(intent);

            }
        });
        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse("sms:" + drivers.get(position).getmPhone()));
                startActivity(sendIntent);
            }
        });
        dialog.setContentView(layout);
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void loadListDriver() {
        final CatLoadingView mView = new CatLoadingView();
        mView.setCanceledOnTouchOutside(false);
        mView.show(getSupportFragmentManager(), "Loading your data");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(InstanceVariants.CHILD_FAVORITE).child(user.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && dataSnapshot.getChildrenCount() > 0) {
                    ArrayList<String> strings = new ArrayList<>();
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        strings.add(issue.getKey());
                    }
                    drivers.clear();

                    for (String item : strings) {
                        loadDriverDetail(item);
                    }
                    mView.dismiss();
                }
                mView.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadDriverDetail(final String userid) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child(InstanceVariants.CHILD_SHARE_USER).child(InstanceVariants.CHILD_ACCOUNT_DRIVERS).child(userid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Driver driver = dataSnapshot.getValue(Driver.class);
                            driver.setUserID(userid);
                            drivers.add(driver);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void loadDriverData(final String phone) {
        final CatLoadingView mView = new CatLoadingView();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child(InstanceVariants.CHILD_SHARE_USER).child(InstanceVariants.CHILD_ACCOUNT_DRIVERS).orderByChild("mPhone").equalTo(phone);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        Driver driver = issue.getValue(Driver.class);
                        driver.setUserID(issue.getKey());
                        if (driver.equals(phone)) drivers.add(driver);
                        adapter.notifyDataSetChanged();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(InstanceVariants.CHILD_FAVORITE).child(user.getUid());
                        ref.child(driver.getUserID()).setValue(true);

                        return;
                    }
                } else
                    Toast.makeText(FavoriteDriverActivity.this, "Không tìm thấy tài xế phù hợp", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void showAlertDialog(final int pot) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chú ý!");
        builder.setMessage("Xóa tài xế này ra khỏi danh sách ưa thích của bạn? ");
        builder.setCancelable(false);
        builder.setPositiveButton("Đổng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                        .child(InstanceVariants.CHILD_FAVORITE)
                        .child(user.getUid()).child(drivers.get(pot).getUserID());
                reference.removeValue();
                dialogInterface.dismiss();
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
}
