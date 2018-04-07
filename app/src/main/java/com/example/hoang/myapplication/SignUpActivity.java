package com.example.hoang.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class SignUpActivity extends AppCompatActivity {
    private LinearLayout group1, group2;
    private boolean check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        group1 = (LinearLayout) findViewById(R.id.groupphone);
        group2 = (LinearLayout) findViewById(R.id.groupsoicity);

        Intent callerIntent = getIntent();
        Bundle packageFromCaller = callerIntent.getBundleExtra("package");
        check = packageFromCaller.getBoolean("check");

        if (check) {
            group1.setVisibility(View.VISIBLE);
        } else {
            group2.setVisibility(View.VISIBLE);
        }


    }
}
