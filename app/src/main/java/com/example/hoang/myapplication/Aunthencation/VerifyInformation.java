package com.example.hoang.myapplication.Aunthencation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.hoang.myapplication.R;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.rilixtech.CountryCodePicker;

public class VerifyInformation extends AppCompatActivity {
    private GoogleSignInAccount accountgg;
    private EditText edtName,edtHo,edtEmail;
    private AppCompatEditText edtPhone;
    private CountryCodePicker countryCodePicker;
    private ImageButton next;
    private TextView txtError;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_information);
        accountgg = getIntent().getParcelableExtra("account");

        edtEmail=(EditText)findViewById(R.id.edtEmail);
        edtHo=(EditText)findViewById(R.id.edtHo);
        edtName=(EditText)findViewById(R.id.edtName);
        edtPhone=(AppCompatEditText) findViewById(R.id.phone_number_edt);
        next=(ImageButton)findViewById(R.id.button_next);
        countryCodePicker=(CountryCodePicker)findViewById(R.id.ccp);
        countryCodePicker.registerPhoneNumberTextView(edtPhone);
        txtError=(TextView)findViewById(R.id.txtError);
        if(accountgg!=null){
            edtEmail.setText(accountgg.getEmail());
            edtName.setText(accountgg.getGivenName());
            edtHo.setText(accountgg.getFamilyName());
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!countryCodePicker.getFullNumber().isEmpty()||!(countryCodePicker.getFullNumber()==null)){
                    if (countryCodePicker.isValid()){
                        txtError.setVisibility(View.GONE);
                    }
                    else {
                        txtError.setVisibility(View.VISIBLE);
                        txtError.setText(getString(R.string.valid_phone_number));
                    }
                }
                else{
                    txtError.setVisibility(View.VISIBLE);
                    txtError.setText(getString(R.string.input_phone_number));
                }
            }
        });
    }
}
