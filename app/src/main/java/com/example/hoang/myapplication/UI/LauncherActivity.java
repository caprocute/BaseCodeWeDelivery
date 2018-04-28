package com.example.hoang.myapplication.UI;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoang.myapplication.Aunthencation.SignUpActivity;
import com.example.hoang.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rilixtech.CountryCodePicker;

public class LauncherActivity extends AppCompatActivity {
    private CountryCodePicker mCcp;
    private AppCompatEditText mEdtPhoneNumber;
    private int mTime = 3;
    private DemNguocRunnable mCounDownRun;
    private Handler mCounDownHandler;
    private ScrollView mScrollLogin;
    private FrameLayout mStart, mFrame, phoneClick;
    private LinearLayout mlayout;
    private TextView connect;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        mCcp = (CountryCodePicker) findViewById(R.id.ccp);
        mEdtPhoneNumber = (AppCompatEditText) findViewById(R.id.phone_number_edt);
        mStart = (FrameLayout) findViewById(R.id.layoutstart);
        mFrame = (FrameLayout) findViewById(R.id.frame);
        phoneClick = (FrameLayout) findViewById(R.id.phoneclick);
        mlayout = (LinearLayout) findViewById(R.id.layout);
        mScrollLogin = (ScrollView) findViewById(R.id.scrollLogin);
        connect = (TextView) findViewById(R.id.connect);
        mCcp.registerPhoneNumberTextView(mEdtPhoneNumber);
        mCounDownHandler = new Handler();
        mCounDownRun = new DemNguocRunnable();
        getscreensize();
        showDemNguoc();
        mAuth = FirebaseAuth.getInstance();
/*
        mCcp.setOnClickListener(onClickListener);
*/
        mEdtPhoneNumber.setOnClickListener(onClickListener);
        connect.setOnClickListener(onClickListener);
        phoneClick.setOnClickListener(onClickListener);

    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(LauncherActivity.this, SignUpActivity.class);
            Bundle bundle = new Bundle();
            switch (v.getId()) {
                case R.id.phoneclick:
                    bundle.putBoolean("check", true);
                    break;
                case R.id.phone_number_edt:
                    bundle.putBoolean("check", true);
                    break;
                case R.id.connect:
                    bundle.putBoolean("check", false);
                    break;
            }
            intent.putExtra("package", bundle);
            startActivity(intent);
        }
    };

    private void getscreensize() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        Toast.makeText(this, "size: " + height + " " + width, Toast.LENGTH_LONG).show();
        if (height < 1920) {
            setScroll();
        }
    }

    private void setScroll() {
        Toast.makeText(this, "size: set scroll", Toast.LENGTH_LONG).show();
        FrameLayout framTop = (FrameLayout) findViewById(R.id.framTop);
        mFrame.removeView(mlayout);
        framTop.getLayoutParams().height = 420;
        mScrollLogin.addView(mlayout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        mScrollLogin.fullScroll(View.FOCUS_DOWN);

    }

    private void showDemNguoc() {
        mTime = 3;
        mCounDownHandler.removeCallbacks(mCounDownRun);
        mCounDownHandler.postDelayed(mCounDownRun, 1000);
    }

    private class DemNguocRunnable implements Runnable {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            handleDemnguoc();
        }
    }

    private void handleDemnguoc() {
        mTime--;
        if (mTime == 0) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(intent);
            } else {
                mlayout.setVisibility(View.VISIBLE);
                mStart.setVisibility(View.GONE);
            }
        }
        mCounDownHandler.postDelayed(mCounDownRun, 1000);
    }
}
