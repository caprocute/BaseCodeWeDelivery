package com.example.hoang.myapplication.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hoang.myapplication.InstanceVariants;
import com.example.hoang.myapplication.Model.Account;
import com.example.hoang.myapplication.Model.Driver;
import com.example.hoang.myapplication.Model.ShareCustomer;
import com.example.hoang.myapplication.R;
import com.example.hoang.myapplication.Test.DriverSettingsActivity;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.roger.catloadinglibrary.CatLoadingView;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserTypeActivity extends AppCompatActivity implements View.OnClickListener {
    private Button loginUser, loginDriver;
    private CircleImageView avatar;
    private TextView txtName;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Account userData;
    private final String CHILD_ACCOUNT = "ACCOUNT";
    private StorageReference mStorageRef;
    private File localFile;
    public static String LOGIN_MODE = "LOGIN_MODE";
    private CatLoadingView mView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type);

        mView = new CatLoadingView();
        mView.setCanceledOnTouchOutside(false);
        mView.show(getSupportFragmentManager(), "Loading your data");
        loginUser = (Button) findViewById(R.id.loginCustomer);
        loginDriver = (Button) findViewById(R.id.loginDriver);
        avatar = (CircleImageView) findViewById(R.id.profileImage);
        txtName = (TextView) findViewById(R.id.txtName);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        final DatabaseReference root = FirebaseDatabase.getInstance().getReference().child(InstanceVariants.CHILD_ACCOUNT);
        DatabaseReference userRoot = root.child(user.getUid());
        loginDriver.setOnClickListener(this);
        loginUser.setOnClickListener(this);
        userRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userData = dataSnapshot.getValue(Account.class);
                    loadData(userData);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadData(Account account) {
        mView.dismiss();
        txtName.setText(account.getFirst_name() + " " + account.getLast_name());
        loadAvatar();
    }

    private void loadAvatar() {
        StorageReference riversRef = mStorageRef.child("AVATAR/" + user.getUid() + ".jpg");
        Glide.with(this /* context */)
                .using(new FirebaseImageLoader())
                .load(riversRef)
                .into(avatar);
    }

    @Override
    public void onClick(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences(LOGIN_MODE, Context.MODE_PRIVATE);
        switch (view.getId()) {
            case R.id.loginCustomer:

                loadCustomerAccount();
                break;
            case R.id.loginDriver:
                loadDriverAccount();
                break;
        }
    }

    private Boolean loadCustomerAccount() {
        DatabaseReference refRoot = FirebaseDatabase.getInstance().getReference(InstanceVariants.CHILD_SHARE_USER);
        final DatabaseReference refCusRoof = refRoot.child(InstanceVariants.CHILD_ACCOUNT_CUSTOMERS);

        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        refCusRoof.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    SharedPreferences sharedPreferences = getSharedPreferences(LOGIN_MODE, Context.MODE_PRIVATE);
                    Intent intent = new Intent(UserTypeActivity.this, MainActivity.class);
                    intent.putExtra("logintype", 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(LOGIN_MODE, true);
                    editor.commit();
                    finish();
                    startActivity(intent);
                    startActivity(intent);

                } else {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference(InstanceVariants.CHILD_ACCOUNT);
                    ref.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                Account account = dataSnapshot.getValue(Account.class);
                                ShareCustomer shareCustomer = new ShareCustomer();
                                shareCustomer.setAvartar(account.getAvartar());
                                shareCustomer.setFirst_name(account.getFirst_name());
                                shareCustomer.setId(account.getId());
                                shareCustomer.setLast_name(account.getLast_name());
                                shareCustomer.setPhone(account.getPhone());
                                shareCustomer.setRating(account.getRating());
                                shareCustomer.setTrip_accept(account.getTrip_accept());
                                shareCustomer.setTrip_cancel(account.getTrip_cancel());
                                shareCustomer.setTrip_count(account.getTrip_count());
                                refCusRoof.child(userId).setValue(shareCustomer);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return true;

    }

    private Driver mDriver;

    private boolean loadDriverAccount() {
        DatabaseReference refRoot = FirebaseDatabase.getInstance().getReference(InstanceVariants.CHILD_SHARE_USER);
        DatabaseReference refDriverRoof = refRoot.child(InstanceVariants.CHILD_ACCOUNT_DRIVERS);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        refDriverRoof.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    SharedPreferences sharedPreferences = getSharedPreferences(LOGIN_MODE, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Intent intent = new Intent(UserTypeActivity.this, MainActivity.class);
                    intent.putExtra("logintype", 1);
                    editor = sharedPreferences.edit();
                    editor.putBoolean(LOGIN_MODE, false);
                    editor.commit();
                    finish();
                    startActivity(intent);

                } else {
                    Intent intent = new Intent(UserTypeActivity.this, DriverSettingsActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return true;
    }
}
