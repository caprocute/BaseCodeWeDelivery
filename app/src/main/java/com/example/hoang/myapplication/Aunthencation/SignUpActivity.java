package com.example.hoang.myapplication.Aunthencation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hoang.myapplication.MainActivity;
import com.example.hoang.myapplication.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.ProviderQueryResult;
import com.rilixtech.CountryCodePicker;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "GoogleLogin";
    private ConstraintLayout group1, group2;
    private boolean check;
    private ImageView btnSPN, btnBack;
    private CountryCodePicker mCcp;
    private AppCompatEditText mEdtPhoneNumber;
    private TextView txtWarning;
    private Button btnGG;
    private Button btnFace;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        group1 = (ConstraintLayout) findViewById(R.id.groupphone);
        group2 = (ConstraintLayout) findViewById(R.id.groupsoicity);
        btnSPN = (ImageView) findViewById(R.id.btnSubmitPhoneNumber);
        btnFace = (Button) findViewById(R.id.btnLoginFacebook);
        btnGG = (Button) findViewById(R.id.btnLoginGoogle);
        btnBack = (ImageButton) findViewById(R.id.button_prvious);

        mCcp = (CountryCodePicker) findViewById(R.id.ccp);
        mEdtPhoneNumber = (AppCompatEditText) findViewById(R.id.phone_number_edt);
        txtWarning = (TextView) findViewById(R.id.txtwarning);
        Intent callerIntent = getIntent();
        Bundle packageFromCaller = callerIntent.getBundleExtra("package");
        check = packageFromCaller.getBoolean("check");
        mCcp.registerPhoneNumberTextView(mEdtPhoneNumber);

        if (check) {
            group1.setVisibility(View.VISIBLE);
            btnSPN.setVisibility(View.VISIBLE);
            group2.setVisibility(View.GONE);
        } else {
            group2.setVisibility(View.VISIBLE);
            group1.setVisibility(View.GONE);
            btnSPN.setVisibility(View.GONE);
        }

        btnSPN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPhoneNumber(mCcp);
            }
        });
        btnGG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInGoogle();

            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                mGoogleSignInClient.revokeAccess();
                checkAccountCreated(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
            }
        }
    }

    // login with google account
    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            intent.putExtra("account", acct);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            finish();
                            startActivity(intent);
                        } else {
                        }
                    }
                });
    }

    // check if ggacc is created
    private void checkAccountCreated(final GoogleSignInAccount account) {
        mAuth.fetchProvidersForEmail(account.getEmail()).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                if (task.isSuccessful()) {
                    //getProviders().size() will return size 1. if email ID is available.
                    int check = task.getResult().getProviders().size();
                    // if is created then login and move to main activity
                    if (check == 1) {
                        firebaseAuthWithGoogle(account);
                    } else
                    // if not move to regist phone number activity
                    {
                        Intent intent = new Intent(SignUpActivity.this, VerifyInformation.class);
                        intent.putExtra("account", account);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    // check phone number
    private boolean checkPhoneNumber(CountryCodePicker mCcp) {
        if (mCcp.isValid()) {
            txtWarning.setVisibility(View.GONE);
            Intent intent = new Intent(SignUpActivity.this, PhoneAuthActivity.class);
            intent.putExtra("phonenumber", mCcp.getFullNumberWithPlus());
            startActivity(intent);
        } else {
            mEdtPhoneNumber.setText("");
            mEdtPhoneNumber.setError(getString(R.string.valid_phone_number).toString());
            txtWarning.setVisibility(View.VISIBLE);
        }
        return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
