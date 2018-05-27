package com.example.hoang.myapplication.Test;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bumptech.glide.Glide;
import com.example.hoang.myapplication.InstanceVariants;
import com.example.hoang.myapplication.Model.Account;
import com.example.hoang.myapplication.Model.Driver;
import com.example.hoang.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DriverSettingsActivity extends AppCompatActivity {

    private EditText mNameField, mPhoneField, mCarField;

    private Button mBack, mConfirm;

    private ImageView mProfileImage;

    private FirebaseAuth mAuth;
    private DatabaseReference mDriverDatabase;

    private String userID;
    private String mName;
    private String mPhone;
    private String mCar;
    private String mService;
    private String mProfileImageUrl;

    private String resultUri;
    private Account account;
    private Boolean isFirst = false;
    private RadioGroup mRadioGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_settings);


        mNameField = (EditText) findViewById(R.id.name);
        mPhoneField = (EditText) findViewById(R.id.phone);
        mCarField = (EditText) findViewById(R.id.car);

        mProfileImage = (ImageView) findViewById(R.id.profileImage);

        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        mBack = (Button) findViewById(R.id.back);
        mConfirm = (Button) findViewById(R.id.confirm);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mDriverDatabase = FirebaseDatabase.getInstance().getReference().child(InstanceVariants.CHILD_SHARE_USER).child(InstanceVariants.CHILD_ACCOUNT_DRIVERS).child(userID);

        getUserInfo();

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInformation();
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                return;
            }
        });
    }

    private void getUserInfo() {
        mDriverDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Driver driver = dataSnapshot.getValue(Driver.class);
                    mNameField.setText(driver.getmName().toString());
                    mPhoneField.setText(driver.getmPhone().toString());
                    mCarField.setText(driver.getmCar().toString());
                    mService = driver.getmService();
                    resultUri=driver.getmProfileImageUrl();
                    switch (mService) {
                        case "HereBike":
                            mRadioGroup.check(R.id.UberX);
                            break;
                        case "HereCar":
                            mRadioGroup.check(R.id.UberBlack);
                            break;
                    }

                    Glide.with(getApplication()).load(driver.getmProfileImageUrl()).into(mProfileImage);
                } else

                    loadFromAccount();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void loadFromAccount() {
        isFirst = true;
        DatabaseReference mAccountDatabase = FirebaseDatabase.getInstance().getReference().child(InstanceVariants.CHILD_ACCOUNT).child(userID);
        mAccountDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    account = dataSnapshot.getValue(Account.class);
                    mNameField.setText(account.getFirst_name());
                    mPhoneField.setText(account.getPhone());
                    Glide.with(getApplication()).load(account.getAvartar()).into(mProfileImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void saveUserInformation() {
        if (mNameField.getText().toString().isEmpty()) {
            mNameField.setError(getString(R.string.not_null));
            return;
        }
        if (mCarField.getText().toString().isEmpty()) {
            mCarField.setError(getString(R.string.not_null));
            return;
        }
        if (mPhoneField.getText().toString().isEmpty()) {
            mPhoneField.setError(getString(R.string.not_null));
            return;
        }
        Driver driver = new Driver();
        driver.setmProfileImageUrl(resultUri);
        driver.setmName(mNameField.getText().toString());
        driver.setmPhone(mPhoneField.getText().toString());
        driver.setmCar(mCarField.getText().toString());
        driver.setUserID(userID);
        if (isFirst) driver.setmProfileImageUrl(account.getAvartar());
        int selectId = mRadioGroup.getCheckedRadioButtonId();

        final RadioButton radioButton = (RadioButton) findViewById(selectId);

        if (radioButton.getText() == null) {
            return;
        }

        driver.setmService(radioButton.getText().toString());

        mDriverDatabase.setValue(driver);
        finish();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
