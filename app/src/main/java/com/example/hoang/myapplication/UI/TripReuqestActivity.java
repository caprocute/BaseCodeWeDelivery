package com.example.hoang.myapplication.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.hoang.myapplication.Model.Trip;
import com.example.hoang.myapplication.R;

public class TripReuqestActivity extends AppCompatActivity implements View.OnClickListener {
    private CheckBox checLoading;
    private TextView txtTip, txtDistance, txtTripSum, txtDriverType;
    private ImageView imgTipNext, imgTipPre, imgBack, imBillBoard;
    private EditText edtNoteTrip;
    private Button btnSend;
    private RadioGroup radioGroup;
    private RadioButton radioButton1, radioButton2;
    private boolean isLoading = false;
    private int tipCount = 0;
    private float countSum;
    private Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_reuqest);

        Intent intent = getIntent();
        trip = (Trip) intent.getParcelableExtra("trip");

        checLoading = (CheckBox) findViewById(R.id.checkLoading);
        txtTip = (TextView) findViewById(R.id.txtTip);
        txtDistance = (TextView) findViewById(R.id.txtDistance);
        txtDriverType = (TextView) findViewById(R.id.txtDriverType);
        txtTripSum = (TextView) findViewById(R.id.txtSum);
        imgTipNext = (ImageView) findViewById(R.id.imgTipNext);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        imBillBoard = (ImageView) findViewById(R.id.imgBillBoard);
        imgTipPre = (ImageView) findViewById(R.id.imgTipPre);
        edtNoteTrip = (EditText) findViewById(R.id.edtNoteTrip);
        btnSend = (Button) findViewById(R.id.btnSend);
        radioButton1 = (RadioButton) findViewById(R.id.rdEconomy);
        radioButton2 = (RadioButton) findViewById(R.id.rdExpress);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        txtDistance.setText((trip.getDistanceSum() / 1000) + " km");
        imgTipPre.setOnClickListener(this);
        imgTipNext.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        txtDriverType.setText(trip.getDrivingMode());

        if (!trip.getExpressMode()) radioButton1.setChecked(true);
        else radioButton2.setChecked(true);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rdEconomy) {
                    trip.setExpressMode(false);
                    calculatorMoney();
                } else {
                    trip.setExpressMode(true);
                    calculatorMoney();
                }
            }
        });
        checLoading.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    trip.setUsingLoading(true);
                    calculatorMoney();
                } else {
                    trip.setUsingLoading(false);
                    calculatorMoney();
                }
            }
        });
        calculatorMoney();
    }

    private void calculatorMoney() {
        countSum = 0;
        // if using loading service

        if (trip.getDrivingMode().equals("HereBike")) {
            if (trip.getExpressMode()) {
                // if < 4km auto 20000VND
                if (trip.getDistanceSum() <= 4000) countSum += 20000;
                else {
                    // >4 km => 5000 vnd for per km next
                    countSum += 20000 + (trip.getDistanceSum() - 4000) * 5;
                }
            } else {
                // if < 4km auto 20000VND
                if (trip.getDistanceSum() <= 4000) countSum += 16000;
                else {
                    // >4 km => 5000 vnd for per km next
                    countSum += 16000 + (trip.getDistanceSum() - 4000) * 4;
                }
            }
        } else {
            if (!trip.getExpressMode()) {
                // if < 4km auto 150000 VND
                if (trip.getDistanceSum() <= 4000) countSum += 150000;
                else if (trip.getDistanceSum() > 4000 && trip.getDistanceSum() <= 10000)
                    countSum += 150000 + (trip.getDistanceSum() - 4000) * 20;
                else if (trip.getDistanceSum() > 10000 && trip.getDistanceSum() <= 15000)
                    countSum += 150000 + 6 * 20000 + (trip.getDistanceSum() - 10000) * 15;
                else if (trip.getDistanceSum() > 15000)
                    countSum += 150000 + 6 * 20000 + 5 * 15000 + (trip.getDistanceSum() - 15000) * 14;
            } else {
                if (trip.getDistanceSum() <= 4000) countSum += 150000;
                else if (trip.getDistanceSum() > 4000 && trip.getDistanceSum() <= 10000)
                    countSum += 150000 + (trip.getDistanceSum() - 4000) * 25;
                else if (trip.getDistanceSum() > 10000 && trip.getDistanceSum() <= 15000)
                    countSum += 150000 + 6 * 25000 + (trip.getDistanceSum() - 10000) * 20;
                else if (trip.getDistanceSum() > 15000)
                    countSum += 150000 + 6 * 25000 + 5 * 20000 + (trip.getDistanceSum() - 15000) * 18;
            }
        }
        countSum += countSum / 1000;
        countSum = Math.round(countSum * 1000) / 1000;
        if (trip.getUsingLoading()) {
            countSum += 30000;
        }
        // count with tip
        if (tipCount >= 0)
            countSum += tipCount * 10000;
        //10% VAT
        countSum += countSum * 10 / 100;
        trip.setMoneySum(countSum);
        txtTripSum.setText(trip.getMoneySum() + " VNĐ");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgTipNext:
                tipCount++;
                txtTip.setText(tipCount + "");
                calculatorMoney();
                break;
            case R.id.imgTipPre:
                if (tipCount > 0) tipCount--;
                txtTip.setText(tipCount + "");
                calculatorMoney();
                break;
            case R.id.imgBack:
                onBackPressed();
                break;
            case R.id.btnSend:
                trip.setMoneySum(countSum);
                final Intent data = new Intent();
                // Truyền data vào intent
                data.putExtra("trip", trip);
                setResult(Activity.RESULT_OK, data);
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {

        // đặt resultCode là Activity.RESULT_CANCELED thể hiện
        // đã thất bại khi người dùng click vào nút Back.
        // Khi này sẽ không trả về data.
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }
}
