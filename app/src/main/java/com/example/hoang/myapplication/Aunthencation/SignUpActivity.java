package com.example.hoang.myapplication.Aunthencation;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.rilixtech.CountryCodePicker;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAGGG = "GoogleLogin";
    private ConstraintLayout group1, group2;
    private boolean check;
    private ImageView btnSPN;
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        setContentView(R.layout.activity_sign_up);

        group1 = (ConstraintLayout) findViewById(R.id.groupphone);
        group2 = (ConstraintLayout) findViewById(R.id.groupsoicity);
        btnSPN = (ImageView) findViewById(R.id.btnSubmitPhoneNumber);
        btnFace = (Button) findViewById(R.id.btnLoginFacebook);
        btnGG = (Button) findViewById(R.id.btnLoginGoogle);

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
/*                firebaseAuthWithGoogle(account);*/
                Intent intent=new Intent(SignUpActivity.this,VerifyInformation.class);
                intent.putExtra("account",account);
                startActivity(intent);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
            }
        }
    }
    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d(TAGGG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                           /* Intent intent=new Intent(SignUpActivity.this,VerifyInformation.class);
                            intent.putExtra("account",acct);
                            startActivity(intent);*/
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAGGG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }
    private boolean checkPhoneNumber(CountryCodePicker mCcp) {
        if (mCcp.isValid()) {
            txtWarning.setVisibility(View.GONE);
            Intent intent = new Intent(SignUpActivity.this, PhoneAuthActivity.class);
            intent.putExtra("phonenumber",  mCcp.getFullNumberWithPlus());
            startActivity(intent);
        } else {
            mEdtPhoneNumber.setText("");
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
