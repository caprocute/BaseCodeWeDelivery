package com.example.hoang.myapplication.UI;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hoang.myapplication.Model.Account;
import com.example.hoang.myapplication.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.roger.catloadinglibrary.CatLoadingView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import me.echodev.resizer.Resizer;

public class UserDetailActivity extends AppCompatActivity implements TextWatcher {
    private ImageView imgProfile, imgChangeProfile;
    private final String CHILD_ACCOUNT = "ACCOUNT";
    private EditText edtName, edtEmail, edtPhone;
    private Button btnSave;
    final DatabaseReference root = FirebaseDatabase.getInstance().getReference().child(CHILD_ACCOUNT);
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Uri resultUri;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

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

        imgChangeProfile = (ImageView) findViewById(R.id.imgChangeProfile);
        imgProfile = (ImageView) findViewById(R.id.imgProfile);
        edtName = (EditText) findViewById(R.id.edtName);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        btnSave = (Button) findViewById(R.id.btnSave);
        initFirebaseData();
        loadUserData();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
    }

    private void saveData() {
        if (edtName.getText().toString().isEmpty()) {
            edtName.setError("Không được để trống");
            return;
        }
        if (edtEmail.getText().toString().isEmpty()) {
            edtEmail.setError("Không được để trống");
            return;
        }
        if (resultUri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        uploadImage();
    }

    private void uploadImage() {
        try {
            File resizedImage = new Resizer(this)
                    .setTargetLength(1080)
                    .setQuality(50)
                    .setOutputFormat("JPEG")
                    .setOutputFilename("resized_image")
                    .setOutputDirPath(resultUri + "copy")
                    .setSourceImage(new File(resultUri.getPath()))
                    .getResizedFile();

            if (resizedImage.getPath() != null) {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading...");
                progressDialog.show();

                StorageReference ref = FirebaseStorage.getInstance().getReference().child("AVATAR/" + user.getUid());
                ref.putFile(Uri.parse(resizedImage.getPath()))
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                Toast.makeText(UserDetailActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(UserDetailActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                        .getTotalByteCount());
                                progressDialog.setMessage("Uploaded " + (int) progress + "%");
                            }
                        });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void checkStatusChange() {
        if (!edtEmail.getText().equals(user.getEmail())) {
            btnSave.setEnabled(true);
            return;
        }
        if (!edtName.getText().equals(userData.getFirst_name() + " " + userData.getLast_name())) {
            btnSave.setEnabled(true);
            return;
        }
        if (resultUri != null && !resultUri.toString().isEmpty()) {
            btnSave.setEnabled(true);
            return;
        }
        btnSave.setEnabled(false);
    }

    private void initFirebaseData() {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    Account userData;

    private void loadUserData() {
        loadAvatar();
        DatabaseReference userRoot = root.child(user.getUid());
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

    private void loadAvatar() {
        StorageReference riversRef = mStorageRef.child("AVATAR/" + user.getUid() + ".jpg");
        Glide.with(this /* context */)
                .using(new FirebaseImageLoader())
                .load(riversRef)
                .into(imgProfile);
    }

    private void loadData(Account data) {
        if (data != null) {
            edtName.setText(data.getLast_name() + " " + data.getFirst_name());
            edtPhone.setText(data.getPhone());
            edtEmail.setText(user.getEmail());
            imgChangeProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, 1);
                }
            });
            edtName.addTextChangedListener(this);
            edtEmail.addTextChangedListener(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            Glide.with(this /* context */)
                    .load(imageUri)
                    .into(imgProfile);
            checkStatusChange();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        checkStatusChange();
    }

    @Override
    public void afterTextChanged(Editable s) {
        checkStatusChange();

    }
}
