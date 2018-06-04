package com.example.kaku.here.Aunthencation;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kaku.here.Model.Account;
import com.example.kaku.here.R;
import com.example.kaku.here.UI.UserTypeActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.concurrent.TimeUnit;

public class PhoneAuthActivity extends AppCompatActivity implements
        View.OnClickListener {

    private static final String TAG = "PhoneAuthActivity";

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;
    private final String CHILD_ACCOUNT = "ACCOUNT";
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private ViewGroup mPhoneNumberViews;
    private GoogleSignInAccount accountgg;
    private TextView mStatusText;
    private TextView mDetailText;
    private TextView mTitle, mHelp;
    private StorageReference mStorageRef;

    private EditText mVerificationField;


    private ImageButton mVerifyButton, mBackButton;


    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_phone_auth);

        // Restore instance state
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        // Assign views
        mPhoneNumberViews = (ViewGroup) findViewById(R.id.phone_auth_fields);
        mStatusText = (TextView) findViewById(R.id.status);
        mDetailText = (TextView) findViewById(R.id.detail);
        mVerificationField = (EditText) findViewById(R.id.field_verification_code);
        mVerifyButton = (ImageButton) findViewById(R.id.button_verify_phone);
        mBackButton = (ImageButton) findViewById(R.id.button_prvious);
        mTitle = (TextView) findViewById(R.id.txttitle);
        mHelp = (TextView) findViewById(R.id.txtHelp);
        mHelp.setOnClickListener(this);
        mBackButton.setOnClickListener(this);

        mVerifyButton.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
                mVerificationInProgress = false;
                updateUI(STATE_VERIFY_SUCCESS, credential);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                mVerificationInProgress = false;
                if (e instanceof FirebaseAuthInvalidCredentialsException) {

                } else if (e instanceof FirebaseTooManyRequestsException) {
                }
                updateUI(STATE_VERIFY_FAILED);
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId);
                mVerificationId = verificationId;
                mResendToken = token;
                updateUI(STATE_CODE_SENT);
            }
        };

        getDataFromIntent();
        Resources res = getResources();
        String s = res.getString(R.string.verify_code, phone);
        mTitle.setText(s);
    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void uploadAvatar(Account account) {
        mStorageRef = FirebaseStorage.getInstance().getReference();

        Bitmap bitmap = getBitmapFromURL(account.getAvartar());
        StorageReference riversRef = mStorageRef.child("AVATAR/" + account.getId() + ".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = riversRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(PhoneAuthActivity.this, "Upload avatar fail", Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
            }
        });
    }

    private Account initAccount() {
        Account account = new Account();
        FirebaseUser user = mAuth.getCurrentUser();
        account.setId(user.getUid());
        account.setPhone(user.getPhoneNumber());
        account.setEmail(user.getEmail());
        if (accountgg != null) {
            account.setFirst_name(accountgg.getGivenName());
            account.setLast_name(accountgg.getFamilyName());
            account.setAvartar(accountgg.getPhotoUrl().toString());
            uploadAvatar(account);
        }
        return account;
    }

    private void createUserData() {
        final DatabaseReference root = FirebaseDatabase.getInstance().getReference().child(CHILD_ACCOUNT);
        DatabaseReference users = root.child(mAuth.getCurrentUser().getUid());
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(PhoneAuthActivity.this, "ACcount created", Toast.LENGTH_SHORT).show();
                } else {
                    root.child(mAuth.getCurrentUser().getUid()).setValue(initAccount());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getDataFromIntent() {
        phone = getIntent().getStringExtra("phonenumber");
        accountgg = getIntent().getParcelableExtra("account");
        if (accountgg == null) {
            startPhoneNumberVerification(phone);
        } else {

        }
    }

    private void linkAccount(AuthCredential authCredential) {
        if (mAuth.getCurrentUser() != null) {
            mAuth.getCurrentUser().linkWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d("hieuhk", "onComplete: link account ");
                        Intent intent = new Intent(PhoneAuthActivity.this, UserTypeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(intent);
                    } else {
                        Log.d("hieuhk", "failer: link account " + task.getException());
                        Toast.makeText(PhoneAuthActivity.this,
                                getString(R.string.error_happened).toString(), Toast.LENGTH_SHORT).show();
                    }
                }

            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        if (accountgg != null) {
            startPhoneNumberVerification(phone);
        } else if (mVerificationInProgress && validatePhoneNumber()) {
            startPhoneNumberVerification(phone);
        }
    }

    private void showHelpDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_login_dialog, null);
        dialogBuilder.setView(dialogView);
        Button mResendButton = (Button) dialogView.findViewById(R.id.button_resend);
        Button mWebButton = (Button) dialogView.findViewById(R.id.button_web);
        Button mCancelButton = (Button) dialogView.findViewById(R.id.button_cancel);
        mResendButton.setOnClickListener(this);
        mWebButton.setOnClickListener(this);

        final AlertDialog alertDialog = dialogBuilder.create();

        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = alertDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


        alertDialog.show();
        mWebButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PhoneAuthActivity.this, getString(R.string.not_support_function), Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });
        mResendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }


    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, this, mCallbacks);
        mVerificationInProgress = true;
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
        // [END verify_with_code]

    }

    // resend verify code
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks,
                token);
    }

    // sign in with phone
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            createUserData();
                            if (accountgg == null) {
                                Intent intent = new Intent(PhoneAuthActivity.this, UserTypeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                finish();
                                startActivity(intent);
                            } else {
                                AuthCredential credentialGG = GoogleAuthProvider.getCredential(accountgg.getIdToken(), null);
                                linkAccount(credentialGG);
                            }
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                mVerificationField.setError("Invalid code.");
                            }
                            updateUI(STATE_SIGNIN_FAILED);
                        }
                    }
                });
    }


    private void updateUI(int uiState) {
        updateUI(uiState, mAuth.getCurrentUser(), null);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            updateUI(STATE_SIGNIN_SUCCESS, user);
        } else {
            updateUI(STATE_INITIALIZED);
        }
    }

    private void updateUI(int uiState, FirebaseUser user) {
        updateUI(uiState, user, null);
    }

    private void updateUI(int uiState, PhoneAuthCredential cred) {
        updateUI(uiState, null, cred);
    }

    private void updateUI(int uiState, FirebaseUser user, PhoneAuthCredential cred) {
        switch (uiState) {
            case STATE_INITIALIZED:
                mDetailText.setText(null);
                break;
            case STATE_CODE_SENT:
                // Code sent state, show the verification field, the
                mDetailText.setText(R.string.status_code_sent);
                break;
            case STATE_VERIFY_FAILED:
                // Verification has failed, show all options
                mDetailText.setText(R.string.status_verification_failed);
                break;
            case STATE_VERIFY_SUCCESS:
                // Verification has succeeded, proceed to firebase sign in
                mDetailText.setText(R.string.status_verification_succeeded);
                // Set the verification text based on the credential
                if (cred != null) {
                    if (cred.getSmsCode() != null) {
                        mVerificationField.setText(cred.getSmsCode());
                    } else {
                        mVerificationField.setText(R.string.instant_validation);
                    }
                }

                break;
            case STATE_SIGNIN_FAILED:
                // No-op, handled by sign-in check
                mDetailText.setText(R.string.status_sign_in_failed);
                break;
            case STATE_SIGNIN_SUCCESS:
                // Np-op, handled by sign-in check
                break;
        }

        if (user == null) {
            // Signed out
            mPhoneNumberViews.setVisibility(View.VISIBLE);
            mStatusText.setText(R.string.signed_out);
        } else {
            // Signed in
            mPhoneNumberViews.setVisibility(View.GONE);
            mVerificationField.setText(null);
            mStatusText.setText(R.string.signed_in);
            mDetailText.setText(getString(R.string.firebase_status_fmt, user.getUid()));
        }
    }

    private boolean validatePhoneNumber() {
        String phoneNumber = phone;
        if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(PhoneAuthActivity.this, "Invalid phone number.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    // onclick listener
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_verify_phone:
                String code = mVerificationField.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    mVerificationField.setError("Cannot be empty.");
                    return;
                }
                verifyPhoneNumberWithCode(mVerificationId, code);
                break;
            case R.id.button_resend:
                resendVerificationCode(phone, mResendToken);
                break;
            case R.id.txtHelp:
                showHelpDialog();
                break;
            case R.id.button_prvious:
                onBackPressed();
                break;
        }
    }
}