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
import android.widget.TextView;

import com.example.hoang.myapplication.Model.Trip;
import com.example.hoang.myapplication.R;

public class TripReuqestActivity extends AppCompatActivity implements View.OnClickListener {
    private CheckBox checLoading;
    private TextView txtTip, txtDistance, txtTripSum;
    private ImageView imgTipNext, imgTipPre;
    private EditText edtNoteTrip;
    private Button btnSend;

    private boolean isLoading = false;
    private int tipCount = 0;
    long countSum;
    Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_reuqest);

        Intent intent = getIntent();
        trip = (Trip) intent.getParcelableExtra("trip");

        checLoading = (CheckBox) findViewById(R.id.checkLoading);
        txtTip = (TextView) findViewById(R.id.txtTip);
        txtDistance = (TextView) findViewById(R.id.txtDistance);
        txtTripSum = (TextView) findViewById(R.id.txtSum);
        imgTipNext = (ImageView) findViewById(R.id.imgTipNext);
        imgTipPre = (ImageView) findViewById(R.id.imgTipPre);
        edtNoteTrip = (EditText) findViewById(R.id.edtNoteTrip);
        btnSend = (Button) findViewById(R.id.btnSend);

        txtDistance.setText((trip.getDistanceSum() / 1000) + " km");
        countSum = trip.getDistanceSum() * 10;
        txtTripSum.setText(countSum+" đ");
        imgTipPre.setOnClickListener(this);
        imgTipNext.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        checLoading.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    countSum += 30000;
                    trip.setUsingLoading(true);
                    txtTripSum.setText(countSum + " đ");
                } else {
                    trip.setUsingLoading(false);
                    countSum -= 30000;
                    txtTripSum.setText(countSum + " đ");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgTipNext:
                tipCount++;
                txtTip.setText(tipCount + "");
                countSum += 10000;
                txtTripSum.setText(countSum + " đ");
                break;
            case R.id.imgTipPre:
                if (tipCount >= 0) tipCount--;
                txtTip.setText(tipCount + "");
                countSum -= 10000;
                txtTripSum.setText(countSum + " đ");
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
