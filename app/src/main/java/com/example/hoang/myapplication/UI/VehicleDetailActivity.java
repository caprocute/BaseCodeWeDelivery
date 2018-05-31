package com.example.hoang.myapplication.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hoang.myapplication.DataProduce;
import com.example.hoang.myapplication.InstanceVariants;
import com.example.hoang.myapplication.MailBox.ChatActivity;
import com.example.hoang.myapplication.Model.Vehicle;
import com.example.hoang.myapplication.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class VehicleDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edtOwnerName, edtBrand, edtColor, edtPlate;
    private ImageView imgVehicleImage, imgVehicleCer, imgDelete;
    private Button btnBack, btnSave;
    private RadioGroup groupVehicleType;
    private RadioButton radMotor, radCar;
    private Uri resultVehicleImage, resultVehicleCer;
    private String mDeviceId;
    private Vehicle mVehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_detail);

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

        initVariants();
        mDeviceId = getIntent().getStringExtra("id");
        if (mDeviceId != null && !mDeviceId.isEmpty()) loadData(mDeviceId);
        else imgDelete.setVisibility(View.GONE);
    }

    private void loadData(final String mDeviceId) {
        DatabaseReference deviceRef = FirebaseDatabase.getInstance().getReference().child(InstanceVariants.CHILD_VEHICLE).child(mDeviceId);
        deviceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mVehicle = dataSnapshot.getValue(Vehicle.class);
                    mVehicle.setId(mDeviceId);
                    extractData(mVehicle);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void extractData(Vehicle mVehicle) {
        edtBrand.setText(mVehicle.getBrand());
        edtOwnerName.setText(mVehicle.getOwnerName());
        edtColor.setText(mVehicle.getColor());
        edtPlate.setText(mVehicle.getPlate());

        if (mVehicle.getVehicleType().equals("Xe máy")) radMotor.setChecked(true);
        else radCar.setChecked(true);

        if (mVehicle.getImgVehicle() != null && !mVehicle.getImgVehicle().isEmpty())
            Glide.with(this /* context */)
                    .load(mVehicle.getImgVehicle())
                    .into(imgVehicleImage);

        if (mVehicle.getImgVehicleCer() != null && !mVehicle.getImgVehicleCer().isEmpty())
            Glide.with(this /* context */)
                    .load(mVehicle.getImgVehicleCer())
                    .into(imgVehicleCer);
        resultVehicleImage = Uri.parse(mVehicle.getImgVehicle());
        resultVehicleCer = Uri.parse(mVehicle.getImgVehicleCer());

    }

    private void initVariants() {
        edtOwnerName = (EditText) findViewById(R.id.edtOwnerName);
        edtBrand = (EditText) findViewById(R.id.edtBrand);
        edtColor = (EditText) findViewById(R.id.edtColor);
        edtPlate = (EditText) findViewById(R.id.edtPlate);
        imgVehicleImage = (ImageView) findViewById(R.id.imgVehicleImage);
        imgDelete = (ImageView) findViewById(R.id.imgDelete);
        imgVehicleCer = (ImageView) findViewById(R.id.imgVehicleCer);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnSave = (Button) findViewById(R.id.btnSave);
        groupVehicleType = (RadioGroup) findViewById(R.id.groupVehicleType);
        radMotor = (RadioButton) findViewById(R.id.radMotor);
        radCar = (RadioButton) findViewById(R.id.radCar);

        imgVehicleImage.setOnClickListener(this);
        imgVehicleCer.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        imgDelete.setOnClickListener(this);

        radMotor.setChecked(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.radMotor:
                break;
            case R.id.radCar:
                break;
            case R.id.imgVehicleImage:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
                break;
            case R.id.imgVehicleCer:
                intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 2);
                break;
            case R.id.btnBack:
                onBackPressed();
                break;
            case R.id.btnSave:
                saveData();
                break;
            case R.id.imgDelete:
                showAlertDialog();
                break;
        }
    }

    public void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chú ý!");
        builder.setMessage("Xóa thông tin phương tiện này? ");
        builder.setCancelable(false);
        builder.setPositiveButton("Đổng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (mDeviceId != null && !mDeviceId.isEmpty()) {
                    FirebaseDatabase.getInstance().getReference()
                            .child(InstanceVariants.CHILD_VEHICLE)
                            .child(mDeviceId)
                            .removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    Toast.makeText(VehicleDetailActivity.this, "Đã xóa", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            });
                }
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

    private void saveData() {
        if (edtBrand.getText().toString().isEmpty()) {
            edtBrand.setError("Không được để trống");
            return;
        }
        if (edtColor.getText().toString().isEmpty()) {
            edtBrand.setError("Không được để trống");
            return;
        }
        if (edtOwnerName.getText().toString().isEmpty()) {
            edtBrand.setError("Không được để trống");
            return;
        }
        if (edtPlate.getText().toString().isEmpty()) {
            edtBrand.setError("Không được để trống");
            return;
        }
        if (resultVehicleCer == null) {
            Toast.makeText(VehicleDetailActivity.this, "Vui lòng cung cấp hình ảnh giấy tờ phương tiện", Toast.LENGTH_LONG);
            return;
        }
        if (resultVehicleImage == null) {
            Toast.makeText(VehicleDetailActivity.this, "Vui lòng cung cấp hình ảnh của phương tiện", Toast.LENGTH_LONG);
            return;
        }

        Vehicle newVehicle = new Vehicle();
        newVehicle.setBrand(edtBrand.getText().toString());
        newVehicle.setColor(edtColor.getText().toString());
        newVehicle.setOwnerName(edtOwnerName.getText().toString());
        newVehicle.setPlate(edtPlate.getText().toString());
        if (mDeviceId != null) newVehicle.setId(mDeviceId);
        int selectId = groupVehicleType.getCheckedRadioButtonId();
        final RadioButton radioButton = (RadioButton) findViewById(selectId);
        if (radioButton.getText() == null) {
            return;
        }
        newVehicle.setVehicleType(radioButton.getText().toString());

        DatabaseReference deviceRef = FirebaseDatabase.getInstance().getReference().child(InstanceVariants.CHILD_VEHICLE);
        DataProduce dataProduce = new DataProduce();

        StorageReference refVehicle = FirebaseStorage.getInstance().getReference().child(InstanceVariants.CHILD_VEHICLE);

        if (mVehicle == null) {
            newVehicle.setId(deviceRef.push().getKey());
            newVehicle.setOwnerId(FirebaseAuth.getInstance().getUid());

            deviceRef.child(newVehicle.getId()).setValue(newVehicle);
            StorageReference refVehicleImage = refVehicle.child(newVehicle.getId() + "a");
            dataProduce.uploadImage(VehicleDetailActivity.this,
                    resultVehicleImage,
                    refVehicleImage,
                    deviceRef.child(newVehicle.getId()).child("imgVehicle"));

            StorageReference refVehicleCer = refVehicle.child(newVehicle.getId() + "b");
            dataProduce.uploadImage(VehicleDetailActivity.this,
                    resultVehicleCer,
                    refVehicleCer,
                    deviceRef.child(newVehicle.getId()).child("imgVehicleCer"));

            return;
        }
        newVehicle.setOwnerId(FirebaseAuth.getInstance().getUid());
        if (mVehicle != newVehicle) {

            deviceRef.child(mVehicle.getId()).setValue(newVehicle);


            StorageReference refVehicleImage = refVehicle.child(mVehicle.getId() + "a");

            if (!mVehicle.getImgVehicleCer().equals(resultVehicleCer))
                dataProduce.uploadImage(VehicleDetailActivity.this,
                        resultVehicleCer,
                        refVehicleImage,
                        deviceRef.child(newVehicle.getId()).child("imgVehicleCer"));

            StorageReference refVehicleCer = refVehicle.child(mVehicle.getId() + "b");
            if (!mVehicle.getImgVehicle().equals(resultVehicleImage))
                dataProduce.uploadImage(VehicleDetailActivity.this,
                        resultVehicleImage,
                        refVehicleCer,
                        deviceRef.child(newVehicle.getId()).child("imgVehicle"));

            return;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            final Uri imageUri = data.getData();
            resultVehicleImage = imageUri;
            Glide.with(this /* context */)
                    .load(imageUri)
                    .into(imgVehicleImage);
        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            final Uri imageUri = data.getData();
            resultVehicleCer = imageUri;
            Glide.with(this /* context */)
                    .load(imageUri)
                    .into(imgVehicleCer);
        }
    }
}
