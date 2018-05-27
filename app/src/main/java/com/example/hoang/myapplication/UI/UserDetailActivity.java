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
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hoang.myapplication.Aunthencation.SignUpActivity;
import com.example.hoang.myapplication.Aunthencation.VerifyInformation;
import com.example.hoang.myapplication.DataProduce;
import com.example.hoang.myapplication.InstanceVariants;
import com.example.hoang.myapplication.Model.Account;
import com.example.hoang.myapplication.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.auth.UserInfo;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
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
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount mGoogleSignInAccount;
    private ProgressDialog progressDialog;

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
        progressDialog = new ProgressDialog(this);
        initFirebaseData();
        loadUserData();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
    /*    edtEmail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                signInGoogle();
                return true;
            }
        });*/
    }

    private void signInGoogle() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void saveData() {
        DatabaseReference refUser = FirebaseDatabase.getInstance().getReference().child(InstanceVariants.CHILD_ACCOUNT).child(user.getUid());
        DatabaseReference refAvatar = refUser.child("avartar");
        DatabaseReference refName = refUser.child("first_name");
        if (edtName.getText().toString().isEmpty()) {
            edtName.setError("Không được để trống");
            return;
        }
        if (edtEmail.getText().toString().isEmpty()) {
            edtEmail.setError("Không được để trống");
            return;
        }
       /* if (!isValidEmail(edtEmail.getText())) return;
        else {
            if (mGoogleSignInAccount != null && !mGoogleSignInAccount.getEmail().equals(user.getEmail())) {

                final AuthCredential credential = GoogleAuthProvider.getCredential(mGoogleSignInAccount.getIdToken(), null);
                // Prompt the user to re-provide their sign-in credentials

                List<? extends UserInfo> providerData = user.getProviderData();
                for (UserInfo userInfo : providerData) {
                    String providerId = userInfo.getProviderId();
                    Log.d("hieuhk", "providerId = " + userInfo.getProviderId());
                    if (providerId.equals("google.com")) {
                        user.unlink(providerId)
                                .addOnCompleteListener(this,
                                        new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (!task.isSuccessful()) {
                                                    Log.d("hieuhk", "unlink error: ");
                                                } else {

                                                    Log.d("hieuhk", "unlink successful: ");
                                                    DataProduce dataProduce = new DataProduce();
                                                    dataProduce.linkAccount(credential,
                                                            user, UserDetailActivity.this);
                                                }
                                            }
                                        });
                    }
                }
            }
        }*/
        if (resultUri != null) {
            DataProduce dataProduce = new DataProduce();
            StorageReference ref = FirebaseStorage.getInstance().getReference().child("AVATAR/" + user.getUid());
            dataProduce.uploadImage(UserDetailActivity.this, resultUri, ref, refAvatar);
        }
        userData.setFirst_name(edtName.getText().toString());
        refName.setValue(userData.getFirst_name());
        if (isValidEmail(edtEmail.getText())) return;

    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void checkStatusChange() {
        if (!edtEmail.getText().equals(user.getEmail())) {
            btnSave.setEnabled(true);
            return;
        }
        if (!edtName.getText().equals(userData.getFirst_name())) {
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

        DatabaseReference userRoot = root.child(user.getUid());
        userRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userData = dataSnapshot.getValue(Account.class);
                    loadData(userData);
                    loadAvatar();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadAvatar() {
        if (userData != null)
            Glide.with(this /* context */)
                    .load(userData.getAvartar())
                    .into(imgProfile);
    }

    private void loadData(Account data) {
        if (data != null) {
            edtName.setText(data.getFirst_name());
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
        } else if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                mGoogleSignInAccount = task.getResult(ApiException.class);
                mGoogleSignInClient.revokeAccess();
                progressDialog.setTitle("Kiểm tra địa chỉ Email...");
                progressDialog.show();
                checkAccountCreated(mGoogleSignInAccount);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
            }
        }
    }

    private void checkAccountCreated(final GoogleSignInAccount account) {
        mAuth.fetchProvidersForEmail(account.getEmail()).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                if (task.isSuccessful()) {
                    //getProviders().size() will return size 1. if email ID is available.
                    int check = task.getResult().getProviders().size();
                    // if is created then login and move to main activity
                    if (check == 1) {
                        Toast.makeText(UserDetailActivity.this,
                                "Email này đã liên kết với một tài khoản khác. Vui lòng thử lại", Toast.LENGTH_LONG).show();
                    } else
                    // if not move to regist phone number activity
                    {
                        edtEmail.setText(account.getEmail());
                    }
                } else Toast.makeText(UserDetailActivity.this,
                        "Có lỗi xảy ra. Vui lòng thử lại", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
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
