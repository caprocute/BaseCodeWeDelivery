package com.example.hoang.myapplication.UI;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hoang.myapplication.InstanceVariants;
import com.example.hoang.myapplication.Model.Account;
import com.example.hoang.myapplication.Model.Driver;
import com.example.hoang.myapplication.Model.Vehicle;
import com.example.hoang.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.NiceSpinnerAdapter;

import java.util.ArrayList;
import java.util.List;

public class DriverSettingsActivity extends AppCompatActivity {

    private EditText mNameField, mPhoneField;
    private NiceSpinner mCarField;

    private Button mBack, mConfirm;

    private ImageView mProfileImage, buttonBack, imgAddVehicle, imgEdit;

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
    private boolean mode;
    private List<String> vehicleName = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ArrayList<Vehicle> vehicleArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_settings);

        mode = getIntent().getBooleanExtra("mode", false);

        mNameField = (EditText) findViewById(R.id.name);
        mPhoneField = (EditText) findViewById(R.id.phone);
        mCarField = (NiceSpinner) findViewById(R.id.car);

        mProfileImage = (ImageView) findViewById(R.id.profileImage);
        imgAddVehicle = (ImageView) findViewById(R.id.imgAddVehicle);
        imgEdit = (ImageView) findViewById(R.id.imgEdit);

        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        mBack = (Button) findViewById(R.id.back);
        mConfirm = (Button) findViewById(R.id.confirm);
        buttonBack = (ImageView) findViewById(R.id.btnBackToolBar2);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mDriverDatabase = FirebaseDatabase.getInstance().getReference().child(InstanceVariants.CHILD_SHARE_USER).child(InstanceVariants.CHILD_ACCOUNT_DRIVERS).child(userID);

        if (vehicleName.isEmpty()) vehicleName.add("Không có phương tiện nào");
        adapter = new ArrayAdapter(DriverSettingsActivity.this, android.R.layout.simple_spinner_item, vehicleName);
        mCarField.setAdapter(adapter);
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
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                return;
            }
        });
        imgAddVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DriverSettingsActivity.this, VehicleDetailActivity.class);
                startActivity(intent);
                return;
            }
        });
        imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DriverSettingsActivity.this, VehicleDetailActivity.class);
                int pos = mCarField.getSelectedIndex();
                intent.putExtra("id", vehicleArrayList.get(pos).getId());
                startActivity(intent);
            }
        });
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.UberX:
                        loadVehicleData("Xe máy");
                        break;
                    case R.id.UberBlack:
                        loadVehicleData("Ô tô");
                        break;
                }
            }
        });


    }
    @Override
    public void onResume(){
        super.onResume();
        loadVehicleData("");
    }
    private void updateUI() {
        mNameField.setEnabled(mode);
        mCarField.setEnabled(mode);
        mNameField.setEnabled(mode);
        mRadioGroup.setEnabled(mode);
    }

    private void getUserInfo() {
        mDriverDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    imgEdit.setEnabled(true);
                    Driver driver = dataSnapshot.getValue(Driver.class);
                    mNameField.setText(driver.getmName().toString());
                    mPhoneField.setText(driver.getmPhone().toString());
                    mCarField.setText(driver.getmCar().toString());
                    mService = driver.getmService();
                    resultUri = driver.getmProfileImageUrl();
                    switch (mService) {
                        case "HereBike":
                            mRadioGroup.check(R.id.UberX);
                            if (driver.getmCar() != null && !driver.getmCar().isEmpty())
                                loadVehicleData("Xe máy");
                            break;
                        case "HereCar":
                            mRadioGroup.check(R.id.UberBlack);
                            if (driver.getmCar() != null && !driver.getmCar().isEmpty())
                                loadVehicleData("Ô tô");
                            break;
                    }

                    Glide.with(getApplication()).load(driver.getmProfileImageUrl()).into(mProfileImage);
                } else {
                    imgEdit.setEnabled(false);
                    loadFromAccount();
                    loadVehicleData("Xe máy");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private void loadVehicleData(final String type) {
        if (userID == null | userID.isEmpty()) return;
        Query query = FirebaseDatabase.getInstance().getReference().child(InstanceVariants.CHILD_VEHICLE).orderByChild("ownerId").equalTo(userID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    vehicleArrayList.clear();
                    vehicleName.clear();
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        Vehicle vehicle = issue.getValue(Vehicle.class);
                        vehicleArrayList.add(vehicle);
                        vehicleName.add(vehicle.getBrand() + " - " + vehicle.getPlate());
                    }
                    adapter = new ArrayAdapter(DriverSettingsActivity.this, android.R.layout.simple_spinner_item, vehicleName);
                    mCarField.setAdapter(adapter);
                    imgEdit.setEnabled(true);
                } else imgEdit.setEnabled(false);
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
