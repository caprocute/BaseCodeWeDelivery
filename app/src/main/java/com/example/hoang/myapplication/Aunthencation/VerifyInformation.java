package com.example.hoang.myapplication.Aunthencation;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoang.myapplication.R;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rilixtech.CountryCodePicker;

public class VerifyInformation extends AppCompatActivity {
    private GoogleSignInAccount accountgg;
    private EditText edtName, edtHo, edtEmail;
    private AppCompatEditText edtPhone;
    private CountryCodePicker countryCodePicker;
    private ImageButton next;
    private TextView txtError;
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
        countryCodePicker = (CountryCodePicker) findViewById(R.id.ccp);
        countryCodePicker.registerPhoneNumberTextView(edtPhone);
        txtError = (TextView) findViewById(R.id.txtError);
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
                    intent.putExtra("account",accountgg);
                    intent.putExtra("phonenumber",countryCodePicker.getFullNumberWithPlus());
                    startActivity(intent);
                }
            }
        });
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
