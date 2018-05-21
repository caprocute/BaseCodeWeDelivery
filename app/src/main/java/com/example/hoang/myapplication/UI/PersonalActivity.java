package com.example.hoang.myapplication.UI;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import com.roger.catloadinglibrary.CatLoadingView;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonalActivity extends AppCompatActivity implements View.OnClickListener {
    private final String CHILD_ACCOUNT = "ACCOUNT";

    private ImageView buttonBack, buttonEdit;
    private TextView title;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private StorageReference mStorageRef;
    private CatLoadingView mView;
    final DatabaseReference root = FirebaseDatabase.getInstance().getReference().child(CHILD_ACCOUNT);
    private CircleImageView imgProfile;
    private TextView txtName, txtEmail, txtPhone, txtRating, txtCount, txtAcceptRate, txtCancelRate;
    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        buttonBack = (ImageView) findViewById(R.id.btnBackToolBar);
        buttonEdit = (ImageView) findViewById(R.id.btnEdit);
        imgProfile = (CircleImageView) findViewById(R.id.imgProfile);
        title = (TextView) findViewById(R.id.txtTitleToolbar);
        txtAcceptRate = (TextView) findViewById(R.id.txtTripAccept);
        txtCancelRate = (TextView) findViewById(R.id.txtTripCancel);
        txtCount = (TextView) findViewById(R.id.txtTripCount);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtName = (TextView) findViewById(R.id.txtName);
        txtPhone = (TextView) findViewById(R.id.txtPhoneNumber);
        txtRating = (TextView) findViewById(R.id.txtRating);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        title.setText("Thông tin cá nhân");
        buttonBack.setOnClickListener(this);
        buttonEdit.setOnClickListener(this);
        initFirebaseData();
        loadUserData();
    }

    private void initFirebaseData() {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    private void loadUserData() {
        mView = new CatLoadingView();
        mView.setCanceledOnTouchOutside(false);
        mView.show(getSupportFragmentManager(), "Loading your data");
        loadAvatar();
        DatabaseReference userRoot = root.child(user.getUid());
        userRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Account userData = dataSnapshot.getValue(Account.class);
                    loadData(userData);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadData(Account data) {
        if (data != null) {
            if (data.getTrip_count() != 0) {
                float cRate = Math.round((float) data.getTrip_cancel() / (float) data.getTrip_count()*100);
                float aRate = Math.round((float) data.getTrip_accept() / (float) data.getTrip_count()*100);
                txtCancelRate.setText(cRate + "%");
                txtAcceptRate.setText(aRate + "%");
            }
            txtName.setText(data.getLast_name() + " " + data.getFirst_name());
            txtPhone.setText(data.getPhone());
            txtEmail.setText(data.getEmail());
            txtCount.setText(data.getTrip_count() + " " + getString(R.string.count_text));
            ratingBar.setMax(5);
            ratingBar.setEnabled(false);
            ratingBar.setRating(data.getRating());
            if (data.getRating() <= 2) {
                txtRating.setText(R.string.rating_2_star);
            } else if (data.getRating() <= 3) {
                txtRating.setText(R.string.rating_3_star);
            } else if (data.getRating() <= 4) {
                txtRating.setText(R.string.rating_4_star);
            } else if (data.getRating() <= 5) {
                txtRating.setText(R.string.rating_5_star);
            }
            mView.dismiss();
        }
    }

    private void loadAvatar() {
        StorageReference riversRef = mStorageRef.child("AVATAR/" + user.getUid() + ".jpg");
        Glide.with(this /* context */)
                .using(new FirebaseImageLoader())
                .load(riversRef)
                .into(imgProfile);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.btnBackToolBar:
                onBackPressed();
                break;
        }
    }

}
