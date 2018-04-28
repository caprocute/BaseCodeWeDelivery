package com.example.hoang.myapplication.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hoang.myapplication.Model.Account;
import com.example.hoang.myapplication.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type);
        loginUser = (Button) findViewById(R.id.loginCustomer);
        loginDriver = (Button) findViewById(R.id.loginDriver);
        avatar = (CircleImageView) findViewById(R.id.profile_image);
        txtName = (TextView) findViewById(R.id.txtName);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        final DatabaseReference root = FirebaseDatabase.getInstance().getReference().child(CHILD_ACCOUNT);
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
        switch (view.getId()) {
            case R.id.loginCustomer:
                Intent intent = new Intent(UserTypeActivity.this, MainActivity.class);
                intent.putExtra("logintype", 0);
                startActivity(intent);
            case R.id.loginDriver:
                intent = new Intent(UserTypeActivity.this, MainActivity.class);
                intent.putExtra("logintype", 1);
                startActivity(intent);
                break;
        }
    }
}
