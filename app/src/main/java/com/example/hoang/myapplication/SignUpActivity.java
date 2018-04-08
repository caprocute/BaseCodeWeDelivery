package com.example.hoang.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.rilixtech.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity {
    private LinearLayout group1, group2;
    private boolean check;
    private ImageView btnSPN;
    private CountryCodePicker mCcp;
    private AppCompatEditText mEdtPhoneNumber;
    private TextView txtWarning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        setContentView(R.layout.activity_sign_up);

        group1 = (LinearLayout) findViewById(R.id.groupphone);
        group2 = (LinearLayout) findViewById(R.id.groupsoicity);
        btnSPN = (ImageView) findViewById(R.id.btnSubmitPhoneNumber);
        mCcp = (CountryCodePicker) findViewById(R.id.ccp);
        mEdtPhoneNumber = (AppCompatEditText) findViewById(R.id.phone_number_edt);
        txtWarning = (TextView) findViewById(R.id.txtwarning);
        Intent callerIntent = getIntent();
        Bundle packageFromCaller = callerIntent.getBundleExtra("package");
        check = packageFromCaller.getBoolean("check");
        mCcp.registerPhoneNumberTextView(mEdtPhoneNumber);

        if (check) {
            group1.setVisibility(View.VISIBLE);
            group2.setVisibility(View.GONE);
        } else {
            group2.setVisibility(View.VISIBLE);
            group1.setVisibility(View.GONE);
        }

        btnSPN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPhoneNumber(mCcp);
            }
        });


    }

    private boolean checkPhoneNumber(CountryCodePicker mCcp) {
        if (mCcp.isValid()) {
            txtWarning.setVisibility(View.GONE);
            Intent intent = new Intent(SignUpActivity.this, PhoneAuthActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("phonenumber", mCcp.getFullNumberWithPlus());
            intent.putExtra("package", bundle);
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
