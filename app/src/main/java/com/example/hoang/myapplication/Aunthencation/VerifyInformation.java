package com.example.hoang.myapplication.Aunthencation;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoang.myapplication.R;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.rilixtech.CountryCodePicker;

public class VerifyInformation extends AppCompatActivity {
    private GoogleSignInAccount accountgg;
    private EditText edtName, edtHo, edtEmail;
    private AppCompatEditText edtPhone;
    private CountryCodePicker countryCodePicker;
    private ImageButton next, back;
    private TextView txtError, txtPolicy;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_information);
        accountgg = getIntent().getParcelableExtra("account");
        mAuth = FirebaseAuth.getInstance();
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtHo = (EditText) findViewById(R.id.edtHo);
        edtName = (EditText) findViewById(R.id.edtName);
        edtPhone = (AppCompatEditText) findViewById(R.id.phone_number_edt);
        next = (ImageButton) findViewById(R.id.button_next);
        back = (ImageButton) findViewById(R.id.button_prvious);
        countryCodePicker = (CountryCodePicker) findViewById(R.id.ccp);
        countryCodePicker.registerPhoneNumberTextView(edtPhone);
        txtError = (TextView) findViewById(R.id.txtError);
        txtPolicy = (TextView) findViewById(R.id.txtPolicy);
        if (accountgg != null) {
            edtEmail.setText(accountgg.getEmail());
            edtName.setText(accountgg.getGivenName());
            edtHo.setText(accountgg.getFamilyName());
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInputField()) {
                    Intent intent = new Intent(VerifyInformation.this, PhoneAuthActivity.class);
                    intent.putExtra("account", accountgg);
                    intent.putExtra("phonenumber", countryCodePicker.getFullNumberWithPlus());
                    startActivity(intent);
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        makingPolicyText(txtPolicy);
    }

    private void makingPolicyText(TextView view) {
        String accept = getString(R.string.policy_accept).toString();
        String term = getString(R.string.term_title).toString();
        String policy = getString(R.string.policy_title).toString();
        SpannableStringBuilder spanTxt = new SpannableStringBuilder(accept);
        spanTxt.append(term);
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://hoangkhachieu96.wixsite.com/here"));
                startActivity(browserIntent);
            }
        }, spanTxt.length() - term.length(), spanTxt.length(), 0);
        int current = spanTxt.length();
        spanTxt.append(getString(R.string.and).toString());
        spanTxt.setSpan(new ForegroundColorSpan(Color.BLACK), current, spanTxt.length(), 0);
        spanTxt.append(policy);
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://hoangkhachieu96.wixsite.com/here"));
                startActivity(browserIntent);
            }
        }, spanTxt.length() - policy.length(), spanTxt.length(), 0);
        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(spanTxt, TextView.BufferType.SPANNABLE);
    }

    private boolean checkInputField() {
        if (!countryCodePicker.getFullNumber().isEmpty() || !(countryCodePicker.getFullNumber() == null)) {
            if (countryCodePicker.isValid()) {
                edtPhone.setError(null);
            } else {
                edtPhone.setError(getString(R.string.valid_phone_number));
                return false;
            }
        } else {
            edtPhone.setError(getString(R.string.valid_phone_number));
        }

        if (edtHo.getText().toString().isEmpty()) {
            edtHo.setError(getString(R.string.not_null));
            return false;
        }
        if (edtName.getText().toString().isEmpty()) {
            edtName.setError(getString(R.string.not_null));
            return false;
        }
        return true;
    }
}
